kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":audio-sample"))
            }
        }
    }
}
