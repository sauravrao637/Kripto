<h2 align="center"><img src="assets/kripto.svg"></h2>
<h2 align="center"><b>Kripto</b></h2>
<h4 align="center">A Cryptocurrency app for Android.</h4>
<p align="center"><a href="#Demonstration">Demonstration</a> &bull; <a href="#description">Description</a> &bull; <a href="#features">Features</a> &bull; <a href="#contribution">Contribution</a> &bull;<a href="#license">License</a></p>

<b>WARNING: THIS IS STILL IN DEVELOPMENT, THEREFORE YOU MAY ENCOUNTER BUGS. IF YOU DO, OPEN AN ISSUE VIA OUR GITHUB REPOSITORY.</b>

## Demonstration
[Demo](https://drive.google.com/file/d/1BkjizhdbdTkmFn1qLyYkrG9UJeunqDkd/view?usp=sharing)


## Description

Kripto is fully powered by the <a href = "https://www.coingecko.com/api/">Coingecko's API</a> service.

Thanks to 
<a href = "https://www.coingecko.com/en"><img src = "https://static.coingecko.com/s/coingecko-logo-d13d6bcceddbb003f146b33c2f7e8193d72b93bb343d38e392897c3df3e78bdd.png"></a>


### Features
* View current price, market cap,price char and much more for more than 7000 coins
* Track more than 400 exchanges and their "Trust Score" as per Coingecko
* Favourite a coin
* Track global Cryptocurrency and Defi Market
* Choose your preferred currency and much more in preferences screen!!
* More to be added :)

### Libraries and Dependencies
* <a href= "https://square.github.io/retrofit/">Retrofit</a> for network requests
* <a href="https://github.com/google/gson">Gson</a> for parsing JSON data
* <a href="https://square.github.io/okhttp/">OkHttp</a>
* <a href="https://github.com/bumptech/glide">Glide</a>
* <a href="https://github.com/PhilJay/MPAndroidChart">MPAndroidChart</a> for graphing
* <a href ="https://heroicons.com/">Heroicons</a> for icons 
* <a href="https://github.com/googlecodelabs/android-hilt">Hilt</a> for DI
* <a href="https://github.com/JakeWharton/timber">Timber</a> for logging
* <a href="https://github.com/pkjvit/Android-Multi-Theme-UI">Multi Theme UI</a>

### Development Setup

Before you begin, you should already have the Android Studio SDK downloaded and set up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

### Setting up the Android Project
For setting up the PSLab Android project you may follow any of the two methods listed below, that is, you may download the repository zip file or you may directly clone the repository to Android Studio.

#### By downloading the zip file

1. Download the _kripto_ project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select _Import Project (Eclipse ADT, Gradle, etc.)

#### By direct cloning


1. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select "check out project from version control".

2. Select git from the drop down menu that appeared.

3. Go to the repository and click clone or download button.

4. From the dropdown that appeared, copy the link.

5. Paste the URL that you copied and press clone.

6. Android studio should now begin building the project with gradle.

7. Once this process is complete and Android Studio opens, check the Console for any build errors.

 - _Note:_ If you receive a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if available) that says _Install missing platform(s) and sync project_ and allow Android studio to fetch you what is missing.

8. Once all build errors have been resolved, you should be all set to build the app and test it.

9. To Build the app, go to _Build>Make Project_ (or alternatively press the Make Project icon in the toolbar).

10.  If the app was built successfully, you can test it by running it on either a real device or an emulated one by going to _Run>Run 'app'_ or pressing the Run icon in the toolbar.

If you want build apk only, go to Build>Build apk and apk would be build and directory where apk is generated would be prompted by Android Studio.

### Permissions Required
1. Internet Access : It is required for the app to load data from web.
2. Read/Write Storage : It is required for feature :- Favourite a coin
## Contribution
Got some ideas or suggestions? feel free to contribute :)
The more is done the better it gets!
Checkout [contribution notes](.github/CONTRIBUTING.md) !!


## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)  

Kripto is Free Software: You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation.
