package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.ProductsAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.ChangeQuantityPopupDialog
import com.varitas.gokulpos.fragmentDialogs.MultiPackPopupDialog
import com.varitas.gokulpos.fragmentDialogs.PricePopupDialog
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.response.ScanBarcode
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint class ProductListActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    private lateinit var productsAdapter : ProductsAdapter
    val viewModel : ProductViewModel by viewModels()
    private var previousScrolled : Boolean = false
    private var currentPage : Int = 1
    private lateinit var dialogFragmentMultiPack : MultiPackPopupDialog
    private lateinit var product : ProductList
    private var parentId = 0

    companion object {
        lateinit var Instance : ProductListActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        product = ProductList()
        previousScrolled = false
        currentPage = 1
        binding.apply {
            binding.layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_ProductList)
            binding.layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@ProductListActivity, R.drawable.ic_home))
            binding.layoutToolbar.imageViewBack.visibility = View.VISIBLE

        /*binding.linearLayoutHeader.visibility = View.VISIBLE
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt(),
                0,
                resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt(),
                resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt())
            binding.swipeRefresh.layoutParams = layoutParams*/
        }
        productsAdapter = ProductsAdapter { productList, _, enums->
            product = productList
            when(enums) {
                Enums.ClickEvents.PRICE -> {
                    managePrice()
                }

                Enums.ClickEvents.VIEW -> {
                    val intent = Intent(this@ProductListActivity, EditProductActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.PRODUCT, product)
                    openActivity(intent)
                }

                Enums.ClickEvents.QUANTITY -> manageQty(product)
                else -> {}
            }
        }
    }

    private fun managePrice() {
        viewModel.getPriceList(product.id!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it != null) {
                    if(it.isNotEmpty()) {
                        if(it.size == 1) {
                            if(it[0].price.isNotEmpty()) {
                                if(it[0].price.size > 0) {
                                    if(it[0].price.size == 1) {
                                        managePrice(it[0].price[0], product.name!!)
                                    } else manageMultiPack(it[0].price, product.name!!, product.id!!)
                                }
                            }
                        } else {
                            val prices = ArrayList<ItemPrice>()
                            for(data in it) {
                                for(price in data.price) prices.add(price)
                            }
                            manageMultiPack(prices, product.name!!, product.id!!)
                        }

                    }
                }
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = productsAdapter
                layoutManager = LinearLayoutManager(this@ProductListActivity)
            }
            layoutHeader.root.visibility = View.GONE
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@ProductListActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }
            fabAdd.clickWithDebounce {
                val intent = Intent(this@ProductListActivity, AddProductActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.PRODUCT, ScanBarcode())
                openActivity(intent)
            }
            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    productsAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    currentPage = 1
                    getProducts()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@ProductListActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToProducts(parentId)
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        getProducts()
    } //endregion

    //region To manage price of product
    fun managePrice(productDetail : ItemPrice, productName : String, isFromMultiPack : Boolean = false) {

        if(isFromMultiPack) {
            if(dialogFragmentMultiPack.isVisible) dialogFragmentMultiPack.dismiss()
        }

        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = PricePopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(PricePopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.TITLE, productName)
        bundle.putParcelable(Default.PRODUCT, productDetail)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, PricePopupDialog::class.java.name)
        dialogFragment.setListener(object : PricePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }
                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isFromMultiPack)
                            getProducts()
                        else {
                            managePrice()
                        }
                    }
                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@ProductListActivity, resources.getString(R.string.lbl_SomethingWrong))
                    }
                    else -> {
                    }
                }
            }
        })
    } //endregion

    //region To manage price of product
    private fun manageMultiPack(productDetail : List<ItemPrice>, productName : String, pos : Int) {
        val ft = supportFragmentManager.beginTransaction()
        dialogFragmentMultiPack = MultiPackPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(MultiPackPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.TITLE, productName)
        bundle.putInt(Default.PRODUCT_ID, pos)
        bundle.putSerializable(Default.PRODUCT, productDetail as ArrayList)
        dialogFragmentMultiPack.arguments = bundle
        dialogFragmentMultiPack.isCancelable = false
        dialogFragmentMultiPack.show(ft, MultiPackPopupDialog::class.java.name)
        dialogFragmentMultiPack.setListener(object : MultiPackPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragmentMultiPack.isVisible) dialogFragmentMultiPack.dismiss()
                        getProducts()
                    }

                    else -> {
                    }
                }
            }
        })
    } //endregion

    //region To manage Qty of product
    private fun manageQty(product : ProductList) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ChangeQuantityPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(ChangeQuantityPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.TITLE, product.name)
        bundle.putParcelable(Default.PRODUCT, product)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ChangeQuantityPopupDialog::class.java.name)
        dialogFragment.setListener(object : ChangeQuantityPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.UPDATE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getProducts()
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@ProductListActivity, resources.getString(R.string.lbl_SomethingWrong))
                    }

                    else -> {
                    }
                }
            }
        })
    } //endregion

    //region To get products
    private fun getProducts() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchProducts().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                productsAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    private fun clearSearch() {
        binding.apply {
            searchProduct.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }

    override fun onResume() {
        binding.shimmer.startShimmer()
        super.onResume()
    }

    override fun onPause() {
        binding.shimmer.stopShimmer()
        super.onPause()
    }
}