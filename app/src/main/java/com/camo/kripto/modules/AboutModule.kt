package com.camo.kripto.modules

import com.camo.kripto.Constants
import com.camo.kripto.remote.api.GHApiHelper
import com.camo.kripto.remote.api.GHService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
object AboutModule {

    @ActivityScoped
    @Provides
    @Named("getGHRetrofit")
    fun getGHRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.GH_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //Doesn't require the adapter
    }

    @Provides
    @ActivityScoped
    fun getGHService(
        @Named("getGHRetrofit")
        retrofit: Retrofit
    ): GHService = retrofit.create(GHService::class.java)

    @Provides
    @ActivityScoped
    fun getGHApiHelper(ghService: GHService): GHApiHelper {
        return GHApiHelper(ghService)
    }
}