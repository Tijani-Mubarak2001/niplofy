package com.adewale.Obe.model.network


import com.adewale.udemine.DiscoverDishListObject
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscoverDishList {
    @GET("categories.php")
    fun getDishCategory(): Single<DiscoverDishListObject.Categories>
}