package com.adewale.niplofy.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adewale.niplofy.R
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.ui.activities.*
import com.adewale.niplofy.ui.adapters.DashboardItemsListAdapter
import com.adewale.niplofy.utils.Constants
import com.myshoppal.models.CartItem
import kotlinx.android.synthetic.main.action_bar_notification_icon.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.item_dashboard_layout.*
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var list: ArrayList<Product>

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }
    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {

            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                // END
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
            R.id.action_search -> {
                startActivity(Intent(activity, SearchViewActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getProductDetails(productID: String, userID: String) {

        mProductId = productID

        mProductOwnerId = userID

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            click_cart.visibility = View.GONE

            Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.error_message_item_added_to_cart),
                    Toast.LENGTH_SHORT
            ).show()

        } else {
            FirestoreClass().getProductDetailsFragment(this, mProductId)
        }

    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        addToCartFromDash()
    }

    private fun addToCartFromDash() {

        val cartItems = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.address,
            mProductDetails.phone_num,
            mProductDetails.email,
            mProductDetails.image,
            Constants.DEFAULT_DASH_CART_QUANTITY
        )

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItemFragment(this, cartItems)
    }


    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()


        val intent = Intent(context, CartListActivity::class.java)
        context?.startActivity(intent)

        Toast.makeText(
                requireActivity(),
                resources.getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show()



    }


    fun callNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
        startActivity(intent)
    }
    fun sentMailto(email: String, title: String){
        val address = email.split(",".toRegex()).toTypedArray()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, "Request to buy $title from Buyer at Niplofy")
            putExtra(Intent.EXTRA_TEXT, "I would buy it for   ")

        }
        if(context?.let { intent.resolveActivity(it.packageManager) } != null){
            startActivity(Intent.createChooser(intent, "Choose an email client"))
        }else{
            Toast.makeText(requireActivity(), "Required app is not installed", Toast.LENGTH_LONG).show()
        }

    }
    private fun getDashboardItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }



    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        // Hide the progress dialog.

        hideProgressDialog()


        if (dashboardItemsList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = LinearLayoutManager(activity)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList, this)

            rv_dashboard_items.adapter = adapter



            //adapter.setOnClickListener(object :
                    //DashboardItemsListAdapter.OnClickListener {

                        //override fun onClick(position: Int, product: Product) {
                            //val intent = Intent(context, ProductDetailsActivity::class.java)
                            //intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                            //intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                            //startActivity(intent)
                            // END
            //})
        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }


    }






}