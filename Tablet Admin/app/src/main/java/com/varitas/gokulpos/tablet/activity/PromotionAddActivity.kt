package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.AutoCompleteAdapter
import com.varitas.gokulpos.tablet.adapter.PromoteAddItemAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityPromotionaddBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.WeekDaysPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.response.DiscountMapList
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.SalesPromotion
import com.varitas.gokulpos.tablet.response.ValidatePromotion
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.CustomerViewModel
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@AndroidEntryPoint class PromotionAddActivity : BaseActivity() {
    private lateinit var binding : ActivityPromotionaddBinding
    val viewModel : OrdersViewModel by viewModels()
    private val viewModelDropDown : ProductFeatureViewModel by viewModels()
    private val customerViewModel : CustomerViewModel by viewModels()
    private lateinit var promotionTypeList : ArrayList<CommonDropDown>
    private lateinit var promotionTypeSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var promotionType : CommonDropDown
    private lateinit var priorityList : ArrayList<CommonDropDown>
    private lateinit var prioritySpinner : ArrayAdapter<CommonDropDown>
    private lateinit var priority : CommonDropDown
    private lateinit var customerList : ArrayList<CommonDropDown>
    private lateinit var customerSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var customer : CommonDropDown
    private lateinit var itemGroupList : ArrayList<CommonDropDown>
    private lateinit var itemGroupSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var itemGroup : CommonDropDown
    private lateinit var discountList: ArrayList<CommonDropDown>
    private lateinit var weekDays: ArrayList<DataDetails>
    private lateinit var discountSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var discount: CommonDropDown
    private lateinit var suggestionAdapter: AutoCompleteAdapter
    private lateinit var promoteAddItemAdapter: PromoteAddItemAdapter
    private lateinit var itemDetails: FavouriteItems
    private lateinit var materialStartDateBuilder: MaterialDatePicker.Builder<Long>
    private lateinit var materialStartDatePicker: MaterialDatePicker<Long>
    private lateinit var materialEndDateBuilder: MaterialDatePicker.Builder<Long>
    private lateinit var materialEndDatePicker: MaterialDatePicker<Long>
    private lateinit var dropDownList: ArrayList<CommonDropDown>
    private lateinit var dropDownSpinner: ArrayAdapter<CommonDropDown>
    private var dropDown: CommonDropDown? = null
    private var promotionId = 0

    companion object {
        lateinit var Instance: PromotionAddActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionaddBinding.inflate(layoutInflater)
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
        binding.apply {
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@PromotionAddActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        promotionId = intent.extras?.getInt(Default.PROMOTION)!!
        promoteAddItemAdapter = PromoteAddItemAdapter {
            promoteAddItemAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
            }
        }
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        materialStartDateBuilder = MaterialDatePicker.Builder.datePicker()
        materialStartDatePicker = materialStartDateBuilder.setCalendarConstraints(constraintsBuilder.build()).build()
        materialEndDateBuilder = MaterialDatePicker.Builder.datePicker()
        materialEndDatePicker = materialEndDateBuilder.build()

        suggestionAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        promotionTypeList = ArrayList()
        weekDays = ArrayList()
        promotionTypeSpinner = ArrayAdapter(this, R.layout.spinner_items, promotionTypeList)
        customerList = ArrayList()
        customer = CommonDropDown(0, resources.getString(R.string.lbl_Customer))
        customerSpinner = ArrayAdapter(this, R.layout.spinner_items, customerList)
        priorityList = ArrayList()
        prioritySpinner = ArrayAdapter(this, R.layout.spinner_items, priorityList)
        itemGroupList = ArrayList()
        itemGroup = CommonDropDown(0, resources.getString(R.string.lbl_ItemGroup))
        itemGroupSpinner = ArrayAdapter(this, R.layout.spinner_items, itemGroupList)
        discountList = ArrayList()
        discountSpinner = ArrayAdapter(this, R.layout.spinner_items, discountList)
        dropDownList = ArrayList()
        dropDownSpinner = ArrayAdapter(this, R.layout.spinner_items, dropDownList)

        itemDetails = FavouriteItems()
        //weekDays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        weekDays.add(DataDetails(1, "Monday"))
        weekDays.add(DataDetails(2, "Tuesday"))
        weekDays.add(DataDetails(3, "Wednesday"))
        weekDays.add(DataDetails(4, "Thursday"))
        weekDays.add(DataDetails(5, "Friday"))
        weekDays.add(DataDetails(6, "Saturday"))
        weekDays.add(DataDetails(7, "Sunday"))
        weekDays.add(DataDetails(8, "All", true))
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Promotion, if(promotionId > 0) resources.getString(R.string.lbl_Edit) else resources.getString(R.string.lbl_Add))
            recycleViewPromoteAddItem.apply {
                adapter = promoteAddItemAdapter
                layoutManager = LinearLayoutManager(this@PromotionAddActivity)
            }
            textInputItemSearch.setAdapter(suggestionAdapter)
            textInputItemSearch.threshold = 0
            header = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_Quantity), resources.getString(R.string.lbl_Type), resources.getString(R.string.lbl_DiscountAmount), resources.getString(R.string.lbl_BogoOffer), resources.getString(R.string.lbl_Priority), resources.getString(R.string.lbl_Action))
            val startDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
            val endDate = Constants.dateFormat_MMM_dd_yyyy.format(1.toDay().sinceNow)
            textInputStartDate.setText(startDate)
            textInputEndDate.setText(endDate)
        }
    } //endregion

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        viewModelDropDown.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModelDropDown.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        customerViewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        if(promotionId > 0) {
            viewModel.getSalesPromotionDetail(promotionId).observe(this) {
                CoroutineScope(Dispatchers.Main).launch {
                    if(it != null) {
                        binding.apply {
                            textInputPromotionName.setText(it.name)
                            checkBoxLimitedTime.isChecked = if(it.isLimitedTime!!) it.isLimitedTime!! else false
                            textInputStartDate.setText(if(it.startDate != null) Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormat_yyyy_MM_dd, parseDate = it.startDate)) else "")
                            textInputEndDate.setText(if(it.endDate != null) Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormat_yyyy_MM_dd, parseDate = it.endDate)) else "")
                            textInputCouponCode.setText(it.couponCode)
                            if(it.discountMapList.size > 0) {
                                promoteAddItemAdapter.apply {
                                    setList(it.discountMapList)
                                }
                            }
                        }
                        manageDropDown(it)
                        for(day in it.mWeekDaysList) {
                            val index = weekDays.indexOfFirst { it.id!! == day.id }
                            if(index >= 0) weekDays[index].isSelected = true
                        }
                    }
                }
            }
        } else manageDropDown()

        viewModelDropDown.getGroupTypeDropdown(Default.TYPE_DISCOUNT).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                discountSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    discount = CommonDropDown(0, resources.getString(R.string.lbl_DiscountType))
                    add(discount)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        prioritySpinner.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
            priority = CommonDropDown(0, resources.getString(R.string.Priority, resources.getString(R.string.lbl_Select)))
            add(priority)
            add(CommonDropDown(1, "1"))
            add(CommonDropDown(2, "2"))
            add(CommonDropDown(3, "3"))
            add(CommonDropDown(4, "4"))
            add(CommonDropDown(5, "5"))
            add(CommonDropDown(6, "6"))
            add(CommonDropDown(7, "7"))
            add(CommonDropDown(8, "8"))
            add(CommonDropDown(9, "9"))
            add(CommonDropDown(10, "10"))
            notifyDataSetChanged()
        }

    }
    //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@PromotionAddActivity, SalesPromotionActivity::class.java))
            }

            textInputItemSearch.setOnItemClickListener { parent, _, position, _->
                itemDetails = parent.getItemAtPosition(position) as FavouriteItems
                textInputItemName.setText(itemDetails.name)
                textInputQuantity.setText("1")
                textInputItemSearch.text.clear()
            }

            checkBoxIsBOGO.setOnCheckedChangeListener { _, isChecked->
                if(isChecked) {
                    layoutIsBongo.visibility = View.VISIBLE
                    layoutDiscount.visibility = View.GONE
                    textInputDiscountValue.setText("0.00")
                    spinnerDiscountType.setSelection(0)
                } else {
                    layoutIsBongo.visibility = View.GONE
                    layoutDiscount.visibility = View.VISIBLE
                    discountSpinner.notifyDataSetChanged()
                }
            }

            buttonWeekDays.clickWithDebounce {
                manageWeekDays()
            }

            buttonAddItem.clickWithDebounce {
                val detailId = when (promotionType.value) {
                    Default.PROMOTION_ITEM -> itemDetails.id
                    Default.PROMOTION_ORDER -> 0
                    Default.PROMOTION_OPEN_DISCOUNT -> 0
                    else -> if (dropDown != null) dropDown!!.value else 0
                }

                if(!TextUtils.isEmpty(textInputPromotionName.text.toString().trim())) {
                    if(checkBoxLimitedTime.isChecked) checkPromotions(detailId!!)
                    else {
                        if ((weekDays.filter { it.isSelected } as ArrayList<DataDetails>).size > 0) {
                            checkPromotions(detailId!!)
                        } else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_SelectWeekDays))
                    }
                } else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PromotionValidation))
            }

            spinnerPromotionType.apply {
                adapter = promotionTypeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        promotionType = spinner?.selectedItem as CommonDropDown
                        setSpinnerData()
                    }
                })
            }

            spinner.apply {
                adapter = dropDownSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        dropDown = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerDiscountType.apply {
                adapter = discountSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        discount = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerPriority.apply {
                adapter = prioritySpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        priority = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            textInputItemSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {}

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                    if(!s.isNullOrEmpty()) fetchSuggestions(s.toString())
                }

                override fun afterTextChanged(s : Editable?) {}
            })

            materialStartDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                textInputStartDate.setText(formatted)
            }

            materialEndDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                textInputEndDate.setText(formatted)
            }

            textInputStartDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialStartDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            textInputEndDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialEndDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                val name = textInputPromotionName.text.toString().trim()
                val couponCode = textInputCouponCode.text.toString().trim()
                if (name.isNotEmpty() && promoteAddItemAdapter.list.isNotEmpty()) {
                    if (promotionId == 0) {
                        val req = SalesPromotion(
                            name = name,
                            startDate = Constants.dateFormat_yyyy_MM_dd.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputStartDate.text.toString().trim())),
                            endDate = getLimitedDate(textInputEndDate.text.toString().trim()),
                            isLimitedTime = checkBoxLimitedTime.isChecked,
                            customerGroup = customer.value!!,
                            itemGroup = itemGroup.value!!,
                            remarks = "",
                            couponCode = couponCode,
                            couponName = "",
                            isSerialized = true,
                            startNum = 0,
                            endNum = 0,
                            mWeekDaysList = weekDays.filter { it.isSelected } as ArrayList<DataDetails>,
                            discountMapList = promoteAddItemAdapter.list
                                                )
                        viewModel.insertSalesPromotion(req).observe(this@PromotionAddActivity) {
                            if(it) openActivity(Intent(this@PromotionAddActivity, SalesPromotionActivity::class.java))
                        }
                    } else {
                        val req = SalesPromotion(
                            id = promotionId,
                            name = name,
                            startDate = Constants.dateFormat_yyyy_MM_dd.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputStartDate.text.toString().trim())),
                            endDate = getLimitedDate(textInputEndDate.text.toString().trim()),
                            isLimitedTime = checkBoxLimitedTime.isChecked,
                            customerGroup = customer.value!!,
                            itemGroup = itemGroup.value!!,
                            remarks = "",
                            couponCode = couponCode,
                            couponName = "",
                            isSerialized = true,
                            startNum = 0,
                            endNum = 0,
                            mWeekDaysList = weekDays.filter { it.isSelected } as ArrayList<DataDetails>,
                            discountMapList = promoteAddItemAdapter.list
                                                )
                        viewModel.updateSalesPromotion(req).observe(this@PromotionAddActivity) {
                            if(it) openActivity(Intent(this@PromotionAddActivity, SalesPromotionActivity::class.java))
                        }
                    }
                } else if (name.isEmpty()) showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PromotionValidation))
                else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PromotionItemValidation))
            }
        }
    } //endregion

    //region To validate Promotion
    private fun validatePromotion(detailId : Int, minAMount : String = "", reqQty : Int = 0) {
        binding.apply {

            val name = when (promotionType.value!!) {
                Default.PROMOTION_ITEM -> if (textInputItemName.text!!.isNotEmpty()) textInputItemName.text.toString().trim() else ""
                Default.PROMOTION_ORDER -> promotionType.label!!
                Default.PROMOTION_OPEN_DISCOUNT -> promotionType.label!!
                else -> if (dropDown != null) dropDown!!.label!! else ""
            }

            val discount = DiscountMapList(
                discountType = if(!checkBoxIsBOGO.isChecked) discount.value else 0,
                isBogo = checkBoxIsBOGO.isChecked,
                sItemName = name,
                quantity = if(textInputQuantity.text!!.isNotEmpty()) textInputQuantity.text.toString().trim().toLong() else 0,
                sDiscountType = if(discount.value!! > 0 && !checkBoxIsBOGO.isChecked) discount.label.toString() else "",
                discountAmount = if(textInputDiscountValue.text!!.isNotEmpty()) textInputDiscountValue.text.toString().trim().toDouble() else 0.00,
                specificationId = if(promotionType.value == Default.PROMOTION_ITEM) itemDetails.specificationId else 0,
                detailId = detailId,
                discountAppId = promotionType.value,
                itemBuy = if(textInputBOGOBuy.text!!.isNotEmpty()) textInputBOGOBuy.text.toString().trim().toInt() else 0,
                itemGet = if(textInputBOGOGet.text!!.isNotEmpty()) textInputBOGOGet.text.toString().trim().toInt() else 0,
                isMinimumAmount = !TextUtils.isEmpty(minAMount),
                minimumAmount = if(!TextUtils.isEmpty(minAMount)) minAMount.toDouble() else 0.00,
                requiredQuantity = reqQty,
                priority = priority.value
            )

            val list = ArrayList<DiscountMapList>()
            list.add(discount)

            val req = ValidatePromotion(
                name = textInputPromotionName.text.toString().trim(),
                startDate = getLimitedDate(textInputStartDate.text.toString().trim()),
                endDate = getLimitedDate(textInputEndDate.text.toString().trim()),
//                startTime = "00:00:00",
//                endTime = "00:00:00",
                mWeekDaysList = weekDays.filter { it.isSelected } as ArrayList<DataDetails>,
                discountMapList = list
                                       )

            viewModel.validatePromotion(req).observe(this@PromotionAddActivity) {
                if(it) {
                    promoteAddItemAdapter.apply {
                        addPromoteList(discount)
                    }
                    //priorityList.remove(priority)
                    //spinnerPriority.setSelection(0)
                    //prioritySpinner.notifyDataSetChanged()
                }
                textInputItemName.text!!.clear()
                textInputQuantity.text!!.clear()
                textInputDiscountValue.text!!.clear()
                textInputBOGOBuy.text!!.clear()
                textInputBOGOGet.text!!.clear()
                textInputMinAmount.text!!.clear()
                textInputReqQty.text!!.clear()
                itemDetails = FavouriteItems()
                spinnerDiscountType.setSelection(0)
                spinner.setSelection(0)
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@PromotionAddActivity, SalesPromotionActivity::class.java))
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    //region To fetch suggestions
    private fun fetchSuggestions(str : String) {
        viewModel.fetchAutoCompleteItems(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestionAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.textInputItemSearch.showDropDown()
                }
            }
        }
    }
    //endregion

    //region To manage drop down
    private fun manageDropDown(promo : SalesPromotion? = null) {
        viewModel.getPromotionTypeDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                promotionTypeSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    addAll(it)
                    if(promo != null) {
                        if(promo.discountMapList.size > 0) {
                            val index = promotionTypeList.indexOfFirst { type-> type.value == promo.discountMapList[0].discountAppId }
                            if(index >= 0) {
                                binding.spinnerPromotionType.setSelection(index)
                                promotionType = promotionTypeList[index]
                            } else {
                                if(it.isNotEmpty()) promotionType = it[0]
                            }

                        }
                    } else {
                        if(it.isNotEmpty()) promotionType = it[0]
                    }
                    notifyDataSetChanged()
                    setSpinnerData()
                }
            }
        }

        viewModel.getCommonGroupDropDown(Default.TYPE_CUSTOMER).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                customerSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    customer = CommonDropDown(0, resources.getString(R.string.lbl_Customer))
                    add(customer)
                    addAll(it)
                    if(promo != null) {
                        val index = customerList.indexOfFirst { type-> type.value == promo.customerGroup }
                        if(index >= 0) {
                            //binding.spinnerCustomer.setSelection(index)
                            customer = customerList[index]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        viewModel.getCommonGroupDropDown(Default.TYPE_ITEM).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                itemGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    itemGroup = CommonDropDown(0, resources.getString(R.string.lbl_ItemGroup))
                    add(itemGroup)
                    addAll(it)
                    if(promo != null) {
                        val index = itemGroupList.indexOfFirst { type-> type.value == promo.itemGroup }
                        if(index >= 0) {
                            // binding.spinnerItemGroup.setSelection(index)
                            itemGroup = itemGroupList[index]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }
    //endregion

    //region To manage week days
    private fun manageWeekDays() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = WeekDaysPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(WeekDaysPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelableArrayList(Default.WEEK, weekDays)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, WeekDaysPopupDialog::class.java.name)
        dialogFragment.setListener(object : WeekDaysPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, days: ArrayList<DataDetails>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> weekDays = ArrayList(days)
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region TO get date if limited checkbox is checked
    private fun getLimitedDate(date : String) : String? {
        return if(binding.checkBoxLimitedTime.isChecked) Constants.dateFormat_yyyy_MM_dd.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = date)) else null
    }
    //endregion

    private fun setSpinnerData() {
        when(promotionType.value) {
            Default.PROMOTION_ITEM -> {
                binding.apply {
                    setupPromotion()
                }
            }

            Default.PROMOTION_BRAND -> {
                viewModelDropDown.getBrandDropDown().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.lbl_SelectBrand))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                        setupPromotion()
                    }
                }
            }

            Default.PROMOTION_DEPARTMENT -> {
                viewModelDropDown.getDepartment().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                        setupPromotion()
                    }
                }
            }

            Default.PROMOTION_CATEGORY -> {
                viewModelDropDown.getParentCategory().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                        setupPromotion()
                    }
                }
            }

            Default.PROMOTION_CUSTOMER -> {
                customerViewModel.getCustomerDropDown().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.lbl_SelectCustomer))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                        setupPromotion(false)
                    }
                }
            }

            Default.PROMOTION_VENDOR -> {
                viewModelDropDown.fetchVendorDropDown().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.lbl_SelectVendor))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                    }
                    setupPromotion()
                }
            }

            Default.PROMOTION_ORDER -> {
                setupPromotion(false)
                binding.linearLayoutOrder.visibility = View.VISIBLE
                binding.linearLayoutData.visibility = View.GONE
            }

            Default.PROMOTION_OPEN_DISCOUNT -> {
                setupPromotion(false)
                binding.linearLayoutOrder.visibility = View.GONE
                binding.linearLayoutData.visibility = View.GONE
            }

            Default.PROMOTION_TENDER -> {
                viewModelDropDown.getTenderDropDown().observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            clear()
                            dropDown = CommonDropDown(0, resources.getString(R.string.Tender, resources.getString(R.string.lbl_Select)))
                            add(dropDown)
                            addAll(it)
                            notifyDataSetChanged()
                        }
                    }
                    setupPromotion(false)
                }
            }
        }
    }

    private fun setupPromotion(isBogoApplicable : Boolean = true) {
        binding.apply {
            checkBoxIsBOGO.isEnabled = isBogoApplicable
            checkBoxIsBOGO.isChecked = false
            linearLayoutData.visibility = View.VISIBLE
            linearLayoutOrder.visibility = View.GONE
            textInputItemSearch.text.clear()
            textInputItemName.text!!.clear()
            textInputQuantity.text!!.clear()
            textInputMinAmount.text!!.clear()
            textInputReqQty.text!!.clear()
            linearLayoutSpinner.visibility = if(promotionType.value != Default.PROMOTION_ITEM) View.VISIBLE else View.GONE
            textInputItemSearch.visibility = if(promotionType.value != Default.PROMOTION_ITEM) View.GONE else View.VISIBLE
            inputLayoutName.visibility = if(promotionType.value != Default.PROMOTION_ITEM) View.GONE else View.VISIBLE
        }
    }

    private fun checkPromotions(detailId : Int) {
        if(priority.value!! > 0) {
            when (promotionType.value) {
                Default.PROMOTION_ORDER -> {
                    val minAMount = if (!TextUtils.isEmpty(binding.textInputMinAmount.text.toString().trim())) binding.textInputMinAmount.text.toString().trim() else ""
                    val reqQty = if (!TextUtils.isEmpty(binding.textInputReqQty.text.toString().trim())) binding.textInputMinAmount.text.toString().trim().toInt() else 0

                    promoteAddItemAdapter.apply {
                        var index = if (!TextUtils.isEmpty(minAMount)) list.indexOfFirst { it.minimumAmount == minAMount.toDouble() }
                        else list.indexOfFirst { it.requiredQuantity == reqQty }

                        if (index < 0) {
                            index = list.indexOfFirst { it.priority == priority.value && it.discountAppId == promotionType.value }
                            if (list.isEmpty()) validatePromotion(detailId)
                            else {
                                if (index < 0) validatePromotion(0, minAMount, reqQty)
                                else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PriorityValidation))
                            }
                        } else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_AlreadyExist))
                    }
                }
                Default.PROMOTION_OPEN_DISCOUNT -> {
                    promoteAddItemAdapter.apply {
                        val index = list.indexOfFirst { it.priority == priority.value && it.discountAppId == promotionType.value }
                        if (list.isEmpty()) validatePromotion(detailId, "", 0)
                        else {
                            if (index < 0) validatePromotion(0)
                            else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PriorityValidation))
                        }
                    }
                }
                else -> {
                    if (detailId > 0) {
                        promoteAddItemAdapter.apply {
                            var index = list.indexOfFirst { it.discountAppId == promotionType.value && it.detailId == detailId }
                            if (index < 0) {
                                index = list.indexOfFirst { it.priority == priority.value && it.discountAppId == promotionType.value }
                                if (list.isEmpty()) validatePromotion(detailId)
                                else {
                                    if (index < 0) validatePromotion(detailId)
                                    else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_PriorityValidation))
                                }
                            } else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_AlreadyExist))
                        }
                    }
                }
            }
        } else showSweetDialog(this@PromotionAddActivity, resources.getString(R.string.lbl_SelectPriority))
    }
}