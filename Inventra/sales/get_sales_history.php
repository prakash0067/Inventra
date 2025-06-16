<?php
header("Content-Type: application/json");
include '../db_config.php';
$conn = getDB();

$sql = "SELECT sale_id, customer_name, total_amount, created_at FROM sales ORDER BY created_at DESC";
$result = $conn->query($sql);

$sales = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        // Convert datetime string to a DateTime object
        $date = new DateTime($row["created_at"]);

        // Format separately
        $formattedDate = $date->format("l, d - F, Y");   // Example: Saturday, 07 - June, 2025
        $formattedTime = $date->format("h:i A");         // Example: 12:45 PM

        $total_items = 0;
        $sales_id = $row["sale_id"];
        $sql2 = "SELECT SUM(quantity) AS total_items FROM sales_items WHERE sale_id = ?";

        if ($stmt = $conn->prepare($sql2)) {
            $stmt->bind_param("i",$sales_id);
            $stmt->execute();
            $result2 = $stmt->get_result();
            $sales_item_array = $result2->fetch_assoc();

            if (!empty($sales_item_array)) {
                $total_items = $sales_item_array["total_items"];
            }

            $result2->close();
            $stmt->close();
        }

        $sales[] = [
            "billNumber" => $row["sale_id"],
            "customerName" => $row["customer_name"],
            "totalAmount" => "₹ " . $row["total_amount"],
            "saleDate" => $formattedDate,
            "saleTime" => $formattedTime,
            "totalItems" => $total_items
        ];
    }
    echo json_encode(["status" => true, "data" => $sales]);
} else {
    echo json_encode(["status" => true, "data" => []]);
}

$conn->close();
?>
