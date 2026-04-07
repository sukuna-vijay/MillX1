<?php
include '../config/db.php';

// Force drop to ensure new schema
$conn->query("DROP TABLE IF EXISTS products");
$conn->query("DROP TABLE IF EXISTS prices");

// Create prices table with correct schema
$sql = "CREATE TABLE prices (
    product_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) DEFAULT 'kg',
    image VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";

if ($conn->query($sql) === TRUE) {
    echo "Table 'prices' created successfully.<br>";
} else {
    echo "Error creating table: " . $conn->error . "<br>";
    exit;
}

// Seed Data
$seeds = [
    ['Wheat', 'Whole grain', 7.00, 'kg'],
    ['Chilli Powder', 'Premium Spices', 15.00, 'kg'],
    ['Corn', 'Fresh Harvest', 10.00, 'kg'],
    ['Ragi', 'Finger Millet', 5.00, 'kg'],
    ['Snacks Flour', 'Refined', 7.00, 'kg'],
    ['Idly Flour', 'Mixed Grain', 15.00, 'kg']
];

foreach ($seeds as $p) {
    $stmt = $conn->prepare("INSERT INTO prices (product_name, description, price, unit) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssds", $p[0], $p[1], $p[2], $p[3]);
    if ($stmt->execute()) {
        echo "Inserted: " . $p[0] . "<br>";
    } else {
        echo "Error: " . $stmt->error . "<br>";
    }
}
?>
