package im.solo.sdk_example.common

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val e: Throwable) : Resource<Nothing>()
}