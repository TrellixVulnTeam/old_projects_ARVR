from tkinter import *
import tkinter.ttk as tk

lõuendikõrgus = 120
lõuendilaius = 800
varvid = ['#f58231', '#ffe119', '#bfef45', 'firebrick1',
          'brown3', '#469990', 'RoyalBlue1', 'gray30',
          '#42d4f4', '#911eb4', '#f032e6', 'lavender']


def loeMuster():
    if i.get() != 3:
        return numbrid[i.get()]
    else:
        return enda_var.get()


def FCFS_Algoritm():
    puhasta()  # puhastame lõuendi juhul kui seal on veel eelmine väljund
    muster = loeMuster()

    # protsesside algusajad ja kestuse ajad kahe dimesioonilises listis
    protsessid = list(map(
        lambda x: list(map(
            lambda y: int(y), x.split(',')
        )), muster.split(';')
    ))

    # sobiliku lõuendi laiuse saamiseks arvutan protsesside ajalise pikkuse
    # kui leidub tühimikke, siis arvestan need pikkusele juurde
    pikkus = joonistusalapikkus(protsessid)

    # üleüldine kast kuhu protsessi ajad sisse lähevad
    canvas.create_rectangle(50, 20, lõuendilaius - 50, 80)

    ooteaeg = []  # keskmise aja arvutmiseks massiiv
    algus = 50  # protsessi algus canvase x-teljel
    lopp = 50  # protsessi lõpp canvase x-teljel
    ootel = []  # kõrge prioteediga järjekord
    tehtud = []  # tehtud protsesside järjekord
    ajad = list(map(lambda x: int(x[1]), protsessid))  # protsesside ajakulude järgimiseks
    q = 0  # aeg algusest
    aste = (lõuendilaius - 100) / pikkus  # sobiva aja astme saavutamiseks arvutatud pikkus

    # algortim
    while len(tehtud) < len(protsessid):
        # protsessi ei katkestata

        # igal ajaühikul jälgime kas uusi protsesse on tulnud
        for m in range(len(protsessid)):

            # leidub protsess
            if protsessid[m][0] == q:

                # kui käivaid protsesse ei ole alustame uut
                if len(ootel) == 0:
                    algus = lopp

                # lisame protsessi järjekorra lõppu
                ootel.append(m)

        # kui järjekorras on mingi käiv protsess
        if len(ootel) > 0:

            # kui protsess on lõppemas
            # lõpetame, märgime tehtuks ja alustame uut
            if ajad[ootel[0]] == 0:
                märgiprotsess(algus, lopp, ootel[0])
                algus = lopp
                tehtud.append(ootel.pop(0))

            # kontrolliks ega viimast protsessi pole eemaldatud
            if len(ootel) > 0:

                # keskmise ooteaja arvutamiseks
                if ajad[ootel[0]] == protsessid[ootel[0]][1]:
                    ooteaeg.append(q - protsessid[ootel[0]][0])

                ajad[ootel[0]] -= 1

        # lõuendile sobilikesse kohtadesse ajaühikute lisamine
        if algus == lopp:
            märgiaeg(algus, q)

        lopp += aste
        q += 1

    label_var.set("FCFS: Keskmine ooteaeg: {0}".format(
        round(sum(ooteaeg) / len(ooteaeg), 2)))


