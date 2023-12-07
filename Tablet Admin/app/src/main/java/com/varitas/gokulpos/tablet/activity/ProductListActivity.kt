package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.ProductsAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityProductBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.ChangeQuantityPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemFilterPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ManageAddToSalePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ManageGroupPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.MultiPackPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.PricePopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.response.ScanBarcode
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint class ProductListActivity : BaseActivity() {

    private lateinit var binding: ActivityProductBinding
    private lateinit var productsAdapter: ProductsAdapter
    val viewModel: ProductViewModel by viewModels()
    val orderViewModel: OrdersViewModel by viewModels()
    private lateinit var dialogFragmentMultiPack: MultiPackPopupDialog
    private lateinit var product: ProductList
    val viewModelFeature: ProductFeatureViewModel by viewModels()
    private var parentId = 0
    private var isFromOrder = false
    private var appliedFilter = false

    companion object {
        lateinit var Instance: ProductListActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
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
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        product = ProductList()
        appliedFilter = false
        binding.apply {
            binding.layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_ProductList)
            binding.layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@ProductListActivity, R.drawable.ic_home))
            binding.layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        productsAdapter = ProductsAdapter { productList, pos, enums ->
            product = productList
            when (enums) {
                Enums.ClickEvents.PRICE -> managePrice()
                Enums.ClickEvents.QUANTITY -> manageQty(product)
                Enums.ClickEvents.FAVOURITE -> manageFavourite(product.id!!, pos)
                else -> {}
            }
        }
    }

    private fun manageFavourite(id: Int, pos: Int) {
        viewModel.makeShortcut(id).observe(this) {
            if (it) {
                productsAdapter.apply {
                    list[pos].isShortcut = !list[pos].isShortcut!!
                    notifyItemChanged(pos)
                }
            }
        }
    }

    private fun managePrice() {
        viewModel.getPriceList(product.id!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) {
                    if (it.isNotEmpty()) {
                        if (it.size == 1) {
                            if (it[0].price.isNotEmpty()) {
                                if (it[0].price.size > 0) {
                                    if (it[0].price.size == 1) managePrice(it[0].price[0], product.name!!)
                                    else manageMultiPack(it[0].price, product.name!!, product.id!!)
                                }
                            }
                        } else {
                            val prices = ArrayList<ItemPrice>()
                            for (data in it) {
                                for (price in data.price)
                                    prices.add(price)
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
            header = Header(resources.getString(R.string.hint_SKU), resources.getString(R.string.lbl_Description), resources.getString(R.string.lbl_Price), resources.getString(R.string.lbl_Quantity), resources.getString(R.string.lbl_Type))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToSubMenu(parentId, isFromOrder)
            }

            imageViewSearch.clickWithDebounce {
                clearSearch()
                if (appliedFilter) getProducts()
                else manageFilter()
            }

            buttonAddProduct.clickWithDebounce {
                val intent = Intent(this@ProductListActivity, AddProductActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.ORDER, isFromOrder)
                intent.putExtra(Default.PRODUCT, ScanBarcode())
                openActivity(intent)
            }

            buttonEditProduct.clickWithDebounce {
                val index = isApplicable()
                if (index >= 0) {
                    val intent = Intent(this@ProductListActivity, EditProductActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    intent.putExtra(Default.PRODUCT, productsAdapter.list[index])
                    openActivity(intent)
                } else showSweetDialog(this@ProductListActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_AtLeastOne)))
            }

            buttonMakeGroup.clickWithDebounce {
                val index = isApplicable()
                if (index >= 0) {
                    product = productsAdapter.list[index]
                    manageGroup(product.id!!, "${product.name} (${product.sku})", index)
                } else showSweetDialog(this@ProductListActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_AtLeastOne)))
            }

            buttonAddToSale.clickWithDebounce {
                val index = isApplicable()
                if (index >= 0) {
                    product = productsAdapter.list[index]
                    manageAddToSale(product, index)
                } else showSweetDialog(this@ProductListActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_AtLeastOne)))
            }

            buttonPrintLabel.clickWithDebounce {

            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    productsAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    clearSearch()
                    getProducts()
                }
                setColorSchemeColors(ContextCompat.getColor(this@ProductListActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToSubMenu(parentId,isFromOrder)
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
    fun managePrice(productDetail: ItemPrice, productName: String, isFromMultiPack: Boolean = false) {

        if (isFromMultiPack) {
            if (dialogFragmentMultiPack.isVisible) dialogFragmentMultiPack.dismiss()
        }

        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = PricePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(PricePopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.TITLE, productName)
        bundle.putParcelable(Default.PRODUCT, productDetail)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, PricePopupDialog::class.java.name)
        dialogFragment.setListener(object : PricePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                clearSearch()
                when (typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (!isFromMultiPack) getProducts()
                        else managePrice()
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
    private fun manageMultiPack(productDetail: ArrayList<ItemPrice>, productName: String, pos: Int) {
        val ft = supportFragmentManager.beginTransaction()
        dialogFragmentMultiPack = MultiPackPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(MultiPackPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.TITLE, productName)
        bundle.putInt(Default.PRODUCT_ID, pos)
        bundle.putSerializable(Default.PRODUCT_DETAIL, productDetail)
        dialogFragmentMultiPack.arguments = bundle
        dialogFragmentMultiPack.isCancelable = false
        dialogFragmentMultiPack.show(ft, MultiPackPopupDialog::class.java.name)
        dialogFragmentMultiPack.setListener(object : MultiPackPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
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
        viewModel.fetchProducts().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                productsAdapter.apply {
                    clearData()
                    setList(it)
                }
                appliedFilter = false
                binding.imageViewSearch.setImageDrawable(ContextCompat.getDrawable(this@ProductListActivity, R.drawable.ic_filter))
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

    //region To manage group
    private fun manageGroup(id: Int, name: String, index: Int) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ManageGroupPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ManageGroupPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putInt(Default.ID, id)
        bundle.putString(Default.PRODUCT, name)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ManageGroupPopupDialog::class.java.name)
        dialogFragment.setListener(object : ManageGroupPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener() {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                productsAdapter.apply {
                    product.isSelected = false
                    list[index].isSelected = false
                    notifyItemChanged(index)
                }
            }
        })
    }
    //endregion

    //region To manage add to sales
    private fun manageAddToSale(product: ProductList, index: Int) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ManageAddToSalePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ManageAddToSalePopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelable(Default.PRODUCT, product)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ManageAddToSalePopupDialog::class.java.name)
        dialogFragment.setListener(object : ManageAddToSalePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener() {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                productsAdapter.apply {
                    product.isSelected = false
                    list[index].isSelected = false
                    notifyItemChanged(index)
                }
            }
        })
    }
    //endregion

    //region To manage filters
    private fun manageFilter() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemFilterPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ItemFilterPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemFilterPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemFilterPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, departmentId: Int, categoryId: Int, brandId: Int, itemTypeId: Int) {
                clearSearch()
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> applyFilter(departmentId, categoryId, brandId, itemTypeId)
                    else -> {
                    }
                }
            }
        })
    }

    private fun applyFilter(departmentId: Int, categoryId: Int, brandId: Int, itemTypeId: Int) {
        viewModel.fetchProductsFilterWise(departmentId, categoryId, brandId, itemTypeId).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                productsAdapter.apply {
                    clearData()
                    setList(it)
                    if (it.isEmpty()) notifyDataSetChanged()
                }
                appliedFilter = true
                binding.imageViewSearch.setImageDrawable(ContextCompat.getDrawable(this@ProductListActivity, R.drawable.ic_reset))
            }
        }
    }
    //endregion

    private fun isApplicable(): Int {
        productsAdapter.apply {
            return list.indexOfFirst { it.isSelected }
        }
    }
}