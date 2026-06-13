plugins {
    `kotlin-dsl`
}

dependencies {
    // makes the generated `libs` accessors usable inside precompiled script plugins
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.mavenPublish)
}
