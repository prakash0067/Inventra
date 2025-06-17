<?php
$host = "your_host_name";
$dbname = "your_database_name";
$username = "your_user_name";
$password = "your_password";

function getDB() {
    global $host, $dbname, $username, $password;

    $conn = new mysqli($host, $username, $password, $dbname);

    if ($conn->connect_error) {
        echo json_encode(["error" => ["text" => "Connection failed: " . $conn->connect_error]]);
        die();
    }

    return $conn;
}
?>
