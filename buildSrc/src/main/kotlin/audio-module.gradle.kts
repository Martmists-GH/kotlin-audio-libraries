plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        binaries.library()
        browser()
        nodejs()
    }
    // TODO: Android?
    val natives = listOfNotNull(
        linuxX64(),
        linuxArm64(),
        macosX64(),
        macosArm64(),
        mingwX64(),
        iosArm64(),
        iosX64(),
        // TODO: More targets?
    )

    sourceSets {
        val commonMain by getting
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        natives.onEach {
            val target = getByName(it.name + "Main")
            target.dependsOn(nativeMain)
        }
    }

    natives.onEach {
        it.compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xallocator=custom")
            }
        }
    }
}
