kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":audio-buffer"))
                api(project(":audio-complex"))
                api(project(":audio-frame"))
            }
        }
    }
}
