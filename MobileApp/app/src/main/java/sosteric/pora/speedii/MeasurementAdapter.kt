package sosteric.pora.speedii

import Measurment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sosteric.pora.speedii.databinding.ItemMeasurementBinding
import sosteric.pora.speedtest.Type
import java.time.format.DateTimeFormatter

class MeasurementAdapter(
    private val measurements: List<Measurment>,
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
) : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val binding = ItemMeasurementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeasurementViewHolder(binding, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        holder.bind(measurements[position])
    }

    override fun getItemCount(): Int {
        return measurements.size
    }

    class MeasurementViewHolder(
        private val binding: ItemMeasurementBinding,
        private val onItemClick: (Int) -> Unit,
        private val onItemLongClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(adapterPosition)
                true
            }

        }

        fun bind(measurement: Measurment) {
            val speedText = itemView.context.getString(R.string.speed,(measurement.speed / 1000000).toString())
            val providerText = itemView.context.getString(R.string.provider, measurement.provider)
            val dateText = itemView.context.getString(R.string.date, measurement.time.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")))


            binding.connectionTypeIcon.setImageResource(
                when (measurement.type) {
                    Type.wifi-> R.drawable.ic_wifi
                    Type.data -> R.drawable.ic_data
                    else -> R.drawable.ic_unknown
                }
            )
            binding.speedTextView.text = speedText
            binding.providerTextView.text = providerText
            binding.timeTextView.text = dateText
        }
    }

}