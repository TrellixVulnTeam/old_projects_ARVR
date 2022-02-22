package Simon;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Edetabel {
    private final ArrayList<Mangija> mangijad;
    private final File fail = new File("edetabel.txt"); // edetabeli faili asukoht
    private final SimonFX fx;

    // Käivitatakse peale mangija nime sisestamist
    //      loetakse ja luuakse list edetabelis olevatest mangijatest
    public Edetabel(SimonFX fx) throws IOException {
        this.mangijad = new ArrayList<>();
        this.fx = fx;
        laeFailist();
    }

    private void laeFailist() throws IOException {
        String rida;

        // kui faili ei eksisteeri siis see luuakse
        if (!fail.exists())
            fail.createNewFile();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fail)))) {

            // loetakse rida realt varasemate mängijate tulemused
            //      ja luuakse kohe nendest Mangija isend ja listakse listi
            while ( (rida = br.readLine()) != null) {
                String[] jupid = rida.split("; ");
                mangijad.add(new Mangija(jupid[0],
                        Integer.parseInt(jupid[1]),
                        Float.parseFloat(jupid[2]),
                        Integer.parseInt(jupid[3])));
            }
        } // Scanner
        catch (Exception ignored) { }

//        fx.uuendaTabel();
    } // laeFailist

    // salvestab mangija tulemused faili
    public void salvestaFaili() {
        // esmalt sorteeritakse Mangijad tulemuste järgi
        sorteeriTulemusteJalgi();

        // kirjutatakse fail üle ja iga mangija andmed eraldi reale
        //      kasutades Mangija.toString() meetodit
        try (FileWriter fw = new FileWriter(fail)) {
            for (Mangija m : mangijad) {
                fw.write(m.toString());
                fw.write(System.getProperty("line.separator"));
            }
        } catch (Exception ignored) {}
    } // salvestaFaili

    // otsib mangijad listist antud nimega isendit
    //      kui leiab siis tagastatakse see isend
    //      kui ei, siis luuakse uus Mangija isend antud nimega
    // Lisaks kaks tervitus võimalust vt Mangija.tervita()
    public Mangija loeAndmed(String nimi) {
        for (Mangija m : mangijad) {
            if (m.getNimi().equalsIgnoreCase(nimi)) {
//                m.tervitus();
                return m;
            }
        }
        mangijad.add(new Mangija(nimi));
//        mangijad.get(mangijad.size() - 1).tervitus();
        return mangijad.get(mangijad.size() - 1);
    } // loeAndmed

    // sorteeritakse mangijad list isendi tulemuste järgi
    private void sorteeriTulemusteJalgi() {
        mangijad.sort(Comparator.comparing(Mangija::getParimTulem).reversed());
    } // sorteeriTulemusteJargi

    // uuendab TableView tabelit sisu mängu aknas
    public void uuendaTabel() {
        fx.edetabel.getItems().clear();
        for (Mangija m : mangijad) {
            fx.edetabel.getItems().add(m);
        }
    }

} // Edetabel
