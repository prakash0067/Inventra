<?php
header('Content-Type: application/json');
require '../db_config.php';

if (!isset($_POST['product_id']) || !isset($_FILES['image'])) {
    echo json_encode(["status" => "error", "message" => "Product ID and image file are required"]);
    exit;
}

$product_id = $_POST['product_id'];
$image = $_FILES['image'];

// Check if image is valid
if ($image['error'] !== 0) {
    echo json_encode(["status" => "error", "message" => "Error uploading image"]);
    exit;
}

// Create uploads directory if not exists
$uploadDir = "../img/";
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

$ext = pathinfo($image['name'], PATHINFO_EXTENSION);
$filename = uniqid("img_") . "." . $ext;
$targetPath = $uploadDir . $filename;

if (move_uploaded_file($image['tmp_name'], $targetPath)) {
    // Save image path to database
    $relativePath = "" . $filename;

    try {
        $conn = getDB();
        $stmt = $conn->prepare("UPDATE products SET image_path = ? WHERE product_id = ?");
        $stmt->execute([$relativePath, $product_id]);

        echo json_encode([
            "status" => "success",
            "message" => "Image uploaded successfully",
            "image_url" => $relativePath
        ]);
    } catch (Exception $e) {
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Failed to move uploaded file"]);
}
?>
