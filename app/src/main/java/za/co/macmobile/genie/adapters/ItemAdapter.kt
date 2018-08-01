package za.co.macmobile.genie.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import za.co.macmobile.genie.R
import za.co.macmobile.genie.models.Item
import kotlinx.android.synthetic.main.card_avatar.view.*
import za.co.macmobile.genie.util.SharedPrefs


class ItemAdapter(private val items: List<Item>, private val context: Context) : RecyclerView.Adapter<ItemAdapter.holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        return holder(LayoutInflater.from(context).inflate(R.layout.card_avatar, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        val item = items.get(position)
        val id = context.resources.getIdentifier(item.resource, "drawable", context.packageName)
        val drawable = context.getResources().getDrawable(id)
        holder.resorce.setImageDrawable(drawable)
        if (item.resource.equals(SharedPrefs.retrieveAvatar(context))) {

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
                    it.isSelected = true
                    SharedPrefs.saveAvatar(context,it.resource)
                    holder.indicator.visibility = View.VISIBLE
                }else{
                    it.isSelected = false
                    holder.indicator.visibility = View.INVISIBLE
                }

            }


            notifyDataSetChanged()
        }



    }

    public class holder(v: View) : RecyclerView.ViewHolder(v) {
        val resorce = v.resource
        val indicator = v.selectionIndicator
    }
}