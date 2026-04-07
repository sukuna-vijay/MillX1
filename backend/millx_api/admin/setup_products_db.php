<?php
include '../config/db.php';

// Create Table
$sql = "CREATE TABLE IF NOT EXISTS products (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) DEFAULT 'kg',
    image VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";

if ($conn->query($sql) === TRUE) {
    echo "Table 'products' created successfully.<br>";
} else {
    echo "Error creating table: " . $conn->error . "<br>";
}

// Seed Data
$products = [
    ['Wheat', 'Whole grain', 7.00, 'kg'],
    ['Chilli Powder', 'Premium Spices', 15.00, 'kg'],
    ['Corn', 'Fresh Harvest', 10.00, 'kg'],
    ['Ragi', 'Finger Millet', 5.00, 'kg'],
    ['Snacks Flour', 'Refined', 7.00, 'kg'],
    ['Idly Flour', 'Mixed Grain', 15.00, 'kg']
];

foreach ($products as $p) {
    $name = $p[0];
    $check = $conn->query("SELECT id FROM products WHERE name = '$name'");
    if ($check->num_rows == 0) {
        $stmt = $conn->prepare("INSERT INTO products (name, description, price, unit) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("ssds", $p[0], $p[1], $p[2], $p[3]);
        if ($stmt->execute()) {
            echo "Inserted: " . $p[0] . "<br>";
        } else {
            echo "Error inserting " . $p[0] . ": " . $stmt->error . "<br>";
        }
    } else {
        echo "Exists: " . $p[0] . "<br>";
    }
}
?>
