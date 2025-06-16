<?php
header('Content-Type: application/json');
require '../db_config.php';

// Decode JSON input
$data = json_decode(file_get_contents("php://input"));

// Validate required fields
$product_id     = $data->product_id     ?? null;
$added_quantity = $data->added_quantity ?? null;
$added_by       = $data->added_by       ?? null;
$remarks        = $data->remarks        ?? '';
$added_at       = $data->added_at       ?? null;

if (!$product_id || !$added_quantity || !$added_by || !$added_at) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields: product_id, added_quantity, added_by, added_at $product_id : $added_quantity : $added_by : $remarks : $added_at"
    ]);
    exit;
}

try {
    $conn = getDB();

    // Optional: check if product exists (recommended for sanity check)
    $stmt = $conn->prepare("SELECT product_name FROM products WHERE product_id = ?");
    $stmt->bind_param("i", $product_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Product not found"
        ]);
        exit;
    }

    $product = $result->fetch_assoc();
    $product_name = $product['product_name'];

    // Insert stock addition record
    $insert = $conn->prepare("INSERT INTO stock_additions (product_id, added_quantity, added_by, remarks, added_at) VALUES (?, ?, ?, ?, ?)");
    $insert->bind_param("iiiss", $product_id, $added_quantity, $added_by, $remarks, $added_at);
    $insert->execute();

    echo json_encode([
        "status" => "success",
        "message" => "$product_name stock updated successfully",
        "product_id" => $product_id,
        "added_quantity" => $added_quantity
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>