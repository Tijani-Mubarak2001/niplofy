package com.adewale.niplofy.ui.activities

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.adewale.niplofy.R
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.ui.adapters.MyProductsListAdapter
import com.adewale.niplofy.ui.adapters.SearchViewAdapter
import kotlinx.android.synthetic.main.activity_search_view.*
import kotlinx.android.synthetic.main.fragment_products.*
import java.util.*
import kotlin.collections.ArrayList

class SearchViewActivity : BaseActivity() {
    private lateinit var fullList: ArrayList<Product>
    private lateinit var tempList: ArrayList<Product>
    var adapterProducts : SearchViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)
        setupActionBar()
        FirestoreClass().getAllProducts(this)
        showProgressDialog(resources.getString(R.string.please_wait))

    }

    fun collectListOfProducts(list: ArrayList<Product>){
        hideProgressDialog()
        fullList = list
        tempList = list
        onSuccessSearchList(tempList)
    }
    private fun onSuccessSearchList(newList: ArrayList<Product>){
        if (newList.size > 0){
            tv_no_searchView_items_found.visibility = View.GONE
            rv_search_view_items.visibility = View.VISIBLE
            rv_search_view_items.layoutManager = LinearLayoutManager(this)
            rv_search_view_items.setHasFixedSize(true)
            adapterProducts = SearchViewAdapter(this)
            adapterProducts!!.setData(newList)
            rv_search_view_items.adapter = adapterProducts
        }

    }

    override fun onResume() {
        FirestoreClass().getAllProducts(this)
        super.onResume()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_search_product_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_search_product_activity.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val item = menu!!.findItem(R.id.search_now)
        val searchView = item?.actionView as SearchView
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterProducts!!.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterProducts!!.filter.filter(newText)
                return true

            }

        })


        return super.onCreateOptionsMenu(menu)
    }

}