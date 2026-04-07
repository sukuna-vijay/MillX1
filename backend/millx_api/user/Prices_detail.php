<?php
include "../auth/check.php";

$data = json_decode(file_get_contents("php://input"), true);
$price_id = $data['price_id'] ?? '';

if($price_id==''){
    echo json_encode(["status"=>"error","message"=>"Price ID required"]);
    exit;
}

$q = mysqli_query($conn,"SELECT * FROM prices WHERE id=$price_id");
if(mysqli_num_rows($q)==0){
    echo json_encode(["status"=>"error","message"=>"Product not found"]);
    exit;
}

$p = mysqli_fetch_assoc($q);

echo json_encode([
    "status"=>"success",
    "price_detail"=>[
        "id"=>$p['id'],
        "name"=>$p['product_name'],
        "category"=>$p['category'],
        "price"=>$p['price'],
        "unit"=>$p['unit'],
        "image"=>"uploads/prices/".$p['image'],
        "description"=>$p['description'],
        "last_updated"=>$p['updated_at']
    ]
]);
?>
