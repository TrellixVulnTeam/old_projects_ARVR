import java.util.concurrent.TimeUnit;

public class Mangija {
    private String nimi;
    private int parimTulem;
    private float keskmineTulem;
    private int kordiMangitud;

    // Edetabel koostab uue tühja Mangija isendi kui failis pole
    public Mangija(String nimi) {
            this.nimi = nimi;
            this.parimTulem = 0;
            this.keskmineTulem = 0;
            this.kordiMangitud = 0;
    } // Mangija

    // Edetabel koostab uuesti isedi kui failis leidub vastav mangija
    public Mangija(String nimi, int parim, float kesk, int kord) {
        // Sisendiks on failist loetud vanad andmed

        this.nimi = nimi;
        this.parimTulem = parim;
        this.keskmineTulem = kesk;
        this.kordiMangitud = kord;
    } // Mangija

    // Mängu lõppedes kutsutav meetod mis uuendab Mangija isendi andmed
    //      Õnnitleb kui said uue parima tulemuse.
    public void uuendaAndmed(SimonMäng m) { // uuendaAndmed
        // Sisendiks on SimonMängu isend, sest mängu tulemuse andmed asuvad seal

        kordiMangitud++;

        if (m.getTulemus() > parimTulem) {
            parimTulem = m.getTulemus();
            System.out.println(" TUBLI!  Said uue parima tulemuse.");
        }
        keskmineTulem(m.getTulemus());
    } // uuendaAndmed

    // Arvutab uue keskmise tulemuse, uuendaAndmed() meetodi jaoks
    private void keskmineTulem(int tulemus) {
        // ennem tulemuse lisamist arvutame eelmisest keskmisest kõikide tulemuste summa
        //  seejärel liidame tulemuse otsa ja jagame uue kordade arvuga, et saada
        //  uus keskmine.
        if (kordiMangitud > 1) {
            this.keskmineTulem = ((keskmineTulem * (kordiMangitud - 1)) + tulemus) / kordiMangitud;
        }
        else {
            this.keskmineTulem = tulemus;
        }
    } // setKeskmineTulem

    public String getNimi() {
        return nimi;
    }

    public int getParimTulem() { return parimTulem;}

    public float getKeskmineTulem() {
        return keskmineTulem;
    }

    // Mangija.toString() meetodi kasutatakse mugavaks faili salvestamiseks.
    @Override
    public String toString() {
        return nimi + "; " +
                parimTulem + "; " +
                keskmineTulem + "; " +
                kordiMangitud;
    } // toString()

    // Meetodi tagatab tervituse sõnumi.
    //      Oleneb kas oled naasev mängija või mitte.
    public void tervitus() throws InterruptedException {
        System.out.printf("%s%s%n%n", (kordiMangitud > 0) ? " Tere tulemast tagasi, " : " Tere, ", nimi );
        TimeUnit.SECONDS.sleep(2);
    }
}