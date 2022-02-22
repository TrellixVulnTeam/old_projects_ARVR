<?php
class Product extends BaseProduct {
    
    function __construct($db) {
        $this->conn = $db;
    }

    public function fetch() {
        $query = "SELECT * FROM $this->tablename";
        return $this->conn->query($query);
    }

    public function delete() {
        echo "deleting " . $this->id . "\n";
        if (empty($this->id)) {
            return false;
        }

        $query = "DELETE FROM $this->tablename WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $this->id);

        if ($stmt->execute()) {
            return true;
        }

        echo "Error: Could not delete " . $stmt->error;

        return false;
    }

    function insert() {
        $query = "INSERT INTO products (sku, name, price, attribute, type) VALUES (?, ?, ?, ?, ?)";
        $stmt = $this->conn->prepare($query);

        if (empty($this->sku) || empty($this->name) || empty($this->price) || empty($this->attribute) || empty($this->type)) {
            echo "Error: All fealds must have a value";
            return false;
        }

        $this->sku = htmlspecialchars(strip_tags($this->sku));
        $this->name = htmlspecialchars(strip_tags($this->name));
        $this->price = htmlspecialchars(strip_tags($this->price));
        $this->attribute = htmlspecialchars(strip_tags($this->attribute));

        $stmt->bind_param("ssdss", $this->sku, $this->name, $this->price, $this->attribute, $this->type);

        if ($stmt->execute()) {
            return true;
        }

        echo "Error: " . $stmt->error;

        return false;
    }
}
?>