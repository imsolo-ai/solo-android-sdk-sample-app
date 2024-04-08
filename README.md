# SOLO AI - SDK - Android Library
Empower Your Android App with Emotional AI: Introducing SOLO AI - Android Sample App

Discover the transformative potential of Emotional AI in your Android app with our SOLO Android Sample App.
This Android Sample App showcases the seamless integration of our cutting-edge Emotional AI technology into your mobile applications, unlocking new dimensions of user engagement, personalization, and insight.
With SOLO SDK, developers can effortlessly implement advanced emotion recognition, facial micro-expression analysis, and sentiment analysis features into their Android apps.
For more details: www.imsolo.ai 

# Solo SDK Integration Guide

This guide outlines the steps required to integrate the Solo SDK into your Android application using Kotlin DSL. Follow the steps below to ensure a successful integration.

## Installation

### 1. Configure Your Project Repositories

Edit your project's `settings.gradle.kts` file to include the Solo SDK repository as shown below:

```kotlin
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
       google()
       mavenCentral()
       maven {
           url = uri("https://imsolo.jfrog.io/artifactory/android/")
       }
   }
}
```

### 2. Add the SDK Dependency

In your application module (commonly `app`), update the `build.gradle.kts` file to add the Solo SDK as a dependency:

```kotlin
dependencies {
    implementation("imsolo:sdk:1.0.9")
}
```

## Configure API Key and App ID

Add your API Key and App ID to your `gradle.properties` file:

```
API_KEY="YOUR_API_KEY"
APP_ID="YOUR_APP_ID"
```

Then, in your app module's `build.gradle.kts` file, inside the `defaultConfig` block, add:

```kotlin
defaultConfig {
    buildConfigField("String", "API_KEY", project.properties["API_KEY"].toString())
    buildConfigField("String", "APP_ID", project.properties["APP_ID"].toString())
}
```

## Initialize the Fragment

### In an Activity

```kotlin
val soloFragment = solo.getMonitoringFragmentInstance()
supportFragmentManager.beginTransaction()
   .replace(R.id.container, soloFragment)
   .commitNow()
```

### In a Fragment

```kotlin
val soloFragment = solo.getMonitoringFragmentInstance()
childFragmentManager.beginTransaction()
   .replace(R.id.solo_container, soloFragment)
   .commitNow()
```

## Set Up Callbacks

```kotlin
Solo.setMonitoringListener(
   fragmentManager: FragmentManager,
   lifecycleOwner: LifecycleOwner,
   monitoringListener: (MonitoringTracker) -> Unit
)
```
