<?php
include_once "./config/database.php";
include_once "./models/baseProduct.php";
include_once "./models/product.php";
include_once "./models/typeProduct.php";
include_once "./models/dvd.php";
include_once "./models/book.php";
include_once "./models/furniture.php";

class API {

    private $db;

    function __construct($db)
    {
        $this->db = $db;

        $this->process();
    }

    private function process() {
        $response = null;

        switch ($_SERVER['REQUEST_METHOD']) {
            case "GET":
                $response = $this->fetch();
                break;
            case "POST":
                $response = $this->insert();
                break;
            case "DELETE":
                $response = $this->delete();
                break;
            case "OPTIONS":
                $this->cors();
                break;
            default:
                $response = array("message"=>"Method not allowed");
        }

        echo json_encode($response);

        exit(0);
    }

    private function fetch() {
        header("Access-Control-Allow-Origin: *");
        header("Content-Type: application/json");

        $product = new Product($this->db);

        $result = $product->fetch();
        $num = $result->num_rows;

        if ($num > 0) {
            $products = array("data"=>array());

            while ($obj = $result->fetch_object()) {
                array_push($products["data"], $obj);
            }

            return $products;
        } else {
            return array("message"=>"No products");
        }
    }

    private function insert() {
        header("Access-Control-Allow-Origin: *");
        header("Content-Type: application/json");
        header("Access-Control-Allow-Methods: POST");

        $data = json_decode(file_get_contents("php://input"));
        
        $product = new $data->type($this->db);

        $product->setSku($data->sku);
        $product->setName($data->name);
        $product->setPrice($data->price);
        $product->setAttribute($data);

        if ($product->insert()) {
            return array("message"=>"Product added");
        } else {
            return array("message"=>"Product could not be added");
        }
    }

    private function delete() {
        header("Access-Control-Allow-Origin: *");
        header("Content-Type: application/json");
        header("Access-Control-Allow-Methods: DELETE");

        $product = new Product($this->db);

        $data = json_decode(file_get_contents("php://input"));
        $count = count($data->ids);

        for ($i = 0; $i < $count; $i++) {
            echo "processing delete " . $data->ids[$i] . "\n";
            $product->setId($data->ids[$i]);
            $product->delete();
        }

        return array("message"=>"Products deleted");
    }

    function cors() {
    
        if (isset($_SERVER['HTTP_ORIGIN'])) {
            header("Access-Control-Allow-Origin: {$_SERVER['HTTP_ORIGIN']}");
            header('Access-Control-Allow-Credentials: true');
            header('Access-Control-Max-Age: 86400');
        }
        
        if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
            
            if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
                header("Access-Control-Allow-Methods: GET, POST, OPTIONS, DELETE");
            
            if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
                header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");
        
            exit(0);
        }
    }
}

$database = new Database();
$db = $database->connect();

$api = new API($db);
?>