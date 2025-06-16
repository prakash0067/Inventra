<?php
header('Content-Type: application/json');
require '../db_config.php';

$sales_id = 0;
$data_type = "sales";

// Validate input
if (isset($_POST["sales_id"])) {
    $sales_id = htmlspecialchars(trim($_POST["sales_id"]));
} else {
    echo json_encode(["status" => "error", "message" => "Sales ID not found"]);
    exit;
}

if (isset($_POST["data_type"])) {
    $data_type = trim($_POST["data_type"]);
}

try {
    $products = array();
    $conn = getDB();

    if ($data_type === "sales") {
        $sql = "SELECT * FROM sales WHERE sale_id = ? LIMIT 1";
        if ($stmt = $conn->prepare($sql)) {
            $stmt->bind_param('i', $sales_id);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows > 0) {
                $products = $result->fetch_assoc();

                // Format date and time
                $dateTime = new DateTime($products['created_at']);
                $formattedDate = $dateTime->format('d F, Y');     // e.g., 12 June, 2025
                $formattedTime = $dateTime->format('h:i A');      // e.g., 10:30 AM

                $products['sales_date'] = $formattedDate;
                $products['sales_time'] = $formattedTime;

                // Optionally remove raw created_at field
                // unset($products['created_at']);
            }

            $result->close();
            $stmt->close();

            echo json_encode([
                "status" => "success",
                "sale" => $products
            ]);
        }
    } else {
        $sql = "SELECT si.quantity AS qty_purchased, p.product_name, p.price AS product_price, p.image_path AS product_image 
                FROM sales_items si 
                JOIN products p ON si.product_id = p.product_id 
                WHERE si.sale_id = ?";

        if ($stmt = $conn->prepare($sql)) {
            $stmt->bind_param('i', $sales_id);
            $stmt->execute();
            $result = $stmt->get_result();

            while ($product = $result->fetch_assoc()) {
                $products[] = $product;
            }

            $result->close();
            $stmt->close();

            echo json_encode([
                "status" => "success",
                "items" => $products
            ]);
        }
    }
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>