<?php
// get_cart_data.php API to get carts data for particular user
header('Content-Type: application/json');
require '../db_config.php';

if (isset($_POST["user_id"])) {
    $user_id = trim($_POST["user_id"]);

    if ($user_id <= 0) {
        echo json_encode(["status" => "error", "message" => "Invalid User ID"]);
        exit;
    }

    try {
        // Get database connection
        $conn = getDB();
        $products = [];
        $sql = "SELECT p.product_id AS productId, p.product_name AS productName, p.description, p.price, p.quantity AS currentStock, image_path AS productImage, c.cart_id AS cartId, c.quantity AS purchaseQuantity, c.added_at AS cartAddedDateTime, c.added_by AS userId FROM cart c, products p WHERE c.product_id = p.product_id AND c.added_by = ?";

        if ($stmt = $conn->prepare($sql)) {
            $stmt->bind_param("i", $user_id);
            $stmt->execute();
            $result = $stmt->get_result();
            
            while ($row = $result->fetch_assoc()) {
                $products[] = $row;
            }
        }

        // Return the results
        echo json_encode([
            "status" => "success",
            "products" => $products
        ]);
    } catch (Exception $e) {
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "User ID not found"]);
    exit;
}
?>
