<?php
include "config/db.php";
$alter = mysqli_query($conn, "ALTER TABLE orders ADD COLUMN product_id INT AFTER user_id");
if($alter) echo "done"; else echo mysqli_error($conn);
?>
