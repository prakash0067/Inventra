<?php
header("Content-Type: application/json");

$response = ["success" => false, "message" => ""];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // Check if file and user_id are present
    if (!isset($_FILES['profile_image']) || !isset($_POST['user_id'])) {
        $response['message'] = 'Missing required fields.';
        echo json_encode($response);
        exit;
    }

    $userId = intval($_POST['user_id']);
    $file = $_FILES['profile_image'];

    // Check for upload errors
    if ($file['error'] !== UPLOAD_ERR_OK) {
        $response['message'] = 'File upload error.';
        echo json_encode($response);
        exit;
    }

    // Validate MIME type
    $allowedMimeTypes = ['image/jpeg', 'image/png'];
    $allowedExtensions = ['jpg', 'jpeg', 'png'];

    $fileExt = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    if (!in_array($fileExt, $allowedExtensions)) {
        $response['message'] = 'Only JPG, JPEG, and PNG files are allowed.';
        echo json_encode($response);
        exit;
    }

    $imageInfo = getimagesize($file['tmp_name']);
    if ($imageInfo === false || !in_array($imageInfo['mime'], $allowedMimeTypes)) {
        $response['message'] = 'Invalid or unsupported image format.';
        echo json_encode($response);
        exit;
    }

    // Prepare upload directory
    $uploadDir = '../profile/';
    if (!file_exists($uploadDir)) {
        mkdir($uploadDir, 0777, true);
    }

    // Unique file name
    $fileName = 'user_' . $userId . '_' . time() . '.jpg';  // Save as .jpg for consistency
    $targetPath = $uploadDir . $fileName;

    // Load original image using GD
    switch ($imageInfo['mime']) {
        case 'image/jpeg':
            $image = imagecreatefromjpeg($file['tmp_name']);
            break;
        case 'image/png':
            $image = imagecreatefrompng($file['tmp_name']);
            break;
        default:
            $response['message'] = 'Unsupported image type.';
            echo json_encode($response);
            exit;
    }

    // Get image size & crop to square
    $width = imagesx($image);
    $height = imagesy($image);
    $size = min($width, $height);
    $x = ($width - $size) / 2;
    $y = ($height - $size) / 2;

    $squareImage = imagecreatetruecolor(300, 300);
    imagecopyresampled($squareImage, $image, 0, 0, $x, $y, 300, 300, $size, $size);

    // Save compressed image as JPG
    if (!imagejpeg($squareImage, $targetPath, 75)) {
        $response['message'] = 'Failed to save processed image.';
        echo json_encode($response);
        exit;
    }

    // Free memory
    imagedestroy($image);
    imagedestroy($squareImage);

    // Update database
    require_once '../db_config.php'; // Make sure this contains $conn = new mysqli(...)

    $conn = getDB();
    $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE user_id = ?");
    if ($stmt) {
        $stmt->bind_param("si", $fileName, $userId);
        if ($stmt->execute()) {
            $response['success'] = true;
            $response['message'] = 'Image uploaded and saved successfully.';
            $response['image_url'] = $targetPath;
        } else {
            $response['message'] = 'Failed to update user profile.';
        }
        $stmt->close();
    } else {
        $response['message'] = 'Database error: failed to prepare statement.';
    }

    $conn->close();

} else {
    $response['message'] = 'Invalid request method.';
}

echo json_encode($response);
?>
