<?php
require_once __DIR__ . '/../config/db.php';

$sql = "ALTER TABLE users 
ADD COLUMN IF NOT EXISTS reset_otp varchar(10) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS reset_otp_expires_at datetime DEFAULT NULL;";

if ($conn->query($sql) === TRUE) {
    echo "Columns added successfully";
} else {
    echo "Error adding columns: " . $conn->error;
}
?>
