<?php
abstract class BaseProduct {
    protected $id;
    protected $sku;
    protected $name;
    protected $price;
    protected $attribute;
    protected $type;

    protected $conn;
    protected $tablename = "products";

    abstract function __construct($db);
    abstract public function fetch();
    abstract public function delete();
    abstract public function insert();

    public function setId($id) {
        $this->id = $id;
    }
    public function setSku($sku) {
        $this->sku = $sku;
    }
    public function setName($name) {
        $this->name = $name;
    }
    public function setPrice($price) {
        $this->price = $price;
    }
}
?>