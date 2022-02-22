//import java.util.Scanner;
//import java.util.concurrent.TimeUnit;
//
//public class PeaKlass {
//
//    // lihtne kasutaja sisend
//    static String saaSisend() { return new Scanner(System.in).nextLine(); } // saaSisend
//
//    // mängu tsükkel
//    static void start(Simon.Mangija mangija) throws InterruptedException {
//        // Sisendiks on Mangija isend
//        // Kasutatakse mängu põhitsüklina
//
//        String sisend;
//        Simon.SimonMäng mang = new Simon.SimonMäng();
//
//        while (true) {
//            // generaaritakse numbrid ja näidatakse neid mängijale
//            //      seejärel kaotatakse
//            mang.mänguOsa(mangija);
//
//            // küsitakse kasutajalt samu numbreid
//            System.out.print(" Sisesta nähtud numbrid:   ");
//            sisend = saaSisend();
//            System.out.println();
//
//            // kui Õige, siis jätkatakse
//            // kui Vale, siis mäng läbi. Seega küsitakse kas soovid uuesti proovida.
//            if (mang.kontrolli(sisend)) {
//                System.out.println(" Õige!");
//                TimeUnit.SECONDS.sleep(2);
//            } else {
//                System.out.println(" Vale!\n   Lips Läbi.\n\n");
//                mang.setTulemus();
//                mangija.uuendaAndmed(mang);
//                System.out.print(" Kas mängid uuesti?  ( jah/ei )\n   :: ");
//                sisend = saaSisend();
//                if (!sisend.equals("jah")) {
//                    break;
//                }
//                mang.setTase(0);
//            }
//            mang.kaotaKäsurealt();
//        } // while
//    } // start
//
//    public static void main(String[] args) throws Exception {
//        // küsitakse nimi.
//        //      Tehakse edetabeli isend mis loeb failist andmed ja koostab edetabeli.
//        //      Tagastab ka manigja isendi kui sellise nimega mängija juba on failis.
//
//        System.out.println();
//        System.out.print(" Sisesta nimi:   ");
//        String nimi = saaSisend();
//        System.out.println();
//
//        // edetabel ja mangija isend
//        Simon.Edetabel tabel = new Simon.Edetabel();
//        Simon.Mangija mangija = tabel.loeAndmed(nimi);
//
//        // väljastab edetabeli
//        tabel.valjastaTabel();
//        TimeUnit.SECONDS.sleep(3);
//
//        // alustab mängu
//        start(mangija);
//
//        // kirjutab faili kõik mangija isendite andmed (kirjutab üle)
//        tabel.salvestaFaili();
//    } // main
//} // PeaKlass