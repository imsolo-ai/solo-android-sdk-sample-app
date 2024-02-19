package im.solo.sdk_example.ui.monitoring.monitoring_data

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import im.solo.sdk.Solo
import im.solo.sdk.model.EmotionResult
import im.solo.sdk_example.databinding.FragmentMonitoringDataBinding


class MonitoringDataFragment : Fragment() {

    lateinit var binding: FragmentMonitoringDataBinding

    private val emotionResultAdapter = EmotionResultAdapter()
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonitoringDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMonitoringResult.adapter = emotionResultAdapter
        Solo.setMonitoringListener(parentFragmentManager,
            this,
            monitoringListener = {
                updateEmotionResults(it.result)
            })
    }

    private fun updateEmotionResults(result: List<EmotionResult>) {
        handler.post {
            emotionResultAdapter.submitList(result)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MonitoringDataFragment()
    }
}