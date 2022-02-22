# TU Genoomika Instituudi Test
</br>

## Ülesseadmise juhend

Projekti saab üles seada kasutades docker-compose'i.

````
git clone git@github.com:CarlSaks/gi_dev_test.git
````

````
cd ./git_dev_test/
````

````
docker compose up -d --build
````

Rakendusele saab ligi kohalikust arvutist aadressiga [http://localhost:8000/](http://localhost:8000/)
</br>

## Projekti Kirjeldus

Projektiks on kasutatud järgmiseid vahendeid
- Kliendipool
    - Vue
        - Bootstrap
        - Axios
- Server
    - Express
        - Sequelize
    - Postgres

</br>
Rakenduse avamisel on võimalik valida mitu test andmete faili ja need serverisse laadida. Näha saab ka üles laadimise protsenti, aga ei näe kui kaugel on serveri andmetöötlus.

</br>
Kui andmebaasi on andmeid laekunud, siis saab otsingu vaates otsida isikukoodi alusel andmeid. Otsing töötab ka siis kui sisestatud on vaid üks number.

</br>
Otsingu tulemusi näidatakse 100 kaupa ja tabelit annab sorteerida kui vajutada tabeli vastava päise peale.

</br>
</br>
Projekti osad on jaotatud eraldi dockeri konteineritesse ja välja on jäetud vastavad pordid

- Kliendipool 8000
- Server 5000
- Andmebaas 5432
</br>
</br>

### Teadaolevad probleemid

- Suure faili töötlemisel ei lisa server kõiki andmeid andmebaasi