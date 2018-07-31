# BarraCoda
An app that allows you to scan products’ barcodes and gives you information about them.
It also allows you to mark your favorite stores on the map so you know where to go to buy
the cheapest and best quality products.

## IMPORTANT
To be able to run this app you need an **API key** for Google Maps. To create your key go to
 [this page](https://developers.google.com/maps/documentation/embed/get-api-key#detailed-guide) 
 and follow the instructions in the **Detailed Guide to getting an API key** section. 
 After you create your key open your project's `gradle.properties` file and add the following line:

 `MapsApiKey = {YOUR_KEY}`

## PROJECT SPECIFICATION
### Common Project Requirements
 - [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines
 - [x] App is written solely in the Java Programming Language
 - [x] App utilizes stable release versions of all libraries, Gradle, and Android Studio.

### Core Platform Development
 - [x] App integrates a third-party library.
 - [x] App validates all input from servers and users. If data does not exist or is in the wrong format,
  the app logs this fact and does not crash.
 - [ ] App includes support for accessibility. That includes content descriptions, navigation using a D-pad,
  and, if applicable, non-audio versions of audio cues.
 - [x] App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts. 
 - [ ] App provides a widget to provide relevant information to the user on the home screen.
 
### Google Play Services
 - [x] App integrates two or more Google services. Google service integrations can be a part of 
 Google Play Services or Firebase.
 - [x] Each service imported in the build.gradle is used in the app.
 - [x] If Location is used, the app customizes the user’s experience by using the device's location.
 - [x] If Admob is used, the app displays test ads. If Admob was not used, student meets specifications.
 - [x] If Analytics is used, the app creates only one analytics instance. If Analytics was not used, 
 student meets specifications.
 - [x] If Maps is used, the map provides relevant information to the user. If Maps was not used, 
 student meets specifications.
 - [x] If Identity is used, the user’s identity influences some portion of the app. If Identity was 
 not used, student meets specifications.
 
### Material Design
 - [x] App theme extends AppCompat.
 - [x] App uses an app bar and associated toolbars.
 - [ ] App uses standard and simple transitions between activities.
 
### Building
 - [x] App builds from a clean repository checkout with no additional configuration.
 - [ ] App builds and deploys using the installRelease Gradle task.
 - [ ] App is equipped with a signing configuration, and the keystore and passwords are included in 
 the repository. Keystore is referred to by a relative path.
 - [x] All app dependencies are managed by Gradle.

### Data Persistence
 - [x] App stores data locally either by implementing a ContentProvider OR using Firebase Realtime 
 Database OR using Room. No third party frameworks nor Persistence Libraries may be used.
 - [ ] Must implement at least one of the three
       If it regularly pulls or sends data to/from a web service or API, app updates data in its 
       cache at regular intervals using a SyncAdapter or JobDispacter.
       OR
       If it needs to pull or send data to/from a web service or API only once, or on a per 
       request basis (such as a search application), app uses an IntentService to do so.
       OR
       It it performs short duration, on-demand requests(such as search), app uses an AsyncTask.
 - [x] If Content provider is used, the app uses a Loader to move its data to its views.
 - [x] If Room is used then LiveData and ViewModel are used when required and no unnecessary calls 
 to the database are made.
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 