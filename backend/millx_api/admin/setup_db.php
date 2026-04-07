
<?php
include '../config/db.php';

if ($conn) {
    // Attempt to add image column
    $sql = "ALTER TABLE machines ADD COLUMN image VARCHAR(255) DEFAULT NULL";
    if ($conn->query($sql) === TRUE) {
        echo "<h1>Success! 'image' column added to 'machines' table.</h1>";
    } else {
        // Check if it failed because it exists
        if (strpos($conn->error, "Duplicate column") !== false) {
             echo "<h1>Column 'image' already exists! You are good to go.</h1>";
        } else {
             echo "<h1>Error: " . $conn->error . "</h1>";
        }
    }
    
    // Also Ensure Uploads Directory
    $target_dir = "../uploads/machines/";
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
        echo "<h2>Created uploads directory.</h2>";
    }
} else {
    echo "<h1>Database Connection Failed</h1>";
}
?>
