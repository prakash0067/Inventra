<?php
header('Content-Type: application/json');
require '../db_config.php';

try {
    $conn = getDB();

    // Prepare the query
    $stmt = $conn->prepare("SELECT * FROM products");

    // Execute the query
    $stmt->execute();

    // Store the result
    $result = $stmt->get_result();

    // Fetch all rows
    $products = [];
    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }

    echo json_encode([
        "status" => "success",
        "products" => $products
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
