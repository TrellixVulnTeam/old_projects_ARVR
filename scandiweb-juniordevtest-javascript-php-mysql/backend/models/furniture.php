<?php
class Furniture extends Product implements TypeProduct {

    protected $type = "furniture";
    
    function setAttribute($data) {
        $this->attribute = $data->height ."x". $data->width ."x". $data->length;
    }
}
?>