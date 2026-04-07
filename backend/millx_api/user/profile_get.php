<?php
// include "../auth/check_user.php"; // File does not exist
include "../config/db.php";
header("Content-Type: application/json");

$user_id = $_GET['user_id'] ?? 0;


/* Fetch user profile */
$q = mysqli_query($conn, "
    SELECT 
        name,
        email,
        phone,
        address,
        profile_image
    FROM users
    WHERE id = '$user_id'
");

if (!$q || mysqli_num_rows($q) == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($q);

/* Attach relative image path */
if (!empty($user['profile_image'])) {
    $user['profile_image'] = "uploads/profiles/" . $user['profile_image'];
} else {
    $user['profile_image'] = "";
}

echo json_encode([
    "status" => "success",
    "data" => $user
]);
?>
