<?php
// Database configuration
$host = "localhost";
$user = "root";
$pass = "";
$db   = "millx_db";

// Connect to MySQL
$conn = mysqli_connect($host, $user, $pass, $db);

// Check connection
if (!$conn) {
    header("Content-Type: application/json");
    echo json_encode([
        "status"  => "error",
        "message" => "Connection failed: " . mysqli_connect_error()
    ]);
    exit;
}
?>
