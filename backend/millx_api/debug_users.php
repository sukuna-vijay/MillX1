<?php
include "config/db.php";
$res = mysqli_query($conn, "SELECT email, reset_otp, reset_otp_expires_at, status FROM users ORDER BY created_at DESC LIMIT 5");
while($row = mysqli_fetch_assoc($res)) {
    print_r($row);
}
?>
