<?php
header("Content-Type: application/json");

require_once '../db_config.php';

$response = [
    "success" => false,
    "message" => "An error occurred"
];

// Validate input
if (!isset($_POST['barcode']) || empty(trim($_POST['barcode']))) {
    $response["message"] = "Barcode is required";
    echo json_encode($response);
    exit;
}

$barcode = trim($_POST['barcode']);

$conn = getDB();
if (!$conn) {
    $response["message"] = "Database connection failed";
    echo json_encode($response);
    exit;
}

$sql = "SELECT product_id, product_name, barcode, quantity, price, image_path FROM products WHERE barcode = ?";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("s", $barcode);
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        if ($row = $result->fetch_assoc()) {
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
?>
