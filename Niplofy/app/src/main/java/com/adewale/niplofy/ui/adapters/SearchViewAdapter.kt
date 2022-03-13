package com.adewale.niplofy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.adewale.niplofy.R
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.ui.activities.ProductDetailsActivity
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_dashboard_layout.view.iv_item_image
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_address
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_item_name
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_item_price
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class SearchViewAdapter(private val context: Context) :
    RecyclerView.Adapter<SearchViewAdapter.ViewHolder>(), Filterable{
    private val layoutInflater = LayoutInflater.from(context)
    var fullList = ArrayList<Product>()
    var filteredList = ArrayList<Product>()

    fun setData(fullList : ArrayList<Product>){
        this.fullList = fullList
        this.filteredList = fullList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val itemView = layoutInflater.inflate(R.layout.item_list_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = fullList[position]
        if (holder is ViewHolder){

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_item_image_for_search)

            holder.itemView.tv_item_search_name.text = model.title
            holder.itemView.tv_item_search_price.text = "₦${model.price}"


            holder.itemView.ib_delete_product.visibility = View.GONE

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return fullList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
         return object: Filter(){
             override fun performFiltering(charsequence: CharSequence?): FilterResults {
                 val filterResults = FilterResults()
                 if( charsequence.toString() == null || charsequence?.length!! < 0){
                     filterResults.count = filteredList.size
                     filterResults.values = filteredList
                 }else{
                     var searchChr = charsequence.toString().toLowerCase()
                     val itemModal = ArrayList<Product>()

                     for (item in filteredList){
                         if (item.title.toLowerCase().contains(searchChr.toLowerCase())){
                             itemModal.add(item)
                         }
                     }
                      filterResults.count = itemModal.size
                     filterResults.values = itemModal
                 }
                 return filterResults
             }

             override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                 fullList = filterResults!!.values as ArrayList<Product>
                 notifyDataSetChanged()
             }

         }
    }
}

