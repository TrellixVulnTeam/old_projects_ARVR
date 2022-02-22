package Simon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class SimonFX extends Application {
    private final BorderPane peaPaan = new BorderPane();
    private final GridPane mangGp = new GridPane();
    private final FlowPane nuppFp = new FlowPane();
    private final FlowPane tabelFp = new FlowPane();
    private final StackPane kasutajaPopUp = new StackPane();
    private final ParallelTransition[] paraTrans = new ParallelTransition[4];
    private Stage primaryStage = null;
    private boolean interaktiivne = false;
    Edetabel edetabelKlass = new Edetabel(this);
    TableView<Mangija> edetabel = null;
    Mangija mangija = null;
    SimonMäng mang = new SimonMäng(this);

    public SimonFX() throws IOException {}

    // alustab mängu
    public void alusta(Boolean a) {
        mang.mänguOsa(a);
    }

    // meetodi kasutajale nuppude järjekorra näitamiseks
    public void näitajarjekord(String gameNumber) {
        String[] lst = gameNumber.split("");
        SequentialTransition sequ = new SequentialTransition();
        sequ.setRate(0.5);
        sequ.setDelay(Duration.seconds(0.7));

        for (String s : lst) {
            ParallelTransition para = paraTrans[Integer.parseInt(s)];
            sequ.getChildren().add(new ParallelTransition(
                    para.getChildren().get(0),
                    para.getChildren().get(1)
            ));
        }
        sequ.play();
        sequ.setOnFinished(event -> kõlblik());
    } // näitajarjekord

    // kaks meetodi nuppude interaktiivuse muutmiseks kasutaja suhtes
    public void kõlbmatu() {
        interaktiivne = false;
    }
    public void kõlblik() { interaktiivne = true; }

    // aken mis ilmub peale vea tegemist
    public void kordusAken() {
        BorderPane bPane = new BorderPane();
        Pane pane = new FlowPane();

        Label silt = new Label("Tegid vea, Kas proovid uuesti?");
        Button nupp1 = new Button("Uuesti");
        Button nupp2 = new Button("Välju");

        HBox hb = new HBox(nupp1, nupp2);
        VBox vb = new VBox(silt, hb);
        bPane.setCenter(pane);
        pane.getChildren().add(vb);

        silt.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 24;");
        nupp1.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 18;");
        nupp2.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 18;");
        bPane.setStyle("" +
                "-fx-background-color: rgba(2,2,2,0.5);");
        pane.setStyle("" +
                "-fx-background-color: #b6872b;" +
                "-fx-alignment: center;");
        hb.setAlignment(Pos.CENTER);
        vb.setAlignment(Pos.CENTER);
        silt.setTextFill(Color.WHITE);
        hb.setSpacing(20);
        vb.setSpacing(10);
        vb.setPadding(new Insets(30));
        pane.setPrefSize(100,50);
        bPane.setPadding(new Insets(150,200,150,200));

        nupp2.setOnAction( event -> {
            mang.setTulemus();
            mangija.uuendaAndmed(mang);
            edetabelKlass.salvestaFaili();
            primaryStage.close();
        });
        nupp1.setOnAction( event -> {
            kasutajaPopUp.getChildren().remove(bPane);
            try {
                kasutajaPopUp.getChildren().remove(1);
            } catch (Exception ignored) { }
            alusta(true);
        });

        kasutajaPopUp.getChildren().add(bPane);
    }

    // aken mis sisaldab endas edetabelit ja kahte nuppu
    private Pane kõrvalPaan() {
        BorderPane paan = new BorderPane();
        FlowPane tabel = this.tabel();
        FlowPane nupud = this.nupud();

        paan.setTop(tabel);
        paan.setCenter(nupud);

        paan.maxWidthProperty().bind(tabelFp.maxWidthProperty());
        for (Node n : paan.getChildren()) {
            FlowPane pane = (FlowPane) n;
            pane.setAlignment(Pos.CENTER);
            pane.setPadding(new Insets(5,15,5,15));
        }

        return paan;
    } // kõrvalPaan

    // TableView tabeli konstruktsioon
    private FlowPane tabel() {
        double laius;

        Label silt = new Label("Edetabel");
        edetabel = new TableView<>();
            edetabel.setEditable(false);
        TableColumn<Mangija, String> nimi = new TableColumn<>("Mängija Nimi");
        nimi.setCellValueFactory(new PropertyValueFactory<>("nimi"));

        TableColumn<Mangija, Integer> keskmine = new TableColumn<>("Keskmine");
        keskmine.setCellValueFactory(new PropertyValueFactory<>("keskmineTulem"));

        TableColumn<Mangija, Double> parim = new TableColumn<>("Parim");
        parim.setCellValueFactory(new PropertyValueFactory<>("parimTulem"));

        edetabel.getColumns().add(nimi);
        edetabel.getColumns().add(keskmine);
        edetabel.getColumns().add(parim);

        VBox vb = new VBox(silt, edetabel);
        tabelFp.getChildren().add(vb);

        // ei saa enam tabeli veergusid ümber tõsta
        edetabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);

        nimi.setPrefWidth(130);
        parim.setPrefWidth(90);
        keskmine.setPrefWidth(70);
        for (TableColumn t: edetabel.getColumns()) {
            t.setStyle("" +
                    "-fx-font-size: 14;" +
                    "-fx-font-family: 'Artifakt Element'");
            t.setSortable(false);
        }
        laius = nimi.getWidth() + parim.getWidth() + keskmine.getWidth();
        edetabel.setPrefSize(laius, 200);
        tabelFp.maxWidthProperty().bind(edetabel.prefWidthProperty());
        silt.setPrefSize(edetabel.getPrefWidth(), silt.getHeight());
        silt.setPadding(new Insets(10,20,5,20));
        silt.setAlignment(Pos.CENTER);
        silt.setStyle("" +
                "-fx-font-size: 32;" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-text-fill: white;");

        return tabelFp;
    } // tabel

    // alusta/lõpeta nuppude loomine ja paigutus
    private FlowPane nupud() {
        Button start = new Button("alusta");
        Button stop = new Button("lõpeta");

        peaPaan.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) && kasutajaPopUp.getChildren().size() == 1) {
                start.fire();
                event.consume();
            }
        });
        stop.setOnAction(event ->{
            mang.setTulemus();
            mangija.uuendaAndmed(mang);
            edetabelKlass.salvestaFaili();
            primaryStage.close();
        });
        start.setOnAction(event -> {
            start.setDisable(true);
            alusta(true);
        });

        HBox hb = new HBox(start, stop);
        nuppFp.getChildren().add(hb);

        nuppFp.maxWidthProperty().bind(tabelFp.maxWidthProperty());
        nuppFp.setMaxHeight(50);
        hb.maxWidthProperty().bind(tabelFp.maxWidthProperty());
        hb.setAlignment(Pos.TOP_CENTER);
        hb.setSpacing(15);
        for (Node b : hb.getChildren()) {
            Button button = (Button) b;
            button.setPrefSize(100, 20);
            button.setStyle("" +
                    "-fx-font-family: 'Artifakt Element';" +
                    "-fx-font-size: 18");
        }

        return nuppFp;
    } // nupud

    // mängu jaoks vaja minevad nupud ja nende "paanid"
    private Pane mänguPaan() {

        // igal nupul on oma eraldi aken, et saaks koostada animatsioone nuppudele
        StackPane bsp1 = new StackPane();
        StackPane bsp2 = new StackPane();
        StackPane bsp3 = new StackPane();
        StackPane bsp4 = new StackPane();
        int jark = 0;
        String[] algVarvid = {"-fx-background-color: lightgreen", "-fx-background-color: #ff4b55", "-fx-background-color: #464eee", "-fx-background-color: #eeec79"};
        String[] varvid = {"-fx-background-color: #31ff39", "-fx-background-color: red", "-fx-background-color: blue", "-fx-background-color: yellow"};

        mangGp.add(bsp1, 0,0,1,1);
        mangGp.add(bsp2, 1,0,1,1);
        mangGp.add(bsp3, 0,1,1,1);
        mangGp.add(bsp4, 1,1,1,1);
        mangGp.setAlignment(Pos.CENTER);

        // kuna kõik nupud on samad, siis saab kõigile omadused paika panna ühe for-tsükkliga
        for (Node pane : mangGp.getChildren()) {
            int varvJark = mangGp.getChildren().indexOf(pane);
            StackPane stackPane = (StackPane) pane;

            stackPane.setPrefSize(200, 200);

            Circle ring = new Circle(stackPane.getHeight()/2, stackPane.getWidth()/2, 125/2d);
            ring.setFill(Color.CYAN);

            Button nupp = new Button();
            nupp.setStyle("" +
                "-fx-background-radius: 10em;" +
                "-fx-pref-height: 125px;" +
                "-fx-pref-width: 125px;" +
                 algVarvid[jark++] + ";" +
                "-fx-border-color: cyan;" +
                "-fx-border-radius: 10em;");

            ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(500), ring);
            scaleTrans.setFromX(1.0);
            scaleTrans.setFromY(1.0);
            scaleTrans.setToX(1.5);
            scaleTrans.setToY(1.5);
            scaleTrans.setCycleCount(1);
            FadeTransition fadeTrans = new FadeTransition(Duration.millis(400), ring);
            fadeTrans.setFromValue(1.0);
            fadeTrans.setToValue(0.0);
            fadeTrans.setCycleCount(1);

            // salvestame animatsioonid järjendisse, et neid hiljem järjendi näitamises kasutada
            this.paraTrans[varvJark] = new ParallelTransition(scaleTrans, fadeTrans);

            nupp.addEventHandler(MOUSE_CLICKED, event -> {
                if (interaktiivne) {
                    paraTrans[varvJark].play();
                    mang.jarjekord(varvJark);
                    paraTrans[varvJark].setOnFinished( event1 -> {
                        try {
                            mang.kasutajaKordab();
                        } catch (Exception ignored) {
                            kõlbmatu();
                            mang.setTulemus();
                            mangija.uuendaAndmed(mang);
                            kordusAken();
                        }
                    });
                }
            });
            nupp.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                if (interaktiivne)
                    nupp.setStyle("" +
                        "-fx-background-radius: 10em;" +
                        "-fx-pref-height: 125px;" +
                        "-fx-pref-width: 125px;" +
                         varvid[varvJark] + ";" +
                        "-fx-border-color: cyan;" +
                        "-fx-border-radius: 10em;");
            });
            nupp.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                if (interaktiivne)
                    nupp.setStyle("" +
                            "-fx-background-radius: 10em;" +
                            "-fx-pref-height: 125px;" +
                            "-fx-pref-width: 125px;" +
                            algVarvid[varvJark] + ";" +
                            "-fx-border-color: cyan;" +
                            "-fx-border-radius: 10em;");
            });

            stackPane.setAlignment(Pos.CENTER);
            stackPane.getChildren().addAll(ring, nupp);
        }

        mangGp.setPrefSize(500, 500);

        return mangGp;
    } // mänguPaan

    // programmi käivitamisel küsitakse kasutaja nime, et lisada mängija edetabelisse
    private Pane PopUp(){
        BorderPane fPane = new BorderPane();
        Pane pane = new FlowPane();

        Label silt = new Label("Sisesta mängija nimi:");
        TextField tekst = new TextField("nimi...");
        Button nupp = new Button("Edasi");
        VBox vb = new VBox(silt,tekst,nupp);
        pane.getChildren().add(vb);
        fPane.setCenter(pane);

        silt.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 24;");
        tekst.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 18;");
        nupp.setStyle("" +
                "-fx-font-family: 'Artifakt Element';" +
                "-fx-font-size: 18;");
        fPane.setStyle("" +
                "-fx-background-color: rgba(2,2,2,0.8);");
        pane.setStyle("" +
                "-fx-background-color: #b6872b;" +
                "-fx-alignment: center;");

        vb.setSpacing(10);
        silt.setTextFill(Color.WHITE);
        tekst.setAlignment(Pos.CENTER);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(30));
        pane.setPrefSize(100,50);
        fPane.setPadding(new Insets(150,200,150,200));


        tekst.addEventHandler(MOUSE_CLICKED, event -> tekst.clear());
        peaPaan.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (kasutajaPopUp.getChildren().size() > 1 && event.getCode().equals(KeyCode.ENTER)) {
                tekst.requestFocus();
                event.consume();
            }
        });
        tekst.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode().equals(KeyCode.ENTER) && kasutajaPopUp.getChildren().size() > 1) {
                System.out.println("enter");
                nupp.fire();
                ev.consume();
            }
        });
        nupp.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println("enter");
            if (event.getCode().equals(KeyCode.ENTER))
                Event.fireEvent(nupp, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                        0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                        true, true, true, true, true, true, null)); });
        nupp.setOnAction( event -> {
            if (tekst.getText().equals("nimi...") || tekst.getText().isBlank()) {
                silt.setText("muuda nimi");
            }
            else if (tekst.getText().length() > 15) {
                silt.setText("Nimi saab olla väiksem kui 15 tähte.");
            }
            else {
                nupp.setDisable(true);
                try {
                    mangija = edetabelKlass.loeAndmed(tekst.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                edetabelKlass.uuendaTabel();
                kasutajaPopUp.getChildren().remove(fPane);
            }
        });

        return fPane;
    }

    // akna koostamine
    private Pane peaPaan() {
        Pane mänguPaan = this.mänguPaan();
        Pane kõrvalPaan = this.kõrvalPaan();

        peaPaan.setStyle("-fx-background-color: #3f3f3f;");

        kasutajaPopUp.getChildren().addAll(peaPaan, PopUp());
        peaPaan.setRight(kõrvalPaan);
        peaPaan.setCenter(mänguPaan);
        return kasutajaPopUp;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene lava = new Scene(peaPaan());

        primaryStage.setTitle("Simon Game");
        primaryStage.setResizable(false);
        primaryStage.setScene(lava);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
