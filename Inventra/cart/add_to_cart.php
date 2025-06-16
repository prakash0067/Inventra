<?php
header('Content-Type: application/json');
include '../db_config.php';
include 'get_overall_product_quantity.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userId = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
    $productId = isset($_POST['product_id']) ? intval($_POST['product_id']) : 0;
    $quantity = isset($_POST['quantity']) ? intval($_POST['quantity']) : 1;

    if ($userId <= 0 || $productId <= 0 || $quantity <= 0) {
        $response['status'] = 'error';
        $response['message'] = 'Invalid input';
        echo json_encode($response);
        exit;
    }

    $conn = getDB();

    // Step 1: Fetch product stock
    $productQuery = "SELECT quantity FROM products WHERE product_id = ?";
    $productStmt = $conn->prepare($productQuery);
    $productStmt->bind_param("i", $productId);
    $productStmt->execute();
    $productResult = $productStmt->get_result();

    if ($productResult->num_rows === 0) {
        $response['status'] = 'error';
        $response['message'] = 'Product not found';
        echo json_encode($response);
        exit;
    }

    $product = $productResult->fetch_assoc();
    $availableStock = intval($product['quantity']);

    // Step 2: Calculate total quantity already in carts (from all users)
    $totalInAllCarts = getProductOverallQuantityFromCart($conn, $productId);

    // Step 3: Get user's existing quantity in cart (if any)
    $checkQuery = "SELECT quantity FROM cart WHERE added_by = ? AND product_id = ?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("ii", $userId, $productId);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $existingItem = $result->fetch_assoc();
        $existingQty = intval($existingItem['quantity']);
        $newQty = $existingQty + $quantity;

        $futureTotalInAllCarts = $totalInAllCarts - $existingQty + $newQty;

        if ($futureTotalInAllCarts > $availableStock) {
            $response['status'] = 'error';
            $response['message'] = 'Requested quantity exceeds available stock across all carts';
            echo json_encode($response);
            exit;
        }

        // Update existing quantity
        $updateQuery = "UPDATE cart SET quantity = ? WHERE added_by = ? AND product_id = ?";
        $updateStmt = $conn->prepare($updateQuery);
        $updateStmt->bind_param("iii", $newQty, $userId, $productId);
        if ($updateStmt->execute()) {
            $response['status'] = 'success';
            $response['message'] = 'Cart quantity updated';
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Failed to update cart';
        }
    } else {
        // Total after insertion should not exceed available stock
        $futureTotalInAllCarts = $totalInAllCarts + $quantity;

        if ($futureTotalInAllCarts > $availableStock) {
            $response['status'] = 'error';
            $response['message'] = 'Requested quantity exceeds available stock across all carts';
            echo json_encode($response);
            exit;
        }

        // Insert new cart entry
        $insertQuery = "INSERT INTO cart (added_by, product_id, quantity) VALUES (?, ?, ?)";
        $insertStmt = $conn->prepare($insertQuery);
        $insertStmt->bind_param("iii", $userId, $productId, $quantity);
        if ($insertStmt->execute()) {
            $response['status'] = 'success';
            $response['message'] = 'Product added to cart';
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Failed to add product to cart';
        }
    }

    echo json_encode($response);

} else {
    $response['status'] = 'error';
    $response['message'] = 'Invalid request method';
    echo json_encode($response);
}
?>
