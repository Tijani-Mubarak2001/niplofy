package com.adewale.niplofy.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adewale.niplofy.models.*
import com.adewale.niplofy.ui.activities.*
import com.adewale.niplofy.ui.fragments.DashboardFragment
import com.adewale.niplofy.ui.fragments.OrdersFragment
import com.adewale.niplofy.ui.fragments.ProductsFragment
import com.adewale.niplofy.ui.fragments.SoldProductsFragment
import com.adewale.niplofy.utils.Constants
import com.adewale.niplofy.utils.Constants.LOGGED_IN_USERNAME
import com.adewale.niplofy.utils.Constants.NIPLOFY_PREFERENCES
import com.adewale.niplofy.utils.Constants.USERS
import com.adewale.niplofy.utils.Constants.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myshoppal.models.CartItem


class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    public var listsize = 0
    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences =
                            activity.getSharedPreferences(
                                    NIPLOFY_PREFERENCES,
                                    Context.MODE_PRIVATE
                            )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    //kEY: logged_in_username
                    //value: First name last name
                    editor.putString(
                            LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    // START
                    when (activity) {
                        is LoginActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity ->{
                            // Call a function of base activity for transferring the result to it.
                            activity.userDetailsSuccess(user)
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting user details.",
                        e
                    )
                }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {

        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cartItem in cartList) {

            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS).document()
            writeBatch.set(documentReference, soldProduct)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cartItem in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating all the details after order placed.",
                    e
            )
        }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldProduct> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }

                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }




    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val list: ArrayList<Order> = ArrayList()

                    for (i in document.documents) {

                        val orderItem = i.toObject(Order::class.java)!!
                        orderItem.id = i.id

                        list.add(orderItem)
                    }

                    fragment.populateOrdersListInUI(list)
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.

                    fragment.hideProgressDialog()

                    Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
                }
    }


    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(order, SetOptions.merge())
                .addOnSuccessListener {

                    // TODO Step 9: Notify the success result.
                    // START
                    // Here call a function of base activity for transferring the result to it.
                    activity.orderPlacedSuccess()
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                    )
                }
    }


    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
                .document(addressId)
                .delete()
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.deleteAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while deleting the address.",
                            e
                    )
                }
    }


    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                // TODO Step 4: Notify the success result to the base class.
                // START
                activity.successAddressListFromFirestore(addressList)
                // END
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }

    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "."
                    + getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while uploading the product details.",
                            e
                    )
                }
    }

    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e("Products List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }

                    when (fragment) {
                        is ProductsFragment -> {
                            fragment.successProductsListFromFireStore(productsList)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is ProductsFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }
                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.

                    activity.addToCartSuccess()


                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()



                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }


    }

    fun addCartItemFragment(fragment: DashboardFragment, addToCart: CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.

                    fragment.addToCartSuccess()


                }
                .addOnFailureListener { e ->

                    fragment.hideProgressDialog()



                    Log.e(
                            fragment.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }

    }


    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {

                    fragment.productDeleteSuccess()
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    fragment.hideProgressDialog()

                    Log.e(
                            fragment.requireActivity().javaClass.simpleName,
                            "Error while deleting the product.",
                            e
                    )
                }
    }



    fun deleteSoldProduct(fragment: SoldProductsFragment, productId: String) {

        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                fragment.soldProductDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the sold product.",
                    e
                )
            }
    }


    fun deleteFragmentSoldProduct(fragment: OrdersFragment, productId: String) {

        mFireStore.collection(Constants.ORDERS)
                .document(productId)
                .delete()
                .addOnSuccessListener {

                    fragment.soldProductDeleteSuccess()
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    fragment.hideProgressDialog()

                    Log.e(
                            fragment.requireActivity().javaClass.simpleName,
                            "Error while deleting the sold product.",
                            e
                    )
                }
    }


    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // TODO Step 8: Notify the success result to the base class.
                    // START
                    // If the document size is greater than 1 it means the product is already added to the cart.
                    if (document.documents.size > 0) {
                        activity.productExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while checking the existing cart list.",
                            e
                    )
                }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }


    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .update(itemHashMap) // A HashMap of fields which are to be updated.
                .addOnSuccessListener {

                    // TODO Step 4: Notify the success result of the updated cart items list to the base class.
                    // START
                    // Notify the success result of the updated cart items list to the base class.
                    when (context) {
                        is CartListActivity -> {
                            context.itemUpdateSuccess()
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }

                    Log.e(
                            context.javaClass.simpleName,
                            "Error while updating the cart item.",
                            e
                    )
                }
    }




    fun getAllProductsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                listsize = productsList.size
                when(activity){
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    is CheckoutActivity -> {
                       activity.successProductsListFromFireStore(productsList)
                    }
                }

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when(activity){
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }

                }


                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }



    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }

                    // Pass the success result to the base fragment.
                    fragment.successDashboardItemsList(productsList)


                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error which getting the dashboard items list.
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
                }
    }
    fun getAllProducts(activity: SearchViewActivity){

        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                activity.collectListOfProducts(productsList)

                // Pass the success result to the base fragment.


            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error while getting dashboard items list.", Toast.LENGTH_LONG).show()
                // Hide the progress dialog if there is any error which getting the dashboard items list.
            }

    }
    fun getProductDetails(activity: Activity, productId: String) {

        // The collection name for PRODUCTS

        when(activity){
            is ProductDetailsActivity -> {
                mFireStore.collection(Constants.PRODUCTS)
                        .document(productId)
                        .get() // Will get the document snapshots.
                        .addOnSuccessListener { document ->

                            // Here we get the product details in the form of document.
                            Log.e(activity.javaClass.simpleName, document.toString())

                            // Convert the snapshot to the object of Product data model class.
                            val product = document.toObject(Product::class.java)!!

                            // TODO Step 4: Notify the success result.
                            // START
                            activity.productDetailsSuccess(product)
                            // END
                        }
                        .addOnFailureListener { e ->

                            // Hide the progress dialog if there is an error.
                            activity.hideProgressDialog()

                            Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
                        }
            }
            is DashboardActivity -> {

            }
            }

        }

    fun getProductDetailsFragment(fragments: DashboardFragment, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get() // Will get the document snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the product details in the form of document.
                    Log.e(fragments.javaClass.simpleName, document.toString())

                    // Convert the snapshot to the object of Product data model class.
                    val product = document.toObject(Product::class.java)!!

                    // TODO Step 4: Notify the success result.
                    // START
                    fragments.productDetailsSuccess(product)
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    fragments.hideProgressDialog()

                    Log.e(fragments.javaClass.simpleName, "Error while getting the product details.", e)
                }
    }


}