def SRTF_Algoritm():
    puhasta()  # lõuendi puhastamine eelmise algoritmi tulemusest
    muster = loeMuster()

    # protsesside algusajad ja kestuse ajad kahe dimesioonilises massiivis
    protsessid = list(map(
        lambda x: list(map(
            lambda y: int(y), x.split(',')
        )), muster.split(';')
    ))

    # lõuendi sobiliku laiuse saamiseks arvutan protsesside ajalise pikkuse
    # kui leidub tühimikke, siis arvestan need pikkusele juurde
    pikkus = joonistusalapikkus(protsessid)

    # üleüldine kast kuhu protsessi ajad sisse lähevad
    canvas.create_rectangle(50, 20, lõuendilaius - 50, 80)

    ooteaeg = []  # keskmise aja arvutmiseks massiiv
    algus = 50  # protsessi algus canvase x-teljel
    lopp = 50  # protsessi lõpp canvase x-teljel
    ootel = []  # kõrge prioteediga järjekord
    tehtud = []  # tehtud protsesside järjekord
    loppajad = [y for y in range(len(protsessid))]
    ajad = list(map(lambda x: int(x[1]), protsessid))  # protsesside ajakulude järgimiseks
    q = 0  # aeg algusest
    aste = (lõuendilaius - 100) / pikkus  # sobiva aja astme saavutamiseks arvutatud pikkus

    # algoritm
    while len(tehtud) < len(protsessid):
        # kui saabub väiksema protsessoriajasooviga protsess, siis käimas olev katkestatakse
        # kui saabub protsess, mille kestus on identne juba käiva protsessiga, eeldatakse käimas olevat protsessi

        # kontrollime kas uusi protsesse on saabunud
        for m in range(len(protsessid)):

            # kui leidub protsess mis saabub antud ajaketkel q
            if protsessid[m][0] == q:

                # alustame uut protsessi kui ei leidu käivaid protsesse
                if len(ootel) == 0:
                    algus = lopp
                    ootel.append(m)

                # kui leidub juba käiv protsess ja uus protsess on väiksema ajakuluga
                # peatame eelmise protsessi ja alustame uut
                elif protsessid[m][1] < protsessid[ootel[0]][1]:
                    märgiprotsess(algus, lopp, ootel[0])
                    algus = lopp
                    ootel.insert(0, m)

                # lisame järjekorda kui pole käivast protsessist väiksem
                else:
                    ootel.append(m)

        # kui on olemas mingi käiv protsess
        if len(ootel) > 0:

            # kui protsess on lõppemas siis lõpetame ja algame uut
            if ajad[ootel[0]] == 0:
                märgiprotsess(algus, lopp, ootel[0])
                algus = lopp

                # keskmise ooteaja arvutamiseks
                loppajad[ootel[0]] = q - protsessid[ootel[0]][0]
                ooteaeg.append(loppajad[ootel[0]] - protsessid[ootel[0]][1])

                # märgime tehtuks
                tehtud.append(ootel.pop(0))

            # kontrollime ega ei eemaldanud viimast protsessi
            if len(ootel) > 0:
                ajad[ootel[0]] -= 1

        # lõuendile sobivatele kohtadele aegade paigutamiseks
        if algus == lopp:
            märgiaeg(algus, q)

        q += 1
        lopp += aste

    label_var.set("SRTF: Keskmine ooteaeg: {0}".format(
        round(sum(ooteaeg) / len(protsessid), 2)))


