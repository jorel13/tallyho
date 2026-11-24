# Tallyho

A simple scorekeeper for team games on Android.

- **Set up** any number of teams (blank names get a default).
- **Score** with big buttons. Scores support half points and are stored as whole half-points, so there is no rounding drift.
- **Reset** or **End** a game with a confirmation. Results show the winner, then go straight to setting up the next game.
- The game is saved on every change, so it survives rotating the phone or closing the app. No permissions needed.

Built with Kotlin and Jetpack Compose, with light and dark themes. Android 8.0 (API 26) or newer.

## Install on a phone
1. Copy `Tallyho.apk` (or a fresh debug build, below) to the phone.
2. Open it and allow **Install unknown apps** when asked. Installing over an older version keeps your data.

## Build from source
Needs JDK 17 and the Android SDK.

```powershell
.\gradlew.bat assembleDebug
```

Build output goes to `%LOCALAPPDATA%\TallyhoBuild\` instead of `app\build\`, so the debug APK is at `%LOCALAPPDATA%\TallyhoBuild\app\outputs\apk\debug\app-debug.apk`. (This kept build files out of OneDrive. It's set in the root `build.gradle.kts` and can be removed now that the repo lives in `C:\dev`.)

## License
MIT. See `LICENSE`.
