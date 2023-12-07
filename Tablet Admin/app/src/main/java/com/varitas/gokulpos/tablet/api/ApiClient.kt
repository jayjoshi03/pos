package com.varitas.gokulpos.tablet.api

import com.varitas.gokulpos.tablet.model.Brand
import com.varitas.gokulpos.tablet.model.BrandDetails
import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.model.CategoryInsertUpdate
import com.varitas.gokulpos.tablet.model.GroupDetail
import com.varitas.gokulpos.tablet.model.GroupInsertUpdateRequest
import com.varitas.gokulpos.tablet.model.ItemGroupDetail
import com.varitas.gokulpos.tablet.model.Movie
import com.varitas.gokulpos.tablet.model.SelectedGroups
import com.varitas.gokulpos.tablet.model.Store
import com.varitas.gokulpos.tablet.model.StoreUpdate
import com.varitas.gokulpos.tablet.model.User
import com.varitas.gokulpos.tablet.request.AddSalePerson
import com.varitas.gokulpos.tablet.request.DepartmentInsertUpdate
import com.varitas.gokulpos.tablet.request.DrawerInsertRequest
import com.varitas.gokulpos.tablet.request.FacilityInsertRequest
import com.varitas.gokulpos.tablet.request.FacilityUpdateRequest
import com.varitas.gokulpos.tablet.request.ItemGroupRequest
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.request.ItemPriceList
import com.varitas.gokulpos.tablet.request.ItemStock
import com.varitas.gokulpos.tablet.request.LoginRequest
import com.varitas.gokulpos.tablet.request.OrderInsertRequest
import com.varitas.gokulpos.tablet.request.ProductInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationTypeInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationUpdateRequest
import com.varitas.gokulpos.tablet.request.TaxInsertRequest
import com.varitas.gokulpos.tablet.request.TaxUpdateRequest
import com.varitas.gokulpos.tablet.request.UOMInsertRequest
import com.varitas.gokulpos.tablet.request.UOMUpdateRequest
import com.varitas.gokulpos.tablet.request.UpdateOrderRequest
import com.varitas.gokulpos.tablet.request.VendorInsertRequest
import com.varitas.gokulpos.tablet.request.VendorUpdateRequest
import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.AddItemToDiscount
import com.varitas.gokulpos.tablet.response.AllOrders
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.Currency
import com.varitas.gokulpos.tablet.response.Customers
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.response.DrawerTransaction
import com.varitas.gokulpos.tablet.response.Employee
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.Invoice
import com.varitas.gokulpos.tablet.response.ItemCartDetail
import com.varitas.gokulpos.tablet.response.ItemCount
import com.varitas.gokulpos.tablet.response.ItemGroupTax
import com.varitas.gokulpos.tablet.response.ItemStockSpecification
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.OpenBatch
import com.varitas.gokulpos.tablet.response.OpenBatchRequest
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.response.OrderPlaced
import com.varitas.gokulpos.tablet.response.PostResponse
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.response.ProductResponse
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.response.Role
import com.varitas.gokulpos.tablet.response.SalesPromotion
import com.varitas.gokulpos.tablet.response.ScanBarcode
import com.varitas.gokulpos.tablet.response.Specification
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.response.Tender
import com.varitas.gokulpos.tablet.response.UOM
import com.varitas.gokulpos.tablet.response.ValidatePromotion
import com.varitas.gokulpos.tablet.response.Vendor
import com.varitas.gokulpos.tablet.utilities.Default
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiClient {

    @GET("users/{user}") fun getUser(@Path("user") user : String) : Call<User>

    @GET("movielist.json") fun getAllMovies() : Call<List<Movie>>

    //region LOGIN

    @POST(Default.api + Default.version1 + "authenticate/Login") fun makeLogin(@Body req : LoginRequest) : Call<PostResponse>

    @GET(Default.api + Default.version1 + "authenticate/Logout/{token}") fun makeLogout(@Path("token") token : String) : Call<PostResponse>

    @GET(Default.api + Default.version1 + "authenticate/menu/{parentid}/{roleid}/true") fun getDashboardMenu(@Path("parentid") parentid : Int, @Path("roleid") roleid : Int) : Call<BaseResponseList<Menus>>

    //endregion

    //region BRANDS
    @GET(Default.api + Default.version1 + Default.apiBrand + "{status}/{storeId}") fun getAllBrands(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Brand>>

    @DELETE(Default.api + Default.version1 + Default.apiBrand + Default.apiDelete + "/{id}") fun deleteBrand(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiBrand + Default.apiInsert) fun insertBrand(@Body req : BrandDetails) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiBrand + Default.apiUpdate) fun updateBrand(@Body req : BrandDetails) : Call<APIResponse>

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

    //region Category
    @GET(Default.api + Default.version1 + Default.apiCategory + "{status}/{storeId}") fun getCategories(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Category>>

    @DELETE(Default.api + Default.version1 + Default.apiCategory + Default.apiDelete + "/{id}") fun deleteCategory(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiDropDown + "{storeId}") fun getParentCategory(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiDropDown + "subcategory/{parentcategory}") fun getSubCategory(@Path("parentcategory") parentcategory : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiDepartment + Default.apiDropDown + "{storeId}") fun getDepartment(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiBrand + Default.apiDropDown + "{storeId}") fun getBrandDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @POST(Default.api + Default.version1 + Default.apiCategory + Default.apiInsert) fun insertCategory(@Body req : CategoryInsertUpdate) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiUpdate + "/{id}") fun getCategoryDetails(@Path("id") id : Int) : Call<BaseResponse<CategoryInsertUpdate>>

    @POST(Default.api + Default.version1 + Default.apiCategory + Default.apiUpdate) fun updateCategory(@Body req : CategoryInsertUpdate) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiCategory + Default.apiListItems + "{id}") fun getCategoryItems(@Path("id") id : Int) : Call<BaseResponseList<ItemCount>>

    //endregion

    //region Taxes

    @GET(Default.api + Default.version1 + Default.apiTax + "{status}/{storeId}") fun getAllTaxes(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Tax>>

    @GET(Default.api + Default.version1 + Default.apiGroup + "listtax/{groupId}") fun getTaxFromGroup(@Path("groupId") groupId : Int) : Call<BaseResponseList<Tax>>

    @DELETE(Default.api + Default.version1 + Default.apiTax + Default.apiDelete + "/{id}") fun deleteTax(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiTax + Default.apiUpdate + "/{id}") fun getTaxDetails(@Path("id") id : Int) : Call<BaseResponse<Tax>>

    @POST(Default.api + Default.version1 + Default.apiTax + Default.apiInsert) fun insertTax(@Body req : TaxInsertRequest) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiTax + Default.apiUpdate) fun updateTax(@Body req : TaxUpdateRequest) : Call<APIResponse>

    //endregion

    //region Group
    @GET(Default.api + Default.version2 + Default.apiGroup + "{status}/{storeId}") fun getGroups(@Path("storeId") storeId: Int, @Path("status") status: Int = Default.ACTIVE): Call<BaseResponseList<SelectedGroups>>

    @DELETE(Default.api + Default.version2 + Default.apiGroup + Default.apiDelete + "/{id}") fun deleteGroup(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version2 + Default.apiGroup + Default.apiInsert) fun insertGroup(@Body req : GroupInsertUpdateRequest) : Call<APIResponse>

    @POST(Default.api + Default.version2 + Default.apiGroup + "additemtogroup") fun insertItemToGroup(@Body req : ItemGroupRequest) : Call<APIResponse>

    @GET(Default.api + Default.version2 + Default.apiGroup + "listitemgrouptax/{groupId}/{itemId}/{specification}/{price}") fun getGroupItemTax(@Path("groupId") groupId: Int, @Path("itemId") itemId: Int, @Path("specification") specification: Int, @Path("price") price: Double): Call<BaseResponse<ItemGroupTax>>

    @GET(Default.api + Default.version1 + "common/" + Default.apiDropDown + "{type}") fun getGroupTypeDropdown(@Path("type") type: Int): Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version2 + Default.apiGroup + "listitems/{type}/{storeId}") fun getGroupTypeList(@Path("type") type : Int, @Path("storeId") storeId : Int) : Call<BaseResponseList<SelectedGroups>>

    @GET(Default.api + Default.version2 + Default.apiGroup + Default.apiUpdate + "/{groupId}") fun getGroupTypeDetails(@Path("groupId") groupId : Int) : Call<BaseResponse<GroupDetail>>

    @GET(Default.api + Default.version2 + Default.apiGroup + "groupitems/{groupId}") fun getFavouriteGroupItems(@Path("groupId") groupId: Int): Call<BaseResponseList<ItemCount>>

    @POST(Default.api + Default.version2 + Default.apiGroup + Default.apiUpdate) fun updateGroup(@Body req: GroupInsertUpdateRequest): Call<APIResponse>

    @GET(Default.api + Default.version2 + Default.apiGroup + "{status}/{storeId}/{typeId}") fun getItemGroup(@Path("status") status: Int, @Path("storeId") storeId: Int, @Path("typeId") typeId: Int): Call<BaseResponseList<ItemGroupDetail>>

    //endregion

    //region Specification
    @GET(Default.api + Default.version1 + Default.apiSpecification + "{status}/{storeId}") fun getSpecification(@Path("storeId") storeId: Int, @Path("status") status: Int = Default.ACTIVE): Call<BaseResponseList<Specification>>

    @DELETE(Default.api + Default.version1 + Default.apiSpecification + Default.apiDelete + "/{id}") fun deleteSpecification(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiUOM + Default.apiDropDown + "{storeId}") fun getUOM(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiType + "listbytype/" + Default.apiDropDown + "{typeId}/{storeId}") fun getSpecificationType(@Path("storeId") storeId : Int, @Path("typeId") typeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @POST(Default.api + Default.version1 + Default.apiSpecification + Default.apiInsert) fun insertSpecification(@Body req : SpecificationInsertRequest) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiSpecificationType + Default.apiInsert) fun insertSpecificationType(@Body req : SpecificationTypeInsertRequest) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiUpdate + "/{id}") fun getSpecificationDetails(@Path("id") id : Int) : Call<BaseResponse<Specification>>

    @POST(Default.api + Default.version1 + Default.apiSpecification + Default.apiUpdate) fun updateSpecification(@Body req : SpecificationUpdateRequest) : Call<APIResponse>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiDropDown + Default.apiSpecification + "{itemid}") fun getItemSpecification(@Path("itemid") itemid : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion

    //region Facility
    @GET(Default.api + Default.version1 + Default.apiFacility + "{status}/{storeId}") fun getFacility(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Facility>>

    @DELETE(Default.api + Default.version1 + Default.apiFacility + Default.apiDelete + "/{id}") fun deleteFacility(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiFacility + Default.apiInsert) fun insertFacility(@Body req : FacilityInsertRequest) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiFacility + Default.apiUpdate + "/{id}") fun getFacilityDetails(@Path("id") id : Int) : Call<BaseResponse<Facility>>

    @POST(Default.api + Default.version1 + Default.apiFacility + Default.apiUpdate) fun updateFacility(@Body req : FacilityUpdateRequest) : Call<APIResponse>
    //endregion

    //region Vendor

    @GET(Default.api + Default.version1 + Default.apiVendor + "{status}/{storeId}") fun getVendors(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Vendor>>

    @GET(Default.api + Default.version1 + Default.apiVendor + Default.apiDropDown + "{storeId}") fun getVendorDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @DELETE(Default.api + Default.version1 + Default.apiVendor + Default.apiDelete + "/{id}") fun deleteVendor(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiVendor + Default.apiUpdate + "/{id}") fun getVendorDetails(@Path("id") id : Int) : Call<BaseResponse<Vendor>>

    @POST(Default.api + Default.version1 + Default.apiVendor + Default.apiInsert) fun insertVendor(@Body req : VendorInsertRequest) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiVendor + "salesperson") fun insertSalePerson(@Body req : AddSalePerson) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiVendor + Default.apiUpdate) fun updateVendor(@Body req : VendorUpdateRequest) : Call<APIResponse>

    //endregion

    //region UOM
    @GET(Default.api + Default.version1 + Default.apiUOM + "{status}/{storeId}") fun fetchUOM(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<UOM>>

    @DELETE(Default.api + Default.version1 + Default.apiUOM + Default.apiDelete + "/{id}") fun deleteUOM(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiUOM + Default.apiInsert) fun insertUOM(@Body req : UOMInsertRequest) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiUOM + Default.apiUpdate + "/{id}") fun getUOMDetails(@Path("id") id : Int) : Call<BaseResponse<UOM>>

    @POST(Default.api + Default.version1 + Default.apiUOM + Default.apiUpdate) fun updateUOM(@Body req : UOMUpdateRequest) : Call<APIResponse>
    //endregion

    //region Product
    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "{status}/{storeId}") fun getAllProducts(@Path("status") status : Int = Default.ACTIVE, @Path("storeId") storeId : Int) : Call<BaseResponseList<ProductList>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "Filter/{storeId}/{brandId}/{categoryId}/{departmentId}/{itemType}/{spId}") fun getProductFilterWise(@Path("brandId") brandId : Int,@Path("categoryId") categoryId : Int,@Path("departmentId") departmentId : Int,@Path("itemType") itemType : Int,@Path("spId") spId : Int,@Path("storeId") storeId : Int) : Call<BaseResponseList<ProductList>>

    @GET(Default.api + Default.version1 + Default.apiStoreItem + "getbyupc/{upc}") fun getBarcodeDetails(@Path("upc") upc : String) : Call<BaseResponse<ScanBarcode>>

    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "size/{storeId}") fun getSizeDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "pack/{storeId}") fun getPackDropDown(@Path("storeId") storeId: Int): Call<BaseResponseList<CommonDropDown>>

    @POST(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiInsert) fun insertProduct(@Body req: ProductInsertRequest): Call<APIResponse>

    @POST(Default.api + Default.version2 + Default.apiStoreVItem + "changeprice") fun updateItemPrice(@Body req: ItemPrice): Call<APIResponse>

    @POST(Default.api + Default.version2 + Default.apiStoreVItem + "createshorcut/{id}") fun createShortcut(@Path("id") id: Int): Call<APIResponse>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "getchangestock/{id}") fun getItemStock(@Path("id") id: Int): Call<BaseResponseList<ItemStockSpecification>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "filterdata/{storeId}/{type}/{id}") fun getFavouriteMenuItem(@Path("storeId") storeId: Int, @Path("type") type: Int, @Path("id") id: Int): Call<BaseResponseList<DataDetails>>

    @POST(Default.api + Default.version2 + Default.apiStoreVItem + "changestock") fun updateItemStock(@Body req: ArrayList<ItemStock>): Call<APIResponse>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiUpdate + "/{id}") fun getItemDetails(@Path("id") id: Int): Call<BaseResponse<ProductResponse>>

    @POST(Default.api + Default.version2 + Default.apiStoreVItem + Default.apiUpdate) fun updateProduct(@Body req: ProductInsertRequest): Call<APIResponse>
    //endregion

    //region Order

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "listshortcut/{storeId}") fun getFavouriteItems(@Path("storeId") storeId : Int) : Call<BaseResponseList<FavouriteItems>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "searchwithspecification/{msg}/{storeId}") fun getAutoCompleteItems(@Path("msg") msg : String,@Path("storeId") storeId : Int) : Call<BaseResponseList<FavouriteItems>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "itemorder/{id}") fun getCartItemDetail(@Path("id") id : Int) : Call<BaseResponse<ItemCartDetail>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "listsoldalongdetail/{id}") fun getSoldAlongItemDetail(@Path("id") id : Int) : Call<BaseResponse<List<ItemCartDetail>>>

    @POST(Default.api + Default.version1 + Default.apiDrawer + "openbalance") fun insertOpenBatch(@Body req : OpenBatchRequest) : Call<BaseResponse<OpenBatch>>

    @POST(Default.api + Default.version1 + Default.apiDrawer + "shiftclose/{id}") fun closeBatch(@Path("id") id : Int) : Call<BaseResponse<OrderPlaced>>

    @POST(Default.api + Default.version1 + Default.apiDrawer + "drawertransation") fun insertDrawerTransaction(@Body req : DrawerInsertRequest) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiDrawer + "getdrawertransationdata/{storeId}/{startDate}/{endDate}") fun getDrawerTransactions(@Path("storeId") storeId : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<DrawerTransaction>>

    @POST(Default.api + Default.version2 + Default.apiOrder + "insert") fun insertOrder(@Body req : OrderInsertRequest) : Call<BaseResponse<OrderPlaced>>

    @POST(Default.api + Default.version2 + Default.apiOrder + "update") fun updateOrder(@Body req : UpdateOrderRequest) : Call<BaseResponse<OrderPlaced>>

    @GET(Default.api + Default.version1 + Default.apiCurrency + "{storeId}") fun getCurrency(@Path("storeId") storeId : Int) : Call<BaseResponseList<Currency>>

    @GET(Default.api + Default.version2 + Default.apiOrder + "holdorder/{storeId}/{empId}/{startDate}/{endDate}") fun getHoldOrders(@Path("storeId") storeId : Int, @Path("empId") empId : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<OrderList>>

    @GET(Default.api + Default.version2 + Default.apiOrder + "{status}/{storeId}/{startDate}/{endDate}") fun getCompletedOrders(@Path("storeId") storeId : Int, @Path("status") status : Int, @Path("startDate") startDate : String, @Path("endDate") endDate : String) : Call<BaseResponseList<CompletedOrder>>

    @POST(Default.api + Default.version2 + Default.apiOrder + "savedraft/{orderId}") fun saveDraftOrder(@Path("orderId") orderId : Int) : Call<BaseResponse<OrderPlaced>>

    @POST(Default.api + Default.version2 + Default.apiOrder + "voidorder/{orderId}") fun deleteHoldOrder(@Path("orderId") orderId : Int) : Call<BaseResponse<OrderPlaced>>

    @GET(Default.api + Default.version2 + Default.apiOrder + "receipt/{empId}/{storeId}/{startDate}/{endDate}") fun getAllReceipts(@Path("empId") empId: Int, @Path("storeId") storeId: Int, @Path("startDate") startDate: String, @Path("endDate") endDate: String): Call<BaseResponseList<AllOrders>>

    @GET(Default.api + Default.version2 + Default.apiOrder + "printlast/{empId}") fun getLastPrint(@Path("empId") empId: Int): Call<BaseResponse<AllOrders>>

    @GET(Default.api + Default.version1 + Default.apiSpecification + Default.apiDropDown + "{storeId}/{typeId}") fun getSizePackDropDown(@Path("storeId") storeId : Int, @Path("typeId") typeId : Int) : Call<BaseResponseList<CommonDropDown>>
    //endregion

    //region Customer

    @GET(Default.api + Default.version1 + Default.apiCustomer + "{status}/{storeId}") fun getCustomers(@Path("storeId") storeId: Int, @Path("status") status: Int): Call<BaseResponseList<Customers>>

    @GET(Default.api + Default.version1 + Default.apiCustomer + Default.apiDropDown + "{status}/{storeId}") fun getCustomerDropDown(@Path("storeId") storeId: Int, @Path("status") status: Int = Default.ACTIVE): Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version2 + Default.apiStoreVItem + "getchangeprice/" + "{id}") fun getPriceList(@Path("id") id : Int) : Call<BaseResponseList<ItemPriceList>>

    @DELETE(Default.api + Default.version1 + Default.apiCustomer + Default.apiDelete + "/{id}") fun deleteCustomer(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiCustomer + Default.apiInsert) fun insertCustomer(@Body req : Customers) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiCustomer + Default.apiUpdate + "/{id}") fun getCustomerDetail(@Path("id") id : Int) : Call<BaseResponse<Customers>>

    @POST(Default.api + Default.version1 + Default.apiCustomer + Default.apiUpdate) fun updateCustomer(@Body req : Customers) : Call<APIResponse>

    //endregion

    //region Purchase Order

    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + "{status}/{storeId}") fun getPurchaseOrders(@Path("storeId") storeId : Int, @Path("status") status : Int) : Call<BaseResponseList<PurchaseOrder>>

    @DELETE(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiDelete + "/{id}") fun deletePurchaseOrder(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiInsert) fun insertPurchaseOrder(@Body req : PurchaseOrder) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiUpdate + "/{id}") fun getPurchaseOrderDetail(@Path("id") id : Int) : Call<BaseResponse<PurchaseOrder>>

    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + Default.apiUpdate) fun updatePurchaseOrder(@Body req : PurchaseOrder) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiPurchaseOrder + "makeinvoice/{orderId}") fun generateInvoice(@Path("orderId") orderId : Int) : Call<APIResponse>

    //endregion

    //region Sales Promotion
    @GET(Default.api + Default.version1 + Default.apiDiscount + "{status}/{storeId}") fun getPromotions(@Path("storeId") storeId : Int, @Path("status") status : Int) : Call<BaseResponseList<SalesPromotion>>

    @GET(Default.api + Default.version1 + Default.apiDiscount + "discountonorder/{storeId}") fun getOpenDiscount(@Path("storeId") storeId : Int) : Call<BaseResponseList<SalesPromotion>>

    @DELETE(Default.api + Default.version1 + Default.apiDiscount + Default.apiDelete + "/{id}") fun deletePromotion(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiDiscount + Default.apiDropDown) fun getPromotionTypeDropDown() : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version2 + Default.apiGroup + Default.apiDropDown + "{type}/{storeId}") fun getCommonGroupDropDown(@Path("type") type : Int, @Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @POST(Default.api + Default.version1 + Default.apiDiscount + Default.apiInsert) fun insertSalesPromotion(@Body req : SalesPromotion) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiDiscount + "additemtodiscount") fun insertItemToPromotion(@Body req: AddItemToDiscount): Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiDiscount + "{discountId}") fun getSalesPromotionDetail(@Path("discountId") discountId: Int): Call<BaseResponse<SalesPromotion>>

    @POST(Default.api + Default.version1 + Default.apiDiscount + Default.apiUpdate) fun updateSalesPromotion(@Body req: SalesPromotion): Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiDiscount + "validateitem") fun validateAppliedPromotion(@Body req: ValidatePromotion): Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiDiscount + Default.apiDropDown + "{typeId}/{storeId}") fun getDiscountDropDown(@Path("typeId") typeId: Int, @Path("storeId") storeId: Int): Call<BaseResponseList<CommonDropDown>>
    //endregion

    //region Role

    @GET(Default.api + Default.version1 + Default.apiRole + "{storeId}") fun getRoles(@Path("storeId") storeId: Int): Call<BaseResponseList<Role>>

    @POST(Default.api + Default.version1 + Default.apiRole + Default.apiInsert) fun insertRole(@Body req: Role): Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiRole + Default.apiUpdate + "/{id}") fun getRoleDetail(@Path("id") id: Int): Call<BaseResponse<Role>>

    @POST(Default.api + Default.version1 + Default.apiRole + Default.apiUpdate) fun updateRole(@Body req : Role) : Call<APIResponse>

    //endregion

    //region Tender
    @GET(Default.api + Default.version1 + Default.apiTender + "{status}/{storeId}") fun getTenderList(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Tender>>

    @DELETE(Default.api + Default.version1 + Default.apiTender + Default.apiDelete + "/{id}") fun deleteTender(@Path("id") id : Int) : Call<APIResponse>

    @POST(Default.api + Default.version1 + Default.apiTender + Default.apiInsert) fun insertTender(@Body req : Tender) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiTender + "dropdownpaymentmode") fun getPaymentModeDropDown() : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiTender + Default.apiDropDown + "{storeId}") fun getTenderDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiTender + "dropdowncardtype") fun getCardTypeDropDown() : Call<BaseResponseList<CommonDropDown>>

    @GET(Default.api + Default.version1 + Default.apiTender + Default.apiUpdate + "/{id}") fun getTenderDetail(@Path("id") id : Int) : Call<BaseResponse<Tender>>

    @POST(Default.api + Default.version1 + Default.apiTender + Default.apiUpdate) fun updateTender(@Body req : Tender) : Call<APIResponse>
    //endregion

    //region Employee
    @GET(Default.api + Default.version1 + Default.apiEmployee + "{status}/{storeId}") fun getEmployees(@Path("storeId") storeId : Int, @Path("status") status : Int = Default.ACTIVE) : Call<BaseResponseList<Employee>>

    @DELETE(Default.api + Default.version1 + Default.apiEmployee + Default.apiDelete + "/{id}") fun deleteEmployee(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiRole + Default.apiDropDown + "{storeId}") fun getRoleDropDown(@Path("storeId") storeId : Int) : Call<BaseResponseList<CommonDropDown>>

    @POST(Default.api + Default.version1 + Default.apiEmployee + Default.apiInsert) fun insertEmployee(@Body req : Employee) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiEmployee + Default.apiUpdate + "/{id}") fun getEmployeeDetail(@Path("id") id : Int) : Call<BaseResponse<Employee>>

    @POST(Default.api + Default.version1 + Default.apiEmployee + Default.apiUpdate) fun updateEmployee(@Body req : Employee) : Call<APIResponse>
    //endregion

    //region Store
    @GET(Default.api + Default.version1 + Default.apiStoreBasic + "{status}") fun getStoreList(@Path("status") status : Int) : Call<BaseResponseList<Store>>

    @POST(Default.api + Default.version1 + Default.apiStoreBasic + "updatestatus") fun updateStoreStatus(@Body req : StoreUpdate) : Call<APIResponse>
    //endregion

    //region Invoice
    @GET(Default.api + Default.version1 + Default.apiInvoice + "view/{status}/{storeId}") fun getInvoiceList(@Path("storeId") storeId : Int, @Path("status") status : Int) : Call<BaseResponseList<Invoice>>

    @DELETE(Default.api + Default.version1 + Default.apiInvoice + Default.apiDelete + "/{id}") fun deleteInvoice(@Path("id") id : Int) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiPurchaseOrder + "search/{msg}/{storeId}") fun getAutoPurchaseOrderList(@Path("msg") msg : String, @Path("storeId") storeId : Int) : Call<BaseResponseList<PurchaseOrder>>

    @POST(Default.api + Default.version1 + Default.apiInvoice + Default.apiInsert) fun insertInvoice(@Body req : Invoice) : Call<APIResponse>

    @GET(Default.api + Default.version1 + Default.apiInvoice + Default.apiUpdate + "/{id}") fun getInvoiceDetail(@Path("id") id : Int) : Call<BaseResponse<Invoice>>

    @POST(Default.api + Default.version1 + Default.apiInvoice + Default.apiUpdate) fun updateInvoice(@Body req : Invoice) : Call<APIResponse>
    //endregion
}