package com.welbtech.autopart.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.welbtech.autopart.model.Cart
import com.welbtech.autopart.view.adapters.CartAdapter
import com.welbtech.autopart.view.util.bottomOnNavOnBackPress
import com.welbtech.autopart.view.util.hideBottomNavOnNav
import com.welbtech.autopart.R
import com.welbtech.autopart.databinding.FragmentCartBinding
import com.welbtech.autopart.model.MotorAccessories
import com.welbtech.autopart.view.adapters.MotorAdapter
import com.welbtech.autopart.viewmodel.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartImpl, MotorAdapter.MotorImpl {
    lateinit var binding: FragmentCartBinding

    private var cartItems = mutableListOf<Cart>()
    private val totalCartPrice=MutableLiveData<Int>()
    private var quantity: Int = 0
    private var amt: Int = 0

    private val productViewmodel: ProductViewModel by viewModels()

    companion object {
        fun newInstance() = CartFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater)

        val recyclerAdapter: CartAdapter by lazy {
            CartAdapter(this)
        }

        binding.checkOutPage.setOnClickListener {
            hideBottomNavOnNav(R.id.action_cartFragment_to_checkoutFragment)
        }

        val cartView = binding.cartRecView
        cartView.adapter = recyclerAdapter
        cartView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        cartView.setHasFixedSize(true)


        productViewmodel.getAllFromCart.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                recyclerAdapter.submitList(it)
                cartItems.clear()
                amt =0
                cartItems.addAll(it)
            }

            cartItems.forEach {
                  amt += it.price!!.toInt()
                 quantity += it.quantity
                 totalCartPrice.value = amt
                Log.d("AMOUNT", ":::::::::::::::::::${amt}")
            }

            Log.d("CART", "OBSERVER:::::::::::::::::::${totalCartPrice.value}")
        })
         totalCartPrice.observe(viewLifecycleOwner, Observer {expenses->
                Log.d("Total", "OBSERVER:::::::::::::::::::${expenses}")
                binding.totalPriceFrag.text = "*Ghc $expenses"
            })



        val motorAdapter: MotorAdapter by lazy {
            MotorAdapter(this)
        }

        val motorRecyclerView = binding.RecomRecViewProductDetailsPage
        motorRecyclerView.adapter = motorAdapter
        motorRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        motorRecyclerView.setHasFixedSize(true)

        productViewmodel.getAllFromMotors.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                motorAdapter.submitList(it)
            }
        })



        return binding.root
    }

    override fun onItemDeleteListener(car: Cart) {
        productViewmodel.deleteSingleCart(car)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomOnNavOnBackPress(R.id.action_cartFragment_to_homeFragment)
    }

    override fun onViewDetailsListener(cart: MotorAccessories) {
     Toast.makeText(activity, "Not yet implemented",Toast.LENGTH_SHORT).show()
    }

    override fun onAddToFavoriteListener(favorites: MotorAccessories) {
       Toast.makeText(activity, "Not yet implemented",Toast.LENGTH_SHORT).show()
    }

    override fun onAddRatingListener(rating: Float) {
      Toast.makeText(activity, "Not yet implemented",Toast.LENGTH_SHORT).show()
    }

}