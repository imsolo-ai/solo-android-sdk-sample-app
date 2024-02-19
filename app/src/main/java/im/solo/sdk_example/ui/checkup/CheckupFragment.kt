package im.solo.sdk_example.ui.checkup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import im.solo.sdk.EventResultListener
import im.solo.sdk.Solo
import im.solo.sdk.exception.SoloException
import im.solo.sdk.model.EmotionalCheckupResult
import im.solo.sdk_example.R
import im.solo.sdk_example.common.Resource
import im.solo.sdk_example.databinding.FragmentCheckupBinding
import im.solo.sdk_example.ui.main.MainViewModel

class CheckupFragment : Fragment() {

    private var _binding: FragmentCheckupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Solo.setFragmentListener(
            childFragmentManager,
            this,
            listenerBox =  {
                Log.d("Box_Coordinate", Gson().toJson(it))
            },
            object : EventResultListener() {
                override fun onCheckupStarted() {
                    super.onCheckupStarted()

                    binding.message.text = "onCheckupStarted"
                }

                override fun onCheckupEnded() {
                    super.onCheckupEnded()

                    binding.message.text = "onCheckupEnded"
                }

                override fun onCheckupResult(result: EmotionalCheckupResult) {
                    childFragmentManager.findFragmentById(R.id.camera_container)?.let {
                        childFragmentManager.beginTransaction()
                            .remove(it)
                            .commit()
                    }

                    binding.result.text = buildString {
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

                override fun onCheckupError(exception: SoloException) {
                    binding.message.text = "onCheckupError: ${exception.message}"
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.soloLiveData.observe(viewLifecycleOwner) { resource ->
            binding.progress.isVisible = resource is Resource.Loading

            when (resource) {
                Resource.Loading -> {
                    binding.message.text = "Loading..."
                }

                is Resource.Success -> {
                    val solo = resource.data

                    val fragment = solo.getFragmentInstance()

                    childFragmentManager.beginTransaction()
                        .replace(R.id.camera_container, fragment)
                        .commit()

                    binding.message.text = "Open fragment.."
                }

                is Resource.Error -> {
                    binding.message.text = resource.e.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = CheckupFragment::class.java.simpleName

        fun newInstance() = CheckupFragment()
    }
}