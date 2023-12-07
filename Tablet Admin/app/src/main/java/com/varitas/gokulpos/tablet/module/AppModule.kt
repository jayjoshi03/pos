package com.varitas.gokulpos.tablet.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.repositories.CustomerRepository
import com.varitas.gokulpos.tablet.repositories.CustomerRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.DashboardRepository
import com.varitas.gokulpos.tablet.repositories.DashboardRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.EmployeeRepository
import com.varitas.gokulpos.tablet.repositories.EmployeeRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.LoginRepository
import com.varitas.gokulpos.tablet.repositories.LoginRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.OrdersRepository
import com.varitas.gokulpos.tablet.repositories.OrdersRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.ProductFeatureRepository
import com.varitas.gokulpos.tablet.repositories.ProductFeatureRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.ProductsRepository
import com.varitas.gokulpos.tablet.repositories.ProductsRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.ReportRepository
import com.varitas.gokulpos.tablet.repositories.ReportsRepositoryImpl
import com.varitas.gokulpos.tablet.repositories.UserRepository
import com.varitas.gokulpos.tablet.repositories.UserRepositoryImpl
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class) class AppModule {

    //region Normal
    @Provides @Singleton @Named("Normal") fun providesRetrofit(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("NormalApi") fun providesApi(@Named("Normal") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesUserRepository(@Named("NormalApi") api: ApiClient): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides @Singleton fun providesLoginRepository(@Named("NormalApi") api: ApiClient): LoginRepository = LoginRepositoryImpl(api)

    //endregion

    //region Employee
    @Provides @Singleton @Named("Employee") fun providesRetrofitEmployee(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("EmployeeApi") fun providesEmployeeApi(@Named("Employee") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesEmployeesRepository(@Named("EmployeeApi") api: ApiClient): EmployeeRepository = EmployeeRepositoryImpl(api)

    //endregion

    //region Sales
    @Provides @Singleton @Named("Sales") fun providesRetrofitSales(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("SalesApi") fun providesSalesApi(@Named("Sales") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesOrdersRepository(@Named("SalesApi") api: ApiClient): OrdersRepository = OrdersRepositoryImpl(api)

    @Provides @Singleton fun providesReportsRepository(@Named("SalesApi") api: ApiClient): ReportRepository = ReportsRepositoryImpl(api)

    //endregion

    //region Customer
    @Provides @Singleton @Named("Customer") fun providesRetrofitCustomer(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("CustomerApi") fun providesCustomerApi(@Named("Customer") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesCustomerRepository(@Named("CustomerApi") api: ApiClient): CustomerRepository = CustomerRepositoryImpl(api)

    //endregion

    //region Product
    @Provides @Singleton @Named("Product") fun providesRetrofitProduct(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("ProductApi") fun providesProductApi(@Named("Product") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesDepartmentCategoryRepository(@Named("ProductApi") api: ApiClient): ProductFeatureRepository = ProductFeatureRepositoryImpl(api)

    @Provides @Singleton fun providesProductsRepository(@Named("ProductApi") api: ApiClient): ProductsRepository = ProductsRepositoryImpl(api)

    //endregion

    //region Store
    @Provides @Singleton @Named("Store") fun providesRetrofitStore(): Retrofit {
        return requestUrl()
    }

    @Provides @Singleton @Named("StoreApi") fun providesStoreApi(@Named("Store") retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Provides @Singleton fun providesDashboardRepository(@Named("StoreApi") api: ApiClient, @Named("SalesApi") salesApi: ApiClient): DashboardRepository = DashboardRepositoryImpl(api,salesApi)

    //endregion

    //region To prepare request
    private var okHttpClient: OkHttpClient? = null

    private fun requestUrl(): Retrofit {
        if (okHttpClient == null) okHttpClient = Utils.initOkHttp()
        return Retrofit.Builder().baseUrl(Default.BASE_URL).client(okHttpClient!!).addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build()
    }

    //endregion
}