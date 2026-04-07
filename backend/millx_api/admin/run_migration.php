<?php
include '../config/db.php';

$filename = 'recreate_machines.sql';
$op_data = '';
$lines = file($filename);

// Disable FK Checks explicitly
mysqli_query($conn, "SET FOREIGN_KEY_CHECKS=0");

foreach ($lines as $line) {
    if (substr($line, 0, 2) == '--' || $line == '') {
        continue;
    }
    $op_data .= $line;
}

$queries = explode(';', $op_data);

foreach ($queries as $query) {
    $query = trim($query);
    if ($query != '') {
        if (mysqli_query($conn, $query)) {
            echo "Success: " . substr($query, 0, 50) . "...<br>";
        } else {
            echo "Error: " . mysqli_error($conn) . "<br>";
        }
    }
}
echo "Migration Completed.";
mysqli_query($conn, "SET FOREIGN_KEY_CHECKS=1");
?>