def RR_Algoritm():
    puhasta()  # lõuendi puhastamine eelmisest algoritmist
    muster = loeMuster()

    # protsesside algusajad ja kestuse ajad kahe dimesioonilises massiivis
    protsessid = list(map(
        lambda x: list(map(
            lambda y: int(y), x.split(',')
        )), muster.split(';')
    ))

    # lõuendile sobiliku laiuse leidmiseks aruvtan protsesside ajalise pikkuse
    # kui leidub tühimikke, siis arvestan need pikkusele juurde
    pikkus = joonistusalapikkus(protsessid)

    # üleüldine kast kuhu protsessi ajad sisse lähevad
    canvas.create_rectangle(50, 20, lõuendilaius - 50, 80)

    ooteaeg = []  # keskmise aja arvutmiseks massiiv
    algus = 50  # protsessi algus canvase x-teljel
    lopp = 50  # protsessi lõpp canvase x-teljel
    ootel = []  # kõrge prioteediga järjekord
    ooteluued = []
    loppajad = [[0 for x in range(1)] for y in range(len(protsessid))]
    tehtud = []  # tehtud protsesside järjekord
    ajad = list(map(lambda x: int(x[1]), protsessid))  # protsesside ajakulude järgimiseks
    q = 0  # aeg algusest
    kvant = 3
    aste = (lõuendilaius - 100) / pikkus  # sobiva aja astme saavutamiseks arvutatud pikkus

    # algoritm
    while len(tehtud) < len(protsessid):
        # Igal ajahetkel täidame uued protsessid alati ennem vanasi protsesse.
        # kui on käimas protsess ja uued tulevad peale, täidetakse käiv protsess ära tema ajakvandi lõpuni
        # ja siis alustatakse uutega
        # peale kvandi läbimist läheb poolik protsess järjekorra lõppu ja alustab järjekorras järgmine

        # protsesside lisamine
        for m in range(len(protsessid)):

            # kui leidub protsess mille algusaeg on vastav
            if protsessid[m][0] == q:

                # alustame uut protsessi kui käivaid ei leidu
                if len(ootel) == 0:
                    algus = lopp
                    ootel.append(m)

                # kui käib mingi protsess mille kant pole veel läbi
                # lisame eraldi saabunud uute protsesside listi
                elif kvant != 0:
                    ooteluued.append(m)

                # kui ajakvant ja kohe eemaldatakse protsess, lisame kohe järgmiseks
                else:
                    ootel.insert(1, m)

        # kui leidub käimasolev protsess
        if len(ootel) > 0:

            # protsess on lõppemas
            if ajad[ootel[0]] == 0 or kvant == 0:
                märgiprotsess(algus, lopp, ootel[0])
                algus = lopp

                # keskmise ooteaja leidmiseks
                loppajad[ootel[0]].append(q)

                if (kvant == 0 and ajad[ootel[0]] == 0) or ajad[ootel[0]] == 0:
                    tehtud.append(ootel.pop(0))
                elif kvant == 0:
                    ootel.append(ootel.pop(0))

                # lisame uued saabunud protsessid järjekorra algusesse
                ootel = ooteluued + ootel
                ooteluued.clear()
                kvant = 3  # uuendame aja

            # kontrollime ega viimast käivat protsessi ei eemaldatud
            if len(ootel) > 0:
                ajad[ootel[0]] -= 1
                kvant -= 1

                # kesmise ooteaja saamiseks
                if q != 0 and algus != pikkus and lopp == algus:

                    if len(loppajad[ootel[0]]) == 1:
                        ooteaeg.append(q - protsessid[ootel[0]][0])

                    else:
                        ooteaeg.append(q - loppajad[ootel[0]][-1])

        # lõuandile sobilikesse kohtadesse ajaühikute lisamiseks
        if algus == lopp:
            märgiaeg(algus, q)

        lopp += aste
        q += 1

    label_var.set("RR3: Keskmine ooteaeg: {0}".format(
        round(sum(ooteaeg) / len(protsessid), 2)))


