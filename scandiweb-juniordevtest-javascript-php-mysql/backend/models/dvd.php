<?php
class Dvd extends Product implements TypeProduct {

    protected $type = "dvd";

    function setAttribute($data) {
        $this->attribute = $data->size;
    }
}
?>