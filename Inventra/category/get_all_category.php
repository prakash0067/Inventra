<?php
header('Content-Type: application/json');
require '../db_config.php';

try {
    $conn = getDB();

    // Prepare the query
    $stmt = $conn->prepare("SELECT category_id AS id, 
    category_name AS name, 
    description, 
    created_at  FROM categories");

    // Execute the query
    $stmt->execute();

    // Store the result
    $result = $stmt->get_result();

    // Fetch all rows
    $categories = [];
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row;
    }

    echo json_encode([
        "status" => "success",
        "categories" => $categories
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
