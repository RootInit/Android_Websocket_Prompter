<div align=center>
    <img src="logo.png" alt="Logo Image" width="30%"/>
    <h1>WS-Prompter</h1>
    <p>A simple Android application written in kotlin which starts a websocket server on localhost and both displays and reads aloud any text received.
</p>
</div>

# About
This is a simple application I developed for personal use as an extremelly niche use case teliprompter. It is intended for use with a separate websocket client script or application.
No guarantee of quality or function is implied. This Is my first attempt at making an android application.

# Usage
Dead simple application with one button. Tap it to start the web server, tap it again to stop it.
If TTS is unavailable the button text will warn you and it will default to display only mode.

Their is no way to disable TTS so if you do not want TTS mute your device (or PR the feature your self).

The websocket runs on port `2352` and can be accessed on localhost (or remotely if the port is unblocked for remote access) at `ws://<Device IP>:2352/` via a websocket client. For testing I used the browser addon "Weasel Websocket Cliet".

# Instalation
Supports devices Android 8.0 (Oreo) or higher.
You can probably re-compile for older devices.

## Instalation from APK
Download most recient release and install the APK
Android will warn you about an "untrusted application"

## Building from source
### Prerequisites
- Java Development Kit (JDK) 8 or newer
- Android Studio installed (if using the GUI method)
- Gradle Version 7.4 or newer (if building with gradle)

### Clone the Repository
Clone the repository to your local machine:
```
git clone https://github.com/RootInit/Android_Websocket_Prompter
```
### Building with Gradle
Navigate to the project directory:
```
cd Android_Websocket_Prompter
```
Run the following Gradle command to build the APK:
```
./gradlew build
./gradlew assembleRelease
```

### Building with Android Studio GUI
> Open Android Studio and select "Open an Existing Project". Navigate to the cloned repository directory and open it as a project.
> Click on the "Build" menu button > Build Bundle / APK(s) > Build APK(s)

### Output
Either build message should create the APK file in the following location: 
```Android_Websocket_Prompter/app/build/outputs/apk/release/app-release-unsigned.apk```
If it doesn't then... Sorry I can't help you.

