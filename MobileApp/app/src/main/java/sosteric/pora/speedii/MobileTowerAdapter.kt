package sosteric.pora.speedii

import MobileTower
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sosteric.pora.speedii.databinding.ItemMobileTowerBinding

class MobileTowerAdapter(
    private val mobileTowers: List<MobileTower>,
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
) : RecyclerView.Adapter<MobileTowerAdapter.MobileTowerViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MobileTowerViewHolder {
        val binding = ItemMobileTowerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MobileTowerViewHolder(binding, onItemClick, onItemLongClick)
    }


    override fun onBindViewHolder(holder: MobileTowerViewHolder, position: Int) {
        holder.bind(mobileTowers[position])
    }

    override fun getItemCount(): Int {
        return mobileTowers.size
    }


    class MobileTowerViewHolder(
        private val binding: ItemMobileTowerBinding,
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

        fun bind(mobileTower: MobileTower) {
            val locationText = itemView.context.getString(R.string.type,mobileTower.type)
            val providerText = itemView.context.getString(R.string.provider, mobileTower.provider)


            binding.typeTextView.text = locationText;
            binding.providerMobileTextView.text = providerText
        }
    }

}

