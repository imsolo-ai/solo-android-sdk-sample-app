package im.solo.sdk_example.ui.monitoring

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.transaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.GsonBuilder
import im.solo.sdk.Solo
import im.solo.sdk.ui.monitoring.SoloMonitoringFragment
import im.solo.sdk_example.R
import im.solo.sdk_example.common.Resource
import im.solo.sdk_example.databinding.FragmentMonitoringBinding
import im.solo.sdk_example.ui.main.MainViewModel
import im.solo.sdk_example.ui.monitoring.monitoring_data.MonitoringDataFragment

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private var solo: Solo? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private var monitroingBottomSheet: BottomSheetBehavior<FrameLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Solo.setMonitoringListener(
            childFragmentManager,
            this,
            listenerBox = {
                Log.d("Box_Coordinate", GsonBuilder().serializeNulls().create().toJson(it))
            }

        ) { result ->
            binding.message.text = buildString {
                append("Avg\n")
                append("neutral: ${result.avg.neutral}\n")
                append("happiness: ${result.avg.happiness}\n")
                append("sad: ${result.avg.sad}\n")
                append("angry: ${result.avg.angry}\n")
                append("disgusted: ${result.avg.disgusted}\n")
                append("fearful: ${result.avg.fearful}\n")
                append("surprised: ${result.avg.surprised}\n")
                append("\n")
                append("energy: ${result.energy}\n")
                append("engagement: ${result.engagement}\n")
                append("interest: ${result.interest}\n")
                append("stress: ${result.stress}\n")
                append("valence: ${result.valence}\n")
                append("wellbeing: ${result.wellbeing}\n")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLiveData()
        initDataResultFragment()

        binding.btnStart.setOnClickListener {
            getMonitoringFragment().startMonitoring()
            binding.message.text = "Monitoring started"
            getMonitoringFragment().startRecording()
        }

        binding.btnStop.setOnClickListener {
            getMonitoringFragment().stopRecording {
                Log.d("path", it.path.toString())

            }
            getMonitoringFragment().stopMonitoring()
            binding.message.text = "Monitoring stopped"
        }

        binding.btnSentMetadata?.setOnClickListener {
            solo?.setMetadata(mapOf("tags" to "listOf()"))
        }

        binding.btnShowMonitoringResult?.setOnClickListener {
            monitroingBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupLiveData() {
        viewModel.soloLiveData.observe(viewLifecycleOwner) { resource ->
            binding.progress.isVisible = resource is Resource.Loading
            when (resource) {
                Resource.Loading -> {
                    binding.message.text = "Loading..."
                }

                is Resource.Success -> {
                    solo = resource.data
                    solo?.let {
                        val fragment = it.getMonitoringFragmentInstance()

                        childFragmentManager.beginTransaction()
                            .replace(R.id.camera_container, fragment, SOLO_MONITORING_FRAGMENT)
                            .commitNow()

                        fragment.startMonitoring()

                        binding.message.text = "Monitoring is ready"
                    }
                }

                is Resource.Error -> {
                    binding.message.text = resource.e.message
                }
            }
        }
    }

    private fun getMonitoringFragment() =
        childFragmentManager.findFragmentByTag(SOLO_MONITORING_FRAGMENT)
                as SoloMonitoringFragment

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initDataResultFragment() {
        childFragmentManager.commit {
            replace(R.id.flBottomSheet, MonitoringDataFragment.newInstance())
        }
        monitroingBottomSheet = BottomSheetBehavior.from(binding.flBottomSheet ?: return)
        monitroingBottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        val TAG: String = MonitoringFragment::class.java.simpleName

        private const val SOLO_MONITORING_FRAGMENT = "SoloMonitoringFragment"

        fun newInstance() = MonitoringFragment()
    }
}