WebDriver Helper
================

WebDriverHelper is a wrapper class around the WebDriver object to help support methods like Explicit waits in Selenium / WebDriver.

It also provides wrapper methods to simplify Predicate and FluentWaits / ExpectedConditions which helps ease in testing asynchronous scenarios in dynamic web pages.

Apart from that it also contains other useful methods, such as Window switching, Javascript scrolling (To fix issues with Chrome testing), and the like.

Pre-requisites
==============

Maven is required to pull the dependencies from Selenium

Java, of course

A web browser such as Firefox, Chrome or Internet Explorer. (Preferably Firefox)

Examples
========

Unfortunately I haven't had the chance to put in a concrete example yet, but as for now you can peruse the simple JUnit test I have written (called SimpleTest)

More to come
============

A proper Page Object (Along with PageFactory, WebElement annotations) model will soon follow once I get a sample site up and running.

Also more useful methods such as HTML table data comparisons, to be used with BDD tools like Cucumber-JVM or JBehave, or just for normal assertion purposes.



ImageMagickCompareUtil
======================

ImageMagickCompareUtil is an attempt to do image based testing on a website to ensure that no changes has occurred to the layout of the website. At present the implementation is very naive and flawed, but it is a starting point.

It will NOT work on web pages with dynamic content / images.

Pre-requisites
==============

You must have ImageMagick installed on your machine.

http://www.imagemagick.org for more details

Usage
=====

Modify ImageMagickCompareUtil.java's PATH_TO_IM_BINARY variable and point it to your ImageMagick's binary path as appropriate.

By default the screenshots are saved at a folder called screenshot in the root folder of this project.

You can run ScreenshotTest which opens Facebook's main page and help page which will then capture the screenshots and save them at screenshot/actual folder.

Create a screenshot/expected folder and copy the files over as your first expected screenshots. Run ScreenshotTest again, which will overwrite your previous screenshot.

You can then run ImageMagickCompareUtil's compareAndCaptureResults() by invoking

```
new ImageMagickCompareUtil().compareAndCaptureResults();
```

The results are saved in the screenshot folder to a file called results.csv