<?php
include 'db_config.php';

function getYearlySalesAmount($conn) {
    $query = "SELECT SUM(total_amount) AS yearlySalesAmount 
              FROM sales 
              WHERE YEAR(created_at) = YEAR(CURRENT_DATE())";

    $result = mysqli_query($conn, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        return $row['yearlySalesAmount'];
    } else {
        return null; // Handle error gracefully
    }
}

$conn = getDB(); // MySQLi connection

header('Content-Type: application/json');
echo json_encode(['yearlySalesAmount' => getYearlySalesAmount($conn)]);

mysqli_close($conn);
?>
