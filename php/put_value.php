<?php
$tableName = $_POST ['tableName'];
$studentNum = $_POST ['studentNum'];
$check = $_POST ['check'];

require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();

$sql = "INSERT INTO ".tableName;
$sql = $sql." (studentNum, check) VALUES (";
$sql = $sql."'".$studentNum."', ";
$sql = $sql."'".$check."')";

echo $sql;

$con->connect();

mysqli_query($con, $sql);
$db.close();
?>