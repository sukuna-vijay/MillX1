<?php
include "../config/db.php";

$name = $_POST['product_name'] ?? '';
$qty = $_POST['product_quantity'] ?? '';
$unit = $_POST['unit'] ?? 'kg';

if(empty($name) || empty($qty)){
    echo json_encode(["status"=>"error", "message"=>"Name and Quantity required"]);
    exit;
}

$imagePath = null;
if(isset($_FILES['image']) && $_FILES['image']['error'] == 0){
    $target_dir = "../uploads/stocks/";
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
    }
    $fileName = time() . "_" . basename($_FILES["image"]["name"]);
    $target_file = $target_dir . $fileName;
    if(move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)){
        $imagePath = "uploads/stocks/" . $fileName;
    }
}

$stmt = $conn->prepare("INSERT INTO stock (product_name, product_quantity, unit, image) VALUES (?, ?, ?, ?)");
$stmt->bind_param("sdss", $name, $qty, $unit, $imagePath);

if($stmt->execute()){
     echo json_encode(["status"=>"success", "message"=>"Stock added"]);
} else {
     echo json_encode(["status"=>"error", "message"=>$stmt->error]);
}
?>
