# LoadApp

This app downloads a file from the internet by clicking on a custom-built button where:
 - width of the button gets animated from left to right;
 - text gets changed based on different states of the button;
 - a circle is animated from 0 to 360 degrees.

A notification will be sent once the download is complete. When the user clicks the notification, the user lands on a detail activity and the notification is dismissed. In the detail activity, the status of the download and a transition animation via MotionLayout will be displayed upon navigating to the activity.

### Dependencies

```
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.0.2'
    implementation 'androidx.core:core-ktx:1.0.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.1.1'
```

## Built With

* [Android Studio](https://developer.android.com/studio) - Default IDE used to build android apps
* [Kotlin](https://kotlinlang.org/) - Default language used to build this project

## License
Please review the following [license agreement](https://bumptech.github.io/glide/dev/open-source-licenses.html)
