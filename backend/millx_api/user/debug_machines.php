<?php
header("Content-Type: application/json");
include '../config/db.php';

$sql = "SELECT * FROM machines";
$result = $conn->query($sql);

$data = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}
echo json_encode($data, JSON_PRETTY_PRINT);
?>
