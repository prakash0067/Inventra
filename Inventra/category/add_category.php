<?php
header('Content-Type: application/json');
require '../db_config.php';

$data = json_decode(file_get_contents("php://input"));

$category_name = $data->categoryName ?? '';
$description = $data->description ?? '';

if (!$category_name) {
    echo json_encode(["status" => "error", "message" => "Category name required"]);
    exit;
}

try {
    $conn = getDB();
    $stmt = $conn->prepare("INSERT INTO categories (category_name, description) VALUES (?, ?)");
    $stmt->execute([$category_name, $description]);

    echo json_encode([
        "status" => "success",
        "message" => "Category created successfully"
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
