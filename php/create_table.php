<?php
$name = $_POST ['name'];
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();

$sql = "CREATE TABLE ".$name;
$sql = $sql." (id int(255) AUTO_INCREMENT PRIMARY KEY,";
$sql = $sql."studentNum varchar(1000) not null,";
$sql = $sql."check varchar(1000) not null)";
echo $sql;

mysql_query($db, $sql);
?>