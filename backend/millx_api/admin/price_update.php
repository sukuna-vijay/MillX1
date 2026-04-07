<?php
include "../config/db.php";

$id = $_POST['id'] ?? '';
$name = $_POST['name'] ?? '';
$price = $_POST['price'] ?? '';
$unit = $_POST['unit'] ?? 'kg';
$description = $_POST['description'] ?? '';

if(empty($id) || empty($name)){
    echo json_encode(["status"=>"error", "message"=>"ID and Name are required"]);
    exit;
}

$imagePath = null;
if(isset($_FILES['image']) && $_FILES['image']['error'] == 0){
    $target_dir = "../uploads/products/";
    if (!file_exists($target_dir)) {
        if (!mkdir($target_dir, 0777, true)) {
             echo json_encode(["status"=>"error", "message"=>"Failed to create upload directory"]);
             exit;
        }
    }
    
    $fileName = time() . "_" . basename($_FILES["image"]["name"]);
    $target_file = $target_dir . $fileName;
    
    if(move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)){
        $imagePath = "uploads/products/" . $fileName;
    } else {
        $error = error_get_last();
        echo json_encode(["status"=>"error", "message"=>"Failed to upload image. Server Error: " . ($error['message'] ?? 'Unknown')]);
        exit;
    }
}
elseif (isset($_FILES['image']) && $_FILES['image']['error'] != UPLOAD_ERR_NO_FILE) {
    // File was sent but had an error (e.g. size)
    echo json_encode(["status"=>"error", "message"=>"Upload Error Code: " . $_FILES['image']['error']]);
    exit;
}

// Update prices table using product_id and product_name
if ($imagePath) {
    $sql = "UPDATE prices SET product_name=?, description=?, price=?, unit=?, image=? WHERE product_id=?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssdssi", $name, $description, $price, $unit, $imagePath, $id);
} else {
    $sql = "UPDATE prices SET product_name=?, description=?, price=?, unit=? WHERE product_id=?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssdsi", $name, $description, $price, $unit, $id);
}

if($stmt->execute()){
     echo json_encode(["status"=>"success", "message"=>"Product updated"]);
} else {
     echo json_encode(["status"=>"error", "message"=>$stmt->error]);
}
?>
