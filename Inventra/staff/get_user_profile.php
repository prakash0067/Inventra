<?php
header("Content-Type: application/json");

require_once '../db_config.php'; 

$response = [
    'success' => false,
    'message' => 'An error occurred'
];

// Sanitize and validate input
$user_id = filter_input(INPUT_GET, 'user_id', FILTER_VALIDATE_INT);

if (!$user_id) {
    http_response_code(400);
    $response['message'] = 'Invalid or missing user ID';
    echo json_encode($response);
    exit;
}

try {
    $conn = getDB();

    if (!$conn) {
        http_response_code(500);
        $response['message'] = 'Database connection failed';
        echo json_encode($response);
        exit;
    }

    $sql = "SELECT username, email, created_at, user_type, profile_pic FROM users WHERE user_id = ?";
    $stmt = $conn->prepare($sql);

    if (!$stmt) {
        throw new Exception("Prepare statement failed");
    }

    $stmt->bind_param("i", $user_id);
    $stmt->execute();

    $result = $stmt->get_result();

    if ($user = $result->fetch_assoc()) {
        // Format created_at
        $date = DateTime::createFromFormat('Y-m-d H:i:s', $user['created_at']);
        if ($date) {
            $user['created_at'] = $date->format('d F, Y h:i A');
        }

        http_response_code(200);
        echo json_encode([
            'success' => true,
            'data' => $user
        ]);
    } else {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'User not found'
        ]);
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
