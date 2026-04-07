<?php
// include "../auth/check_user.php"; // File does not exist
include "../config/db.php";
header("Content-Type: application/json");

/* Read form data */
$user_id = $_POST['user_id'] ?? 0;
$name    = trim($_POST['name'] ?? '');
// Email is not updatable via this endpoint usually, or if it is, assign to $email
// $email = trim($_POST['email'] ?? ''); 

$phone   = trim($_POST['phone'] ?? '');
$address = trim($_POST['address'] ?? '');

/* Validation */
if ($name === '') {
    echo json_encode([
        "status"  => "error",
        "message" => "Name is required"
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

    $new_filename = "user_" . $user_id . "_" . time() . "." . $ext;
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
            phone = '$phone', 
            address = '$address' 
            $image_sql 
        WHERE id = '$user_id'";

if (mysqli_query($conn, $sql)) {
    // Return updated data
    $fetch = mysqli_query($conn, "SELECT name, email, phone, address, profile_image FROM users WHERE id = '$user_id'");
    $updated_user = mysqli_fetch_assoc($fetch);
    
    if (!empty($updated_user['profile_image'])) {
        $updated_user['profile_image'] = "uploads/profiles/" . $updated_user['profile_image'];
    }

    echo json_encode([
        "status" => "success",
        "message" => "Profile updated successfully",
        "data" => $updated_user
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Database update failed: " . mysqli_error($conn)
    ]);
}
?>
