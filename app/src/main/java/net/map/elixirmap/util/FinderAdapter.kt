/*
package net.map.elixirmap.util

import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.finder_list_item.*
import net.map.elixirmap.R
import net.u2023.live.maps.android.bean.PlaceBean
import net.u2023.live.maps.android.ui.DetailActivity

class FinderAdapter(private val mContext: Context) : BaseBindingAdapter<PlaceBean.FeaturesEntity>() {

    override val layoutId: Int
        get() = R.layout.finder_list_item

    override fun onConvert(holder: BaseViewHolder, item: PlaceBean.FeaturesEntity, position: Int) {
        holder.place_name.text = item.text
        holder.streets_name.text = item.properties.address

        holder.place_info.setOnClickListener {
            mContext.startActivity(Intent(mContext, DetailActivity::class.java).apply {
                putExtra("lat", item.center[1])
                putExtra("lon", item.center[0])
            })
        }
    }
}
*/
