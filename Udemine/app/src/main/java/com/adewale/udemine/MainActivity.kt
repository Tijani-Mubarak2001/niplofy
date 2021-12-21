package com.adewale.udemine

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    private lateinit var mDicoverDishViewModel: DiscoverDishListViewModel
    private lateinit var DesignAdapter: designAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showProgressDialog()

        mDicoverDishViewModel = ViewModelProvider(this).get(DiscoverDishListViewModel::class.java)

        mDicoverDishViewModel.getDishListFromAPI()




        mDicoverDishViewModel.discoverDishResponse.observe(this, { discoverDishResponse ->
            discoverDishResponse?.let {
                Log.i("Discover Dish Response", "$discoverDishResponse")
                DesignAdapter = designAdapter(this, discoverDishResponse.categories)
                designViewPager.adapter = DesignAdapter
                hideProgressDialog()
            }
        })

        mDicoverDishViewModel.discoverDishLoadingError.observe(this,
            { dataError ->
                dataError?.let {
                    Log.e("Discover Dish API Error", "$dataError")
                    hideProgressDialog()
                }
            })
        mDicoverDishViewModel.loadDiscoverDish.observe(this, { loadDiscoverDish ->
            loadDiscoverDish?.let {
                Log.e("Discover Dish Loading", "$loadDiscoverDish")


            }
        })
    }



    private fun showProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialoge_progress)

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}