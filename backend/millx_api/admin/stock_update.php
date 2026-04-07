<?php
include "../config/db.php";

$id = $_POST['product_id'] ?? '';
$name = $_POST['product_name'] ?? '';
$qty = $_POST['product_quantity'] ?? '';
$unit = $_POST['unit'] ?? 'kg';

if(empty($id) || empty($name)){
    echo json_encode(["status"=>"error", "message"=>"ID and Name required"]);
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

if ($imagePath) {
    $stmt = $conn->prepare("UPDATE stock SET product_name=?, product_quantity=?, unit=?, image=? WHERE product_id=?");
    $stmt->bind_param("sdssi", $name, $qty, $unit, $imagePath, $id);
} else {
    $stmt = $conn->prepare("UPDATE stock SET product_name=?, product_quantity=?, unit=? WHERE product_id=?");
    $stmt->bind_param("sdsi", $name, $qty, $unit, $id);
}

if($stmt->execute()){
     echo json_encode(["status"=>"success", "message"=>"Stock updated"]);
} else {
     echo json_encode(["status"=>"error", "message"=>$stmt->error]);
}
?>
