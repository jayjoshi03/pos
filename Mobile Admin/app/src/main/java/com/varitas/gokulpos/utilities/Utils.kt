package com.varitas.gokulpos.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.varitas.gokulpos.R
import com.varitas.gokulpos.response.PostResponse
import me.moallemi.tools.extension.date.now
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object Utils {

    //region To check internet availability
    fun isInternetAvailable(context : Context) : Boolean {
        var isOnline = false
        val manager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isOnline = try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
                capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } else {
                val activeNetworkInfo = manager.activeNetworkInfo
                activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
            }
        } catch(e : java.lang.Exception) {
            e.printStackTrace()
            printAndWriteException(e)
            false
        }
        return isOnline
    } //endregion

    //region To fetch login response
    fun fetchLoginResponse() : PostResponse {
        val gson = Gson()
        val json : String = PreferenceData.getPrefString(SharedPreferencesKeys.LOGIN_RESPONSE)
        return gson.fromJson(json, PostResponse::class.java)
    } //endregion

    //region To init OKHttp
    fun initOkHttp() : OkHttpClient {
        val cacheSize = 5 * 1024 * 1024 // 5 MiB
        val cacheDirectory = File("/path/to/cache/directory")
        val cache = Cache(cacheDirectory, cacheSize.toLong())

        val requestTimeOut = 300
        val httpClient = OkHttpClient().newBuilder().cache(cache).connectTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS).readTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS).writeTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addNetworkInterceptor(Interceptor { chain : Interceptor.Chain->
            val original = chain.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder().build()
            val isLogin = PreferenceData.getPrefBoolean(key = SharedPreferencesKeys.IS_LOGIN, defaultValue = false)
            val requestBuilder : Request.Builder = if(!isLogin) {
                original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "public, max-age=5")
                    .url(url)
            } else {
                val response = fetchLoginResponse()
                Log.e("AUTHORIZATION", ":${response.singleResult!!.accessToken}")
                original.newBuilder()
                    .header("Authorization", "${Default.BEARER} ${response.singleResult!!.accessToken}")
                    .header("Cache-Control", "public, only-if-cached, max-stale=86400")
                    .url(url)
            }
            Log.e("API URL", ":$url")
            val request = requestBuilder.build()
            chain.proceed(request)
        })
        httpClient.addInterceptor(interceptor)
        return httpClient.build()
    } //endregion

    //region To print & write exception
    fun printAndWriteException(ex : Exception) {
        ex.printStackTrace()
        Log.e("Exception", ex.message.toString())
    } //endregion

    //region To get two decimal values
    fun getTwoDecimalValue(double : Double) : String {
        return DecimalFormat(Constants.decimalFormatTwoPrecision).format(((double.times(100)).toInt() / 100.0)).toString()
    } //endregion

    //region To convert string to date
    fun convertStringToDate(formatter : SimpleDateFormat, parseDate : String?) : Date {
        return try {
            formatter.parse(parseDate!!)!!
        } catch(ex : Exception) {
            Date()
        }
    } //endregion

    //region To set amount with currency
    fun setAmountWithCurrency(context : Context, amount : Double) : String {
        return context.resources.getString(R.string.Price, PreferenceData.getPrefString(SharedPreferencesKeys.CURRENCY), getTwoDecimalValue(amount))
    } //endregion

    //region To get auto generated UPC
    fun getAutoGeneratedUPCBarcode() : String {
        val allowedChars = "0123456789"
        return (1 .. 12).map { allowedChars.random() }.joinToString("")
    }
    //endregion

    //region To get UTC date
    fun getUTCDate() : String {
        var dateStr = ""
        val c = Calendar.getInstance()
        c.time = now()
        try {
            val formatter = SimpleDateFormat(Constants.dateFormat_UPC)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            dateStr = formatter.format(c.time)
        } catch(e : ParseException) {
            dateStr = ""
            Firebase.crashlytics.recordException(e)
            e.printStackTrace()
        }
        return dateStr
    }
    //endregion

    //region To calculate Margin and Markup
    fun calculateMargin(pricePerUnit : Double, currentCost : Double, buyDown : Double) : Double {
        val margin = if(pricePerUnit > 0.00) getTwoDecimalValue(((((pricePerUnit - (currentCost - buyDown)) / pricePerUnit).toString()).toDouble() * 100)).toDouble()
        else 0.00
        return if(margin > 0.00) margin else 0.00
    }

    fun calculateMarkUp(pricePerUnit : Double, currentCost : Double, buyDown : Double) : Double {
        val markup = if(pricePerUnit > 0.00 && currentCost > 0.00 && (currentCost != buyDown)) getTwoDecimalValue(((((pricePerUnit - (currentCost - buyDown)) / (currentCost - buyDown)).toString()).toDouble() * 100)).toDouble() else 0.00
        return if(markup > 0.00) markup else 0.00
    }
    //endregion

    //region To format phone number
    fun phoneNumberFormat(phoneNumber : String) : String {
        return try {
            if(!TextUtils.isEmpty(phoneNumber)) if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) if(PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country) != null) PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country) else phoneNumber
            else phoneNumber else phoneNumber
        } catch(ex : Exception) {
            ""
        }
    }
    //endregion
}