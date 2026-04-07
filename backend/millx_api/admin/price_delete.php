<?php
include "../config/db.php";

$data = json_decode(file_get_contents("php://input"), true);
$id = $data['id'] ?? '';

if(empty($id)){
    echo json_encode(["status"=>"error", "message"=>"ID required"]);
    exit;
}

$stmt = $conn->prepare("DELETE FROM prices WHERE product_id = ?");
$stmt->bind_param("i", $id);

if($stmt->execute()){
    echo json_encode(["status"=>"success", "message"=>"Product deleted"]);
} else {
    echo json_encode(["status"=>"error", "message"=>$stmt->error]);
}
?>
