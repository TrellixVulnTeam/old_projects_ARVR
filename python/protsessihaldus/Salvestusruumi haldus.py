from tkinter import *
import tkinter as tk
import string


class HarlduriErand(Exception):
    def __init__(self, msg, value=None, samm=None):
        self.msg = msg
        self.value = value
        self.samm = samm

    def __str__(self):
        if self.samm is not None:
            return '{} => {} sammul {}'.format(self.msg, self.value, self.samm)
        else:
            return '{} => {}'.format(self.msg, self.value)


class SisendiViga(HarlduriErand):
    def __init__(self, value):
        super().__init__('Sisend mustris on viga!', value)


class TundmatuKäsk(HarlduriErand):
    def __init__(self, value, samm):
        super().__init__('Tundmatu käsk!', value, samm)


class PolePiisavaltRuumi(HarlduriErand):
    def __init__(self, value, samm):
        super().__init__('Faili jaoks ei jätku ruumi!', value, samm)


class FailiEiLeidu(HarlduriErand):
    def __init__(self, value, samm):
        super().__init__('Sellist faili ei leidunud!', value, samm)


varvid = ['#f58231', '#ffe119', '#bfef45', 'firebrick1',
          'brown3', '#469990', 'RoyalBlue1', 'gray30',
          '#42d4f4', '#911eb4', '#f032e6', 'lavender']

tavaMuster = 'A,2;B,3;A,-;C,4;B,+3;D,5;E,15;C,-;F,5'


def kaivita():
    muster = parsemuster()
    andmed = []
    rida = ['' for _ in range(48)]

    try:

        if type(muster) is not list:
            raise SisendiViga(muster)

        # algoritm
        for samm in range(len(muster)):

            #  uus fail käsk
            if re.match(r'^[^+-]\d*$', muster[samm][1]):
                käsk = "uus"
                suurus = int(re.findall(r'\d+', muster[samm][1])[0])

            #  suurenda fail käsk
            elif re.match(r"^[^-]+\d+$", muster[samm][1]):
                if sum(x == muster[samm][0] for x in rida) == 0:
                    raise FailiEiLeidu(muster[samm][0], samm)

                käsk = "suurenda"
                suurus = int(re.findall(r'\d+', muster[samm][1])[0])

            #  eemalda fail käsk
            elif re.match(r'^-$', muster[samm][1]):
                suurus = sum(x == muster[samm][0] for x in rida)
                käsk = "eemalda"

                if suurus == 0:
                    raise FailiEiLeidu(muster[samm][0], samm)

            #  ERROR kui mustris on tundmatu käsk
            else:
                raise TundmatuKäsk(muster[samm], samm)

            #  võtame rea iga korda algusest peale läbi
            for blokk in range(len(rida)):

                if käsk == "uus" or käsk == "suurenda":
                    if suurus != 0 and rida[blokk] == '':
                        rida[blokk] = muster[samm][0]
                        suurus -= 1

                    elif suurus == 0:
                        break

                #  eemalda
                else:
                    if suurus != 0 and rida[blokk] == muster[samm][0]:
                        rida[blokk] = ''
                        suurus -= 1

                    elif suurus == 0:
                        break

            andmed.append(list(rida))

            if suurus != 0:
                andmed[-1][-1] = 'viga'
                raise PolePiisavaltRuumi(muster[samm][0], samm + 1)

    except SisendiViga as e:
        puhasta()
        erand.set('VIGA: {}'.format(e))
        return

    except (TundmatuKäsk, PolePiisavaltRuumi, FailiEiLeidu) as e:
        joonestalouend(andmed, 'VIGA: {}'.format(e))
        return

    joonestalouend(andmed)


def arvutused(andmed):
    lst = dict()
    viimane = ''

    for blokk in andmed:
        if blokk != '':
            if blokk in lst:
                if blokk != viimane:
                    lst[blokk][1] = False

                lst[blokk][0] += 1
            else:
                lst[blokk] = [1, True]

        viimane = blokk

    v1 = (1 - len([k for k, v in lst.items() if v[1] is True]) / len([k for k, v in lst.items()])) * 100
    v2 = (1 - sum([v[0] for k, v in lst.items() if v[1] is True]) / sum([v[0] for k, v in lst.items()])) * 100

    return [v1, v2]


def loemuster():
    if i.get() != 3:
        return numbrid[i.get()]
    else:
        return valikSisend.get()


def puhasta():
    # lõuendi puhastamine
    canvas.delete("all")
    arvutus1.set("")
    arvutus2.set("")
    erand.set("")


def parsemuster():
    muster = loemuster()

    if re.match(r'^([A-Z]+,[-+]?[\d;]+)+$', muster):
        return list(map(
            lambda x: list(map(
                lambda y: y, x.split(',')
            )), muster.split(';')
        ))

    return 'Sisend puudub' if muster == '' else muster


