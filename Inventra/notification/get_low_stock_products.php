<?php
include '../db_config.php';

$conn = getDB();
$query = "SELECT product_name, quantity FROM products WHERE quantity < 10";
$result = mysqli_query($conn, $query);

$response = array();
while ($row = mysqli_fetch_assoc($result)) {
    $response[] = $row;
}
echo json_encode($response);
?>
