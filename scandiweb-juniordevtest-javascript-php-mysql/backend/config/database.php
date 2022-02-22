<?php
class Database {

    public $conn;

    function connect() {
        $cleardb_url = parse_url(getenv("CLEARDB_DATABASE_URL"));
        $server = $cleardb_url["host"];
        $database = substr($cleardb_url["path"], 1);
        $user = $cleardb_url["user"];
        $password = $cleardb_url["pass"];

        $this->conn = null;
        mysqli_report(MYSQLI_REPORT_OFF);

        // create connection to mysql server
        $this->conn = new mysqli($server, $user, $password);

        if ($this->conn === false) {
            die("Error: Could not connect. " . $this->conn->connect_error . "\n");
        }

        // create database if not exists
        $this->conn->query("CREATE DATABASE IF NOT EXISTS $database");

        $this->conn->select_db($database);

        // create table if not exists
        $this->conn->query("CREATE TABLE IF NOT EXISTS products (
                     id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                     sku VARCHAR(30) NOT NULL UNIQUE,
                     name VARCHAR(50) NOT NULL,
                     price DECIMAL(10,2) NOT NULL,
                     attribute VARCHAR(30) NOT NULL,
                     type ENUM('dvd','book','furniture') NOT NULL
                 )");

        return $this->conn;
    }
}
?>