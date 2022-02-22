from tkinter import *
import tkinter.ttk as tk
import string
import random


class Leitud(Exception):
    pass


class PoleRuumi(Exception):
    pass


class MitteSobilik(Exception):
    pass


varvid = ['#f58231', '#ffe119', '#bfef45', 'firebrick1',
          'brown3', '#469990', 'RoyalBlue1', 'gray30',
          '#42d4f4', '#911eb4', '#f032e6', 'lavender']

tavaMuster = '1,10;6,6;3,7;2,4;1,6;5,2;1,4;5,2;3,1'


def yldinemeetod(meetod):
    puhasta()
    label_var.set(meetod)
    protsessid = parsemuster()
    malu = []
    protsess = []

    if protsessid[0][0] == '':  # kui valitud on mustri isesisestamine ja lahter tyhi, siis meetod katkestab
        return None

    for p in range(len(protsessid)):

        # protsesside marke ja suuruse sildi maarmaine
        protsess.append(string.ascii_uppercase[p] + ' : ' + ','.join(protsessid[p]))
        asukohad = []
        alguspunktid = {}

        if len(malu) == p and p < 10:
            malu.append(['-'] * 50)

        # otsime real koik alguskohad ja nende alade pikkused
        jark = True
        viimane = ''
        for koht in range(50):
            if jark and malu[p][koht] == '-':
                viimane = str(koht)
                alguspunktid[viimane] = 1
                jark = False
            elif malu[p][koht] != '-':
                jark = True
            else:
                alguspunktid[viimane] += 1

        try:
            while True:
                try:

                    a = int(protsessid[p][0])

                    if len(alguspunktid) != 0:

                        # erinevate algoritmide erinevused tulevad jargnevatest if lausetest
                        if meetod == "FirstFit":
                            vaikseimkey = list(alguspunktid.keys())[0]
                            vaikseimvalue = alguspunktid.get(vaikseimkey)
                        elif meetod == "LastFit":
                            vaikseimkey = list(alguspunktid.keys())[-1]
                            vaikseimvalue = alguspunktid.get(vaikseimkey)
                        elif meetod == "BestFit":
                            vaikseimkey = min(alguspunktid, key=alguspunktid.get)
                            vaikseimvalue = alguspunktid.get(vaikseimkey)
                        elif meetod == "WorstFit":
                            vaikseimkey = max(alguspunktid, key=alguspunktid.get)
                            vaikseimvalue = alguspunktid.get(vaikseimkey)
                        else:
                            vaikseimkey, vaikseimvalue = random.choice(list(alguspunktid.items()))

                        # teisena liigume edasi x teljel
                        for x in range(int(vaikseimkey), int(vaikseimkey) + a + 1):
                            a -= 1
                            b = int(protsessid[p][1])

                            # esimesena liigume y teljel
                            for y in range(p, p + b + 1):

                                # kui vaadatav ala on veel 50x10 ala sees
                                if x < 50 and y < 10 and b != 0:

                                    if len(malu) == y:
                                        malu.append(['-'] * 50)

                                    # kui ala on vaba, siis salvestame koha
                                    if malu[y][x] == '-':
                                        asukohad.append(str(x) + "," + str(y))
                                        b -= 1
                                        continue

                                    # kui ala ei ole vaba, siis kustutame salvestatud
                                    # kohad ja liigume jargmist vaba kohta
                                    else:
                                        if vaikseimvalue - 1 >= int(protsessid[p][0]):
                                            alguspunktid[str(int(vaikseimkey + 1))] = vaikseimvalue - 1

                                        del alguspunktid[vaikseimkey]
                                        raise MitteSobilik

                                # kui salvestatud ala on sobiliku suurusega ja
                                # rohkem ala ei ole vaja protsessile määrata hõivame selle ala mälus
                                elif (x <= 50 and a == 0) and (y <= 10 and b == 0):
                                    for osa in asukohad:
                                        info = osa.split(',')
                                        malu[int(info[1])][int(info[0])] = string.ascii_uppercase[p] + " " + varvid[p]
                                    raise Leitud

                                # kui liigume mälu alast valja märgime ja katkestame
                                elif y == 10 and b != 0:
                                    malu[p][0] = 'viga'
                                    raise PoleRuumi

                                # kui liigume mälu alast valja märgime ja katkestame
                                elif x == 50 and a != 0:
                                    del alguspunktid[vaikseimkey]
                                    raise MitteSobilik

                                # kui y teljel protsess rohkem malu ala ei vaja
                                elif b == 0:
                                    break

                    # kui pole vaba ruumi real
                    else:
                        malu[p][0] = 'viga'
                        raise PoleRuumi

                # katkestamine ja jatkamine mitte väga heal meetodil
                except MitteSobilik:
                    asukohad.clear()
                    continue
        except Leitud:
            continue
        except PoleRuumi:
            break

    # print('\n'.join([''.join(['{:4}'.format(item) for item in row]) for row in malu]))
    # print(protsess)

    # valjastame pildi malu alast
    joonestalouend(malu, protsess)


def first_fit():
    yldinemeetod("FirstFit")


def last_fit():
    yldinemeetod("LastFit")


def best_fit():
    yldinemeetod("BestFit")


def worst_fit():
    yldinemeetod("WorstFit")


def random_fit():
    yldinemeetod("RandomFit")


# abimeetodid

def loemuster():
    if i.get() != 3:
        return numbrid[i.get()]
    else:
        return enda_var.get()


