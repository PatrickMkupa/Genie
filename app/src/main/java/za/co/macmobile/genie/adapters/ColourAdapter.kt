package za.co.macmobile.genie.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scwang.wave.MultiWaveHeader
import kotlinx.android.synthetic.main.card_avatar.view.*
import za.co.macmobile.genie.R
import za.co.macmobile.genie.R.id.waveHeader
import za.co.macmobile.genie.models.Item
import za.co.macmobile.genie.util.SharedPrefs

class ColourAdapter(private val items: List<Item>, private val context: Context, private val waveHeader: MultiWaveHeader) : RecyclerView.Adapter<ItemAdapter.holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.holder {
        return ItemAdapter.holder(LayoutInflater.from(context).inflate(R.layout.card_avatar, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemAdapter.holder, position: Int) {
        val item = items.get(position)

        holder.resorce.setBackgroundColor(Color.parseColor(item.resource))
        if (item.resource.equals(SharedPrefs.retrieveSecColour(context))) {

            item.isSelected = true;
        } else {

            item.isSelected = false
        }
        if (item.isSelected){
            holder.indicator.visibility = View.VISIBLE
        }else{
            holder.indicator.visibility = View.INVISIBLE
        }
        holder.resorce.setOnClickListener {
            items.forEach {
                if(item.resource.equals(it.resource)){
                    SharedPrefs.saveSecColour(context, item.resource)
                    waveHeader.startColor = Color.parseColor(item.resource)
                    waveHeader.closeColor = Color.parseColor(item.resource)
                    it.isSelected = true
                    holder.indicator.visibility = View.VISIBLE
                }else{
                    it.isSelected = false
                    holder.indicator.visibility = View.INVISIBLE
                }
            }

            notifyDataSetChanged()
        }

    }


}