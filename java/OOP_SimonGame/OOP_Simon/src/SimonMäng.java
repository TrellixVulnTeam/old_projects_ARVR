import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SimonMäng {
    private int tulemus;
    private int tase;
    private static ArrayList<Integer> appi = new ArrayList<>();

    public SimonMäng() {
        this.tase = 0;
        this.tulemus = 0;
    }

    // genereeritakse numrid, näidatakse terminalis ja tasemest oleneva ajaga
    //      seejärel kaotatakse arv
    public void mänguOsa(Simon.Mangija mangija) throws InterruptedException {
        appi.clear();
        genereeriNumbrid( ++this.tase);
        näitaMänguNr( appi.toString());
        kaotaKäsurealt();
    }

    // genereerib numbreid ühest viieni
    //      hulk oleneb tasemest
    public void genereeriNumbrid(int tase) {
        for (int i = 0; i < tase + 2; i++) {
            appi.add((int) (Math.random() * 5) + 1);
        }
    }

    // kuvab ekraanil genereeritud numbreid
    //      kuvamise aeg oleneb samuti tasemest
    public void näitaMänguNr(String gameNumber) throws InterruptedException {
        System.out.printf("   Tase: %d%n", tase);
        System.out.println("Number on: " + gameNumber.replaceAll("[\\[\\],]", ""));
        TimeUnit.SECONDS.sleep(tase + 3);
    }

    // kuna ei leidnud kuidas windows command prompti kästku "cls" kasutada java terminalis
    //      siis väljastame 300 tühja rida, et arv ära kaotada
    public void kaotaKäsurealt() {
        for (int i = 0; i < 300; i++) {
            System.out.println();
        }
    }

    // võrdleb genereeritud arvurida ja sisend arvvurinda sõnetena ja tagastab tõeväärtuse
    public boolean kontrolli(String vastus) {
        return formaadi(vastus).equals( formaadi(appi.toString()) );
    }

    // eemaldab arvuridatelt kirjavahemärgid ja muud mitte-numbrid
    public String formaadi(String sisend) {
        return sisend
                .strip()
                .replaceAll("[\\D\\s]", "");
    }

    public int getTulemus() {
        return tulemus;
    }

    public void setTulemus() {
        this.tulemus = tase;
    }

    public void setTase(int tase) {
        this.tase = tase;
    }

}