# SOLO AI - SDK - Android Library
Empower Your Android App with Emotional AI: Introducing SOLO AI - Android Sample App

Discover the transformative potential of Emotional AI in your Android app with our SOLO Android Sample App.
This Android Sample App showcases the seamless integration of our cutting-edge Emotional AI technology into your mobile applications, unlocking new dimensions of user engagement, personalization, and insight.
With SOLO SDK, developers can effortlessly implement advanced emotion recognition, facial micro-expression analysis, and sentiment analysis features into their Android apps.
For more details: www.imsolo.ai 

## Get library

Add repository to your root level `gradle.build` file

```gradle
repositories {
    google()
    mavenCentral()
    ...
    maven {
        url 'https://imsolo.jfrog.io/artifactory/android/'
    }
}
```

Add dependency to your project level `gradle.build` file

```gradle
dependencies {
    implementation "imsolo:sdk:1.0.9"
}
```

## Example

To run the example project, clone the repo.

Create file `solo.properties` in root of project.
Put your `APP_ID` and `APP_KEY` variables without quotes.

```text
API_KEY=<YOUR_API_KEY>
APP_ID=<YOUR_APP_ID>
```

Then sync Gradle (from Android Studio or manually).

## Library Documentation

### Initialization

To be able to work with face expression estimating you should
initialize `Solo` object.
This method require network connection and can throw exceptions.
Configure your session with `Solo` object
(userId is a required parameter!).

```kotlin
Solo.init(credentials: SoloCredentials)
Solo.setFragmentListener(
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner,
    listener: EventResultListener
)
```

#### Exceptions:
All exceptions is inherited from `SoloException`
```kotlin
NotValidCredentials
NotAuthorizedException
NetworkOnMainThreadException
NetworkConnectionException
```

#### Methods:
```kotlin
fun setIdentify(identify: Identify)
fun setContent(contentId: String?)
fun setMetadata(metadata: Map<String, String>)
fun removeMetadata(key: String)
fun removeMetadata(keys: Set<String>)
fun reset() //revert to default configuration
fun getFragmentInstance(): Fragment
```

#### Example of initialization in ViewModel:

```kotlin
runCatching {
    Solo.init(SoloCredentials(BuildConfig.API_KEY, BuildConfig.APP_ID))
}.onSuccess {
    it.setIdentify(Identify(USER_ID))
    _soloLiveData.postValue(Resource.Success(it))
}.onFailure {
    _soloLiveData.postValue(Resource.Error(it))
    it.printStackTrace()
}
```

### Estimation flow

#### Fragment initialization

Estimation flow works in a fragment. Create fragment instance
with initialized `Solo` object.

```kotlin
val soloFragment = solo.getFragmentInstance()
```

Then you will be able to put this `soloFragment` in a container with `FragmentManager`.

In Activity:
```kotlin
supportFragmentManager.beginTransaction()
    .replace(R.id.solo_container, soloFragment)
    .runOnCommit { soloFragment.startMonitoring() }
    .commit()
```

In Fragment:
```kotlin
childFragmentManager.beginTransaction()
    .replace(R.id.solo_container, soloFragment)
    .runOnCommit { soloFragment.startMonitoring() }
    .commit()
```
#### Note
Before you place `soloFragment` in container,
request `Manifest.permission.CAMERA` permission with
convenient for you way.

#### Callback initialization
To get callbacks and results from this fragment
you should initialize a listener with `Solo` class
in `onCreate` method of your `Activity` or `Fragment`.
`FragmentManager` must be the same as when `soloFragment`
placed in a container.

#### Checkup callback

```kotlin
Solo.setFragmentListener(
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner,
    listener: EventResultListener
)
```

Callback methods:

```kotlin
EventResultListener {
    fun onMonitoringStarted()  //optional
    fun onMonitoringEnded()    //optional
    fun onMonitoringResult(result: EmotionalCheckupResult)
    fun onMonitoringError(exception: SoloException)
}
```

#### Monitoring callback
```kotlin
Solo.setMonitoringListener(
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner,
    listenerBox: (List<Box>) -> Unit, //returns the coordinates of faces
    listener: (EmotionalCheckupResult) -> Unit //returns the processing result from the server
)

```kotlin
Solo.setMonitoringListener(
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner,
    monitoringListener: (MonitoringTracker) -> Unit //returns the emotion result for each image frame
)
```

### Models

#### SoloCredentials
```kotlin
data class SoloCredentials(
    val apiKey: String,
    val appId: String
)
```

#### Identify
```kotlin
data class Identify(
    val userId: String,
    val groupId: String? = null,
    val sessionId: String? = null
)
```

#### EmotionalCheckupResult
```kotlin
data class EmotionalCheckupResult(
    val avg: AvgEmotion,
    val valence: Float,
    val energy: Float,
    val wellbeing: Float,
    val stress: Float,
    val interest: Float,
    val engagement: Float,
    val soloSessionId: Long,
    val sessionUnitId: Int?,
)
```

#### AvgEmotion
```kotlin
data class AvgEmotion(
    val happiness: Float,
    val neutral: Float,
    val angry: Float,
    val disgusted: Float,
    val surprised: Float,
    val sad: Float,
    val fearful: Float,
)
```

#### Record video
```kotlin
fun startRecording() //use for start recording video
fun stopRecording(callBack:(Uri)->Unit) // use for stop recording and get path
```
