<?php
header("Content-Type: application/json");
include '../db_config.php'; // Your DB connection file

$response = ["success" => false, "data" => []];

try {
    // Decode JSON input
    $input = json_decode(file_get_contents("php://input"), true);
    $range = isset($input['range']) ? $input['range'] : 'day';

    // Validate range
    if (!in_array($range, ['day', 'month', 'year'])) {
        throw new Exception("Invalid range value");
    }

    $conn = getDB();

    // Build a temporary table of the last 5 time periods (day, month, year)
    $dates = [];
    $now = new DateTime();
    for ($i = 4; $i >= 0; $i--) {
        $temp = clone $now;
        if ($range === 'month') {
            $temp->modify("-$i month");
            $key = $temp->format("Y-m");
            $label = $temp->format("M Y");
        } elseif ($range === 'year') {
            $temp->modify("-$i year");
            $key = $temp->format("Y");
            $label = $temp->format("Y");
        } else {
            $temp->modify("-$i day");
            $key = $temp->format("Y-m-d");
            $label = $temp->format("d M");
        }
        $dates[$key] = [
            "label" => $label,
            "total" => 0.0
        ];
    }

    // Fetch totals from database for matching periods
    switch ($range) {
        case 'month':
            $query = "
                SELECT DATE_FORMAT(created_at, '%Y-%m') AS period, SUM(total_amount) AS total
                FROM sales
                WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 5 MONTH)
                GROUP BY period
            ";
            break;
        case 'year':
            $query = "
                SELECT DATE_FORMAT(created_at, '%Y') AS period, SUM(total_amount) AS total
                FROM sales
                WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 5 YEAR)
                GROUP BY period
            ";
            break;
        default: // day
            $query = "
                SELECT DATE(created_at) AS period, SUM(total_amount) AS total
                FROM sales
                WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 5 DAY)
                GROUP BY period
            ";
            break;
    }

    $result = $conn->query($query);

    while ($row = $result->fetch_assoc()) {
        $period = $row["period"];
        if (isset($dates[$period])) {
            $dates[$period]["total"] = (float) $row["total"];
        }
    }

    // Re-index and return response
    $response["data"] = array_values($dates);
    $response["success"] = true;

} catch (Exception $e) {
    $response["message"] = $e->getMessage();
}

echo json_encode($response);
