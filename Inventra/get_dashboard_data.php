<?php
include 'db_config.php';

function getCurrentStock($conn) {
    $query = "SELECT SUM(quantity) AS currentStock FROM products";
    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_assoc($result);
    return $row['currentStock'];
}

function getTotalCategories($conn) {
    $query = "SELECT COUNT(*) AS totalCategories FROM categories";
    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_assoc($result);
    return $row['totalCategories'];
}

function getTodaysSales($conn) {
    $query = "SELECT SUM(total_amount) AS totalSales 
              FROM sales
              WHERE DATE(created_at) = CURDATE()";
    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_assoc($result);
    return $row['totalSales'];
}

function getTotalOrdersCurrentMonth($conn) {
    $query = "SELECT COUNT(*) AS totalOrders 
              FROM sales 
              WHERE YEAR(created_at) = YEAR(CURRENT_DATE()) 
              AND MONTH(created_at) = MONTH(CURRENT_DATE())";
    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_assoc($result);
    return $row['totalOrders'];
}

function getTotalUsers($conn) {
    $query = "SELECT COUNT(*) AS totalUsers FROM users";
    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_assoc($result);
    return $row['totalUsers'];
}

// Get DB connection using mysqli
$conn = getDB(); // make sure getDB() returns a mysqli connection

// Switch case to handle different API requests
if (isset($_GET['type'])) {
    $type = $_GET['type'];

    switch ($type) {
        case 'current_stock':
            echo json_encode(['currentStock' => getCurrentStock($conn)]);
            break;
        case 'total_categories':
            echo json_encode(['totalCategories' => getTotalCategories($conn)]);
            break;
        case 'todays_sales':
            echo json_encode(['totalSales' => getTodaysSales($conn)]);
            break;
        case 'total_orders_current_month':
            echo json_encode(['totalOrders' => getTotalOrdersCurrentMonth($conn)]);
            break;
        case 'total_users':
            echo json_encode(['totalUsers' => getTotalUsers($conn)]);
            break;
        default:
            echo json_encode(['error' => 'Invalid type']);
            break;
    }
} else {
    echo json_encode(['error' => 'Type parameter is missing']);
}

mysqli_close($conn);
?>
