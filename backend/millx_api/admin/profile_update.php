<?php
include "../auth/check_admin.php";
include "../config/db.php";
header("Content-Type: application/json");

/* Read form data (using $_POST for multipart/form-data support) */
$name    = trim($_POST['name'] ?? '');
$email   = trim($_POST['email'] ?? '');
$phone   = trim($_POST['phone'] ?? '');
$address = trim($_POST['address'] ?? '');

/* Validation */
if ($name === '' || $email === '') {
    echo json_encode([
        "status"  => "error",
        "message" => "Name and Email are required"
    ]);
    exit;
}

/* Image upload handling */
$image_sql = "";

if (!empty($_FILES['image']['name'])) {

    $allowed = ['jpg','jpeg','png','webp'];
    $ext = strtolower(pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION));

    if (!in_array($ext, $allowed)) {
        echo json_encode([
            "status"  => "error",
            "message" => "Invalid image format"
        ]);
        exit;
    }

    if ($_FILES['image']['size'] > 2 * 1024 * 1024) {
        echo json_encode([
            "status"  => "error",
            "message" => "Image size must be under 2MB"
        ]);
        exit;
    }

    $dir = "../uploads/profiles/";
    if (!file_exists($dir)) {
        mkdir($dir, 0777, true);
    }

    $new_filename = "admin_" . $admin_id . "_" . time() . "." . $ext;
    $target_file = $dir . $new_filename;

    if (move_uploaded_file($_FILES['image']['tmp_name'], $target_file)) {
        $image_sql = ", profile_image = '$new_filename'";
    } else {
        echo json_encode([
            "status"  => "error",
            "message" => "Failed to upload image"
        ]);
        exit;
    }
}

/* Update Database */
$sql = "UPDATE users SET 
            name = '$name', 
            email = '$email',
            phone = '$phone', 
            address = '$address' 
            $image_sql 
        WHERE id = '$admin_id'";

if (mysqli_query($conn, $sql)) {
    // Return updated data
    $fetch = mysqli_query($conn, "SELECT name, email, phone, address, profile_image FROM users WHERE id = '$admin_id'");
    $updated_admin = mysqli_fetch_assoc($fetch);
    
    if (!empty($updated_admin['profile_image'])) {
        $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off' || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";
        $domainName = $_SERVER['HTTP_HOST'] . '/millx_api/';
        $base_url = $protocol . $domainName;
        $updated_admin['profile_image'] = $base_url . "uploads/profiles/" . $updated_admin['profile_image'];
    }

    echo json_encode([
        "status" => "success",
        "message" => "Admin Profile updated successfully",
        "data" => $updated_admin
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Database update failed: " . mysqli_error($conn)
    ]);
}
?>
