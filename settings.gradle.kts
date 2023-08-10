rootProject.name = "audio-libs"

include(
    rootProject.projectDir.list()!!.filter { it.startsWith("audio-") }.map { ":$it" }
)

include(":examples")
