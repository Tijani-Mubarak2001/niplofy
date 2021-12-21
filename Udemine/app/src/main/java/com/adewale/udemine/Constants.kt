package com.adewale.udemine

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val MEALTIME: String = "MealTime"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL : String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE : String = "Online"

    const val EXTRA_DISH_DETAILS: String = "DishDetails"
    const val EXTRA_DISH_CATEGORY: String = "extra_category"
    const val EXTRA_DISH_DESCRIPTION: String = "extra_description"
    const val EXTRA_DISH_IMAGE: String = "extra_cat_image"
    const val EXTRA_MEAL_ID: String = "extra_meal_id"


    const val IS_A_FAVOURITE : String = "false"

    const val SEARCH_API_ENDPOINT :String = "api/recipes/v2"


    const val SEARCH_API_TYPE: String = "type"
    const val SEARCH_API_Q: String = "q"
    const val SEARCH_APP_ID: String = "app_id"
    const val SEARCH_API_KEY: String = "app_key"
    const val SEARCH_BASE_URL = "https://api.edamam.com/"

    const val SEARCH_API_KEY_VALUE: String = "39285edea6b509ded9aa8ea34a40671c"
     const val SEARCH_APP_ID_VALUE: String = "9e6a2ce9"
    const val SEARCH_API_TYPE_VALUE: String = "public"



    const val NOTIFICATION_ID = "Obe_notification_id"
    const val NOTIFICATION_NAME = "ObeDish"
    const val NOTIFICATION_CHANNEL = "Obe_channel_01"




    const val EXTRA_TITLE: String = "extra_title"
    const val EXTRA_IMAGE: String = "extra__image"
    const val EXTRA_CATEGORY: String = "extra__category"
    const val EXTRA_AREA: String = "extra__area"
    const val EXTRA_TYPE: String = "extra__type"
    const val EXTRA_INGREDIENT: String = "extra__ingredient"
    const val EXTRA_COOKING_TIME: String = "extra__cooking_time"
    const val EXTRA_MEASURE: String = "extra__measure"
    const val EXTRA_MEAL_TIME: String = "extra__meal_time"
    const val EXTRA_DIRECTION_TO_COOK: String = "extra__direction_to_cook"
    const val EXTRA_YOUTUBE_SOURCE: String = "extra__youtube_source"
    const val EXTRA_SOURCE: String = "extra__source"


    const val EXTRA_CUISINE: String = "cuisine"
    const val EXTRA_DISHTYPE: String = "extra__dishtype"
    const val EXTRA_HEALTHLABELS: String = "extra__healthlabels"
    const val EXTRA_CALORIES: String = "extra__calories"
    const val EXTRA_MEAL_TYPE: String = "extra__mealtype"


    const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    const val TEXT_API_ENDPOINT : String = "food/trivia/random"

    const val TEXT_API_KEY :String = "rapidapi-key"

    const val CATEGORY_QUERY :String = "c"
    const val LOOKUP_QUERY :String = "i"
    const val DISCOVER_API_KEY: String = "rapidapi-key"
    const val TEXT_BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"

    const val TEXT_API_KEY_VALUE: String = "9b32d81d7amshe0ce1382dfcd1d5p1fcd76jsn4f06da80c43e"

    const val DISCOVER_API_KEY_VALUE: String = "849100c57dmshafe55409cbbea74p157eacjsnaae6bcc77d05"


    const val DISCOVER_BASE_URL = "https://cooking-recipe2.p.rapidapi.com/"

    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "FilterSelection"



    fun dishTypes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("snacks")
        list.add("Swallow")
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Dinner")
        list.add("Rice")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")
        return list
    }

    fun mealTime(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Dinner")
        list.add("Other")
        return list
    }

    fun dishCategories(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Eba")
        list.add("Amala")
        list.add("Semo")
        list.add("Apu")
        list.add("Cassava Fufu")
        list.add("Pounded Yam")
        list.add("Tuwo")
        list.add("Wheat")
        list.add("Poundo Yam")
        list.add("Tuwo Shinkafa")
        list.add("Tuwo Masara")
        list.add("Potato Fufu")
        list.add("Corn Fufu")
        list.add("Carrot Fufu")
        list.add("Cocoyam Fufu")
        list.add("Tigernut Fufu")
        list.add("Oat Fufu")
        list.add("Yam")
        list.add("Rice")
        list.add("Jollof Rice")
        list.add("Fried Rice")
        list.add("Ofada Rice")
        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Pudding & Dessert")
        list.add("Drinks")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Sandwiches & Wraps")
        list.add("Cookies")
        list.add("Pasta & risotto")
        list.add("Salad")
        list.add("Bread & doughs")
        list.add("Curry")
        list.add("Vegetable side")
        list.add("Soup")
        list.add("Antipasti")
        list.add("Roast")
        list.add("Shawarma")
        list.add("Pies & Pastries")
        list.add("Sauces & Condiments")
        list.add("Drinks")
        list.add("Meatballs")
        list.add("Muffins")
        list.add("Pasta Bakes")
        list.add("Other")
        return list
    }


    fun dishCookTime(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("10 MINS")
        list.add("15 MINS")
        list.add("20 MINS")
        list.add("30 MINS")
        list.add("45 MINS")
        list.add("50 MINS")
        list.add("60 MINS")
        list.add("90 MINS")
        list.add("120 MINS")
        list.add("150 MINS")
        list.add("180 MINS")
        return list
    }
}