<?php
include "../config/db.php";
header("Content-Type: application/json");

/**
 * ✅ Allow only POST or GET (you can restrict to POST if needed)56789
 */
if ($_SERVER['REQUEST_METHOD'] !== 'GET' && $_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
    exit;
}

/**
 * ✅ Read Authorization Token from Header
 */
$headers = getallheaders();
$token   = trim($headers['Authorization'] ?? '');

/**
 * ✅ Token validation
 */
if ($token === '') {
    echo json_encode([
        "status" => "error",
        "message" => "Authorization token required"
    ]);
    exit;
}

/**
 * ✅ Verify token & get user role
 */
$tokenQuery = mysqli_query(
    $conn,
    "SELECT u.id, u.role 
     FROM user_tokens t 
     JOIN users u ON t.user_id = u.id 
     WHERE t.token = '$token'"
);

if (!$tokenQuery || mysqli_num_rows($tokenQuery) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid or expired token"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($tokenQuery);

/**
 * ✅ Admin role check
 */
if ($user['role'] !== 'admin') {
    echo json_encode([
        "status" => "error",
        "message" => "Access denied. Admin only"
    ]);
    exit;
}

/**
 * ✅ Fetch dashboard data
 */
$activeMachines   = 0;
$inactiveMachines = 0;

/* Active machines count */
$q1 = mysqli_query(
    $conn,
    "SELECT COUNT(*) AS total FROM machines WHERE machine_status = 'active'"
);
if ($q1) {
    $activeMachines = mysqli_fetch_assoc($q1)['total'];
}

/* Inactive machines count */
$q2 = mysqli_query(
    $conn,
    "SELECT COUNT(*) AS total FROM machines WHERE machine_status = 'inactive'"
);
if ($q2) {
    $inactiveMachines = mysqli_fetch_assoc($q2)['total'];
}

/**
 * ✅ Final success response
 */
echo json_encode([
    "status" => "success",
    "data" => [
        "active_machines"   => (int)$activeMachines,
        "inactive_machines" => (int)$inactiveMachines
    ]
]);
exit;
?>
