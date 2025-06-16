<?php
include 'db_config.php';

function getSalesAmount($conn) {
    $query = "SELECT SUM(total_amount) AS totalSales 
              FROM sales 
              WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) 
              AND YEAR(created_at) = YEAR(CURRENT_DATE())";

    $result = mysqli_query($conn, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        return $row['totalSales'];
    } else {
        return null; // or handle error
    }
}

$conn = getDB(); // Make sure this returns a mysqli connection

header('Content-Type: application/json');
echo json_encode(['totalSales' => getSalesAmount($conn)]);

mysqli_close($conn);
?>
