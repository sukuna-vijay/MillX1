<?php
include "../config/db.php";

echo "<h1>MillX Database Setup & Fix</h1>";

// 1. Create Uploads Directory
$target_dir = "../uploads/products/";
if (!file_exists($target_dir)) {
    if (mkdir($target_dir, 0777, true)) {
        echo "<p style='color:green'>[OK] Uploads directory created: $target_dir</p>";
    } else {
        echo "<p style='color:red'>[ERROR] Failed to create uploads directory. Check permissions.</p>";
    }
} else {
    echo "<p style='color:green'>[OK] Uploads directory exists.</p>";
}

// 2. Check/Create 'prices' table
// We use 'prices' table for the Admin Price List / Manage Customer Orders
$sql = "CREATE TABLE IF NOT EXISTS prices (
    product_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) DEFAULT 'kg',
    image VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";

if ($conn->query($sql) === TRUE) {
    echo "<p style='color:green'>[OK] Table 'prices' checked/created.</p>";
} else {
    echo "<p style='color:red'>[ERROR] changing table: " . $conn->error . "</p>";
}

// 3. Ensure 'image' column exists (Fix for existing tables)
$checkColumn = $conn->query("SHOW COLUMNS FROM prices LIKE 'image'");
if ($checkColumn->num_rows == 0) {
    $sql_alter = "ALTER TABLE prices ADD COLUMN image VARCHAR(255) DEFAULT NULL";
    if ($conn->query($sql_alter) === TRUE) {
        echo "<p style='color:green'>[FIX] Column 'image' added to 'prices' table.</p>";
    } else {
        echo "<p style='color:red'>[ERROR] Failed to add 'image' column: " . $conn->error . "</p>";
    }
} else {
    echo "<p style='color:green'>[OK] Column 'image' exists in 'prices'.</p>";
}

// 4. Seed Data (Only if empty)
$checkEmpty = $conn->query("SELECT count(*) as count FROM prices");
$row = $checkEmpty->fetch_assoc();
if ($row['count'] == 0) {
    echo "<p>Table is empty. Seeding defaults...</p>";
    $seeds = [
        ['Karuka Bran', 'Premium Quality Feed', 25.00, 'bag'],
        ['Satha Bran', 'Standard Mix', 20.00, 'bag'],
        ['Thevanam', 'Cattle Feed Pro+', 35.00, 'bag'],
        ['Peanut Oil Cake', 'Organic Residue', 45.00, 'bag'],
        ['Wheat', 'Whole grain', 7.00, 'kg']
    ];

    foreach ($seeds as $p) {
        $stmt = $conn->prepare("INSERT INTO prices (product_name, description, price, unit) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("ssds", $p[0], $p[1], $p[2], $p[3]);
        if ($stmt->execute()) {
            echo "Inserted: " . $p[0] . "<br>";
        } else {
            echo "Error inserting " . $p[0] . ": " . $stmt->error . "<br>";
        }
    }
    echo "<p style='color:green'>[OK] Seeding complete.</p>";
} else {
    echo "<p style='color:gray'>[INFO] Table already has data. Skipping seed.</p>";
}

echo "<h2>Setup Complete. You can now use the App.</h2>";
?>
