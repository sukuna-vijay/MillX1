<?php
include '../config/db.php';

// Drop existing table if exists
$conn->query("DROP TABLE IF EXISTS stock");

// Create stock table with image column
$sql = "CREATE TABLE stock (
    product_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    unit VARCHAR(50) DEFAULT 'kg',
    image VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";

if ($conn->query($sql) === TRUE) {
    echo "Table 'stock' created successfully with columns: product_id, product_name, product_quantity, unit, image.<br>";
} else {
    echo "Error creating table: " . $conn->error . "<br>";
    exit;
}

// Seed Data
$seeds = [
    ['Wheat', 500, 'kg'],
    ['Chilli', 100, 'kg'],
    ['Corn', 250, 'kg'],
    ['Rice', 50, 'bags'],
    ['Paddy', 20, 'bags']
];

foreach ($seeds as $s) {
    $stmt = $conn->prepare("INSERT INTO stock (product_name, product_quantity, unit) VALUES (?, ?, ?)");
    $stmt->bind_param("sds", $s[0], $s[1], $s[2]);
    if ($stmt->execute()) {
        echo "Inserted: " . $s[0] . "<br>";
    } else {
        echo "Error: " . $stmt->error . "<br>";
    }
}
?>
