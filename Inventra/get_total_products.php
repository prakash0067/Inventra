<?php
include 'db_config.php';

function getTotalProducts($conn) {
    $query = "SELECT COUNT(*) AS total FROM products";
    $result = mysqli_query($conn, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        return $row['total'];
    } else {
        return null; // or handle error appropriately
    }
}

$conn = getDB(); // should return a mysqli connection

header('Content-Type: application/json');
echo json_encode(['totalProducts' => getTotalProducts($conn)]);

mysqli_close($conn);
?>
