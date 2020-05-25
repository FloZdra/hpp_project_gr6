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
## Sommaire

* [A propos](#à-propos-du-projet)
  * [Réalisé avec](#réalisé-avec)
  * [Auteurs](#auteurs)
* [Commencer](#commencer)
  * [Prérequis](#prérequis)
  * [Installation](#installation)
* [Benchmark](#benchmark)
* [Tests](#tests)
  * [Intégration continue](#intégration-continue)
* [Optimisations apportées](#optimisations-apportées)

<!-- ABOUT THE PROJECT -->
## À propos du projet
Ce projet a été réalisé dans le cadre de l'option programmation haute performance.
Le but est de trouver les trois plus grandes chaines de contaminations dans des fichiers csv.
La taille des échantillons varie de 20 à 1 000 0000. L'objectif est donc d'avoir un programme performant en traitant
les données d'une manière optimale et rapide.

### Réalisé avec
* [Java OpenJDK](https://openjdk.java.net/)
* [Maven](https://maven.apache.org/)
* [JMH](https://openjdk.java.net/projects/code-tools/jmh/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
* [Github](https://github.com/)

### Auteurs
Kamil CAGLAR - kamil.caglar@telecom-st-etienne.fr  
Lisa CALERO - lisa.calero@telecom-st-etienne.fr  
Florian ZDRADA - florian.zdrada@telecom-st-etienne.fr  


<!-- GETTING STARTED -->
## Commmencer
Afin d'éxécuter l'application deux solutions s'offrent à vous :
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
La première étape de notre projet fût la conception de tests d'intégrations et de tests unitaires. 
Cela nous a permis de ne pas avoir de régressions durant la phase d'optimisations. 
Nous avons effectué plusieurs tests vérifiant plusieurs cas critiques : 

- [Test 1](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test1): Exemple du sujet.

- [Test 2](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test2): Cas d'une personne faisant remonter une chaîne basse dans le classement. 

- [Test 3](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test3): Cas de deux personnes avec la même date de contamination.
 
- [Test 4](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test4): Plus de 4 chaînes en mémoire.

- [Test 5](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test5): Cas avec un patient qui devient le nouveau root (son contaminateur est à 0).
  
- [Test 6](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test6): Cas avec 3 patients dans 3 fichiers différents.

- [Test 7](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test7): Cas d’égalité de score.
  
- [Test 8](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test8): Cas de deux chaînes ayant la même racine.
  
- [Test 9](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test9): Mise en évidence du score qui change en fonction des jours.
  
- [Test 10](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test10): Cas avec 3 chaînes ayant la même racine.

- [Test 11](https://github.com/kamilcglr/hpp_project_gr6/tree/dev/src/main/resources/input_test/test11): Cas avec les fichiers de 5000 lignes de données en entrée. 

<!-- Intégration continue -->
### Intégration continue
Nous avons mis en place une pipeline qui vérifie qu'aucune régression n'a lieu sur notre branche principale. 
Nous avons utilisé Github Actions avec Maven. https://github.com/kamilcglr/hpp_project_gr6/actions
A chaque push sur dev ou master, on s'assure que :
- Build : le projet se compile 
- Test : les tests sont validés et une mesure de la couverture de code est réalisée
- Package : un .jar est créé.

<!-- Optimisations -->
## Optimisations apportées
Une fois notre algorithme et notre code natif fonctionnant, nous avons apporté des modifications dans le but d'optimiser le code, notamment en temps. 
De ce fait, à chaque tentative d'optimisation, nous avons enregistré le temps d'exécution grâce au benchmark jmh selon différentes tailles de fichiers.
Puis nous l'avons comparé avec le temps d'exécution de la précédente optimisation.
![Results_table](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/results_table.png)

Sur ce second tableau, on peut voir que globalement, entre le premier algorithme naif et la dernière optimisation le temps
d'éxécution a été divisé par 10 (pour 5000). 
![Results_table_global](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/results_table_global.png)

Avec ces résultats, nous avons donc obtenus les graphiques suivants, mettant en évidence l'évolution du temps d'exécution en fonction du changement opéré : 
- Avec JMH :
![20](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/20.PNG)
Pour 20, l'optimisation ne peut pas être mesurée du fait de la taille très petite de l'échantillon.

![5k](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/5K.PNG)
![20_5Kk](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/20_5K.PNG)

- Sans JMH (trop long) :
Nous n'avons pas effectué de mesure avec l'algorithme naif pour 1 Million, le temps d'éxécution était beaucoup trop long (en jours).
![1M](https://github.com/kamilcglr/hpp_project_gr6/blob/dev/images/1M.PNG)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=flat-square
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=flat-square
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
