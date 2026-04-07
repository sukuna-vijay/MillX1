<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "../config/db.php";
// include __DIR__ . "/../auth/check_admin.php";

header("Content-Type: application/json");

/**
 * Read RAW JSON
 */
$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Machine ID is required"
    ]);
    exit;
}

$id = intval($data['id']);

/**
 * Delete machine
 */
$sql = "DELETE FROM machines WHERE id = '$id'";
$delete = mysqli_query($conn, $sql);

if ($delete) {
    if (mysqli_affected_rows($conn) > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "Machine deleted successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Machine not found or already deleted"
        ]);
    }
} else {
    echo json_encode([
        "status" => "error",
        "message" => mysqli_error($conn)
    ]);
}
?>
