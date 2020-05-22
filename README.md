![Java CI with Maven](https://github.com/kamilcglr/hpp_project_gr6/workflows/Java%20CI%20with%20Maven/badge.svg)
[![codecov](https://codecov.io/gh/kamilcglr/hpp_project_gr6/branch/dev/graph/badge.svg?token=P4AQ072RCG)](https://codecov.io/gh/kamilcglr/hpp_project_gr6)
[![Contributors][contributors-shield]][contributors-url]
[![MIT License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://www.telecom-st-etienne.fr/">
    <img src="https://www.telecom-st-etienne.fr/wp-content/uploads/sites/3/2015/12/logo_tse_H_BL_web1-1.png" alt="Logo" >
  </a>

  <h3 align="center">High Performance Computing Project - Group 6</h3>

  <p align="center">
    Projet de programmation haute performance 2019 - 2020.
  </p>
</p>


<!-- TABLE OF CONTENTS -->
## Table of Contents

* [A propos](#a-propos-du-projet)
  * [Réalisé avec](#réalisé-avec)
  * [Auteurs](#auteurs)
* [Commmencer](#getting-started)
  * [Prerequis](#prerequisites)
  * [Installation](#installation)
* [Benchmark](#benchmark)
* [Tests](#tests)
  * [Intégration continue](#intégration-continue)
* [Optimisations apportées](#optimisations-apportées)

<!-- ABOUT THE PROJECT -->
## A propos du projet
TODO

### Réalisé avec
* [Java OpenJDK](https://openjdk.java.net/)
* [Maven](https://maven.apache.org/)
* [JMH](https://openjdk.java.net/projects/code-tools/jmh/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
* [Github](https://github.com/)

### Auteurs
Your Name - [@your_linkedin](https://linkedin.com/your_username) - email@example.com


<!-- GETTING STARTED -->
## Commmencer
Afin d'éxécuter le logiciel deux solutions s'offrent à vous :
- lancer un benchmark jmh
- lancer directement le projet dans votre IDE

### Prérequis
Le projet a été testé et réalisé avec Java 13 de AdoptJDK (adopt-openj9-13.0.2-1) et la version 3.6.3 de Maven.
L'IDE utilisé est IntelliJ et fournit la meilleure expérience out of the box mais le projet devrait fonctionner avec n'importe quel autre IDE.

### Installation
Afin de lancer un benchmark 
1. Clonez le repo
```sh
git clone https://github.com/kamilcglr/hpp_project_gr6.git
```
2. Installez les dépendances Maven
```sh
mvn install
```
3. Vous pouvez ensuite lancer un benchmark en éxécutant :
```sh
 java -jar target/benchmarks.jar 
 ```
 
Si vous souhaitez executer le projet dans votre IDE ou modifier le code source
1. Clonez le repo
```sh
git clone https://github.com/kamilcglr/hpp_project_gr6.git
```
2. Installez les dépendances Maven
```sh
mvn install
```
3. Lancez le main dans la classe main. Vous pouvez choisir la taille du fichier 20, 5000 ou 1000000 lignes.

<!-- Benchmarks -->
## Benchmark
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark) //
```
JMH a été configuré afin d'éxécuter 3 tours de chauffe (sans prise de mesures) puis 5 itérations durant lesquelles le temps d'éxécution moyen est mesuré. Cette opération est effectuée 10 fois pour chaque taille de fichier (20 et 5000).

En sortie, vous aurez alors le temps moyen (score) pour chaque taille avec l'erreur (error) en millisecondes.
```sh
Benchmark                                  (size)  Mode  Cnt    Score   Error  Units
BenchmarkCovidTracer.benchmarkCovidTracer      20  avgt   50    1,233 ± 0,022  ms/op
BenchmarkCovidTracer.benchmarkCovidTracer    5000  avgt   50    96,33 ± 5,888  ms/op
```

<!-- Tests -->
## Tests
La première étape de notre projet fût la conception de tests. En effet, afin de s'assurer que nos modifications dans le code n'apportaient pas d'erreur dans l'algorithme, nous avons effectué plusieurs tests vérifiant plusieurs cas. 

- Test 1 : Exemple du sujet.
  Fichiers d'entrée : 
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584540000, unknown, " "
  2, "Hervé", "Renoir", "11/03/1971", 1584712800, unknown, " "
  Italy
  3, "Valentina", "Rossi", "21/01/1963", 1584644400, 1, " "
  4, "Marco", "Guili", "06/01/1956", 1585324800, unknown, " "
  5, "Stella", "Capelli", "21/01/1949", 1587312000, 4, " "
  Spain
  6, "Ricardo", "Rodriguez", "03/10/1964", 1587052800, 4, " "
  
- Test 2 : Cas d'une personne faisant remonter une chaîne basse dans le classement. 
  Fichier d'entrée : 
  France 
  1, "Cerise", "Dupond", "21/01/1963", 1584540000, "unknown" (18/03/2020) 
  2, "Valentina", "Rossi", "21/01/1963", 1584579600, "1" (19/03/2020) 
  3, "Hervé", "Renoir", "11/03/1971", 1584712800, "unknown" (20/03/2020) 
  4, "Marco", "Guili", "06/01/1956", 1585324800, "unknown" (27/03/2020)
  7, "Jamel", "Debbouze", "01/01/1973", 1585357200, "1" (28/03/2020) 
  
  Fichier de sortie : 
  France, 1, 10;
  France, 1, 20;
  France, 1, 20; France, 3, 10;
  France, 4, 10; France, 1, 8; France, 3, 4;
  France, 1, 14; France, 4, 10; France, 1, 8;
  
- Test 3 : Cas de deux personnes avec la même date de contamination.
  Fichier d'entrée :  
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584540000, unknown, " "
  2, "Hervé", "Renoir", "11/03/1971", 1584540000, unknown, " "
  
  Fichier de sortie : 
  France, 1, 10;
  France, 1, 10; France, 2, 10;
 
- Test 4 : Plus de 4 chaînes en mémoire.
  Fichier d'entrée : 
  France
  1, "Valentina", "Rossi", "21/01/1963", 1584489600, unknown, " "
  2, "Cerise", "Dupond", "21/01/1963", 1584576000, 1, " "
  3, "Hervé", "Renoir", "11/03/1971", 1584662400, unknown, " "
  4, "El", "Profesor", "03/10/1975", 1584748800, unknown, " "
  5, "Marco", "Guili", "06/01/1956", 1585267200, 3, " "
  6, "Ricardo", "Rodriguez", "03/10/1964", 1585440000, 4, " "
  7, "Farid", "Ben", "03/10/1964", 1586390400, 5, " "
  8, "Ciro", "Barilla", "03/10/2001", 1586908800, 6, " "
  
  Fichier de sortie : 
  France, 1, 10;
  France, 1, 20;
  France, 1, 20; France, 3, 10;
  France, 1, 20; France, 3, 10; France, 4, 10;
  France, 3, 20; France, 4, 10; France, 1, 8;
  France, 3, 14; France, 4, 14; France, 1, 8;
  France, 3, 14; France, 4, 4;
  France, 3, 10; France, 8, 10;

- Test 5 : Cas avec un patient qui devient le nouveau root (son contaminateur est à 0).
  Fichier d'entrée :
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584547200, unknown, " "
  2, "Valentina", "Rossi", "21/01/1963", 1584633600, 1, " "
  3, "Ricardo", "Rodriguez", "03/10/1964", 1586102400, 1, " "
  
  Fichier de sortie : 
  France, 1, 10;
  France, 1, 20;
  France, 3, 10;
  
- Test 6 : Cas avec 3 patients dans 3 fichiers différents.
  Fichiers d'entrée : 
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584493200, unknown, " "
  Italy 
  2, "Valentina", "Rossi", "21/01/1963", 1584579600, unknown, " "
  Spain
  3, "Ricardo", "Rodriguez", "03/10/1964", 1584666000, unknown, " "
  
  Fichier de sortie :
  France, 1, 10;
  France, 1, 10; Italy, 2, 10;
  France, 1, 10; Italy, 2, 10; Spain, 3, 10;
  
- Test 7 : Cas d’égalité de score.
  Fichier d'entrée : 
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584493200, unknown, " "
  2, "Valentina", "Rossi", "21/01/1963", 1584579600, unknown, " "
  3, "Ricardo", "Rodriguez", "03/10/1964", 1585270800, unknown, " "
  
  Fichier de sortie :
  France, 1, 10;
  France, 1, 10; France, 2, 10;
  France, 3, 10; France, 1, 4; France, 2, 4;
  
- Test 8 : Cas de deux chaînes ayant la même racine.
   Fichier d'entrée : 
  France
  1, "Cerise", "Dupond", "21/01/1963", 1584540000, unknown, " "
  2, "Hervé", "Renoir", "11/03/1971", 1584558000, 1, " "
  3, "Simone", "Proust", "11/03/1975", 1584712800, 2, " "
  4, "Edouard", "Elric", "30/08/1987", 1584799200, 3, " "
  5, "Jhon", "Smith", "26/04/1982", 1584885600, 2, " "
  
  Fichier de sortie :
  France, 1, 10;
  France, 1, 20;
  France, 1, 30;
  France, 1, 40;
  France, 1, 40; France, 1, 30;
  
- Test 9 : Mise en évidence du score qui change en fonction des jours.
  Fichier d'entrée :
  France
  1, "Cerise", "Dupond", "21/01/1963", 1583071200, "unknown" (01/03/2020 à 14:00:00)
  2, "Hervé", "Renoir", "11/03/1971", 1583676000, "1" (08/03/2020 à 14:00:00 )
  3, "Simone", "Proust", "11/03/1975", 1583679600, "2" (08/03/2020 à 15:00:00 )
  4, "Edouard", "Elric", "30/08/1987", 1584280800, "3" (15/03/2020 à 14:00:00)
  5, "Jhon", "Smith", "26/04/1982", 1584284400, "4" (15/03/2020 à 15:00:00)
  6, "Vincent", "Szymanski", "26/04/1982", 1585501200, "4" (29/03/2020 à 17:00:00)
  
  Fichier de sortie :
  France, 1, 10;
  France, 1, 20;
  France, 1, 24;
  France, 1, 34;
  France, 1, 34;
  France, 6, 10;
  
- Test 10 : Cas avec 3 chaînes ayant la même racine.
  Fichier d'entrée :
  France :
  1, "Cerise", "Dupond", "21/01/1963", 1583071200, unknown, " "
  2, "Hervé", "Renoir", "11/03/1971", 1583071201, 1, " "
  3, "Simone", "Proust", "11/03/1975", 1583071202, 1, " "
  4, "Edouard", "Elric", "30/08/1987", 1583071203, 2, " "
  5, "Jhon", "Smith", "26/04/1982", 1583071204, 2, " "
  6, "Vincent", "Szymanski", "26/04/1982", 1583071205, 3, " "
  7, "Lisa", "Calero", "31/12/1998", 1583071206, 3, " "
  
  Fichier de sortie :
  France, 1, 10;
  France, 1, 20;
  France, 1, 20; France, 1, 20;
  France, 1, 30; France, 1, 20;
  France, 1, 30; France, 1, 30; France, 1, 20;
  France, 1, 30; France, 1, 30; France, 1, 30;
  France, 1, 30; France, 1, 30; France, 1, 30;

- Test 11 : Cas avec les fichiers de 5000 lignes de données en entrée. 

<!-- Intégration continue -->
### Intégration continue
Nous avons mis en place une pipeline qui vérifie qu'aucune régréssion n'a lieu sur notre branche principale. Nous avons utilisé Github Actions avec Maven. https://github.com/kamilcglr/hpp_project_gr6/actions
A chaque push sur dev ou master, on s'assure que :
- Build : le projet se compile 
- Test : les tests sont validés et une mesure de la couverture de code est réalisée
- Package : un .jar est créé.

<!-- Optmisisations -->
## Optimisations apportées
Une fois notre algorithme et notre code natif fonctionnant, nous avons apporté des modifications dans le but d'optimiser le code, notamment en temps. De ce fait, à chaque tentative d'optimisation, nous avons enregistré le temps d'exécution grâce au Benchmark en fonction du nombre de données en entrée puis nous l'avons comparé avec le temps d'exécution sur le code natif, comme vous pouvez le voir sous le tableau ci-dessous : 
![Table_part1](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/images/table1.png)
![Table_part2](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/images/table2.png)


Avec ces calculs, nous avons donc obtenus les graphiques suivants, mettant en évidence l'évolution du temps d'exécution en fonction du changement opéré : 
- Avec JMH :
![20_et_5k](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/img1_avecJMH.PNG)
![1M](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/images/img2_avecJMH.png)

- Sans JMH :
![20](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/images/img1_sansJMH.png)
![5k](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/images/img2_sansJMH.png)


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=flat-square
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=flat-square
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
