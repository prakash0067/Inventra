<?php
include('../db_config.php');

header('Content-Type: application/json');

// Check if request is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Read raw POST data
    $input = json_decode(file_get_contents("php://input"), true);
    $conn = getDB();
    // Extract name and email
    $name = trim($input['username'] ?? '');
    $email = trim($input['email'] ?? '');
    $password = trim($input['password'] ?? '');
    $user_type = 'staff';

    // Basic validation
    if (empty($name) || empty($email)) {
        echo json_encode(['status' => 'failure', 'message' => 'Name and Email are required.']);
        exit;
    }

    // Check if email already exists
    $checkStmt = $conn->prepare("SELECT user_id FROM users WHERE email = ?");
    $checkStmt->bind_param("s", $email);
    $checkStmt->execute();
    $checkStmt->store_result();

    if ($checkStmt->num_rows > 0) {
        echo json_encode(['status' => 'exists', 'message' => 'Email already registered.']);
        exit;
    }

    // Insert user
    $stmt = $conn->prepare("INSERT INTO users (username, email, password, user_type) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $name, $email, $password, $user_type);

    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => "Staff ".$name." added successfully"]);
    } else {
        echo json_encode(['status' => 'failure', 'message' => 'Insert failed.']);
    }

    $stmt->close();
    $checkStmt->close();
    $conn->close();
} else {
    echo json_encode(['status' => 'failure', 'message' => 'Invalid request method.']);
}
?>
