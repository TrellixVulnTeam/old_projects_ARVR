import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Edetabel {
    private ArrayList<Mangija> mangijad;
    private File fail = new File("edetabel.txt"); // edetabeli faili asukoht

    // Käivitatakse peale mangija nime sisestamist
    //      loetakse ja luuakse list edetabelis olevatest mangijatest
    public Edetabel() {
        this.mangijad = new ArrayList<>();
        laeFailist();
    }

    private void laeFailist() {
        String[] rida;

        // kui faili ei eksisteeri siis see luuakse
        try {
            fail.createNewFile();
        } catch (Exception ignored) {}
        try ( Scanner sc = new Scanner(fail)) {

            // loetakse rida realt varasemate mängijate tulemused
            //      ja luuakse kohe nendest Mangija isend ja listakse listi
            while (sc.hasNextLine()) {
                rida = sc.nextLine().split("; ");
                mangijad.add(new Mangija(rida[0],
                        Integer.parseInt(rida[1]),
                        Float.parseFloat(rida[2]),
                        Integer.parseInt(rida[3])));
            }
        } // Scanner
        catch (Exception ignored) {
        }
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
    public Mangija loeAndmed(String nimi) throws Exception {
        for (Mangija m : mangijad) {
            if (m.getNimi().equalsIgnoreCase(nimi)) {
                m.tervitus();
                return m;
            }
        }
        mangijad.add(new Mangija(nimi));
        mangijad.get(mangijad.size() - 1).tervitus();
        return mangijad.get(mangijad.size() - 1);
    } // loeAndmed

    // sorteeritakse mangijad list isendi tulemuste järgi
    private void sorteeriTulemusteJalgi() {
        mangijad.sort(Comparator.comparing(Mangija::getParimTulem).reversed());
    } // sorteeriTulemusteJargi

    // Edetabeli väljastus meetod
    public void valjastaTabel() {
        System.out.print("" +
                "   Edetabel:\n" +
                "" + "=".repeat(28));
        System.out.println();

        for (int i = 0; i < mangijad.size(); i++) {
            System.out.printf(" %d. %-10s || %.2f || %s"
                    , (i + 1)
                    , (mangijad.get(i).getNimi().length() < 10) ?
                            mangijad.get(i).getNimi()
                            : mangijad.get(i).getNimi()
                    , mangijad.get(i).getKeskmineTulem()
                    , mangijad.get(i).getParimTulem());

            System.out.println();
        }
        System.out.println("" + "=".repeat(28) + ".");
        System.out.println();
    } // valjastTabel
} // Edetabel
