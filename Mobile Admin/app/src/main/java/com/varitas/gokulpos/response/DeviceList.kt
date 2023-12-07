package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class DeviceList(@SerializedName("id") var id: Int? = null, @SerializedName("deviceName") var deviceName: String? = null, @SerializedName("type") var type: String? = null, @SerializedName("configuration") var configuration: String? = null, @SerializedName("statusClass") var statusClass: String? = null, @SerializedName("macAddress") var macAddress: String? = null, @SerializedName("ipaddress") var ipaddress: String? = null, @SerializedName("serialNo") var serialNo: Int? = null, @SerializedName("empoyeeName") var employeeName: String? = null, @SerializedName("driverUrl") var driverUrl: String? = null, @SerializedName("storeId") var storeId: Int? = null) {
    fun deviceDetail(): String {
        return try {
            StringBuilder().append(deviceName!!).append(", ").append(type!!).toString()
        } catch (ex: Exception) {
            ""
        }
    }
}