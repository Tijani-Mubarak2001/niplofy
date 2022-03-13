package com.adewale.niplofy.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adewale.niplofy.R
import com.adewale.niplofy.firestore.FirestoreClass
import com.adewale.niplofy.models.Product
import com.adewale.niplofy.ui.activities.AddProductActivity
import com.adewale.niplofy.ui.adapters.MyProductsListAdapter
import kotlinx.android.synthetic.main.fragment_products.*


class ProductsFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    fun deleteProduct(productID: String) {

        showAlertDialogToDeleteProduct(productID)
    }
    fun productDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
                requireActivity(),
                resources.getString(R.string.product_delete_success_message),
                Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        getProductListFromFireStore()
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProducts = MyProductsListAdapter(requireActivity(), productsList, this)
            rv_my_product_items.adapter = adapterProducts


        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }
    private fun getProductListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getProductsList(this@ProductsFragment)
    }
    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_products, container, false)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_addProduct -> {

                startActivity(Intent(activity, AddProductActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)
            // END

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}