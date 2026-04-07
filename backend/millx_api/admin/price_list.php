<?php
include "../config/db.php";

header("Content-Type: application/json");

// Select from prices table
// Map product_id -> id, product_name -> name for Android compatibility
$sql = "SELECT product_id as id, product_name as name, price, description, unit, image FROM prices";
$result = $conn->query($sql);

$products = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $products[] = $row;
    }
}

echo json_encode($products);
?>