def puhasta():
    # lõuendi puhastamine
    label_var.set("")
    canvas.delete("all")
    joonestalouend([['-'] * 50 for m in range(10)])
    protsessid_var.set('\n\n'.join(['-' for m in range(10)]))


def parsemuster():
    return list(map(
        lambda x: list(map(
            lambda y: y, x.split(',')
        )), loemuster().split(';')
    ))


# valjastab malu pildi
def joonestalouend(arg, protsess=None):
    x = 0
    y = 0
    protsessid_var.set('')
    eelmine = 0

    for m in range(len(arg)):
        try:
            for n in range(len(arg[m])):
                if arg[m][0] != 'viga':
                    info = arg[m][n].split(' ')
                    if len(info) == 1:
                        canvas.create_rectangle(x, y, x + 23, y + 30, fill='LIGHTGRAY')
                    else:
                        canvas.create_rectangle(x, y, x + 23, y + 30, fill=info[1])

                        if eelmine != m:
                            protsessid_var.set(
                                protsessid_var.get() +
                                "\n\n" + protsess[m])
                            eelmine = m
                        elif m == 0:
                            protsessid_var.set(protsess[0])

                    canvas.create_text(x + 11.5, y + 15, text=info[0])
                    x += 23
                else:
                    canvas.create_rectangle(2, 2 + (30 * m), 1153, 2 + (30 * m) + 30, fill='BLACK')
                    canvas.create_text(1153 / 2, 2 + (30 * m) + 15, text='Protsess ei mahu mällu', fill='WHITE')
                    raise PoleRuumi
        except PoleRuumi:
            protsessid_var.set(
                protsessid_var.get() +
                "\n\n" + protsess[m])
            break

        x = 0
        y += 30


# tkinter
root = Tk()
root.title("Mälu Algoritmid")
root.rowconfigure(0, weight=1)
root.columnconfigure(0, weight=1)
root.resizable(0, 0)

label_var = StringVar()
enda_var = StringVar()
protsessid_var = StringVar()
protsessid_var.set('\n\n'.join(['-' for m in range(10)]))
i = IntVar()
numbrid = ['4,5;2,7;9,2;4,6;7,1;6,4;8,2;3,3;1,2;9,1',
           '1,10;6,6;3,8;8,4;3,6;12,2;1,4;5,2;2,1;2,7',
           '5,10;6,6;3,8;8,4;3,6;5,5;1,4;15,3;3,2;8,1']

ylemine = tk.Frame(root)  # , borderwidth=1, relief=RIDGE
alumine = tk.Frame(root)
valikud = tk.Frame(ylemine)
valik_vasak = tk.Frame(valikud)
valik_parem = tk.Frame(valikud)
algod = tk.Frame(ylemine)
# canvas = Canvas(alumine, height=lõuendikõrgus, width=lõuendilaius)

# elementide loomine

l1 = tk.Label(ylemine, text='Vali või siseta kuni kümneelemendiline järjend kujul 1,10;6,6;3,7;2,4;1,6;5,2;1,4;5,2;3,1')
l2 = tk.Label(ylemine, text='Algoritmi käivitamiseks klõpsa nupule')
l3 = tk.Label(alumine, textvariable=label_var)
lv1 = tk.Label(valik_parem, text=numbrid[0])
lv2 = tk.Label(valik_parem, text=numbrid[1])
lv3 = tk.Label(valik_parem, text=numbrid[2])

r1 = tk.Radiobutton(valik_vasak, text='Esimene', variable=i, value=0)
r2 = tk.Radiobutton(valik_vasak, text='Teine', variable=i, value=1)
r3 = tk.Radiobutton(valik_vasak, text='Kolmas', variable=i, value=2)
r4 = tk.Radiobutton(valik_vasak, text='Enda oma', variable=i, value=3)
run1 = tk.Button(algod, text='First-Fit', command=first_fit)
run2 = tk.Button(algod, text='Last-Fit', command=last_fit)
run3 = tk.Button(algod, text='Best-Fit', command=best_fit)
run4 = tk.Button(algod, text='Worst-Fit', command=worst_fit)
run5 = tk.Button(algod, text='Random-Fit', command=random_fit)
clear = tk.Button(ylemine, text='Puhasta väljund', command=puhasta)

enda = tk.Entry(valik_parem, textvariable=enda_var)

lEtapp = tk.Label(alumine, text="Etapp")
lLisatud = tk.Label(alumine, text="Lisatud\nprotsessid")
lmaluNummerdus = tk.Label(alumine, text="    ".join([str(k) for k in range(50)]))
lProtsessid = tk.Label(alumine, textvariable=protsessid_var)
canvas = Canvas(alumine, width=1151, height=331)
joonestalouend([['-'] * 50 for k in range(10)])

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
run5.grid(row=1, column=5, padx=5)

clear.grid(row=4, column=2, padx=80, pady=10)

l3.grid(row=1, column=1, columnspan=3, padx=[10, 10], pady=[10, 10], sticky='N')
lEtapp.grid(row=2, column=1, padx=5, pady=5, sticky='E')
lLisatud.grid(row=2, column=2, padx=5, pady=5, sticky='N')
lmaluNummerdus.grid(row=2, column=3, sticky='S')
lProtsessid.grid(row=3, column=1, columnspan=2, pady=[10, 10], sticky='N')
canvas.grid(row=3, column=3)

root.mainloop()
