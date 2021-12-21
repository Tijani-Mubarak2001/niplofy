package com.adewale.Obe.model.network

import com.adewale.udemine.Constants
import com.adewale.udemine.DiscoverDishListObject
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DiscoverDishApiService {

    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL) // Set the API base URL.
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build() // Create the Retrofit instance using the configured values.
        // Create an implementation of the API endpoints defined by the service interface in our case it is RandomDishAPI.
        .create(DiscoverDishList::class.java)
    // END

    // TODO Step 4: Create a function that will initial the API call and returns the API response.
    // START
    fun getDishCategory(): Single<DiscoverDishListObject.Categories> {
        // TODO Step 6: Pass the values to the method as required params
        // START
        return api.getDishCategory()
        // END
    }
}