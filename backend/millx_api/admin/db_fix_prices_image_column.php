<?php
include "../config/db.php";

echo "Checking database schema...<br>";

// 1. Check if 'prices' table exists
$checkTable = $conn->query("SHOW TABLES LIKE 'prices'");
if ($checkTable->num_rows == 0) {
    echo "Table 'prices' does not exist. Please run setup_prices_table.php first.<br>";
    exit;
} else {
    echo "Table 'prices' exists.<br>";
}

// 2. Check if 'image' column exists
$checkColumn = $conn->query("SHOW COLUMNS FROM prices LIKE 'image'");
if ($checkColumn->num_rows == 0) {
    echo "Column 'image' missing. Adding it now...<br>";
    $sql = "ALTER TABLE prices ADD COLUMN image VARCHAR(255) DEFAULT NULL";
    if ($conn->query($sql) === TRUE) {
        echo "Column 'image' added successfully.<br>";
    } else {
        echo "Error adding column: " . $conn->error . "<br>";
    }
} else {
    echo "Column 'image' already exists.<br>";
}

// 3. Check upload directory
$target_dir = "../uploads/products/";
if (!file_exists($target_dir)) {
    echo "Upload directory missing. Creating...<br>";
    if (mkdir($target_dir, 0777, true)) {
        echo "Directory created.<br>";
    } else {
        echo "Failed to create directory. Check permissions.<br>";
    }
} else {
    echo "Upload directory exists.<br>";
}

echo "Fix Check Complete.";
?>
