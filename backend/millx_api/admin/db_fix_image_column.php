<?php
include '../config/db.php';

header('Content-Type: application/json');

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

$sql = "ALTER TABLE machines ADD COLUMN image VARCHAR(255) DEFAULT NULL";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Column 'image' added successfully"]);
} else {
    // Check if duplicate column error
    if ($conn->errno == 1060) {
         echo json_encode(["message" => "Column 'image' already exists"]);
    } else {
         echo json_encode(["error" => "Error adding column: " . $conn->error]);
    }
}

$conn->close();
?>
