<h1 align="center"> LoadApp-File-Downloader </h1>

<p align="center">
    <a href="https://bumptech.github.io/glide/dev/open-source-licenses.html">
        <img alt="License Badge" src="https://img.shields.io/badge/License-Open--Source-LightBlue?style=plastic&logo=ReadMe&logoColor=6495ed&color=6495ed">
        </a></br>
    <a href="https://kotlinlang.org/docs/android-overview.html">
        <img alt="Kotlin Badge" src="https://img.shields.io/badge/Kotlin-100%25-LightPurple?style=plastic&logo=Kotlin&color=%238c53c6&link=https%3A%2F%2Fkotlinlang.org%2Fdocs%2Fandroid-overview.html"/>
        </a>
    <a href="https://developer.android.com/tools/releases/platforms">
        <img alt="API Badge" src="https://img.shields.io/badge/API-24_–_33-LightGreen?style=plastic&logo=Android&color=%2374d77c"/>
        </a></br>
    <a href="https://www.udacity.com/course/android-kotlin-developer-nanodegree--nd940">
        <img alt="Udacity Badge" src="https://img.shields.io/badge/Udacity-Android_Kotlin_Developer_Nanodegree-MediumPurple?style=plastic&logo=Udacity&logoColor=%236533cb&label=Udacity&color=%236533cb"/>
        </a></br>
    <a href="https://developer.android.com/studio/releases">
        <img alt="Android Studio Badge" src="https://img.shields.io/badge/Android_Studio_Giraffe-2022.3.1-Yellow?style=plastic&logo=Android%20Studio&color=%23ffff00"/>
        </a>
    <a href="https://gradle.org/releases/">
        <img alt="Gradle Badge" src="https://img.shields.io/badge/Gradle-7.5-Aqua?style=plastic&logo=Gradle&logoColor=%234DC9C0&color=%234DC9C0">
        </a>
</p>

<p align="center">
    <img src="https://github.com/SVENTRIPIKAL/LoadApp-File-Downloader/assets/90730468/153a0273-031a-4611-b5db-d93351f4a9a3" alt="App Logo"/>
</p>

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
directory, or the notification's `Action Button` to navigate to a Details screen  
animated by `MotionLayout` which provides the downloaded file's name, final  
download status, and a return to Main screen button.

## Inspiration
[***Project concept, starter code, & photo***](https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter) provided by [***Udacity: Android Kotlin Developer Nanodegree***](https://www.udacity.com/course/android-kotlin-developer-nanodegree--nd940) course

## Features
- `Permission Request Codes` managed by the OS system via `Androidx.Activity` library
    - > **IMPORTANT**  
      > Permissions need to be granted in order for the app to run as described
- `Intent Activities` driven by user decisions within Dialogs & Notifications
- `API` level checks which differentiate application functionality & permission dialogs
    - `Android 8.0 (Level 26) & Above` - `POST_NOTIFICATIONS` permission handling
    - `Former Releases` - `WRITE_EXTERNAL_STORAGE` permission handling
- A `Material 3 Design` Component, `Radio Buttons`, allowing users to select a file
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
- A `Custom Button` extending the View class which provides a personalized style & animation
- Custom view `attributes` provided for `value animator`, `listener`, & a `.xml` animation file
- An overridden `onDraw` method & `Painter` object which positions & paints objects on the `Canvas`

## Installation
1. `Fork` the `Repository` & `Clone` it to your `Local Computer`
2. `Open` the `Project` in `Android Studio` ***(current version recommended)***
3. `Launch` the `App` on any `Android Emulator` or `Physcial Device` running `API Level 24+`
4. `Enjoy` & feel free to `Submit` a `Pull Request` if you happen across a `🐛`
5. `🌟` the `Repository` if you found this project helpful & `Follow` me for a follow back `🤝`

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
`LoadApp` utilizes the following `libraries` to function properly. A brief `description` defining their usage is provided, but each library is `hyperlinked` to their specific `documentation` page for more comprehensive inquiries.

| **Library** | **Description** |
| ------ | ------ |
| [**Androidx Activity**](https://developer.android.com/jetpack/androidx/releases/activity) | `allows the OS system to manage permission request codes` |
| [**Androidx Core-KTX**](https://developer.android.com/kotlin/ktx#core) | `provides Kotlin support for Android Framework & Support Libraries` |
| [**Androidx AppCompat**](https://developer.android.com/jetpack/androidx/releases/appcompat) | `provides backwards compatibility for older API levels` |
| [**Androidx Material Design**](https://developer.android.com/develop/ui/views/theming/look-and-feel) | `provides visual, motion, and interaction design across platforms and devices` |
| [**Androidx ConstraintLayout**](https://developer.android.com/develop/ui/views/layout/constraint-layout) | `provides a flat view hierarchy when creating large, complex layouts` |
| [**Androidx MotionLayout**](https://developer.android.com/develop/ui/views/animations/motionlayout) | `a subclass of ConstraintLayout that helps manage motion & widget animations` |
| [**Androidx JUnit**](https://developer.android.com/training/testing/local-tests) | `provides local support for the JUnit testing framework` |
| [**Androidx Espresso**](https://developer.android.com/training/testing/local-tests) | `provides support for UI unit testing` |

## License
Please review the following [license agreement](https://bumptech.github.io/glide/dev/open-source-licenses.html)
