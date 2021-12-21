package com.adewale.udemine

object DiscoverDishListObject {
    data class Categories(
    val categories: List<Category>
)

data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryDescription: String,
    val strCategoryThumb: String
)
}

