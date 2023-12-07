package com.varitas.gokulpos.tablet.repositories

import android.content.Context
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.Payments
import com.varitas.gokulpos.tablet.response.SalesSummary
import com.varitas.gokulpos.tablet.response.TopItems
import com.varitas.gokulpos.tablet.response.TopItemsByCategories

class ReportsRepositoryImpl(private val api : ApiClient) : ReportRepository {

    override suspend fun getMenus(context : Context, isFromProduct : Boolean) : List<Menus> {
        val reports = ArrayList<Menus>()
        if(!isFromProduct) {
//            reports.add(Menus(context.resources.getString(R.string.Menu_DailyReports), ContextCompat.getDrawable(context, R.drawable.ic_daily_report)!!, Enums.Menus.DAILY_REPORTS))
//            reports.add(Menus(context.resources.getString(R.string.Menu_CashSalesSummary), ContextCompat.getDrawable(context, R.drawable.ic_cash_sales)!!, Enums.Menus.CASH_AND_SALES))
//            reports.add(Menus(context.resources.getString(R.string.Menu_CloseOut), ContextCompat.getDrawable(context, R.drawable.ic_z_report)!!, Enums.Menus.Z_REPORT))
//            reports.add(Menus(context.resources.getString(R.string.Menu_DaySalesSummary), ContextCompat.getDrawable(context, R.drawable.ic_day_sales)!!, Enums.Menus.DAY_SALES))
//            reports.add(Menus(context.resources.getString(R.string.Menu_DepartmentSalesSummary), ContextCompat.getDrawable(context, R.drawable.ic_department_report)!!, Enums.Menus.DEPARTMENT_SALES))
//            reports.add(Menus(context.resources.getString(R.string.Menu_TransactionLogSummary), ContextCompat.getDrawable(context, R.drawable.ic_transaction_log)!!, Enums.Menus.TRANSACTION_LOG))
//            reports.add(Menus(context.resources.getString(R.string.Menu_OperationRecordLog), ContextCompat.getDrawable(context, R.drawable.ic_operation_record)!!, Enums.Menus.OPERATION_RECORD))
//            reports.add(Menus(context.resources.getString(R.string.Menu_SalesSummary), ContextCompat.getDrawable(context, R.drawable.ic_sales_summary)!!, Enums.Menus.SALES_SUMMARY))
        } else {
//            reports.add(Menus(context.resources.getString(R.string.Menu_Category), ContextCompat.getDrawable(context, R.drawable.ic_category)!!, Enums.Menus.CATEGORY))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Department), ContextCompat.getDrawable(context, R.drawable.ic_dept)!!, Enums.Menus.DEPARTMENT))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Brand), ContextCompat.getDrawable(context, R.drawable.ic_brand)!!, Enums.Menus.BRAND))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Tax), ContextCompat.getDrawable(context, R.drawable.ic_menu_tax)!!, Enums.Menus.TAX))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Specification), ContextCompat.getDrawable(context, R.drawable.ic_menu_specification)!!, Enums.Menus.SPECIFICATION))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Facility), ContextCompat.getDrawable(context, R.drawable.ic_menu_facility)!!, Enums.Menus.FACILITY))
//            reports.add(Menus(context.resources.getString(R.string.Menu_UOM), ContextCompat.getDrawable(context, R.drawable.ic_menu_uom)!!, Enums.Menus.UOM))
//            reports.add(Menus(context.resources.getString(R.string.Menu_Vendor), ContextCompat.getDrawable(context, R.drawable.ic_menu_vendor)!!, Enums.Menus.VENDOR))
//            reports.add(Menus(context.resources.getString(R.string.lbl_Products), ContextCompat.getDrawable(context, R.drawable.ic_manage)!!, Enums.Menus.PRODUCTS))
        }
        return reports
    }

    override suspend fun fetchSales(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponse<SalesSummary>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getSalesSummary(startDate, endDate, storeId).enqueue(object : Callback<BaseResponse<SalesSummary>> {
//            override fun onResponse(call: Call<BaseResponse<SalesSummary>>, response: Response<BaseResponse<SalesSummary>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("SALES DETAILS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<SalesSummary>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("SALES DETAILS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchPaymentTypes(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<Payments>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getPaymentList(startDate, endDate, storeId).enqueue(object : Callback<BaseResponseList<Payments>> {
//            override fun onResponse(call: Call<BaseResponseList<Payments>>, response: Response<BaseResponseList<Payments>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("PAYMENTS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<Payments>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("PAYMENTS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchTopItems(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<TopItems>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getTopItems(startDate, endDate, storeId).enqueue(object : Callback<BaseResponseList<TopItems>> {
//            override fun onResponse(call: Call<BaseResponseList<TopItems>>, response: Response<BaseResponseList<TopItems>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("TOP ITEMS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<TopItems>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("TOP ITEMS", t.toString())
//            }
//        })
    }

    override suspend fun fetchTopCategories(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponse<TopItemsByCategories>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getTopItemsByCategories(startDate, endDate, storeId).enqueue(object : Callback<BaseResponse<TopItemsByCategories>> {
//            override fun onResponse(call: Call<BaseResponse<TopItemsByCategories>>, response: Response<BaseResponse<TopItemsByCategories>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("TOP ITEMS BY CATEGORIES DETAILS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<TopItemsByCategories>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("TOP ITEMS BY CATEGORIES DETAILS ERROR", t.toString())
//            }
//        })
    }
}