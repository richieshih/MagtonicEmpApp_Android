package com.magtonic.magtonicempapp.data

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.magtonic.magtonicempapp.R
import com.magtonic.magtonicempapp.persistence.History


class HistoryAdapter(context: Context?, resource: Int, objects: List<History>) :
    ArrayAdapter<History>(context as Context, resource, objects)  {

    //private val mTAG = HistoryAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var items: List<History>
    private var mContext: Context? = null

    init {
        this.mContext = context
        this.items = objects
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): History? {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //Log.e(mTAG, "getView = "+ position);
        val view: View
        val holder: ViewHolder
        if (convertView == null || convertView.tag == null) {
            //Log.e(mTAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null)
            holder = ViewHolder(view)
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);


        val historyItem = items[position]

        holder.itemDesc.text = historyItem.getDesc()
        holder.itemTime.text = historyItem.getTime()
        holder.itemDate.text = historyItem.getDate()

        when(historyItem.getCode()) {
            "00","02","04" -> holder.itemImage.setImageResource(R.drawable.baseline_arrow_forward_black_36)
            "01","03","05" -> holder.itemImage.setImageResource(R.drawable.baseline_arrow_back_black_36)
            else -> holder.itemImage.setImageResource(R.drawable.baseline_warning_black_36)
        }


        return view
    }

    class ViewHolder (view: View) {
        var itemImage: ImageView = view.findViewById(R.id.history_icon)
        var itemDesc: TextView = view.findViewById(R.id.historyDesc)
        var itemDate: TextView = view.findViewById(R.id.historyDate)
        var itemTime: TextView = view.findViewById(R.id.historyTime)


    }
}