def doublePriority_FCFS_Algoritm():
    puhasta()  # lõuendi puhastamine eelmisest algoritmist
    muster = loeMuster()

    # protsesside algusajad ja kestuse ajad kahe dimesioonilises massiivis
    protsessid = list(map(
        lambda x: list(map(
            lambda y: int(y), x.split(',')
        )), muster.split(';')
    ))

    # lõuendile sobiliku laiuse saamiseks arvutan protsesside ajalise pikkuse
    # kui leidub tühimikke, siis arvestan need pikkusele juurde
    pikkus = joonistusalapikkus(protsessid)

    # üleüldine kast kuhu protsessi ajad sisse lähevad
    canvas.create_rectangle(50, 20, lõuendilaius - 50, 80)

    ooteaeg = []  # keskmise aja arvutmiseks massiiv
    algus = 50  # protsessi algus canvase x-teljel
    lopp = 50  # protsessi lõpp canvase x-teljel
    korge = []  # kõrge prioteediga järjekord
    madal = []  # madala prioteediga järjekord
    tehtud = []  # tehtud protsesside järjekord
    ajad = list(map(lambda x: int(x[1]), protsessid))  # protsesside ajakulude järgimiseks
    q = 0  # aeg algusest
    aste = (lõuendilaius - 100) / pikkus  # sobiva aja astme saavutamiseks arvutatud pikkus

    # algoritm
    while len(tehtud) < len(protsessid):
        # kui protsessi kestus on <= 6 ajaühiku, pannakse protsess kõrge prioteediga FCFS järjekorda.
        # kui ps pn > 6, pannakse madala prioteediga FCFS järjekorda
        # madala prioteediga ps täidetakse ainult siis kui kõrgeid pole
        # kui saabub kõrge prioteediga töö katkestatakse madala prioteediga ps

        # protsesside lisamine ajajärgu järgi
        for m in range(len(protsessid)):

            # kui leidub protsess mille algusaeg on hetke ajajärk
            if protsessid[m][0] == q:

                # kui protsessi kestus on kuus või vähem
                # lisame protsessi kõrge prioteediga järjekorda
                if protsessid[m][1] <= 6:

                    # kui mõlemad järjekorrd on tühjad
                    # alustame uut protsessi
                    if len(korge) == 0 and len(madal) == 0:
                        algus = lopp

                    # kui käivad ainult madala prioteediga protsessid
                    # lõpetame madala prioteediga protsessi
                    # alustame kõrge prioteediga protsessi
                    elif len(korge) == 0 and len(madal) > 0:
                        märgiprotsess(algus, lopp, madal[0])
                        algus = lopp

                    korge.append(m)

                # kui protsess kestus on suurem kui 6
                # lisame protsessi madala prioteediga järjekorda
                else:

                    # kui protsessid puuduvad alustame uut
                    if len(korge) == 0 and len(madal) == 0:
                        algus = lopp

                    madal.append(m)

        # kui leidub kõrge prioteediga protsess
        if len(korge) > 0:

            # kui protsess on lõppemas
            # lõpetame protsessi, märgime tehtuks ja alustame uut
            if ajad[korge[0]] == 0:
                märgiprotsess(algus, lopp, korge[0])
                algus = lopp
                tehtud.append(korge.pop(0))

            # kontrollime ega viimast kõrge prioteediga protsessi ei eemaldanud
            if len(korge) > 0:

                # keskmise ooteaja leidmiseks
                if ajad[korge[0]] == protsessid[korge[0]][1] or algus == lopp:
                    ooteaeg.append(q - protsessid[korge[0]][0])

                ajad[korge[0]] -= 1

            # juhul kui viimane kõrge prioteediga protsess lõppes
            # alustame või jätkame madala prioteediga protsessi
            # kui selline protsess leidub
            elif len(madal) > 0:

                # kui protsess on lõppemas
                # lõpetame protsessi, märgime tehtuks ja alustame uut
                if ajad[madal[0]] == 0:
                    märgiprotsess(algus, lopp, madal[0])
                    algus = lopp
                    tehtud.append(madal.pop(0))

                # kontroll ega viimast madala prioteediga protsessi ei lõpetatud
                if len(madal) > 0:

                    # keskmise ooteaja leidmiseks
                    if ajad[madal[0]] == protsessid[madal[0]][1] or algus == lopp:
                        ooteaeg.append(q - protsessid[madal[0]][0])

                    ajad[madal[0]] -= 1

        # kui leidub madala prioteediga protsess
        elif len(madal) > 0:

            # kui protsess on lõppemas
            # lõpetame protsessi, märgime tehtuks ja alustame uut
            if ajad[madal[0]] == 0:
                märgiprotsess(algus, lopp, madal[0])  # lõpetame protsessi
                algus = lopp
                tehtud.append(madal.pop(0))

            # kontroll ega viimast madala prioteediga protsessi ei lõpetatud
            if len(madal) > 0:

                # keskmise ooteaja leidmiseks
                if ajad[madal[0]] == protsessid[madal[0]][1] or algus == lopp:
                    ooteaeg.append(q - protsessid[madal[0]][0])

                ajad[madal[0]] -= 1

        # lõuendile sobilikesse kohtadesse ajaühikute lisamine
        if lopp == algus:
            märgiaeg(algus, q)

        lopp += aste
        q += 1

    label_var.set("2xFCFS: Keskmine ooteaeg: {0}".format(
        round(sum(ooteaeg) / len(ooteaeg), 2)))


def märgiaeg(algus, q):
    # lõuendile ajajärgu märkimine

    canvas.create_text(algus, 90, text=q)


def märgiprotsess(algus, lopp, v, color=""):
    # lõuendile lõppeva protsessi joonistamine ja protsessi numbri märkimine

    canvas.create_rectangle(algus, 20, lopp, 80, fill=varvid[v] if color == "" else color)
    canvas.create_text(algus + (lopp - algus) / 2, 50, text=str.format('P{0}', v + 1))


