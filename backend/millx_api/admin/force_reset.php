<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include '../config/db.php';

function run_query($conn, $sql, $msg) {
    if (mysqli_query($conn, $sql)) {
        echo "Success: $msg<br>";
    } else {
        echo "Error ($msg): " . mysqli_error($conn) . "<br>";
    }
}

// 1. Disable FK
run_query($conn, "SET FOREIGN_KEY_CHECKS=0", "Disable FK");

// 2. Drop Table
run_query($conn, "DROP TABLE IF EXISTS `machines`", "Drop machines");

// 3. Create Table
$create_sql = "CREATE TABLE `machines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `machine_name` varchar(255) NOT NULL,
  `machine_status` enum('Running','Stopped','Maintenance') DEFAULT 'Stopped',
  `min_capacity` int(11) DEFAULT 0,
  `max_capacity` int(11) DEFAULT 0,
  `unit` varchar(50) DEFAULT 'kg',
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
run_query($conn, $create_sql, "Create machines");

// 4. Insert Data
$insert_sql = "INSERT INTO `machines` (`machine_name`, `machine_status`, `min_capacity`, `max_capacity`, `unit`, `description`) VALUES
('Idly Batter Machine', 'Running', 10, 50, 'kg', 'High speed batter grinding machine for commercial use.'),
('Rice Mill Machine', 'Stopped', 100, 1000, 'kg', 'Large capacity rice milling unit.'),
('Spice Grinder', 'Running', 5, 25, 'kg', 'Compact spice grinder for turmeric and chili.')";
run_query($conn, $insert_sql, "Insert Data");

// 5. Enable FK
run_query($conn, "SET FOREIGN_KEY_CHECKS=1", "Enable FK");

echo "Force Reset Completed.";
?>
