package com.berfinilik.myfoodapp.ui.fragments

import Constants.Companion.CATEGORY_NAME
import Constants.Companion.MEAL_AREA
import Constants.Companion.MEAL_ID
import Constants.Companion.MEAL_NAME
import Constants.Companion.MEAL_STR
import Constants.Companion.MEAL_THUMB
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.berfinilik.myfoodapp.adapters.FavoriteMealsRecyclerAdapter
import com.berfinilik.myfoodapp.data.pojo.MealDB
import com.berfinilik.myfoodapp.mvvm.DetailsMVVM
import com.berfinilik.myfoodapp.ui.MealBottomDialog
import com.berfinilik.myfoodapp.ui.activites.MealDetailesActivity
import com.berfinilik.myfoodapp.R
import com.berfinilik.myfoodapp.databinding.FragmentFavoriteMealsBinding


import com.google.android.material.snackbar.Snackbar


class FavoriteMeals : Fragment() {
    lateinit var recView:RecyclerView
    lateinit var fBinding: FragmentFavoriteMealsBinding
    private lateinit var myAdapter: FavoriteMealsRecyclerAdapter
    private lateinit var detailsMVVM: DetailsMVVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = FavoriteMealsRecyclerAdapter()
        detailsMVVM = ViewModelProvider(this).get(DetailsMVVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fBinding = FragmentFavoriteMealsBinding.inflate(inflater,container,false)
        return fBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView(view)
        onFavoriteMealClick()
        onFavoriteLongMealClick()
        observeBottomDialog()

        detailsMVVM.observeSaveMeal().observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                myAdapter.setFavoriteMealsList(it)
                if (it.isEmpty()) {
                    fBinding.tvFavEmpty.visibility = View.VISIBLE
                } else {
                    fBinding.tvFavEmpty.visibility = View.GONE
                }
            }
        })

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favoriteMeal = myAdapter.getMelaByPosition(position)
                detailsMVVM.deleteMeal(favoriteMeal)
                showDeleteSnackBar(favoriteMeal)
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recView)

    }

    private fun showDeleteSnackBar(favoriteMeal: MealDB) {
        Snackbar.make(requireView(),"Meal was deleted",Snackbar.LENGTH_LONG).apply {
            setAction("undo",View.OnClickListener {
                detailsMVVM.insertMeal(favoriteMeal)
            }).show()
        }
    }

    private fun observeBottomDialog() {
        detailsMVVM.observeMealBottomSheet().observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                val bottomDialog = MealBottomDialog()
                val b = Bundle()
                if (it.isNotEmpty()) {
                    b.putString(CATEGORY_NAME, it[0].strCategory)
                    b.putString(MEAL_AREA, it[0].strArea)
                    b.putString(MEAL_NAME, it[0].strMeal)
                    b.putString(MEAL_THUMB, it[0].strMealThumb)
                    b.putString(MEAL_ID, it[0].idMeal)
                    bottomDialog.arguments = b
                    bottomDialog.show(childFragmentManager, "Favorite bottom dialog")
                }
            }
        })
    }


        private fun prepareRecyclerView(v:View) {
        recView =v.findViewById<RecyclerView>(R.id.fav_rec_view)
        recView.adapter = myAdapter
        recView.layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
    }

    private fun onFavoriteMealClick(){
        myAdapter.setOnFavoriteMealClickListener(object : FavoriteMealsRecyclerAdapter.OnFavoriteClickListener{
            override fun onFavoriteClick(meal: MealDB) {
                val intent = Intent(context, MealDetailesActivity::class.java)
                intent.putExtra(MEAL_ID,meal.mealId.toString())
                intent.putExtra(MEAL_STR,meal.mealName)
                intent.putExtra(MEAL_THUMB,meal.mealThumb)
                startActivity(intent)
            }

        })
    }

    private fun onFavoriteLongMealClick() {
        myAdapter.setOnFavoriteLongClickListener(object : FavoriteMealsRecyclerAdapter.OnFavoriteLongClickListener{
            override fun onFavoriteLongCLick(meal: MealDB) {
                detailsMVVM.getMealByIdBottomSheet(meal.mealId.toString())
            }

        })
    }


}