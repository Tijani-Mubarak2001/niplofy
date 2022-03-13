package com.adewale.niplofy.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val ORDER_PRODUCTS: String = "order_products"

    const val DASHPRODUCTS: String = "dashproducts"
    const val CART_ITEMS: String = "cart_items"
    const val CART_DASH_ITEMS: String = "cart_dash_items"
    const val CART_QUANTITY: String = "cart_quantity"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_MY_ORDER_DETAILS"
    const val SOLD_PRODUCTS: String = "sold_products"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"
    const val CART_PRESSED: Boolean = true

    const val STOCK_QUANTITY: String = "stock_quantity"
    const val ADDRESSES: String = "addresses"

    const val ORDERS: String = "orders"

    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val PRODUCT_ID: String = "product_id"


    const val NIPLOFY_PREFERENCES: String = "NiplofyPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS : String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val CAMERA = 1

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"

    // Firebase database field names
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val PRODUCT_IMAGE: String = "Product_Image"

    const val USER_ID: String = "user_id"

    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_DASH_PRODUCT_ID: String = "extra__dash_product_id"

    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val EXTRA_DASH_PRODUCT_OWNER_ID: String = "extra_dash_product_owner_id"

    const val DEFAULT_CART_QUANTITY: String = "1"
    const val DEFAULT_DASH_CART_QUANTITY: String = "1"

    const val EXTRA_ADDRESS: String = "extra_address"
    const val EXTRA_PHONE_NUM: String = "extra_phone_num"
    const val EXTRA_EMAIL: String = "extra_email"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}