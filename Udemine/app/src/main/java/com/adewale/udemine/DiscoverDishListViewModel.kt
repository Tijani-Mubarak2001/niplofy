package com.adewale.udemine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adewale.Obe.model.network.DiscoverDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class DiscoverDishListViewModel : ViewModel(){

    private val discoverDishApiService = DiscoverDishApiService()

    private val compositeDisposable = CompositeDisposable()


    val loadDiscoverDish =  MutableLiveData<Boolean>()
    val discoverDishResponse = MutableLiveData<DiscoverDishListObject.Categories>()
    val discoverDishLoadingError = MutableLiveData<Boolean>()

    fun getDishListFromAPI() {
        // Define the value of the load random dish.
        loadDiscoverDish.value = true

        // Adds a Disposable to this container or disposes it if the container has been disposed.
        compositeDisposable.add(
                // Call the RandomDish method of RandomTextApiService class.
                discoverDishApiService.getDishCategory()

                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<DiscoverDishListObject.Categories>() {
                            override fun onSuccess(value: DiscoverDishListObject.Categories?) {
                                // Update the values with response in the success method.
                                loadDiscoverDish.value = false
                                discoverDishResponse.value = value!!
                                discoverDishLoadingError.value = false
                            }

                            override fun onError(e: Throwable?) {
                                // Update the values in the response in the error method
                                loadDiscoverDish.value = false
                                discoverDishLoadingError.value = true
                                e!!.printStackTrace()
                            }
                        })
        )
    }
}