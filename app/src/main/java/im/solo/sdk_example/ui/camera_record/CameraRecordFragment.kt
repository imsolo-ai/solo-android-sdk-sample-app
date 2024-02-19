package im.solo.sdk_example.ui.camera_record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import im.solo.sdk.Solo
import im.solo.sdk.ui.camera_recorder.CameraRecordController
import im.solo.sdk_example.R
import im.solo.sdk_example.common.Resource
import im.solo.sdk_example.databinding.FragmentCameraRecorderBinding
import im.solo.sdk_example.ui.main.MainViewModel

class CameraRecordFragment : Fragment() {

    private var _binding: FragmentCameraRecorderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    private var solo: Solo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraRecorderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.soloLiveData.observe(viewLifecycleOwner, ::initSolo)

        binding.btnRecord.setOnClickListener {
            binding.btnSave.isEnabled = true
            binding.btnRecord.isEnabled = false
            getCameraRecordFragment().startRecording()
            binding.message.setText(R.string.record_started)
        }

        binding.btnSave.setOnClickListener {
            binding.btnSave.isEnabled = false
            binding.btnRecord.isEnabled = true
            getCameraRecordFragment().stopRecording {
                Log.d("Path", it.path.toString())
            }
            binding.message.setText(R.string.save_video)
        }
    }

    private fun initSolo(resource: Resource<Solo>) {
        when (resource) {
            Resource.Loading -> {
                binding.message.setText(R.string.loading)
            }

            is Resource.Success -> {
                val solo = resource.data

                val fragment = solo.getCameraRecorderFragmentInstance()

                childFragmentManager.beginTransaction()
                    .replace(R.id.camera_container, fragment, CAMERA_RECORD_FRAGMENT)
                    .commitNow()


                binding.message.setText(R.string.monitoring_is_ready)
            }

            is Resource.Error -> {
                binding.message.text = resource.e.message
            }
        }
    }

    private fun getCameraRecordFragment() =
        childFragmentManager.findFragmentByTag(CAMERA_RECORD_FRAGMENT) as CameraRecordController

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = CameraRecordFragment::class.java.simpleName

        private const val CAMERA_RECORD_FRAGMENT = "CameraRecordFragment"

        fun newInstance() = CameraRecordFragment()
    }
}