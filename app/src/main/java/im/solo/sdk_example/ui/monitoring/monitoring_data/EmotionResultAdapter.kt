package im.solo.sdk_example.ui.monitoring.monitoring_data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import im.solo.sdk.model.EmotionResult
import im.solo.sdk_example.R
import im.solo.sdk_example.databinding.ItemMonitoringResultBinding

class EmotionResultAdapter :
    ListAdapter<EmotionResult, EmotionResultAdapter.EmotionResultHolder>(PlacesDiffCallback()) {

    class PlacesDiffCallback : DiffUtil.ItemCallback<EmotionResult>() {
        override fun areItemsTheSame(oldItem: EmotionResult, newItem: EmotionResult) =
            oldItem.emotions.toString() == newItem.emotions.toString()

        override fun areContentsTheSame(oldItem: EmotionResult, newItem: EmotionResult) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionResultHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitoring_result, parent, false)
        return EmotionResultHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionResultHolder, position: Int) {
        return holder.bind(getItem(position), position)
    }

    open inner class EmotionResultHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMonitoringResultBinding.bind(itemView)

        fun bind(emotionResult: EmotionResult, position: Int) {
            val emotion = emotionResult.emotions
            with(binding) {
                tvFace.text = "Face: ${position.inc()}"
                tvNeutral.text = "Neutral: ${emotion.neutral ?: 0f}"
                tvHappy.text = "Happy: ${emotion.happy ?: 0f}"
                tvSad.text = "Sad: ${emotion.sad ?: 0f}"
                tvAngry.text = "Angry: ${emotion.angry ?: 0f}"
                tvFearful.text = "Fearful: ${emotion.fearful ?: 0f}"
                tvDisgusted.text = "Disgusted: ${emotion.disgusted ?: 0f}"
                tvSurprised.text = "Surprised: ${emotion.surprised ?: 0f}"
                tvMood.text = "Mood: ${emotion.mood ?: 0f}"
                tvEnergy.text = "Energy: ${emotion.energy ?: 0f}"
                tvStress.text = "Stress: ${emotion.stress ?: 0f}"
                tvWellbeing.text = "Wellbeing: ${emotion.wellbeing ?: 0f}"
                tvInterest.text = "Interest: ${emotion.interest ?: 0f}"
                tvEngagement.text = "Engagement: ${emotion.engagement ?: 0f}"
                tvPlayedSeconds.text = "Timestamp ${System.currentTimeMillis()}"
            }
        }
    }

}