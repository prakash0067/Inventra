<?php
$host = "localhost";
$dbname = "Inventra";
$username = "root";
$password = "";

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