<?php
include "../auth/check_admin.php";

$q = mysqli_query($conn,"
    SELECT name, email, phone, address, profile_image
    FROM users
    WHERE id = $admin_id
");

if(mysqli_num_rows($q)==0){
    echo json_encode(["status"=>"error","message"=>"Admin not found"]);
    exit;
}

$profile = mysqli_fetch_assoc($q);

/* Attach full image URL */
if (!empty($profile['profile_image'])) {
    $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off' || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";
    $domainName = $_SERVER['HTTP_HOST'] . '/millx_api/';
    $base_url = $protocol . $domainName;
    $profile['profile_image'] = $base_url . "uploads/profiles/" . $profile['profile_image'];
} else {
    $profile['profile_image'] = "";
}

echo json_encode([
    "status"=>"success",
    "data"=>$profile
]);
?>
