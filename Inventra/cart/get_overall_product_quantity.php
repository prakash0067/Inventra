<?php

// function to fetch products quantity in cart 
function getProductOverallQuantityFromCart($conn, $product_id) {
    $sql = "SELECT SUM(quantity) AS total_quantity FROM cart WHERE product_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $product_id);
    $stmt->execute();
    $productResult2 = $stmt->get_result();
    $product2 = $productResult2->fetch_assoc();

    // If null, it means no such product is in cart yet
    return $product2['total_quantity'] ?? 0;
}
