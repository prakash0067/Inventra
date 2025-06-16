<?php
header("Content-Type: application/json");
include('../db_config.php');

$response = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['id'])) {
    $staffId = intval($_POST['id']);
    $conn = getDB();

    // Step 1: Check if staff exists
    $checkQuery = "SELECT username FROM users WHERE user_id = ? AND user_type = 'staff'";
    $checkStmt = mysqli_prepare($conn, $checkQuery);

    if ($checkStmt) {
        mysqli_stmt_bind_param($checkStmt, "i", $staffId);
        mysqli_stmt_execute($checkStmt);
        mysqli_stmt_store_result($checkStmt);

        if (mysqli_stmt_num_rows($checkStmt) > 0) {
            // Fetch the username
            mysqli_stmt_bind_result($checkStmt, $username);
            mysqli_stmt_fetch($checkStmt);
            mysqli_stmt_close($checkStmt);

            // Step 2: Proceed to delete
            $deleteQuery = "DELETE FROM users WHERE user_id = ?";
            $deleteStmt = mysqli_prepare($conn, $deleteQuery);

            if ($deleteStmt) {
                mysqli_stmt_bind_param($deleteStmt, "i", $staffId);
                $exec = mysqli_stmt_execute($deleteStmt);

                if ($exec) {
                    $response = [
                        "status" => "success",
                        "message" => "Staff $username deleted successfully"
                    ];
                } else {
                    $response = [
                        "status" => "failure",
                        "error" => mysqli_stmt_error($deleteStmt)
                    ];
                }

                mysqli_stmt_close($deleteStmt);
            } else {
                $response = [
                    "status" => "failure",
                    "error" => mysqli_error($conn)
                ];
            }

        } else {
            $response = [
                "status" => "not_found",
                "message" => "Staff not found or already deleted"
            ];
            mysqli_stmt_close($checkStmt);
        }

    } else {
        $response = [
            "status" => "failure",
            "error" => mysqli_error($conn)
        ];
    }

    mysqli_close($conn);
} else {
    $response = [
        "status" => "failure",
        "message" => "Missing ID parameter"
    ];
}

echo json_encode($response);
