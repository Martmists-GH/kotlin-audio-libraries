kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":audio-fft"))
                implementation(project(":audio-filter"))
                implementation(project(":audio-window"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.lets-plot:lets-plot-batik:3.2.0")
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.4.1")
            }
        }
    }
}
