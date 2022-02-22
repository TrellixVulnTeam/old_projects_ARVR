package Simon;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SimonMäng {
    private int tulemus;
    private int tase;
    private final ArrayList<Integer> appi = new ArrayList<>();
    private final ArrayList<String> jarjekord = new ArrayList<>();
    private final SimonFX fx;

    public SimonMäng(SimonFX fx) {
        this.tase = 0;
        this.tulemus = 0;
        this.fx = fx;
    }

    // genereeritakse numrid, näidatakse kasutajale kombinatsioon läbi nuppude ja
    // siis jäädakse ootama kasutaja sisestus kombinatsiooni
    public void mänguOsa(Boolean a) {
        if (a) {
            this.tase = 0;
        }
        appi.clear();
        jarjekord.clear();
        genereeriJarjekord( ++this.tase);
        fx.näitajarjekord( formaadi(appi.toString()));
    } // mänguOsa

    // genereerib numbreid nullist kolmeni
    //      hulk oleneb tasemest
    public void genereeriJarjekord(int tase) {
        Random random = new Random();
        for (int i = 0; i < tase + 2; i++) {
            appi.add(random.nextInt(4));
        }
    } // genereeriJarjekord

    // kui kasutaja saab ja vajutab värvilisi nuppe aknas, siis kontrollitakse kas sisestatud vastav
    // number (igale nuppule vastab number) on järjendis siiamaani korrektne
    //   Kui järjekorda satub vale number, siis visatakse erind, ja sellega teate aken.
    public void kasutajaKordab() throws InterruptedException {
        String lst = formaadi(appi.toString());
        String lst2 = formaadi(jarjekord.toString());
        String subLst = lst.substring(0, lst2.length());

        if ( subLst.equals( lst2) && lst.length() > lst2.length())
            return;
        if ( !subLst.equals( lst2) || lst2.length() > lst.length())
            throw new ebakorrektneJarjendErind("Vale number järjendis.");
        if ( lst.equals(lst2)) {
            setTulemus();
            fx.mangija.uuendaAndmed(this);
            fx.edetabelKlass.uuendaTabel();
            TimeUnit.SECONDS.sleep(1);
            fx.alusta(false);
        }
    } // kasutajaKordab

    // nuppu vajutusel lisatakse jarjekorda nuppule vastav number, mida siis ka kontrollitakse
    public void jarjekord(int i) {
        jarjekord.add(String.format("%s", i));
    } // jarjekord

    // eemaldab arvuridatelt kirjavahemärgid ja muud mitte-numbrid
    public String formaadi(String sisend) {
        return sisend
                .strip()
                .replaceAll("[\\D\\s]", "");
    } // formaadi

    public int getTulemus() {
        return tulemus;
    }

    public void setTulemus() {
        this.tulemus = tase;
    }
}