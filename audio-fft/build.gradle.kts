kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":audio-complex"))
                api(project(":audio-frame"))
            }
        }
    }
}
