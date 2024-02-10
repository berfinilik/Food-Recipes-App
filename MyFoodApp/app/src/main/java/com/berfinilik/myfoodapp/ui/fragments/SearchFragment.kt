package com.berfinilik.myfoodapp.ui.fragments

import Constants.Companion.MEAL_ID
import Constants.Companion.MEAL_STR
import Constants.Companion.MEAL_THUMB
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.berfinilik.myfoodapp.adapters.MealRecyclerAdapter
import com.berfinilik.myfoodapp.mvvm.SearchMVVM
import com.berfinilik.myfoodapp.ui.activites.MealDetailesActivity
import com.berfinilik.myfoodapp.databinding.FragmentSearchBinding
import com.bumptech.glide.Glide


class SearchFragment : Fragment() {
    private lateinit var myAdapter: MealRecyclerAdapter
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchMvvm: SearchMVVM
    private var mealId = ""
    private var mealStr = ""
    private var mealThub = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = MealRecyclerAdapter()
        searchMvvm = ViewModelProvider(this).get(SearchMVVM::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchClick()
        observeSearchLiveData()
        setOnMealCardClick()
    }

    private fun setOnMealCardClick() {
        binding.searchedMealCard.setOnClickListener {
            val intent = Intent(context, MealDetailesActivity::class.java)

            intent.putExtra(MEAL_ID, mealId)
            intent.putExtra(MEAL_STR, mealStr)
            intent.putExtra(MEAL_THUMB, mealThub)

            startActivity(intent)


        }
    }

    private fun onSearchClick() {
        binding.icSearch.setOnClickListener {
            searchMvvm.searchMealDetail(binding.edSearch.text.toString(), context)

        }
    }

    private fun observeSearchLiveData() {
        searchMvvm.observeSearchLiveData().observe(viewLifecycleOwner, { t ->
            t?.let {
                if (it == null) {
                    Toast.makeText(context, "No such a meal", Toast.LENGTH_SHORT).show()
                } else {
                    binding.apply {
                        mealId = it.idMeal
                        mealStr = it.strMeal
                        mealThub = it.strMealThumb

                        Glide.with(requireContext().applicationContext)
                            .load(it.strMealThumb)
                            .into(imgSearchedMeal)

                        tvSearchedMeal.text = it.strMeal
                        searchedMealCard.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}
