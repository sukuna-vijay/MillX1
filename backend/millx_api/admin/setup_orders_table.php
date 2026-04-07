<?php
include '../config/db.php';

if ($conn) {
    // Add product_id column
    $sql = "ALTER TABLE orders ADD COLUMN product_id INT(11) NOT NULL DEFAULT 0 AFTER user_id";
    if ($conn->query($sql) === TRUE) {
        echo "<h1>Success! 'product_id' column added to 'orders' table.</h1>";
    } else {
        if (strpos($conn->error, "Duplicate column") !== false) {
             echo "<h1>Column 'product_id' already exists.</h1>";
        } else {
             echo "<h1>Error: " . $conn->error . "</h1>";
        }
    }

    // Optional: make machine_id nullable or remove it if strictly replacing
    // For safety, we keep machine_id but make it nullable if not already
    $sql2 = "ALTER TABLE orders MODIFY COLUMN machine_id INT(11) NULL";
    $conn->query($sql2);

} else {
    echo "<h1>Database Connection Failed</h1>";
}
?>
