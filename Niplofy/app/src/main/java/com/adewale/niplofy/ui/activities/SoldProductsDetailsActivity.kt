package com.adewale.niplofy.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.adewale.niplofy.R
import com.adewale.niplofy.models.SoldProduct
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_sold_products_details.*
import java.text.SimpleDateFormat
import java.util.*

class SoldProductsDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_products_details)


        var productDetails: SoldProduct = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }
        setupActionBar()

        setupUI(productDetails)

        btn_phone_call.setOnClickListener{
            call_number(productDetails.address.mobileNumber)
        }
        btn_mail_to.setOnClickListener{
            sent_mailto(productDetails.address.email, productDetails.title)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }



    private fun setupUI(productDetails: SoldProduct) {

        tv_sold_product_details_id.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        GlideLoader(this@SoldProductsDetailsActivity).loadProductPicture(
            productDetails.image,
            iv_product_item_image
        )
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text ="$${productDetails.price}"
        tv_sold_product_quantity.text = productDetails.sold_quantity

        tv_sold_details_address_type.text = productDetails.address.type
        tv_sold_details_full_name.text = productDetails.address.name
        tv_sold_details_address.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        tv_sold_details_additional_note.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = productDetails.address.otherDetails
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        tv_sold_details_mobile_number.text = productDetails.address.mobileNumber

        tv_sold_product_sub_total.text = productDetails.sub_total_amount
        tv_sold_product_shipping_charge.text = productDetails.shipping_charge
        tv_sold_product_total_amount.text = productDetails.total_amount

    }

    fun call_number(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
        startActivity(intent)
    }

    fun sent_mailto(email: String, title: String){
        val address = email.split(",".toRegex()).toTypedArray()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, "Confirming Your Request to buy ${title} from Seller at Niplofy")
            putExtra(Intent.EXTRA_TEXT,"Thanks For Patronizing me")

        }
        startActivity(Intent.createChooser(intent, "Choose an email client"))
    }



}