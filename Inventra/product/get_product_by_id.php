<?php
header("Content-Type: application/json");

require_once '../db_config.php';

$response = [
    "success" => false,
    "message" => "An error occurred"
];

// Validate input
if (!isset($_POST['product_id']) || empty(trim($_POST['product_id']))) {
    $response["message"] = "Product ID is required";
    echo json_encode($response);
    exit;
}

$product_id = intval($_POST['product_id']);

$conn = getDB();
if (!$conn) {
    $response["message"] = "Database connection failed";
    echo json_encode($response);
    exit;
}

// Step 1: Get product details
$sql = "
    SELECT 
        p.product_id, 
        p.product_name, 
        p.description, 
        p.quantity, 
        p.price, 
        p.image_path, 
        c.category_name
    FROM 
        products p
    JOIN 
        categories c ON p.category_id = c.category_id
    WHERE 
        p.product_id = ?
";

$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $product_id);
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        if ($row = $result->fetch_assoc()) {
            
            // Step 2: Get total sales separately
            $sales_sql = "SELECT SUM(quantity) AS total_sales FROM sales_items WHERE product_id = ?";
            $sales_stmt = $conn->prepare($sales_sql);
            if ($sales_stmt) {
                $sales_stmt->bind_param("i", $product_id);
                if ($sales_stmt->execute()) {
                    $sales_result = $sales_stmt->get_result();
                    $sales_row = $sales_result->fetch_assoc();
                    $row["total_sales"] = $sales_row["total_sales"] ?? 0;
                }
                $sales_stmt->close();
            }

            $response["success"] = true;
            $response["product"] = $row;
            unset($response["message"]);
        } else {
            $response["message"] = "Product not found";
        }
    } else {
        $response["message"] = "Query execution failed";
    }
    $stmt->close();
} else {
    $response["message"] = "Failed to prepare SQL statement";
}

$conn->close();

echo json_encode($response);
