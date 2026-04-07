<?php
include "../middleware/auth_check.php";

$data = json_decode(file_get_contents("php://input"), true);
$machine_id = $data['machine_id'] ?? '';

if($machine_id==''){
    echo json_encode(["status"=>"error","message"=>"Machine ID required"]);
    exit;
}

$q = mysqli_query($conn,"SELECT * FROM machines WHERE id=$machine_id");
if(mysqli_num_rows($q)==0){
    echo json_encode(["status"=>"error","message"=>"Machine not found"]);
    exit;
}

$m = mysqli_fetch_assoc($q);

echo json_encode([
    "status"=>"success",
    "machine"=>[
        "id"=>$m['id'],
        "name"=>$m['name'],
        "image"=>"uploads/machines/".$m['image'],
        "capacity"=>$m['min_capacity']."-".$m['max_capacity']." ".$m['unit'],
        "status"=>$m['status'],
        "description"=>$m['description']
    ]
]);
?>
