package com.adewale.niplofy.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adewale.niplofy.R
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.models.User
import com.adewale.niplofy.ui.activities.CartListActivity
import com.adewale.niplofy.ui.activities.DashboardActivity
import com.adewale.niplofy.ui.activities.ProductDetailsActivity
import com.adewale.niplofy.ui.fragments.DashboardFragment
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import com.myshoppal.models.CartItem
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_dashboard_layout.view.iv_item_image
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_address
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_item_name
import kotlinx.android.synthetic.main.item_dashboard_layout.view.tv_item_price
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class DashboardItemsListAdapter(
        private val context: Context,
        private var list: ArrayList<Product>,
        private val fragment: DashboardFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_dashboard_layout,
                        parent,
                        false
                )
        )
    }




    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]


        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                    model.image,
                    holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "₦${model.price}"
            holder.itemView.tv_address.text = model.address
            holder.itemView.tv_phone_number.text = model.phone_num
            holder.itemView.tv_email_address.text = model.email


            holder.itemView.phone_call.setOnClickListener {
                fragment.callNumber(model.phone_num)
            }
            holder.itemView.mail_to.setOnClickListener{
                fragment.sentMailto(model.email, model.title)
            }
            holder.itemView.click_cart.setOnClickListener{
                fragment.getProductDetails(model.product_id, model.user_id)
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }




    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}