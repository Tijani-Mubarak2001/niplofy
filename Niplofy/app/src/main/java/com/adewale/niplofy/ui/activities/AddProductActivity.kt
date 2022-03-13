package com.adewale.niplofy.ui.activities

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adewale.niplofy.R
import com.adewale.niplofy.databinding.ActivityAddProductBinding
import com.adewale.niplofy.databinding.DialogCustomImageSelectorBinding
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.GlideLoader
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class AddProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddProductBinding

    private var mSelectedImageFileUri: Uri? = null
    private var thumbnail: Bitmap? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.ivAddUpdateProduct.setOnClickListener(this@AddProductActivity)
        setupActionBar()

        btn_submit.setOnClickListener(this)
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        customImageSelectionDialog()
                        return
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        showErrorSnackBar("Your Products Details are valid", false)
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddProductActivity)

        val binding: DialogCustomImageSelectorBinding = DialogCustomImageSelectorBinding.inflate(
            layoutInflater
        )

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        // TODO Step 7: Assign the click for Camera and Gallery. Show the Toast message for now.
        // START
        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    //permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                @Suppress("DEPRECATION")
                                startActivityForResult(intent, Constants.CAMERA)
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }).onSameThread().check()


            dialog.dismiss()

        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,

                    ).withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        @Suppress("DEPRECATION")
                        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddProductActivity,
                            "You have denied the storage permission to select image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }


                }).onSameThread().check()


            dialog.dismiss()
        }
        // END

        //Start the dialog and display it on screen.
        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun uploadProductImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )

    }
    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        showErrorSnackBar("Product image is uploaded successfully. Image URL: $imageURL", false)
        mProductImageURL = imageURL

        uploadProductDetails()
    }
    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
                this.getSharedPreferences(Constants.NIPLOFY_PREFERENCES, Context.MODE_PRIVATE)
                        .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim { it <= ' ' },
            et_product_price.text.toString().trim { it <= ' ' },
            et_product_description.text.toString().trim { it <= ' ' },
            et_product_address.text.toString().trim { it <= ' ' },
            et_phone_num.text.toString().trim { it <= ' ' },
            et_email_address.text.toString().trim { it <= ' ' },
            et_product_quantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }
    fun productUploadSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CAMERA) {

                data?.extras?.let {

                    thumbnail= data.extras!!.get("data") as Bitmap // Bitmap from camera

                    val bytes = ByteArrayOutputStream()
                    thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    @Suppress("DEPRECATION")
                    val path: String = MediaStore.Images.Media.insertImage(
                        applicationContext.contentResolver,
                        thumbnail,
                        "val",
                        null
                    )
                    mSelectedImageFileUri = Uri.parse(path)

                    // TODO Step 2: Here we will replace the setImageBitmap using glide as below.
                    // START
                    // mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.

                    // Set Capture Image bitmap to the imageView using Glide
                    Glide.with(this@AddProductActivity)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivProductImage)
                    // END

                    // Replace the add icon with edit icon once the image is loaded.
                    mBinding.ivAddUpdateProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddProductActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {




            // Replace the add icon with edit icon once the image is selected.
            iv_add_update_product.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddProductActivity,
                    R.drawable.ic_vector_edit
                )
            )

            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the product image in the ImageView.
                GlideLoader(this@AddProductActivity).loadUserPicture(
                    mSelectedImageFileUri!!,
                    iv_product_image
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(et_phone_num.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_phone_num), true)
                false
            }
            TextUtils.isEmpty(et_email_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email_address), true)
                false
            }


            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_product_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_address),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}