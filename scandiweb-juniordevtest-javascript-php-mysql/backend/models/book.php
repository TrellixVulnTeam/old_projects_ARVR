<?php
class Book extends Product implements TypeProduct {

    protected $type = "book";

    function setAttribute($data) {
        $this->attribute = $data->weight;
    }
}
?>