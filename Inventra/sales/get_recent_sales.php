<?php
header("Content-Type: application/json");
include '../db_config.php'; // your DB connection file

$response = ["success" => false, "data" => []];

try {
    $conn = getDB();

    $sql = "SELECT sale_id, customer_name, total_amount, created_at, payment_status 
            FROM sales 
            ORDER BY created_at DESC 
            LIMIT 5";

    $result = $conn->query($sql);

    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }

    while ($row = $result->fetch_assoc()) {
        $formattedDate = date("l, d M Y h:i A", strtotime($row["created_at"]));
        $response["data"][] = [
            "sale_id" => $row["sale_id"],
            "customer_name" => $row["customer_name"],
            "total_amount" => number_format((float)$row["total_amount"], 2),
            "created_at" => $formattedDate,
            "payment_status" => $row["payment_status"]
        ];
    }

    $response["success"] = true;
} catch (Exception $e) {
    $response["message"] = $e->getMessage();
}

echo json_encode($response);
