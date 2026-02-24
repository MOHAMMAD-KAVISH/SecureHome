package com.example.securehomeplus.ui.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.securehomeplus.data.database.entities.SecurityFactor
import com.example.securehomeplus.databinding.ItemSurveyQuestionBinding

class SurveyAdapter(
    private val items: List<SecurityFactor>,
    private val answerCallback: (questionId: Int, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<SurveyAdapter.VH>() {

    private val checkedMap = mutableMapOf<Int, Boolean>()

    inner class VH(val b: ItemSurveyQuestionBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(q: SecurityFactor) {
            b.tvQuestion.text = q.question
            b.tvHint.text = "(Check if applicable)"

            // Remove old listeners to avoid multiple triggers
            b.checkbox.setOnCheckedChangeListener(null)

            // Restore previous state
            b.checkbox.isChecked = checkedMap[q.id] ?: false

            //  Handle checkbox toggle manually
            b.checkbox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                checkedMap[q.id] = isChecked
                answerCallback(q.id, isChecked)
                updateCardVisual(isChecked)
            }

            //  When the entire card or row is clicked
            b.cardRoot.setOnClickListener {
                val newState = !b.checkbox.isChecked
                b.checkbox.isChecked = newState
                checkedMap[q.id] = newState
                answerCallback(q.id, newState)
                updateCardVisual(newState)
            }

            //  Restore visual highlight on bind
            updateCardVisual(b.checkbox.isChecked)

            //  Optional icon setup
            q.iconResName?.let { name ->
                val resId = b.root.context.resources.getIdentifier(
                    name,
                    "drawable",
                    b.root.context.packageName
                )
                if (resId != 0) b.ivIcon.setImageResource(resId)
            }
        }

        private fun updateCardVisual(isChecked: Boolean) {
            // Smooth visual highlight animation when selected
            b.cardRoot.animate()
                .scaleX(if (isChecked) 1.03f else 1f)
                .scaleY(if (isChecked) 1.03f else 1f)
                .setDuration(150)
                .start()

            b.cardRoot.cardElevation = if (isChecked) 10f else 5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSurveyQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
