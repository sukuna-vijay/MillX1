<?php
header("Content-Type: application/json");
include '../config/db.php';

$response = array();

if ($conn) {
    // Exact same query as admin/machines_list.php
    $sql = "SELECT * FROM machines ORDER BY id DESC";
    $result = mysqli_query($conn, $sql);

    if ($result) {
        $machines = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $machines[] = $row;
        }
        echo json_encode($machines);
    } else {
        echo json_encode(array("error" => "Query Failed: " . mysqli_error($conn)));
    }
} else {
    echo json_encode(array("error" => "Database Connection Failed"));
}
?>
