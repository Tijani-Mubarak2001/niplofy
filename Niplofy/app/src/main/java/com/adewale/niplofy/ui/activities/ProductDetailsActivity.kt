package com.adewale.niplofy.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.adewale.niplofy.R
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.ui.activities.BaseActivity
import com.adewale.niplofy.ui.fragments.ProductsFragment
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import com.myshoppal.models.CartItem
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_product_details.mail_to
import kotlinx.android.synthetic.main.activity_product_details.phone_call
import kotlinx.android.synthetic.main.item_dashboard_layout.*
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*


class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    // A global variable for product id.
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_product_details)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }


        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId =
                    intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }


        setupActionBar()

        getProductDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
        safety_tips_button.setOnClickListener(this)
        ll_product_safety_tips.setOnClickListener(this)

        phone_call.setOnClickListener{
            call_number(tv_phone_description_number.text.toString())
        }

        mail_to.setOnClickListener{
            sent_mailto(tv_email_description_address.text.toString(), tv_product_details_title.text.toString())
        }

    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }


    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }


    fun productDetailsSuccess(product: Product) {

        mProductDetails = product

        // Hide Progress dialog.
        //hideProgressDialog()

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "#${product.price}"
        tv_product_details_description.text = product.description
        tv_product_address_description.text = product.address
        tv_product_Phone_number_description.text = product.phone_num
        tv_phone_description_number.text = product.phone_num
        tv_email_description_address.text = product.email
        tv_product_details_available_quantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() ==0 ){
            hideProgressDialog()
            btn_add_to_cart.visibility = View.GONE

            tv_product_details_available_quantity.text =
                    resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_available_quantity.setTextColor(
                    ContextCompat.getColor(
                            this@ProductDetailsActivity,
                            R.color.colorSnackBarError
                    )
            )
        }


        if (FirestoreClass().getCurrentUserID() == product.user_id) {
            // Hide Progress dialog.
            hideProgressDialog()
        } else {
            FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
        }
    }

    private fun sent_mailto(email: String, title: String){
        val address = email.split(",".toRegex()).toTypedArray()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, "Request to buy ${title} from Buyer at Niplofy")
            putExtra(Intent.EXTRA_TEXT,"I would buy it for   ")

        }
        startActivity(Intent.createChooser(intent, "Choose an email client"))
    }
    fun call_number(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
        startActivity(intent)
    }

    fun addToCart() {

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
            Constants.DEFAULT_CART_QUANTITY
        )

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItems)
    }


    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
                this@ProductDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show()

        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.ll_product_safety_tips ->{
                    startActivity(Intent(this@ProductDetailsActivity, SafetyTipsActivity::class.java))
                }
                R.id.safety_tips_button ->{
                    startActivity(Intent(this@ProductDetailsActivity, SafetyTipsActivity::class.java))
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }


    }
}