<?php
header('Content-Type: application/json');
include '../db_config.php';

$response = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST' &&
    isset($_POST['user_id']) &&
    isset($_POST['customer_name']) &&
    isset($_POST['phone']) &&
    isset($_POST['payment_method']) &&
    isset($_POST['payment_status'])) {

    $user_id = $_POST['user_id'];
    $customer_name = $_POST['customer_name'];
    $phone = $_POST['phone'];
    $payment_method = $_POST['payment_method'];
    $payment_status = $_POST['payment_status'];

    if (empty($user_id) || empty($customer_name) || empty($phone) || empty($payment_method) || empty($payment_status)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'All fields are required'
        ]);
        exit;
    }

    $conn = getDB();

    // Step 1: Join cart with products to get price
    $cartQuery = $conn->prepare("
        SELECT c.product_id, c.quantity, p.price 
        FROM cart c
        JOIN products p ON c.product_id = p.product_id
        WHERE c.added_by = ?
    ");
    $cartQuery->bind_param("i", $user_id);
    $cartQuery->execute();
    $cartResult = $cartQuery->get_result();

    $cartItems = [];
    $totalAmount = 0;

    while ($row = $cartResult->fetch_assoc()) {
        $cartItems[] = $row;
        $totalAmount += $row['price'] * $row['quantity'];
    }

    if (empty($cartItems)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Cart is empty'
        ]);
        exit;
    }

    // Step 2: Insert into sales table
    $insertSale = $conn->prepare("
        INSERT INTO sales (customer_name, customer_mobile_number, total_amount, payment_status, payment_method, sold_by) 
        VALUES (?, ?, ?, ?, ?, ?)
    ");
    $insertSale->bind_param("ssdsss", $customer_name, $phone, $totalAmount, $payment_status, $payment_method, $user_id);

    if ($insertSale->execute()) {
        $sale_id = $insertSale->insert_id;

        // Step 3: Insert items into sales_items
        $insertItem = $conn->prepare("
            INSERT INTO sales_items (sale_id, product_id, quantity, price) 
            VALUES (?, ?, ?, ?)
        ");
        foreach ($cartItems as $item) {
            $insertItem->bind_param("iiid", $sale_id, $item['product_id'], $item['quantity'], $item['price']);
            $insertItem->execute();
        }
        $insertItem->close();

        // Step 3.5: Update stock in products table
        $updateStock = $conn->prepare("UPDATE products SET quantity = quantity - ? WHERE product_id = ?");
        foreach ($cartItems as $item) {
            $updateStock->bind_param("ii", $item['quantity'], $item['product_id']);
            $updateStock->execute();
        }
        $updateStock->close();

        // Step 4: Clear cart
        $deleteCart = $conn->prepare("DELETE FROM cart WHERE added_by = ?");
        $deleteCart->bind_param("i", $user_id);
        $deleteCart->execute();
        $deleteCart->close();

        $response['status'] = 'success';
        $response['message'] = 'Order placed successfully';
        $response['sale_id'] = $sale_id;
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Failed to insert sale';
    }

    $insertSale->close();
    $conn->close();
} else {
    $response['status'] = 'error';
    $response['message'] = 'Invalid request or missing parameters';
}

echo json_encode($response);
?>