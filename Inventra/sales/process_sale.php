<?php
include '../db_config.php';

header('Content-Type: application/json');

// Read raw POST data
$data = json_decode(file_get_contents("php://input"), true);

// Validate input
if (
    !isset($data['customer_name'], $data['customer_mobile'],
    $data['payment_status'], $data['payment_method'], $data['cart_items'])
) {
    echo json_encode(["status" => false, "message" => "Missing required fields"]);
    exit;
}

$customerName = $data['customer_name'];
$customerMobile = $data['customer_mobile'];
$paymentStatus = $data['payment_status'];
$paymentMethod = $data['payment_method'];
$cartItems = $data['cart_items'];

$totalAmount = 0;
foreach ($cartItems as $item) {
    $totalAmount += $item['price'] * $item['quantity'];
}

$conn = getDB();
// Begin transaction
$conn->begin_transaction();

try {

    // Insert into sales
    $stmt = $conn->prepare("INSERT INTO sales (customer_name, customer_mobile_number, total_amount, payment_status, payment_method) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("ssdss", $customerName, $customerMobile, $totalAmount, $paymentStatus, $paymentMethod);
    $stmt->execute();
    $saleId = $stmt->insert_id;
    $stmt->close();

    // Insert each item
    foreach ($cartItems as $item) {
        $productId = $item['product_id'];
        $quantity = $item['quantity'];
        $price = $item['price'];

        // Insert into sales_items
        $stmt = $conn->prepare("INSERT INTO sales_items (sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("iiid", $saleId, $productId, $quantity, $price);
        if (!$stmt->execute()) throw new Exception("Failed to insert sales item.");
        $stmt->close();

        // Update product stock
        $stmt = $conn->prepare("UPDATE products SET quantity = quantity - ? WHERE product_id = ?");
        $stmt->bind_param("ii", $quantity, $productId);
        if (!$stmt->execute()) throw new Exception("Failed to update product stock.");
        $stmt->close();
    }

    $conn->commit();

    echo json_encode(["status" => true, "message" => "Sale processed successfully."]);
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["status" => false, "message" => "Transaction failed: " . $e->getMessage()]);
}

$conn->close();
?>