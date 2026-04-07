<?php
include "../auth/check.php";

$data = json_decode(file_get_contents("php://input"), true);
$stock_id = $data['stock_id'] ?? '';

if($stock_id==''){
    echo json_encode(["status"=>"error","message"=>"Stock ID required"]);
    exit;
}

$q = mysqli_query($conn,"SELECT * FROM stock WHERE id=$stock_id");
if(mysqli_num_rows($q)==0){
    echo json_encode(["status"=>"error","message"=>"Stock not found"]);
    exit;
}

$s = mysqli_fetch_assoc($q);

echo json_encode([
    "status"=>"success",
    "stock_detail"=>[
        "id"=>$s['id'],
        "name"=>$s['product_name'],
        "short_desc"=>$s['short_desc'],
        "quantity"=>$s['quantity'],
        "unit"=>$s['unit'],
        "image"=>"uploads/stock/".$s['image'],
        "description"=>$s['description'],
        "last_updated"=>$s['updated_at']
    ]
]);
?>
