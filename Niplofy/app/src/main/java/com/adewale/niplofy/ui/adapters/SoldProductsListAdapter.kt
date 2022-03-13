package com.myshoppal.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adewale.niplofy.R
import com.adewale.niplofy.models.SoldProduct
import com.adewale.niplofy.ui.activities.SoldProductsDetailsActivity
import com.adewale.niplofy.ui.fragments.ProductsFragment
import com.adewale.niplofy.ui.fragments.SoldProductsFragment
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

/**
 * A adapter class for sold products list items.
 */
open class SoldProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>,
    private val fragment: SoldProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_item_image_for_search
            )

            holder.itemView.tv_item_search_name.text = model.title
            holder.itemView.tv_item_search_price.text = "$${model.price}"
            holder.itemView.btn_delivered.visibility = View.VISIBLE

            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteSoldProduct(model.id)
            }
            holder.itemView.btn_delivered.setOnClickListener{
                fragment.conFirmDeliveredProduct()
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductsDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}