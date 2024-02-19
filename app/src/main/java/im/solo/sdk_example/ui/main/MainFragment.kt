package im.solo.sdk_example.ui.main

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import im.solo.sdk_example.R
import im.solo.sdk_example.common.Resource
import im.solo.sdk_example.databinding.MainFragmentBinding
import im.solo.sdk_example.ui.camera_record.CameraRecordFragment
import im.solo.sdk_example.ui.checkup.CheckupFragment
import im.solo.sdk_example.ui.monitoring.MonitoringFragment

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { !it.value }) {
            Toast.makeText(
                requireContext(),
                R.string.camera_or_record_audio_permission_is_not_granted,
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))

        viewModel.soloLiveData.observe(viewLifecycleOwner) { resource ->
            binding.progress.isVisible = resource is Resource.Loading
            binding.btnCheckup.isEnabled = resource is Resource.Success
            binding.btnMonitoring.isEnabled = resource is Resource.Success
            binding.btnRecordCamera.isEnabled = resource is Resource.Success

            when (resource) {
                Resource.Loading -> {
                    binding.message.setText(R.string.loading)
                }

                is Resource.Success -> {
                    binding.message.setText(R.string.solo_initialized)
                }

                is Resource.Error -> {
                    binding.message.text = resource.e.message
                }
            }
        }

        binding.btnCheckup.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, CheckupFragment.newInstance(), CheckupFragment.TAG)
                .addToBackStack(CheckupFragment.TAG)
                .commit()
        }

        binding.btnMonitoring.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, MonitoringFragment.newInstance(), MonitoringFragment.TAG)
                .addToBackStack(MonitoringFragment.TAG)
                .commit()
        }

        binding.btnRecordCamera.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, CameraRecordFragment.newInstance(), MonitoringFragment.TAG)
                .addToBackStack(CameraRecordFragment.TAG)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}