def joonestalouend(andmed=None, viga=None):
    puhasta()

    if andmed is not None and andmed[-1][-1] != 'viga':
        vastused = arvutused(andmed[-1])

        arvutus1.set("Allesjäänud failidest on fragmenteeriunud {:.2f}%.".format(vastused[0]))
        arvutus2.set("Fragmenteerunud failidele kuulub {:.2f}% kasutatud ruumist.".format(vastused[1]))

    if andmed is None:
        andmed = [['' for _ in range(48)] for _ in range(9)]

    if viga:
        erand.set(viga)

    kirjeX, kirjeY, blokkX, blokkY = 40, 25, 80, 10
    blokkAsteX, blokkAsteY = 20, 30
    vahe, sammJark = 5, 1

    for rida in andmed:
        canvas.create_text(kirjeX, kirjeY, text="Samm {}".format(sammJark))
        kirjeY += blokkAsteY + vahe

        for blokk in rida:  # oletades et blokk on ainult täht
            if blokk == 'viga':
                canvas.create_rectangle(80, blokkY + blokkAsteY / 1.5, blokkX + blokkAsteX, blokkY + blokkAsteY, fill='#ff0000')
                return

            canvas.create_rectangle(blokkX, blokkY, blokkX + blokkAsteX, blokkY + blokkAsteY,
                                    fill=varvid[string.ascii_uppercase.index(blokk)] if blokk != '' else '#ffffff')
            canvas.create_text(blokkX + blokkAsteX / 2, blokkY + blokkAsteY / 2, text=blokk if blokk != '' else '')
            blokkX += blokkAsteX

        blokkY += blokkAsteY + vahe
        blokkX = 80
        sammJark += 1


# tkinter
root = Tk()
root.title("Salvestusruumi haldus")
root.rowconfigure(0, weight=1)
root.columnconfigure(0, weight=1)
root.resizable(0, 0)

valikSisend = StringVar()
arvutus1 = StringVar()
arvutus2 = StringVar()
erand = StringVar()
i = IntVar()
numbrid = ['A,2;B,3;A,-;C,4;B,+3;D,5;E,15;C,-;F,5',
           'A,4;B,3;C,6;D,5;C,+2;B,-;E,5;A,-;F,10',
           'A,2;B,3;C,4;D,5;B,-;E,7;D,-;E,+3;F,10']

ylemine = tk.Frame(root)
ylemine_vasak = tk.Frame(root)
ylemine_parem = tk.Frame(root)
alumine = tk.Frame(root)

#################### ylemine

##### ylemine vasak
ylemine_vasak_label1 = tk.Label(ylemine_vasak,
                                text='Vali või siseta järjend (kujul A,2;B,3;A,-;C,4;B,+2). Max 10 faili.')
ylemine_vasak_valikud = tk.Frame(ylemine_vasak)
ylemine_vasak_label2 = tk.Label(ylemine_vasak, text='Algoritmi käivitamiseks klõpsa nupule')
ylemine_vasak_nupud = tk.Frame(ylemine_vasak)

### ylemine vasak valikud
valikud_vasak = tk.Frame(ylemine_vasak_valikud)
valikud_parem = tk.Frame(ylemine_vasak_valikud)

# ylemine vasak valikud vasak
valik1 = tk.Radiobutton(valikud_vasak, text='Esimene', variable=i, value=0)
valik2 = tk.Radiobutton(valikud_vasak, text='Teine', variable=i, value=1)
valik3 = tk.Radiobutton(valikud_vasak, text='Kolmas', variable=i, value=2)
valik4 = tk.Radiobutton(valikud_vasak, text='Enda oma', variable=i, value=3)

# ylemine vasak valikud parem
valikSilt1 = tk.Label(valikud_parem, text=numbrid[0])
valikSilt2 = tk.Label(valikud_parem, text=numbrid[1])
valikSilt3 = tk.Label(valikud_parem, text=numbrid[2])
valikSisend = tk.Entry(valikud_parem, textvariable=valikSisend)

# ylemine vasak nupud
kaivitaNupp = tk.Button(ylemine_vasak_nupud, text='Käivita', command=kaivita)
puhastaNupp = tk.Button(ylemine_vasak_nupud, text='Puhasta väljund', command=puhasta)

##### ylemine parem
ylemine_parem_label1 = tk.Label(ylemine_parem, text='Arvutused:')
ylemine_parem_laber2 = tk.Label(ylemine_parem, textvariable=arvutus1)
ylemine_parem_laber3 = tk.Label(ylemine_parem, textvariable=arvutus2)
ylemine_parem_laber4 = tk.Label(ylemine_parem, textvariable=erand)

#################### alumine

canvas = Canvas(alumine, width=1070, height=335)
joonestalouend()

#################### paigutus

ylemine.grid(row=1, column=1)
alumine.grid(row=2, column=1, columnspan=2)

ylemine_vasak.grid(row=1, column=1, sticky='wn', padx=[10, 30], pady=10)
ylemine_parem.grid(row=1, column=2, sticky='wn', padx=[0, 10], pady=10)

ylemine_vasak_label1.grid(row=1, sticky='w', pady=[0, 10])
ylemine_vasak_valikud.grid(row=2, sticky='w')
ylemine_vasak_label2.grid(row=3, sticky='w', pady=[10, 5])
ylemine_vasak_nupud.grid(row=4, sticky='w', padx=[10, 0])

ylemine_parem_label1.grid(row=1, sticky='w')
ylemine_parem_laber2.grid(row=2, sticky='w')
ylemine_parem_laber3.grid(row=3, sticky='w')
ylemine_parem_laber4.grid(row=4, sticky='w')

valikud_vasak.grid(row=1, column=1, sticky='w', padx=[0, 10])
valikud_parem.grid(row=1, column=2, sticky='w')

valik1.grid(row=1, sticky='w', pady=2)
valik2.grid(row=2, sticky='w', pady=2)
valik3.grid(row=3, sticky='w', pady=2)
valik4.grid(row=4, sticky='w', pady=2)
valikSilt1.grid(row=1, sticky='w', pady=4)
valikSilt2.grid(row=2, sticky='w', pady=4)
valikSilt3.grid(row=3, sticky='w', pady=4)
valikSisend.grid(row=4, sticky='w', pady=4)

kaivitaNupp.grid(row=1, column=1, padx=[0, 10])
puhastaNupp.grid(row=1, column=2)

canvas.grid(row=1, column=1)

root.mainloop()
