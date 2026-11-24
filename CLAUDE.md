# Tallyho

Android team scorekeeper (Kotlin, Jetpack Compose). See README.md.

- **Verify:** `.\gradlew.bat assembleDebug` must succeed. There are no automated tests yet.
- **Version:** bump `versionCode` and `versionName` in `app/build.gradle.kts` for every APK the user will install. Current: 1.0.3 (4).
- **State:** `GameStore.kt` holds all state and writes it to SharedPreferences on every change. Scores are Int half-points.
- **Build output** goes to `%LOCALAPPDATA%\TallyhoBuild` (set in the root `build.gradle.kts`).
