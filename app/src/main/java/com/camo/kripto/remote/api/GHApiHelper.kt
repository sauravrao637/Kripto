package com.camo.kripto.remote.api

import com.camo.kripto.ui.presentation.about.Contributors

class GHApiHelper(val ghService: GHService) {
    suspend fun getContributors(): Contributors = ghService.getContributors()
}