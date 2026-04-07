<?php
include "config/db.php";
$res = mysqli_query($conn, "SHOW COLUMNS FROM orders");
while ($row = mysqli_fetch_assoc($res)) {
    file_put_contents("orders_schema.txt", $row['Field'] . "\n", FILE_APPEND);
}
$res2 = mysqli_query($conn, "SHOW COLUMNS FROM prices");
while ($row = mysqli_fetch_assoc($res2)) {
    file_put_contents("prices_schema.txt", $row['Field'] . "\n", FILE_APPEND);
}
echo "done";
?>
