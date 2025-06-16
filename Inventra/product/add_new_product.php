<?php
header('Content-Type: application/json');
require '../db_config.php';

$data = json_decode(file_get_contents("php://input"));

$product_name = $data->productName ?? '';
$category_id = $data->categoryId ?? '';
$description = $data->description ?? '';
$price = $data->price ?? '';
$quantity = $data->quantity ?? '';
$barcode = $data->barcodeText ?? '';

if (!$product_name || !$category_id || !$price || !$quantity) {
    echo json_encode(["status" => "error", "message" => "Product name, category, price, and quantity are required"]);
    exit;
}

try {
    $conn = getDB();
    $stmt = $conn->prepare("INSERT INTO products (product_name, category_id, description, price, quantity, barcode) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$product_name, $category_id, $description, $price, $quantity, $barcode]);

    // Get last inserted product ID
    $product_id = $conn->insert_id;

    echo json_encode([
        "status" => "success",
        "message" => "Product created successfully",
        "productId" => $product_id
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>