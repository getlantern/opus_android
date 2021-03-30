## NOTE: THIS IS A FORK OF THE DEPRECATED opus_android project
This fork removes a lot of functionality and keeps only the OpusRecorder functionality for recording
opus files. Since Android 5, Android natively supports playback of Opus anyway.

### Set Up Permissions
You'll need the android.Manifest.permission.RECORD_AUDIO permission. First, include it in your AndroidManifest.xml.

```
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

Also, you'll need to check for the permission at runtime, as newer Android versions require the user
to confirm this permission. For example:

```
public void onUiAction(View v) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        startRecording();
    } else if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 123);
    } else {
        startRecording();
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Don't have enough permissions", Toast.LENGTH_LONG).show();
    } else {
        startRecording();
    }
}
```

### How to record audio

To start:

```
OpusRecorder.OpusApplication application = OpusRecorder.OpusApplication.VOIP; // set this to AUDIO for something more optimized for music
int sampleRate = 16000; // record audio at 16Khz sample rate
int bitRate = 16000; // encode into Opus at approximately 16 Kbps
boolean stereo = false; // record mono
Runnable stopRecording = OpusRecorder.startRecording(fileName, application, sampleRate, bitRate, stereo);
...

To stop:

```
stopRecording.run()
```

**Well, you can stop reading if you don't need to modify the library code.** Your project should be working if you follow the steps above.

##Project Compilation

- pre-requisites

1.	JDK v1.8 or higher  
2.	SDK v2.2.1 or higher  
3.	NDK  r10d or higher (Note: remember to export NDK's path) 
4.	Android Studio (with SDK) 1.2.1 or higher  

- Summary of set up:

1.	Get the source code.[Git] (https://github.com/louisyonge/opus_android.git)  
2	remember to export NDK's path. Take Linux for example, add the following code to the end of the file "/etc/profile", and then reboot your system.
```
NDK_ROOT=/usr/local/lib/android-ndk-r9d
export PATH=$NDK_ROOT:$PATH
```

3.	Open it in Android Studio, and modify the path of SDK and NDK in the file "local.properties"
4.	Compile and run.  

- Testing Hints:

1. This demo use external storage to store audio file. Be sure your Android device has a SD card when testing this demo. You could also store them in internal storage by changing source code.
2. Recommend to use a real Android device instead of Android virtual device. . Some times AVD has no sound system supports.
3. When testing the master branch, firstly you need to copy at least one wav file and an opus file to the folder "OpusPlayer" under the root of SD card. Secondly, launch the demo. Then you can play or encode/decode these audio files.

## NDK Compilation
### Development Environment

SDK, NDK, Android Studio, Eclipse

Note: Android Studio 1.2 does not support the debug of Native code. So it is wise to develop the C&C++ code in Eclipse. For this project, codes under the folder "OpusPlayer\opuslib\src\main\jni" are Native codes. The rest of this project is Java codes, developed by Android Studio.(deprecated)

### How to debug Native code

* Android NDK compiler
1. cd "OpusPlayer\opuslib\src\main\jni".
2. Issure command "ndk-build".
5. Click build in Android Studio.
6. Watch logs in logcat.
* Linux
As Android Studio does not support JNI debugging(for version 1.2 and earlier), using Eclipse in Linux is a good way to debug Native codes. There is a valid Makefile under the JNI folder of this project. So you can ether import the JNI code to a Eclipse project, or just cd to the folder and issue compile command "make". When compiling for Linux, Comment out "#define ANDROID_V" in the file "\OpusPlayer\opuslib\src\main\jni\include\config.h", and uncomment it when compiling by for Android. This Macro is a switch to redirect std-stream to logcat, and vice versa. "Opus_demo.c" is the console demo of testing opuslib for Linux. Cygwin is not recommended, because you might encounter some strange compilation problem.


## Open Project Reference ###

1. Opus (git://git.opus-codec.org/opus.git)

2. Opus-tools (git://git.xiph.org/opus-tools.git)

3. Opusfile (git://git.xiph.org/opusfile.git)

##Licence

Project uses [Apache 2.0 License](LICENSE)


###Have fun!