def joonistusalapikkus(protsessid):
    # lõuendile sobiliku joonestusala leidmiseks
    # arvutan protsesside ajakulude summa
    # ja liidan vahepealsed tühimikud

    summa = protsessid[0][0] + protsessid[0][1]

    for el in range(1, len(protsessid)):
        if summa < protsessid[el][0]:
            summa += protsessid[el][0] - summa

        summa += protsessid[el][1]

    return summa


def puhasta():
    # lõuendi puhastamine

    label_var.set("")
    canvas.delete("all")


# tkinter
root = Tk()
root.title("Protsessoriaja Haldus")
root.resizable(0, 0)

label_var = StringVar()
enda_var = StringVar()
i = IntVar()
numbrid = ['0,7;1,5;2,3;3,1;4,2;5,1', '0,2;0,4;12,4;15,5;21,10', '0,4;1,5;2,2;3,1;4,6;6,3']

ylemine = tk.Frame(root)  # , borderwidth=1, relief=RIDGE
alumine = tk.Frame(root)
valikud = tk.Frame(ylemine)
valik_vasak = tk.Frame(valikud)
valik_parem = tk.Frame(valikud)
algod = tk.Frame(ylemine)
canvas = Canvas(alumine, height=lõuendikõrgus, width=lõuendilaius)

# elementide loomine

l1 = tk.Label(ylemine, text='Vali või siseta järjend (kujul  1,10;3,3;4,1;8,6;15,2 )')
l2 = tk.Label(ylemine, text='Algoritmi käivitamiseks klõpsa nupule')
l3 = tk.Label(alumine, textvariable=label_var)
lv1 = tk.Label(valik_parem, text=numbrid[0])
lv2 = tk.Label(valik_parem, text=numbrid[1])
lv3 = tk.Label(valik_parem, text=numbrid[2])

r1 = tk.Radiobutton(valik_vasak, text='Esimene', variable=i, value=0)
r2 = tk.Radiobutton(valik_vasak, text='Teine', variable=i, value=1)
r3 = tk.Radiobutton(valik_vasak, text='Kolmas', variable=i, value=2)
r4 = tk.Radiobutton(valik_vasak, text='Enda oma', variable=i, value=3)
run1 = tk.Button(algod, text='FCFS', command=FCFS_Algoritm)
run2 = tk.Button(algod, text='SRTF', command=SRTF_Algoritm)
run3 = tk.Button(algod, text='RR 3', command=RR_Algoritm)
run4 = tk.Button(algod, text='2xFCFS', command=doublePriority_FCFS_Algoritm)
clear = tk.Button(ylemine, text='Puhasta väljund', command=puhasta)

enda = tk.Entry(valik_parem, textvariable=enda_var)

# paigutus

ylemine.grid(row=1, sticky='w')
alumine.grid(row=2, sticky='w')

l1.grid(row=1, sticky='w', padx=10, pady=10)
valikud.grid(row=2, sticky='w', padx=20)
valik_vasak.grid(row=1, column=1)
valik_parem.grid(row=1, column=2)

r1.grid(row=1, sticky='w', pady=2)
r2.grid(row=2, sticky='w', pady=2)
r3.grid(row=3, sticky='w', pady=2)
r4.grid(row=4, sticky='w', pady=2)

lv1.grid(row=1, sticky='w', padx=15, pady=2)
lv2.grid(row=2, sticky='w', padx=15, pady=2)
lv3.grid(row=3, sticky='w', padx=15, pady=2)
enda.grid(row=4, sticky='w', padx=15, pady=2)
#
l2.grid(row=3, sticky='w', padx=10, pady=10)
algod.grid(row=4, padx=10)

run1.grid(row=1, column=1, padx=5)
run2.grid(row=1, column=2, padx=5)
run3.grid(row=1, column=3, padx=5)
run4.grid(row=1, column=4, padx=5)

clear.grid(row=4, column=2, padx=80, pady=10)

l3.grid(row=1, sticky='w', padx=20, pady=20)
canvas.grid(row=2)

label_var.set("Algne")

root.mainloop()
