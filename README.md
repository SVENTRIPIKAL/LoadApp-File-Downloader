# LoadApp-File-Downloader

![LICENSE](https://img.shields.io/badge/License-MIT-blue?style=plastic&logo=License&color=%236495ed)  
![KOTLIN](https://img.shields.io/badge/Kotlin-100%25-Pink?style=plastic&logo=Kotlin&color=%238c53c6)
![API](https://img.shields.io/badge/API-24%2B-Green?style=plastic&logo=API&color=%2379ff2f)  
![ANDROID STUDIO](https://img.shields.io/badge/Android%20Studio%20Giraffe-2022.3.1-yellow?style=plastic&logo=Android%20Studio&color=%23ffff00)

## Table of Contents
- [Description](#description)
- [Inspiration](#inspiration)
- [Features](#features)
- [Installation](#installation)
- [TakeAways](#takeaways)
- [Dependencies](#dependencies)
- [License](#license)

## Description
This application downloads a file from the internet using `Download Manager`,  
saves the file to the user's `External Storage Public Directory`, and sends  
the user a `Notification` after completion. The download button provided on the  
Main screen is a `Custom Button` extending the `View` class which animates both  
horizontally and in a center-filled arc while the file downloads. Once completed,  
users can click on the notification content to navigate directly to their `Downloads`  
directory, or the notification's `Action Button` to navigate to a Details screen with  
animated views via `MotionLayout` that provides the downloaded file's name, final  
download status, and a return to Main screen button.

## Inspiration
Basic concept & photos provided by ***Udacity's Android Kotlin Developer Nanodegree*** course

## Features
- `Permission Request Codes` managed by the OS system via `Androidx.Activity` library
    - > **IMPORTANT**  
      > Permissions need to be granted in order for the app to run as intended
- `Intent Activities` driven by user decisions within Dialogs & Notifications
- `API` level checks which differentiate application functionality & permission dialogs
    - `Android 8.0 (Level 26) & Above` - `POST_NOTIFICATIONS` permission handling
    - `Former Releases` - `WRITE_EXTERNAL_STORAGE` permission handling
- A `Material 3 Design` Component, `Radio Buttons`, allowing users to select pre-linked files
    - An `Edit Text View` dialog that allows users to enter their own download `URL` 
- `Multi-threading` to allow `UI` animations while concurrently running background `IO` tasks:
    1. Tries to create `/LOADAPP` directory in `/storage/emulated/0/Download`
        - `Success` - saves downloaded files to `/LOADAPP`
        - `Fail` - saves downloaded files to `/storage/emulated/0/Download`
    2. Opens a `HttpURLConnection` to retrieve a `Response Code` from any provided URL
        - `200 OK` - queues the request & downloads the file via Download Manager
        - `Other Responses` - displays the response code to the user via `Toast` message
    3. Handles anticipated `Exceptions` by displaying a `Toast` message to the user
        - `UnknownHostException` - "Unknown Host...Address Does Not Exist"
        - `MalformedURLException` - "Malformed URL...No DNS Protocol Exists"
        - `Other Exceptions` - Prints exception & message to console for future handling
- A `Custom Button` extending the View class in order to provide a custom style & animation
- Custom view `attributes` provided to `value animator`, `listener`, & a `.xml` animation
- An overridden `onDraw` method & `Painter` object to position & paint objects on the `Canvas`

## Installation
1. Fork the repository & clone it to your local machine
2. Open the project in Android Studio - ***latest version is recommended***
3. Launch the app on any emulator or physcial device running API Level 24+
4. Enjoy & feel free to message me if you happen across any bugs
5. :star: the repository if you found this project helpful & follow me for a follow back

## TakeAways
1. How to handle multiple API levels, exceptions, & anticipated user actions
2. How to handle & override the back button with the onBackPressedDispatcher
3. How to multi-thread & keep the UI thread unblocked by long-running IO tasks
4. How to extend the View class to customize view attributes & animations
5. How to paint on the Custom Canvas & allocate empty spaces for future painting
6. How to create, send, & apply Intents & Pending Intents to Notification Builder
7. How to create Notification Channels for API Levels 26 & Above
8. How to animate Layout Views & create transitional sequences with MotionLayout
9. How to use Androidx.Activity to allow the OS to request user permissions for you
10. How to request user permissions manually via an Alert Dialog & Intent
11. How to send information to other Activities as an Extra within an Intent
12. How to create a new directory in External Public Directory & save data to it
13. How to request & process HTTP Status Codes from URLs
14. How to queue a request for download with Download Manager
15. How to use the Broadcast Receiver to track & process Download Manager statuses
16. How to build & send notifications with Notification Manager, & apply Actions & Flags

## Dependencies
```
    // androidx.activity [Allow system to manage permission request codes]
    def activityVersion = "1.7.2"
    implementation "androidx.activity:activity-ktx:$activityVersion"

    //noinspection GradleDependency
    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

## License
```
MIT License

Copyright (c) 2023 SVENTRIPIKAL (Jose Garza)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
