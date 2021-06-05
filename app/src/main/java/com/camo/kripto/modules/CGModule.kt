package com.camo.kripto.modules

import android.content.Context
import androidx.room.Room
import com.camo.kripto.Constants
import com.camo.kripto.local.AppDb
import com.camo.kripto.remote.api.CGApiHelper
import com.camo.kripto.remote.api.CGService
import com.camo.kripto.repos.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CGModule {

    @Singleton
    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    @Provides
    @Singleton
    fun getCGService(): CGService = getRetrofit().create(CGService::class.java)

    @Provides
    @Singleton
    fun getCGApiHelper(): CGApiHelper = CGApiHelper(getCGService())

    @Provides
    @Singleton
    fun getAppDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(
        context.applicationContext,
        AppDb::class.java,
        "kriptoDB.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun getRepo(@ApplicationContext context:Context): Repository = Repository(getAppDb(context), getCGApiHelper())

}