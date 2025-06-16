<?php
header('Content-Type: application/json');
require '../db_config.php';

// Get the input data
$data = json_decode(file_get_contents("php://input"));

$searchQuery = $data->searchQuery ?? '';

if (!$searchQuery) {
    echo json_encode(["status" => "error", "message" => "Search query is required"]);
    exit;
}

try {
    // Get database connection
    $conn = getDB();

    // Prepare the query with the LIKE operator for matching product names
    $stmt = $conn->prepare("SELECT * FROM products WHERE product_name LIKE ?");
    
    // Add "%" before and after the search query for partial matching
    $searchQuery = "%" . $searchQuery . "%";
    
    // Bind the search query to the statement
    $stmt->bind_param("s", $searchQuery);

    // Execute the statement
    $stmt->execute();
    
    // Get the result
    $result = $stmt->get_result();
    
    // Fetch all matching products
    $products = [];
    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }

    // Return the results
    echo json_encode([
        "status" => "success",
        "products" => $products
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
