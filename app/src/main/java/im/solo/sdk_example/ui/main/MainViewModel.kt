package im.solo.sdk_example.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.solo.sdk.Identify
import im.solo.sdk.Solo
import im.solo.sdk.SoloCredentials
import im.solo.sdk.model.MetaDataResponse
import im.solo.sdk_example.BuildConfig
import im.solo.sdk_example.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _soloLiveData = MutableLiveData<Resource<Solo>>(Resource.Loading)
    val soloLiveData: LiveData<Resource<Solo>> get() = _soloLiveData

    init {
        initSolo()
    }

    private fun initSolo() {
        _soloLiveData.value = Resource.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                Solo.init(SoloCredentials(BuildConfig.API_KEY, BuildConfig.APP_ID))
            }.onSuccess {
                it.setIdentify(
                    Identify(
                        USER_ID,
                        USER_NAME,
                        GROUP_ID,
                        GROUP_NAME,
                        SESSION_ID
                    )
                )

                // Client configuration
                it.setFramesPerSecond(it.config.minFps)
                _soloLiveData.postValue(Resource.Success(it))
            }.onFailure {
                _soloLiveData.postValue(Resource.Error(it))
                it.printStackTrace()
            }
        }
    }

    companion object {
        private const val USER_ID = "Demo Android App"
        private const val USER_NAME = "Android Name"
        private const val GROUP_ID = "Android Group Id"
        private const val GROUP_NAME = "Android Group Name"
        private const val SESSION_ID = "Mobile SDK Example"
    }
}