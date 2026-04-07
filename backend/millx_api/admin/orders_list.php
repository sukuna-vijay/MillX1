<?php
include "../config/db.php";
header("Content-Type: application/json");

/**
 * Allow GET or POST
 */
if ($_SERVER['REQUEST_METHOD'] !== 'GET' && $_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status"=>"error","message"=>"Invalid request method"]);
    exit;
}

/**
 * Read token
 */
// Read token from header and strip "Bearer " prefix
$authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
if (empty($authHeader) && function_exists('getallheaders')) {
    $h = getallheaders();
    $authHeader = $h['Authorization'] ?? $h['authorization'] ?? '';
}
$token = (strpos($authHeader, 'Bearer ') === 0) ? trim(substr($authHeader, 7)) : trim($authHeader);
$token = mysqli_real_escape_string($conn, $token);

if ($token === '') {
    echo json_encode(["status"=>"error","message"=>"Authorization token required"]);
    exit;
}

/**
 * Check admin
 */
$q = mysqli_query($conn,
    "SELECT u.role 
     FROM user_tokens t 
     JOIN users u ON t.user_id=u.id 
     WHERE t.token='$token'"
);

if (!$q || mysqli_num_rows($q) == 0) {
    echo json_encode(["status"=>"error","message"=>"Invalid token"]);
    exit;
}

$role = mysqli_fetch_assoc($q)['role'];
if ($role !== 'admin') {
    echo json_encode(["status"=>"error","message"=>"Admin only"]);
    exit;
}

/**
 * Fetch orders
 */
$q = mysqli_query($conn,
    "SELECT 
        o.id AS order_id,
        u.name AS user_name,
        u.phone AS user_phone,
        p.product_name,
        p.image,
        p.price,
        p.unit,
        o.quantity,
        o.order_status,
        o.created_at
     FROM orders o
     JOIN users u ON o.user_id = u.id
     LEFT JOIN prices p ON o.product_id = p.product_id
     ORDER BY o.created_at DESC"
);

$orders = [];
while ($row = mysqli_fetch_assoc($q)) {
    $orders[] = $row;
}

/**
 * Response
 */
echo json_encode([
    "status"=>"success",
    "orders"=>$orders
]);
exit;
?>
