<?php
include "../config/db.php";

header("Content-Type: application/json");

// Select from stock table with image
$sql = "SELECT product_id, product_name, product_quantity, unit, image FROM stock";
$result = $conn->query($sql);

$stocks = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $stocks[] = $row;
    }
}

echo json_encode($stocks);
?>
