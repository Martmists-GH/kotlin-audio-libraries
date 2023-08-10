group = "com.martmists.audio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    group = rootProject.group
    version = rootProject.version
    buildDir = rootProject.buildDir.resolve(name)

    apply(plugin="audio-module")
}
