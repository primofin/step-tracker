# Step Tracker
Step tracker is built for everyone who keeps moving all the day and wants to track their own step

## Screenshot
![alt text](https://github.com/vynmetropolia/step-tracker/blob/master/screenshots/1.png "Screenshot 1")   ![alt text](https://raw.githubusercontent.com/vynmetropolia/step-tracker/master/screenshots/2.png "Screenshot 2")
![alt text](https://github.com/vynmetropolia/step-tracker/blob/master/screenshots/3.png "Screenshot 3")   ![alt text](https://raw.githubusercontent.com/vynmetropolia/step-tracker/master/screenshots/4.png "Screenshot 4")

## Install

### Run Only
* Download the APK file in order to install [here](https://github.com/vynmetropolia/step-tracker/raw/master/app-debug.apk)
### Developer Mode
* Clone the project
* Create a Firebase Project [here](https://console.firebase.google.com/). Then you can follow the instruction to add Firebase to the application.
* Create a realtime database by following this [instruction](https://firebase.google.com/docs/database/android/start)
* Configure Google API and get your backend server's client ID by following this [instruction](https://developers.google.com/identity/sign-in/android/start-integrating)
* Replace your web server client ID in this [file](https://github.com/vynmetropolia/step-tracker/blob/master/app/src/main/java/com/example/steptracker/fragments/ProfileFragment.kt#L99)
> Remember to Create OAuth client ID both for Web Sever and Android Application. Then replace the tokenid by using the __Web Server Cliend ID__ to avoid error code 10

* Build the application

## Features
* Tracking daily steps
* Calculate the BMI for tracking personal health and give the result interpretation
* Running app in foreground
* Save data online through Google Firebase
* Sync data among phones in realtime
* Do logging by using Google Account
* Visualizing user’s steps in the recent days by graph

## Concepts implemented: 
* Phone’s internal sensor(s) : **TYPE_STEP_COUNTER**
* Fragments, Activities
* Persistence : Interal Storage
* Connection to Google Services, Firebase Realtime Database
* Services: Foreground service, Background service.
* Have AR related functionality
* Map
* GPS
## Authors
1. [Vy Nguyen](https://github.com/vynmetropolia)
2. [Nhan Mai](https://github.com/RenMai)
