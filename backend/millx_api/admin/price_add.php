<?php
include "../config/db.php";

$name = $_POST['name'] ?? '';
$price = $_POST['price'] ?? '';
$unit = $_POST['unit'] ?? 'kg';
$description = $_POST['description'] ?? '';

if(empty($name) || empty($price)){
    echo json_encode(["status"=>"error", "message"=>"Name and Price are required"]);
    exit;
}

$imagePath = null;
if(isset($_FILES['image']) && $_FILES['image']['error'] == 0){
    $target_dir = "../uploads/products/";
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
    }
    $fileName = time() . "_" . basename($_FILES["image"]["name"]);
    $target_file = $target_dir . $fileName;
    if(move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)){
        $imagePath = "uploads/products/" . $fileName;
    }
}

// Insert into prices using correct column names
$sql = "INSERT INTO prices (product_name, description, price, unit, image) VALUES (?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssdss", $name, $description, $price, $unit, $imagePath);

if($stmt->execute()){
     echo json_encode(["status"=>"success", "message"=>"Product added"]);
} else {
     echo json_encode(["status"=>"error", "message"=>$stmt->error]);
}
?>
