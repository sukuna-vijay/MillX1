<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "../config/db.php";

header("Content-Type: application/json");

// Check if request is POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
    exit;
}

// Get inputs (Multipart/Form-Data)
$id             = isset($_POST['id']) ? intval($_POST['id']) : 0;
$machine_name   = isset($_POST['name']) ? mysqli_real_escape_string($conn, $_POST['name']) : '';
$machine_status = isset($_POST['status']) ? mysqli_real_escape_string($conn, $_POST['status']) : '';
$min            = isset($_POST['min']) ? mysqli_real_escape_string($conn, $_POST['min']) : '';
$max            = isset($_POST['max']) ? mysqli_real_escape_string($conn, $_POST['max']) : '';
$unit           = isset($_POST['unit']) ? mysqli_real_escape_string($conn, $_POST['unit']) : '';
$description    = isset($_POST['description']) ? mysqli_real_escape_string($conn, $_POST['description']) : '';

// Validation
if ($id == 0 || $machine_name == '') {
    echo json_encode([
        "status" => "error",
        "message" => "Machine ID and Name required"
    ]);
    exit;
}

// Image Upload Logic
$image_update_sql = "";
if (isset($_FILES['image']) && $_FILES['image']['error'] == 0) {
    $target_dir = "../uploads/machines/";
    
    // Create directory if not exists
    if (!file_exists($target_dir)) {
        if (!mkdir($target_dir, 0777, true)) {
            echo json_encode(["status" => "error", "message" => "Failed to create uploads directory"]);
            exit;
        }
    }

    $file_extension = pathinfo($_FILES["image"]["name"], PATHINFO_EXTENSION);
    $new_filename = "machine_" . $id . "_" . time() . "." . $file_extension;
    $target_file = $target_dir . $new_filename;

    if (move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)) {
        // Prepare SQL to update image column
        $image_url = "uploads/machines/" . $new_filename; // Relative path for DB
        $image_update_sql = ", image = '$image_url'";
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to upload file"]);
        exit;
    }
}

/**
 * Update machine
 */
$sql = "UPDATE machines SET
        machine_name = '$machine_name',
        machine_status = '$machine_status',
        min_capacity = '$min',
        max_capacity = '$max',
        unit = '$unit',
        description = '$description'
        $image_update_sql
    WHERE id = '$id'";

if (mysqli_query($conn, $sql)) {
    echo json_encode([
        "status" => "success",
        "message" => "Machine updated successfully",
        "image_update" => ($image_update_sql != "")
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Update failed: " . mysqli_error($conn)
    ]);
}
?>
