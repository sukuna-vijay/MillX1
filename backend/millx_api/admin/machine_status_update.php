<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "../config/db.php";
include __DIR__ . "/../auth/check_admin.php";

header("Content-Type: application/json");

/**
 * Read RAW JSON
 */
$data = json_decode(file_get_contents("php://input"), true);

/**
 * Get inputs
 */
$id             = trim($data['id'] ?? '');
$machine_status = trim($data['status'] ?? '');

/**
 * Validation
 */
if ($id == '' || $machine_status == '') {
    echo json_encode([
        "status" => "error",
        "message" => "Machine ID and Status required"
    ]);
    exit;
}

/**
 * Allowed statuses
 */
// Status Validation (Optional: You can restrict if needed, but for now allow what app sends)
// $allowed_status = ['Running', 'Stopped', 'Maintenance'];
// if (!in_array($machine_status, $allowed_status)) ...

/**
 * Check machine exists
 */
$check = mysqli_query($conn, "SELECT id FROM machines WHERE id='$id'");
if (mysqli_num_rows($check) == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Machine not found"
    ]);
    exit;
}

/**
 * Update machine status
 */
$update = mysqli_query(
    $conn,
    "UPDATE machines SET machine_status='$machine_status' WHERE id='$id'"
);

if ($update) {
    echo json_encode([
        "status" => "success",
        "message" => "Machine status updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => mysqli_error($conn)
    ]);
}
