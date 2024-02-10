package com.berfinilik.myfoodapp.ui.fragments

import Constants.Companion.CATEGORY_NAME
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.berfinilik.myfoodapp.adapters.CategoriesRecyclerAdapter
import com.berfinilik.myfoodapp.data.pojo.Category
import com.berfinilik.myfoodapp.mvvm.CategoryMVVM
import com.berfinilik.myfoodapp.ui.activites.MealActivity
import com.berfinilik.myfoodapp.R
import com.berfinilik.myfoodapp.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment(R.layout.fragment_category) {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var myAdapter: CategoriesRecyclerAdapter
    private lateinit var categoryMvvm: CategoryMVVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = CategoriesRecyclerAdapter()
        categoryMvvm = ViewModelProvider(this).get(CategoryMVVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        observeCategories()
        onCategoryClick()
    }

    private fun onCategoryClick() {
       myAdapter.onItemClicked(object : CategoriesRecyclerAdapter.OnItemCategoryClicked{
           override fun onClickListener(category: Category) {
               val intent = Intent(context, MealActivity::class.java)
               intent.putExtra(CATEGORY_NAME,category.strCategory)
               startActivity(intent)
           }
       })
    }

    private fun observeCategories() {
        categoryMvvm.observeCategories().observe(viewLifecycleOwner, Observer { t ->
            if (t.isNullOrEmpty()) {
                // Liste boşsa, bir hata durumu olabilir. Burada gerekli işlemleri yapabilirsiniz.
                // Örneğin: Log.e("CategoryFragment", "No categories found")
            } else {
                // Liste doluysa, kategorileri adaptöre set edebilirsiniz.
                myAdapter.setCategoryList(t)
            }
        })
    }

    private fun prepareRecyclerView() {
        binding.favoriteRecyclerView.apply {
            adapter = myAdapter
            layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
        }
    }


}