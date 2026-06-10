plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.reflect)

    // javax.inject:1 is the JSR-330 standard for Dependency Injection in Java.
    // We use 'compileOnly' to provide access to @Inject annotations in UseCases
    // while keeping the Domain module lightweight and decoupled from any
    // specific DI framework implementation (like Hilt or Dagger).
    compileOnly("javax.inject:javax.inject:1")
}
