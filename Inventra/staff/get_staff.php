<?php
header("Content-Type: application/json");
require_once('../db_config.php');

$response = [];

try {
    $conn = getDB();

    // Prepare query for security
    $stmt = $conn->prepare("SELECT user_id, username, email, created_at, profile_pic FROM users WHERE user_type = ?");
    $userType = 'staff';
    $stmt->bind_param("s", $userType);
    $stmt->execute();

    $result = $stmt->get_result();
    $staff = [];

    while ($row = $result->fetch_assoc()) {
        // Format created_at to "24 June, 2025"
        $date = new DateTime($row['created_at']);
        $row['created_at'] = $date->format('d F, Y'); // Example: 24 June, 2025

        $staff[] = $row;
    }

    if (count($staff) > 0) {
        http_response_code(200);
        $response = [
            "status" => "success",
            "message" => "Staff list retrieved successfully",
            "staff" => $staff
        ];
    } else {
        http_response_code(200);
        $response = [
            "status" => "empty",
            "message" => "No staff members found",
            "staff" => []
        ];
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(500);
    $response = [
        "status" => "error",
        "message" => "Internal Server Error: " . $e->getMessage()
    ];
}

echo json_encode($response);
