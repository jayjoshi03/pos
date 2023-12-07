package com.varitas.gokulpos.api

import com.varitas.gokulpos.model.Menus
import com.varitas.gokulpos.model.Movie
import com.varitas.gokulpos.model.User
import com.varitas.gokulpos.request.BrandInsertRequest
import com.varitas.gokulpos.request.BrandUpdateRequest
import com.varitas.gokulpos.request.CategoryInsertUpdate
import com.varitas.gokulpos.request.DepartmentInsertUpdate
import com.varitas.gokulpos.request.FacilityInsertRequest
import com.varitas.gokulpos.request.FacilityUpdateRequest
import com.varitas.gokulpos.request.GroupInsertUpdateRequest
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.request.ItemPriceList
import com.varitas.gokulpos.request.ItemStock
import com.varitas.gokulpos.request.LoginRequest
import com.varitas.gokulpos.request.ProductInsertRequest
import com.varitas.gokulpos.request.SpecificationInsertRequest
import com.varitas.gokulpos.request.SpecificationTypeInsertRequest
import com.varitas.gokulpos.request.SpecificationUpdateRequest
import com.varitas.gokulpos.request.TaxInsertRequest
import com.varitas.gokulpos.request.TaxUpdateRequest
import com.varitas.gokulpos.request.UOMInsertRequest
import com.varitas.gokulpos.request.UOMUpdateRequest
import com.varitas.gokulpos.request.VendorInsertRequest
import com.varitas.gokulpos.request.VendorUpdateRequest
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.Brand
import com.varitas.gokulpos.response.Category
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Customers
import com.varitas.gokulpos.response.Department
import com.varitas.gokulpos.response.Employee
import com.varitas.gokulpos.response.Facility
import com.varitas.gokulpos.response.FavouriteItems
import com.varitas.gokulpos.response.Group
import com.varitas.gokulpos.response.Invoice
import com.varitas.gokulpos.response.ItemCount
import com.varitas.gokulpos.response.ItemStockSpecification
import com.varitas.gokulpos.response.OrderList
import com.varitas.gokulpos.response.PostResponse
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.response.ProductResponse
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.response.SalesPerson
import com.varitas.gokulpos.response.ScanBarcode
import com.varitas.gokulpos.response.Specification
import com.varitas.gokulpos.response.Store
import com.varitas.gokulpos.response.StoreStatus
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.response.Tender
import com.varitas.gokulpos.response.UOM
import com.varitas.gokulpos.response.Vendor
import com.varitas.gokulpos.utilities.Default
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiClient {
    @GET("users/{user}") fun getUser(@Path("user") user : String) : Call<User>
    @GET("movielist.json") fun getAllMovies() : Call<List<Movie>>
    //region DEVELOP
//    @GET("api/Category/GetAllSubCategory") fun getSubCategories() : Call<BaseResponseList<Category>>
//
//    @GET("api/DropDown/GetTenderDetails") fun getTenders(@Query("storeId") storeId : Int) : Call<BaseResponseList<DropDown>>
//
//    @GET("api/Tax/GetDetailsByStoreId") fun getTaxes(@Query("storeId") storeId : Int) : Call<BaseResponseList<Tax>>
//
//    @GET("api/Customer/GetCustomerDetailMob") fun getCustomerDetail(@Query("id") id : Int, @Query("storeId") storeId : Int) : Call<BaseResponse<CustomerDetail>>
//
//    @GET("api/Dropdown/GetDepartment") fun getDepartmentDropDown(@Query("storeId") storeId : Int) : Call<BaseResponseList<DropDown>>
//
//    @GET("api/Dropdown/GetProductType") fun getItemTypeDropDown(@Query("storeId") storeId : Int) : Call<BaseResponseList<DropDown>>
//
//    @GET("api/Category/GetCategoryDepartment") fun getCategoryDropDown(@Query("storeId") storeId : Int) : Call<BaseResponseList<DropDownCategories>>
//
//    @GET("api/Order/getOrderSummaryDataForMobApp") fun getOrderAnalysis(@Query("StartDate") startDate : String, @Query("EndDate") endDate : String, @Query("storeId") storeId : Int) : Call<BaseResponse<OrderAnalytics>>
//
//    @GET("api/Order/salesSummaryReport") fun getSalesSummary(@Query("startDate") startDate : String, @Query("endDate") endDate : String, @Query("storeId") storeId : Int) : Call<BaseResponse<SalesSummary>>
//
//    @GET("api/Order/salesByPaymentType") fun getPaymentList(@Query("startDate") startDate : String, @Query("endDate") endDate : String, @Query("storeId") storeId : Int) : Call<BaseResponseList<Payments>>
//
//    @GET("api/Order/getDataOfTopItems") fun getTopItems(@Query("startDate") startDate : String, @Query("endDate") endDate : String, @Query("storeId") storeId : Int) : Call<BaseResponseList<TopItems>>
//
//    @GET("api/Device/GetDeviceDetailByStoreIdforMobApp") fun getDevices(@Query("storeId") storeId : Int) : Call<BaseResponseList<DeviceList>>
//
//    @POST("api/Product/AddProductStorePriceMap") fun updatePrice(@Body req : ChangePriceRequest) : Call<APIResponse>
//
//    @POST("api/Product/AddStockQuantity") fun updateQuantity(@Body req : ChangeQtyRequest) : Call<APIResponse>
//
//    @POST("api/Product/InsertUpdate") fun updateProduct(@Body req : UpdateProductDetail) : Call<APIResponse>
//
//    @GET("api/Dropdown/GetAttributeMob") fun getAttributes(@Query("attribute") attribute : String) : Call<BaseResponseList<DropDown>>
//
//    @GET("api/Product/GetProductDataByProductId") fun getProductDetails(@Query("productId") id : Int) : Call<BaseResponse<ProductDetail>>
//
//    @GET("api/DropDown/GetCategory") fun getCategory(@Query("storeId") storeId : Int) : Call<BaseResponseList<DropDown>>
//
//    @GET("api/Category/GetDetailsByParentCategoryId") fun getDetailsByParentCategory(@Query("parentCategoryId") parentCategoryId : Int) : Call<BaseResponseList<SubCategory>>
//
//    @GET("api/Notification/GetNotification") fun getNotifications(@Query("storeId") storeId : Int) : Call<BaseResponseList<Notifications>>
//
//    @POST("api/Notification/Update") fun updateNotifications(@Body req : NotificationRequest) : Call<APIResponse>
//
//    @GET("api/Order/getDataOfCategory") fun getTopItemsByCategories(@Query("startDate") startDate : String, @Query("endDate") endDate : String, @Query("storeId") storeId : Int) : Call<BaseResponse<TopItemsByCategories>>
//
//    @GET("api/Order/getDataOfTopSellingItems") fun getTopSellingProducts(@Query("paymentMethod") paymentMethod : Int, @Query("storeId") storeId : Int) : Call<BaseResponseList<TopSellingItems>>
//
//    @GET("api/Order/getDataOfWeeklySales") fun getWeeklySales(@Query("paymentMethod") paymentMethod : Int, @Query("storeId") storeId : Int) : Call<BaseResponseList<WeeklySaleBar>>
    //endregion
    //region LOGIN
    @POST(Default.api + Default.version1 + "authenticate/Login") fun makeLogin(@Body req : LoginRequest) : Call<PostResponse>
    @GET(Default.api + Default.version1 + "authenticate/Logout/{token}") fun makeLogout(@Path("token") token : String) : Call<PostResponse>
    @GET(Default.api + Default.version1 + "authenticate/menu/{parentid}/{roleid}/true") fun getDashboardMenu(@Path("parentid") parentid : Int, @Path("roleid") roleid : Int) : Call<BaseResponseList<Menus>>
    //endregion
    //region BRANDS
    @GET(Default.api + Default.version1 + Default.apiBrand + "{status}/{storeId}") fun getAllBrands(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Brand>>
    @DELETE(Default.api + Default.version1 + Default.apiBrand + Default.apiDelete + "/{id}") fun deleteBrand(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiBrand + Default.apiInsert) fun insertBrand(@Body req : BrandInsertRequest) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiBrand + Default.apiUpdate) fun updateBrand(@Body req : BrandUpdateRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiBrand + Default.apiUpdate + "/{id}") fun getBrandDetails(@Path("id") id : Int) : Call<BaseResponse<Brand>>
    @GET(Default.api + Default.version1 + Default.apiBrand + Default.apiListItems + "{id}") fun getBrandItems(@Path("id") id : Int) : Call<BaseResponseList<ItemCount>>
    //endregion
    //region DEPARTMENTS
    @GET(Default.api + Default.version1 + Default.apiDepartment + "{status}/{storeId}") fun getDepartments(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Department>>
    @DELETE(Default.api + Default.version1 + Default.apiDepartment + Default.apiDelete + "/{id}") fun deleteDepartment(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiDepartment + Default.apiInsert) fun insertDepartment(@Body req : DepartmentInsertUpdate) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiDepartment + Default.apiUpdate + "/{id}") fun getDepartmentDetails(@Path("id") id : Int) : Call<BaseResponse<DepartmentInsertUpdate>>
    @POST(Default.api + Default.version1 + Default.apiDepartment + Default.apiUpdate) fun updateDepartment(@Body req : DepartmentInsertUpdate) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiDepartment + Default.apiListItems + "{id}") fun getDepartmentItems(@Path("id") id : Int) : Call<BaseResponseList<ItemCount>>
    //endregion
    //region CATEGORY
    @GET(Default.api + Default.version1 + Default.apiCategory + "{status}/{storeId}") fun getCategories(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Category>>
    @DELETE(Default.api + Default.version1 + Default.apiCategory + Default.apiDelete + "/{id}") fun deleteCategory(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiDropDown + "{storeId}") fun getParentCategory(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version1 + Default.apiDepartment + Default.apiDropDown + "{storeId}") fun getDepartment(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version1 + Default.apiBrand + Default.apiDropDown + "{storeId}") fun getBrandDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @POST(Default.api + Default.version1 + Default.apiCategory + Default.apiInsert) fun insertCategory(@Body req : CategoryInsertUpdate) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiUpdate + "/{id}") fun getCategoryDetails(@Path("id") id : Int) : Call<BaseResponse<CategoryInsertUpdate>>
    @POST(Default.api + Default.version1 + Default.apiCategory + Default.apiUpdate) fun updateCategory(@Body req : CategoryInsertUpdate) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiListItems + "{id}") fun getCategoryItems(@Path("id") id : Int) : Call<BaseResponseList<ItemCount>>
    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiDropDown + "subcategory/{catID}") fun getSubCategoryDropDown(@Path("catID") catID : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion
    //region TAXES
    @GET(Default.api + Default.version1 + Default.apiTax + "{status}/{storeId}") fun getAllTaxes(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Tax>>
    @DELETE(Default.api + Default.version1 + Default.apiTax + Default.apiDelete + "/{id}") fun deleteTax(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiTax + Default.apiUpdate + "/{id}") fun getTaxDetails(@Path("id") id : Int) : Call<BaseResponse<Tax>>
    @POST(Default.api + Default.version1 + Default.apiTax + Default.apiInsert) fun insertTax(@Body req : TaxInsertRequest) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiTax + Default.apiUpdate) fun updateTax(@Body req : TaxUpdateRequest) : Call<APIResponse>
    //endregion
    //region GROUP
    @GET(Default.api + Default.version2 + Default.apiGroup + Default.apiDropDown  + "{type}/{storeId}") fun getTaxGroups(@Path("type") type : Int,@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version2 + Default.apiGroup + "{status}/{storeId}") fun getGroups(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Group>>
    @DELETE(Default.api + Default.version2 + Default.apiGroup + Default.apiDelete + "/{id}") fun deleteGroup(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version2 + Default.apiGroup + Default.apiInsert) fun insertGroup(@Body req : GroupInsertUpdateRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + "common/" + Default.apiDropDown + "{type}") fun getGroupTypeDropdown(@Path("type") type : Int) : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version2 + Default.apiGroup + "listitems/{type}/{storeId}") fun getGroupTypeList(@Path("type") type : Int, @Path("storeId") storeId : Int) : Call<BaseResponseList<Group>>
    @GET(Default.api + Default.version1 + Default.apiGroup + Default.apiUpdate + "/" + Default.apiItems + "{groupId}/{storeId}") fun getGroupDetails(@Path("groupId") groupId : Int, @Path("storeId") storeId : Int) : Call<BaseResponse<GroupInsertUpdateRequest>>
    @POST(Default.api + Default.version2 + Default.apiGroup + Default.apiUpdate) fun updateGroup(@Body req : GroupInsertUpdateRequest) : Call<APIResponse>
    @GET(Default.api + Default.version2 + Default.apiGroup + Default.apiUpdate + "/{groupId}") fun getGroupTypeDetails(@Path("groupId") groupId : Int) : Call<BaseResponse<GroupInsertUpdateRequest>>
    //endregion
    //region SPECIFICATION
    @GET(Default.api + Default.version1 + Default.apiSpecification + "{status}/{storeId}") fun getSpecification(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Specification>>
    @DELETE(Default.api + Default.version1 + Default.apiSpecification + Default.apiDelete + "/{id}") fun deleteSpecification(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiUOM + Default.apiDropDown + "{storeId}") fun getUOM(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version1 + Default.apiSpecificationType + "listbytype/" + Default.apiDropDown + "{typeId}/{storeId}") fun getSpecificationType(@Path("storeId") storeId : Int, @Path("typeId") typeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @POST(Default.api + Default.version1 + Default.apiSpecification + Default.apiInsert) fun insertSpecification(@Body req : SpecificationInsertRequest) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiSpecificationType + Default.apiInsert) fun insertSpecificationType(@Body req : SpecificationTypeInsertRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiUpdate + "/{id}") fun getSpecificationDetails(@Path("id") id : Int) : Call<BaseResponse<Specification>>
    @POST(Default.api + Default.version1 + Default.apiSpecification + Default.apiUpdate) fun updateSpecification(@Body req : SpecificationUpdateRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "type/{storeId}") fun getSpecificationDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion
    //region FACILITY
    @GET(Default.api + Default.version1 + Default.apiFacility + "{status}/{storeId}") fun getFacility(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Facility>>
    @DELETE(Default.api + Default.version1 + Default.apiFacility + Default.apiDelete + "/{id}") fun deleteFacility(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiFacility + Default.apiInsert) fun insertFacility(@Body req : FacilityInsertRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiFacility + Default.apiUpdate + "/{id}") fun getFacilityDetails(@Path("id") id : Int) : Call<BaseResponse<Facility>>
    @POST(Default.api + Default.version1 + Default.apiFacility + Default.apiUpdate) fun updateFacility(@Body req : FacilityUpdateRequest) : Call<APIResponse>
    //endregion
    //region VENDOR
    @GET(Default.api + Default.version1 + Default.apiVendor + "{status}/{storeId}") fun getVendors(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Vendor>>
    @DELETE(Default.api + Default.version1 + Default.apiVendor + Default.apiDelete + "/{id}") fun deleteVendor(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiVendor + Default.apiUpdate + "/{id}") fun getVendorDetails(@Path("id") id : Int) : Call<BaseResponse<Vendor>>
    @POST(Default.api + Default.version1 + Default.apiVendor + Default.apiInsert) fun insertVendor(@Body req : VendorInsertRequest) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiVendor + Default.apiUpdate) fun updateVendor(@Body req : VendorUpdateRequest) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiVendor + "salesperson") fun addSalesPerson(@Body req : SalesPerson) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiVendor + "salesperson/{id}") fun getSalesPersonCount(@Path("id") id : Int) : Call<BaseResponseList<SalesPerson>>
    //endregion
    //region UOM
    @GET(Default.api + Default.version1 + Default.apiUOM + "{status}/{storeId}") fun fetchUOM(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<UOM>>
    @DELETE(Default.api + Default.version1 + Default.apiUOM + Default.apiDelete + "/{id}") fun deleteUOM(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiUOM + Default.apiInsert) fun insertUOM(@Body req : UOMInsertRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiUOM + Default.apiUpdate + "/{id}") fun getUOMDetails(@Path("id") id : Int) : Call<BaseResponse<UOM>>
    @POST(Default.api + Default.version1 + Default.apiUOM + Default.apiUpdate) fun updateUOM(@Body req : UOMUpdateRequest) : Call<APIResponse>
    //endregion
    //region PRODUCT
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "{status}/{storeId}") fun getAllProducts(@Path("status") status : Int = Default.ACTIVE, @Path("storeId") storeId : Int) : Call<BaseResponseList<ProductList>>
    @GET(Default.api + Default.version1 + Default.apiStoreItem + "getbyupc/{upc}") fun getBarcodeDetails(@Path("upc") upc : String) : Call<BaseResponse<ScanBarcode>>
    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "pack/{storeId}") fun getPackDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @POST(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiInsert) fun insertProduct(@Body req : ProductInsertRequest) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiStoreItem + "getitempricedata" + "/{id}") fun getItemPrice(@Path("id") id : Int) : Call<BaseResponse<ItemPrice>>
    @POST(Default.api + Default.version2 + Default.apiStoreVItem + "changeprice") fun updateItemPrice(@Body req : ItemPrice) : Call<APIResponse>
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "getchangestock" + "/{id}") fun getItemStock(@Path("id") id : Int) : Call<BaseResponseList<ItemStockSpecification>>
    @POST(Default.api + Default.version2 + Default.apiStoreVItem + "changestock") fun updateItemStock(@Body req : ArrayList<ItemStock>) : Call<APIResponse>
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiUpdate + "/{id}") fun getItemDetails(@Path("id") id : Int) : Call<BaseResponse<ProductResponse>>
    @POST(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiUpdate) fun updateProduct(@Body req : ProductInsertRequest) : Call<APIResponse>
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "getchangeprice/" + "{id}") fun getPriceList(@Path("id") id : Int) : Call<BaseResponseList<ItemPriceList>>
    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "{storeId}/{typeId}") fun getSizePackDropDown(@Path("storeId") storeId : Int, @Path("typeId") typeId : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion
    //region CUSTOMER
    @GET(Default.api + Default.version1 + Default.apiCustomer + "{status}/{storeId}") fun getCustomers(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Customers>>
    @DELETE(Default.api + Default.version1 + Default.apiCustomer + Default.apiDelete + "/{id}") fun deleteCustomer(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiCustomer + Default.apiInsert) fun insertCustomer(@Body req : Customers) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiCustomer + Default.apiUpdate + "/{id}") fun getCustomerDetail(@Path("id") id : Int) : Call<BaseResponse<Customers>>
    @POST(Default.api + Default.version1 + Default.apiCustomer + Default.apiUpdate) fun updateCustomer(@Body req : Customers) : Call<APIResponse>
    //endregion
    //region EMPLOYEE
    @GET(Default.api + Default.version1 + Default.apiEmployee + "{status}/{storeId}") fun getEmployees(@Path("status") status : Int = Default.ACTIVE, @Path("storeId") storeId : Int) : Call<BaseResponseList<Employee>>
    @DELETE(Default.api + Default.version1 + Default.apiEmployee + Default.apiDelete + "/{id}") fun deleteEmployee(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiRole + Default.apiDropDown + "{storeId}") fun getRoleDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @POST(Default.api + Default.version1 + Default.apiEmployee + Default.apiInsert) fun insertEmployee(@Body req : Employee) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiEmployee + Default.apiUpdate + "/{id}") fun getEmployeeDetail(@Path("id") id : Int) : Call<BaseResponse<Employee>>
    @POST(Default.api + Default.version1 + Default.apiEmployee + Default.apiUpdate) fun updateEmployee(@Body req : Employee) : Call<APIResponse>
    //endregion
    //region ORDER
    @GET(Default.api + Default.version2 + Default.apiOrder + "{status}/{storeId}/{startDate}/{endDate}") fun getOrderList(@Path("status") status : Int, @Path("storeId") storeId : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<OrderList>>
    @GET(Default.api + Default.version2 + Default.apiOrder + Default.apiCustomer + "{customerId}/{startDate}/{endDate}") fun getCustomerOrder(@Path("customerId") customerId : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<OrderList>>
    @GET(Default.api + Default.version2 + Default.apiOrder + Default.apiUser + "{userID}/{startDate}/{endDate}") fun getEmployeeOrder(@Path("userID") userID : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<OrderList>>
    @GET(Default.api + Default.version2 + Default.apiOrder + Default.apiUpdate + "/" + Default.apiItems + Default.apiPayment + "{orderId}") fun getOrderDetails(@Path("orderId") orderId : Int) : Call<BaseResponse<OrderList>>
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "searchwithspecification/{msg}/{storeId}") fun getAutoCompleteItems(@Path("msg") msg : String, @Path("storeId") storeId : Int) : Call<BaseResponseList<FavouriteItems>>
    @GET(Default.api + Default.version2 + Default.apiOrder + Default.apiView + "{empId}/{storeId}/{status}/{startDate}/{endDate}") fun getLastOrder(@Path("empId") empId : Int, @Path("storeId") storeId : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String, @Path("status") status : Int) : Call<BaseResponseList<OrderList>>
    //endregion
    //region TENDER
    @GET(Default.api + Default.version1 + Default.apiTender + "{status}/{storeId}") fun getTenderList(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Tender>>
    @DELETE(Default.api + Default.version1 + Default.apiTender + Default.apiDelete + "/{id}") fun deleteTender(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiTender + Default.apiInsert) fun insertTender(@Body req : Tender) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiTender + "dropdownpaymentmode") fun getPaymentModeDropDown() : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version1 + Default.apiTender + "dropdowncardtype") fun getCardTypeDropDown() : Call<BaseResponseList<CommonDropDown>>
    @GET(Default.api + Default.version1 + Default.apiTender + Default.apiUpdate + "/{id}") fun getTenderDetail(@Path("id") id : Int) : Call<BaseResponse<Tender>>
    @POST(Default.api + Default.version1 + Default.apiTender + Default.apiUpdate) fun updateTender(@Body req : Tender) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiTender + Default.apiDropDown + "{storeId}") fun getTenderDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion
    //region PURCHASE ORDER
    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiView + "{status}/{storeId}") fun getPurchaseOrderList(@Path("storeId") storeId : Int, @Path("status") status : Int) : Call<BaseResponseList<PurchaseOrder>>
    @DELETE(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiDelete + "/{id}") fun deletePurchaseOrder(@Path("id") id : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiVendor + Default.apiDropDown + "{storeId}") fun getVendorDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>
    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiInsert) fun insertPurchaseOrder(@Body req : PurchaseOrder) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiUpdate + "/{id}") fun getPurchaseOrderDetail(@Path("id") id : Int) : Call<BaseResponse<PurchaseOrder>>
    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiUpdate) fun updatePurchaseOrder(@Body req : PurchaseOrder) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + "makeinvoice/{orderId}") fun generateInvoice(@Path("orderId") orderId : Int) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + "search/{msg}/{storeId}") fun getAutoCompletePurchaseOrder(@Path("msg") msg : String, @Path("storeId") storeId : Int) : Call<BaseResponseList<PurchaseOrder>>
    //endregion
    //region INVOICE
    @GET(Default.api + Default.version1 + Default.apiInvoice + Default.apiView + "{status}/{storeId}") fun getInvoiceList(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.CONFIRM_ORDER) : Call<BaseResponseList<Invoice>>
    @DELETE(Default.api + Default.version1 + Default.apiInvoice + Default.apiDelete + "/{id}") fun deleteInvoice(@Path("id") id : Int) : Call<APIResponse>
    @POST(Default.api + Default.version1 + Default.apiInvoice + Default.apiInsert) fun insertInvoice(@Body req : Invoice) : Call<APIResponse>
    @GET(Default.api + Default.version1 + Default.apiInvoice + Default.apiUpdate + "/{id}") fun getInvoiceDetail(@Path("id") id : Int) : Call<BaseResponse<Invoice>>
    @POST(Default.api + Default.version1 + Default.apiInvoice + Default.apiUpdate) fun updateInvoice(@Body req : Invoice) : Call<APIResponse>
    //endregion
    //region STORE
    @GET(Default.api + Default.version1 + Default.apiStore + "{status}") fun getStoreList(@Path("status") status : Int) : Call<BaseResponseList<Store>>
    @POST(Default.api + Default.version1 + Default.apiStore + "updatestatus") fun updateStoreStatus(@Body req : StoreStatus) : Call<APIResponse>
    //endregion
}