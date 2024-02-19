# Solo Android Library

Native Android library for AI Face expression estimation

## Get library

Add repository to your root level `gradle.build` file

```gradle
repositories {
    google()
    mavenCentral()
    ...
    maven {
        url 'https://imsolo.jfrog.io/artifactory/android/'
        // Use credentials if anonymous access disabled
        credentials {
            username "username"
            password "password"
        }
    }
}
```

Add dependency to your project level `gradle.build` file

```gradle
dependencies {
    implementation "imsolo:sdk:1.0.0"
}
```

## Publish library

To publish library, create file `artifactory.properties` in root of project.
```text
artifactory_keyRepo=<KEY_REPO>
artifactory_user=<USER_LOGIN>
artifactory_password=<TOKEN>
artifactory_contextUrl=https://imsolo.jfrog.io/artifactory
```
Then `Clean project` and run `artifactoryPublish` task manually or from file `artifactory.gradle` via Android Studio.
Token can be generated from jFrog `Set Me Up` button or manually. And for send on JFrog you need use command  ./gradlew artifactoryPublish

artifactory_keyRepo="/android/" it's a path for artifacts currently it looks like
artifactory_user - it's your email from jfrog
artifactory_password - generated from jFrog `Set Me Up` button

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
    .commit()
```

In Fragment:
```kotlin
childFragmentManager.beginTransaction()
    .replace(R.id.solo_container, soloFragment)
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
    fun onCheckupStarted()  //optional
    fun onCheckupEnded()    //optional
    fun onCheckupResult(result: EmotionalCheckupResult)
    fun onCheckupError(exception: SoloException)
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