WebDriverHelper
===============

WebDriverHelper is a wrapper class around the WebDriver object to help support methods like Explicit waits in Selenium / WebDriver.

It also provides wrapper methods to simplify Predicate and FluentWaits / ExpectedConditions which helps ease in testing asynchronous scenarios in dynamic web pages, which is arguably the trickiest problem in automated testing.

Apart from that it also contains other useful methods, such as Window switching, Javascript scrolling (To fix issues with Chrome testing), and the like.

CompareUtil
===========

CompareUtil is a class that allows you to compare tabular data used in popular BDD tools such as Cucumber-JVM and JBehave. The class will compare List of Maps for the expected and actual results.

Pre-requisites
==============

Maven is required to pull the dependencies

Java 1.6

A web browser such as Firefox, Chrome or Internet Explorer. (Preferably Firefox as you don't have to mess around with drivers and such)

Examples
========

Unfortunately I haven't had the chance to put in a concrete example yet, but as for now you can peruse the simple JUnit test I have written (called SimpleTest)

Alternatively you may also run the BDD version of the test by running CucumberTest (this is an example running with Cucumber-JVM)
