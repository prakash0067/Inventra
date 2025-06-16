<?php
// get_cart_total.php - API to calculate total cart amount for a specific user
header('Content-Type: application/json');
require '../db_config.php';

if (isset($_POST["user_id"])) {
    $user_id = intval(trim($_POST["user_id"]));

    if ($user_id <= 0) {
        echo json_encode(["status" => "error", "message" => "Invalid User ID"]);
        exit;
    }

    try {
        $conn = getDB();

        $sql = "SELECT SUM(p.price * c.quantity) AS totalAmount
                FROM cart c
                JOIN products p ON c.product_id = p.product_id
                WHERE c.added_by = ?";

        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();

        $total = $row["totalAmount"] ?? 0;

        echo json_encode([
            "status" => "success",
            "total" => round($total, 2)
        ]);

    } catch (Exception $e) {
        echo json_encode(["status" => "error", "message" => "Server error: " . $e->getMessage()]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "User ID is required"]);
}
?>
