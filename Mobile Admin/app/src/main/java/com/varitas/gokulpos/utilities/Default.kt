package com.varitas.gokulpos.utilities

object Default { //LOCAL
    //const val BASE_URL = "http://192.168.29.254/Portal/"
    //region DEVELOPMENT
//    const val BASE_URL = "http://20.246.136.52/"
//    const val BASE_URL = "http://vikaspvaritas23-001-site2.etempurl.com/"
//    const val BASE_URL_EMPLOYEE = "http://vikaspvaritas23-001-site1.etempurl.com/"
//    const val BASE_URL_SALES = "http://vikaspvaritas23-001-site3.etempurl.com/"
//    const val BASE_URL_CUSTOMER = "http://vikaspvaritas23-001-site4.etempurl.com/"
//    const val BASE_URL_STORE = "http://vikaspvaritas23-001-site6.etempurl.com/" //endregion

    const val BASE_URL = "http://products.gokulpos.com/"
    const val PRINT_URL = "http://20.246.231.252/v1/Receipt/"
    const val REPORT_URL = "http://20.246.231.252/v1/Report/"
//    const val BASE_URL_PRODUCT = "http://192.168.29.254/Portal/Product/"

    //region RELEASE
    //    const val BASE_URL = "http://20.253.112.242/" //Auth
    //    const val BASE_URL_EMPLOYEE = "http://20.253.124.148/" //Employee
    //    const val BASE_URL_SALES = "http://4.236.220.67/" //Order
    //    const val BASE_URL_CUSTOMER = "http://4.157.99.91/" //Customer
    //    const val BASE_URL_PRODUCT = "http://20.253.103.55/" //Product
    //    const val BASE_URL_STORE = "http://20.246.135.79/" //Store

    /*auth.gokulpos.com
    customers.gokulpos.com
    employee.gokulpos.com
    order.gokulpos.com
    products.gokulpos.com
    store.gokulpos.com*/
    // endregion

    const val SUCCESS_API = 200
    const val WIFI = "Wifi"
    const val IS_CONNECTED = "isConnected"
    const val IS_QTY = "isQty"
    const val TITLE = "Title"
    const val BEARER = "Bearer"
    const val CUSTOMER_ID = "CustomerId"
    const val PRODUCT_ID = "ProductId"
    const val EMPLOYEE_ID = "EmployeeId"
    const val ORDER_ID = "OrderId"
    const val ID = "Id"
    const val REPORTS = "Reports"
    const val ACTIVE = 1
    const val REJECT = 23
    const val INACTIVE = 2
    const val MARGIN = 0
    const val MARKUP = 1
    const val PRODUCT = "Product"
    const val SIZE = "Size"
    const val PACK = "Pack"
    const val CATEGORY = "Category"
    const val BRAND = "Brand"
    const val VENDOR = "Vendor"
    const val TAX = "Tax"
    const val DEPARTMENT = "Department"
    const val SPECIFICATION = "Specification"
    const val FACILITY = "Facility"
    const val UOM = "UOM"
    const val USER = "User"
    const val NAME = "Name"
    const val ITEMCOUNT = "ItemCount"
    const val GROUP = "Group"
    const val TENDER = "Tender"
    const val PARENT_ID = "ParentId"
    const val IS_HOLD = "IsHold"
    const val IS_VOID = "IsVoid"
    const val INVOICE = "Invoice"



    const val ALL_DATA = 0
    const val CONFIRM_ORDER = 5
    const val SPECIFICATION_TYPE = 1
    const val PENDING_STORE = 22

    const val HOLD_ORDER = 6
    const val VOID_ORDER = 7

    const val TYPE_ITEM = 4
    const val TYPE_TAX = 1



    //region Menu
    const val MANAGE_PRODUCT = 1
    const val MANAGE_ROLE = 2
    const val MANAGE_CUSTOMER = 3
    const val MANAGE_EMPLOYEE = 4
    const val MANAGE_GROUP = 5
    const val MANAGE_ORDER = 6
    const val MANAGE_SALES_PROMO = 8
    const val MANAGE_TENDER = 9
    const val MANAGE_INVENTORY = 51
    const val MANAGE_STORE = 52
    const val MANAGE_REPORT = 55
    //endregion

    //region SubMenu
    const val MANAGE_BRAND = 10
    const val MANAGE_CATEGORY = 11
    const val MANAGE_DEPARTMENT = 12
    const val MANAGE_FACILITY = 13
    const val MANAGE_PRODUCT_ITEM = 14
    const val MANAGE_SPECIFICATION = 15
    const val MANAGE_TAX = 16
    const val MANAGE_UOM = 17
    const val MANAGE_VENDOR = 18
    const val MANAGE_PURCHASE_ORDER = 53
    const val MANAGE_INVOICE = 54
    const val MANAGE_REPORT_DEPARTMENT = 56
    const val MANAGE_REPORT_SALE = 57
    const val MANAGE_REPORT_OPERATION_RECORD = 58
    const val MANAGE_REPORT_CASH_SALE = 59
    const val MANAGE_REPORT_SALE_EXCEL = 60
    //endregion

    //regionInvoice and Purchase Status
    const val ORDER_PLACED = 5
    const val DISPATCH = 19
    const val PARTIALLY_RECEIVED = 20
    const val FULLY_RECEIVED = 21
    const val CANCEL = 24
    const val PAID = 10
    const val PARTIAL = 11
    const val UNPAID = 12
    //endregion

    //region API Component
    const val api = "api/"
    const val version1 = "v1.0/"
    const val version2 = "v2.0/"

    const val apiInsert = "insert"
    const val apiUpdate = "update"
    const val apiDelete = "delete"

    const val apiDropDown = "dropdown/"
    const val apiBrand = "brand/"
    const val apiDepartment = "department/"
    const val apiCategory = "category/"
    const val apiTax = "tax/"
    const val apiGroup = "group/"
    const val apiSpecification = "specification/"
    const val apiSpecificationType = "specificationtype/"
    const val apiUOM = "uom/"
    const val apiFacility = "facility/"
    const val apiVendor = "vendor/"
    const val apiStoreItem = "storeitem/"
    const val apiStoreVItem = "storeitemv/"
    const val apiCustomer = "customer/"
    const val apiEmployee = "employee/"
    const val apiRole = "role/"
    const val apiOrder = "order/"
    const val apiUser = "user/"
    const val apiPayment = "payment/"
    const val apiItems = "items/"
    const val apiListItems = "listitems/"
    const val apiTender = "tender/"
    const val apiPurchaseOrder = "purchaseorder/"
    const val apiInvoice = "invoice/"
    const val apiStore = "storebasic/"
    const val apiView = "view/"
    //endregion
}