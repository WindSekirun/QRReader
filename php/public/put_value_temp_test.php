<?php
$DB_HOST = "localhost";
$DB_USER = "windsekirun";
$DB_PASSWORD = "";
$DB_NAME = "windsekirun";

$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME);
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$tableName = "checked20150407";
$studentNum = "20152492";
$checked = "1";

$sql = "INSERT INTO ".$tableName;
$sql = $sql." (studentNum, checked) VALUES (";
$sql = $sql."'".$studentNum."', ";
$sql = $sql."'".$checked."')";
echo $sql;

if (mysqli_query($conn, $sql)) {
    echo "Data Insert successfully";
} else {
    echo "Error inserting data: " . mysqli_error($conn);
}

mysqli_close($conn);
?>