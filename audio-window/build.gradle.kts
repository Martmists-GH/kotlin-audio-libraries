kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":audio-frame"))
                api(project(":audio-math-ext"))
            }
        }
    }
}
