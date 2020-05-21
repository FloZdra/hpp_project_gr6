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

  <h3 align="center">High Performance Computing Group 6</h3>

  <p align="center">
    Projet de programmation haute performance 2019 - 2020.
  </p>
</p>


<!-- TABLE OF CONTENTS -->
## Table of Contents

* [A propos](#a-propos-du-projet)
  * [Réalisé avec](#realise-avec)
* [Commmencer](#getting-started)
  * [Prerequis](#prerequisites)
  * [Installation](#installation)
* [Benchmark](#benchmark)
* [Tests](#tests)
* [Optimisations](#optimisation)

<!-- ABOUT THE PROJECT -->
## A propos du projet
TODO

### Réalisé avec
* [Java OpenJDK](https://openjdk.java.net/)
* [Maven](https://maven.apache.org/)
* [JMH](https://openjdk.java.net/projects/code-tools/jmh/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
* [Github](https://github.com/)

<!-- GETTING STARTED -->
## Commmencer

Afin d'éxécuter le logiciel deux solutions s'offrent à vous :
- lancer un benchmark jmh
- lancer directement le projet dans votre IDE

### Prérequis

Le projet a été testé et réalisé avec Java 13 de AdoptJDK (adopt-openj9-13.0.2-1) et la version 3.6.3 de Maven.
L'IDE utilisé est IntelliJ et fournit la meilleur expérience out of the box mais le projet devrait fonctionner avec n'importe quel autre IDE.

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
3. Lancez le main dans la classe main. Vous pouvez choisir la taille du fichier 20, 5000 ou 1000000

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
BenchmarkCovidTracer.benchmarkCovidTracer      20  avgt   50    0,312 ± 0,017  ms/op
BenchmarkCovidTracer.benchmarkCovidTracer    5000  avgt   50  193,806 ± 6,008  ms/op
```

<!-- Tests -->
## Tests

<!-- Optmisisations -->
## Optmisisations

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=flat-square
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=flat-square
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
