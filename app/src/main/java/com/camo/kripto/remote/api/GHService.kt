package com.camo.kripto.remote.api

import com.camo.kripto.ui.presentation.about.Contributors
import retrofit2.http.GET

interface GHService {
    @GET("contributors")
    suspend fun getContributors(): Contributors
}