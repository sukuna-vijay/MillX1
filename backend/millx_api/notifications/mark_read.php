<?php
include "../middleware/auth_check.php";
mysqli_query($conn,"UPDATE notifications SET is_read=1 WHERE user_id=$user_id");
echo json_encode(["status"=>"success"]);
?>
