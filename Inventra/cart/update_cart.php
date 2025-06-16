<?php
header('Content-Type: application/json');
require '../db_config.php';
require 'get_overall_product_quantity.php';

function response($status, $message)
{
    echo json_encode(["status" => $status, "message" => $message]);
    exit;
}


if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = isset($_POST['user_id']) ? (int) $_POST['user_id'] : 0;
    $product_id = isset($_POST['product_id']) ? (int) $_POST['product_id'] : 0;
    $cart_id = isset($_POST['cart_id']) ? (int) $_POST['cart_id'] : 0;
    $action = isset($_POST['action']) ? $_POST['action'] : '';

    if ($user_id <= 0 || $product_id <= 0 || empty($action)) {
        response("error", "Missing or invalid parameters.");
    }

    $conn = getDB();

    // Check if product exists in cart
    $stmt = $conn->prepare("SELECT quantity FROM cart WHERE product_id = ? AND added_by = ?");
    $stmt->bind_param("ii", $product_id, $user_id);
    $stmt->execute();
    $cartResult = $stmt->get_result();

    if ($cartResult->num_rows === 0) {
        response("error", "Product not found in cart.");
    }

    $cartItem = $cartResult->fetch_assoc();
    $currentQuantity = $cartItem['quantity'];

    if ($action === 'increase') {
        // Get total stock from products table
        $stmt = $conn->prepare("SELECT quantity FROM products WHERE product_id = ?");
        $stmt->bind_param("i", $product_id);
        $stmt->execute();
        $productResult = $stmt->get_result();

        if ($productResult->num_rows === 0) {
            response("error", "Product not found.");
        }

        $product = $productResult->fetch_assoc();
        $availableStock = $product['quantity'];

        // Get total quantity of this product already added to cart (by all users)
        $totalInCart = getProductOverallQuantityFromCart($conn, $product_id);

        if ($totalInCart >= $availableStock) {
            response("error", "Cannot add more. Product is out of stock.");
        }

        $newQuantity = $currentQuantity + 1;
        $stmt = $conn->prepare("UPDATE cart SET quantity = ? WHERE product_id = ? AND added_by = ?");
        $stmt->bind_param("iii", $newQuantity, $product_id, $user_id);
        $stmt->execute();

        response("success", "Quantity increased.");
    } else if ($action === 'decrease') {
        if ($currentQuantity <= 1) {
            response("error", "Minimum quantity is 1. You can remove the item instead.");
        }

        $newQuantity = $currentQuantity - 1;
        $stmt = $conn->prepare("UPDATE cart SET quantity = ? WHERE product_id = ? AND added_by = ?");
        $stmt->bind_param("iii", $newQuantity, $product_id, $user_id);
        $stmt->execute();

        response("success", "Quantity decreased.");
    } else if ($action === 'remove') {
        $stmt = $conn->prepare("DELETE FROM cart WHERE cart_id = ?");
        $stmt->bind_param("i", $cart_id);
        $stmt->execute();

        response("success", "Product removed from cart.");
    } else {
        response("error", "Invalid action.");
    }
} else {
    response("error", "Invalid request method.");
}
?>