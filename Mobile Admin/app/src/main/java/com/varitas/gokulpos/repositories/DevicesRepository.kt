package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.DeviceList

interface DevicesRepository {

    suspend fun fetchDevices(storeId: Int, onSuccess: (response: BaseResponseList<DeviceList>) -> Unit, onFailure: (t: Throwable) -> Unit)

}