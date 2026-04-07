<?php
// include "../auth/check.php";
include "../config/db.php";

// Use 'prices' table instead of legacy 'products'
$sql = "SELECT product_id as id, product_name as name, price, description, unit, image FROM prices";
$q = mysqli_query($conn, $sql);

$list = [];

if ($q) {
    while($row = mysqli_fetch_assoc($q)){
        $list[] = [
            "id" => $row['id'],
            "name" => $row['name'],
            "price" => $row['price'],
            "unit" => $row['unit'],
            "description" => $row['description'],
            "image" => $row['image'] // Include image path
        ];
    }
}

echo json_encode($list);
?>
