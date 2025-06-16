<?php
include '../db_config.php';

function getSalesByYear(mysqli $conn, int $year): float {
    $query = "SELECT SUM(total_amount) AS total_sales FROM sales WHERE YEAR(created_at) = ?";
    $stmt = $conn->prepare($query);
    if (!$stmt) return 0;

    $stmt->bind_param("i", $year);
    $stmt->execute();

    $result = $stmt->get_result();
    $data = $result->fetch_assoc();
    $stmt->close();

    return floatval($data['total_sales'] ?? 0);
}

try {
    $conn = getDB();
    $currentYear = date("Y");
    $previousYear = $currentYear - 1;

    $currentSales = getSalesByYear($conn, (int)$currentYear);
    $previousSales = getSalesByYear($conn, (int)$previousYear);

    $growth = 0.0;
    if ($previousSales > 0) {
        $growth = (($currentSales - $previousSales) / $previousSales) * 100;
    }

    $response = [
        "sales_growth_percent" => round($growth, 2)
    ];

    echo json_encode($response);
    $conn->close();
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server Error", "message" => $e->getMessage()]);
}
?>
