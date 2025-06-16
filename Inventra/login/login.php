<?php
include("../db_config.php");

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['email']) && $_POST['password']) {
    // Get email and password from POST
    $email = $_POST['email'];
    $passwordIV = $_POST['password'];

    $conn = getDB();
    // Prepare the SQL statement
    $stmt = $conn->prepare("SELECT user_id, username, user_type, profile_pic FROM users WHERE email = ? AND password = ?");
    $stmt->bind_param("ss", $email, $passwordIV); // 'ss' = two strings

    // Execute and get result
    $stmt->execute();
    $result = $stmt->get_result();

    // Check if user found
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode([
            "user_id" => $row['user_id'],
            "name" => $row['username'],
            "user_type" => $row['user_type'],
            "profile_pic" => $row['profile_pic']
        ]);
    } else {
        http_response_code(401); // Unauthorized
        echo json_encode(["error" => "Invalid credentials"]);
    }

    // Close statement
    $stmt->close();
    $conn->close();
} else {
    http_response_code(405); // Method Not Allowed
    echo json_encode(["error" => "POST method required"]);
}
?>
