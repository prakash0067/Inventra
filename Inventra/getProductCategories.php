<?php
include 'db_config.php';

function getProductCategories($conn) {
    // Get the total number of products
    $queryTotalProducts = "SELECT COUNT(*) AS totalProducts FROM products";
    $resultTotal = mysqli_query($conn, $queryTotalProducts);
    $rowTotal = mysqli_fetch_assoc($resultTotal);
    $totalProducts = $rowTotal['totalProducts'];

    // If no products, return empty array
    if ($totalProducts == 0) {
        return [];
    }

    // Query to get product count and percentage per category
    $queryCategories = "
        SELECT 
            c.category_name,
            COUNT(p.product_id) AS productCount,
            (COUNT(p.product_id) / $totalProducts) * 100 AS percentage
        FROM categories c
        LEFT JOIN products p ON c.category_id = p.category_id
        GROUP BY c.category_id
    ";

    $result = mysqli_query($conn, $queryCategories);
    $categories = [];

    while ($row = mysqli_fetch_assoc($result)) {
        $categories[] = [
            'category_name' => $row['category_name'],
            'productCount' => (int)$row['productCount'],
            'percentage' => round($row['percentage'], 2)
        ];
    }

    return $categories;
}

$conn = getDB();
header('Content-Type: application/json');
echo json_encode(getProductCategories($conn));
mysqli_close($conn);
?>
