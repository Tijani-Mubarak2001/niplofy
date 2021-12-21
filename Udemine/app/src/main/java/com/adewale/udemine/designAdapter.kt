package com.adewale.udemine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.card_item.view.*
import com.bumptech.glide.Glide

class designAdapter (private val context: Context, val dishes: List<DiscoverDishListObject.Category>) : PagerAdapter(){

    override fun getCount(): Int {
        return dishes.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val dish = dishes[position]
        val view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false)

        Glide.with(context)
            .load(dish.strCategoryThumb)
            .centerCrop()
            .into(view.coursesimages)
        view.course_title.text = dish.strCategory
        view.description.text = dish.strCategoryDescription


        return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}