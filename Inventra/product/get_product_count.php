<?php
header("Content-Type: application/json");
include '../db_config.php';

$conn = getDB();

$response = [
    "total_products" => 0,
    "total_quantity" => 0,
    "product_growth" => 0.0,
    "quantity_growth" => 0.0,
    "success" => false,
    "error" => null
];

try {
    // 1. Get total products and total quantity (overall)
    $overallSql = "SELECT COUNT(*) AS total_products, SUM(quantity) AS total_quantity FROM products";
    $overallResult = $conn->query($overallSql);

    if ($overallResult && $overallResult->num_rows > 0) {
        $overallData = $overallResult->fetch_assoc();
        $response["total_products"] = (int)$overallData["total_products"];
        $response["total_quantity"] = (int)$overallData["total_quantity"];
    }

    // 2. Get product counts and quantity by month (last 2 months)
    $growthSql = "
        SELECT
            DATE_FORMAT(created_at, '%Y-%m') AS month,
            COUNT(*) AS product_count,
            SUM(quantity) AS total_quantity
        FROM products
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 2 MONTH)
        GROUP BY month
        ORDER BY month DESC
        LIMIT 2
    ";

    $growthResult = $conn->query($growthSql);

    if ($growthResult && $growthResult->num_rows > 0) {
        $rows = [];
        while ($row = $growthResult->fetch_assoc()) {
            $rows[] = $row;
        }

        $currentMonth = $rows[0];
        $lastMonth = $rows[1] ?? ["product_count" => 0, "total_quantity" => 0];

        if ($lastMonth["product_count"] > 0) {
            $response["product_growth"] = round(
                (($currentMonth["product_count"] - $lastMonth["product_count"]) / $lastMonth["product_count"]) * 100,
                2
            );
        }

        if ($lastMonth["total_quantity"] > 0) {
            $response["quantity_growth"] = round(
                (($currentMonth["total_quantity"] - $lastMonth["total_quantity"]) / $lastMonth["total_quantity"]) * 100,
                2
            );
        }
    }

    $response["success"] = true;

} catch (Exception $e) {
    $response["error"] = "Database error: " . $e->getMessage();
}

$conn->close();
echo json_encode($response);
?>
