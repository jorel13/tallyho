plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

// Keep build output out of OneDrive so hundreds of MB of intermediates don't sync.
val localAppData: String? = System.getenv("LOCALAPPDATA")
if (localAppData != null) {
    allprojects {
        layout.buildDirectory.set(file("$localAppData/TallyhoBuild/${project.name}"))
    }
}
