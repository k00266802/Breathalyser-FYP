/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.breathalyser_fyp.screens.splash

import androidx.compose.runtime.mutableStateOf
import com.example.breathalyser_fyp.LOGIN_SCREEN
import com.example.breathalyser_fyp.SPLASH_SCREEN
import com.example.breathalyser_fyp.model.service.AccountService
import com.example.breathalyser_fyp.model.service.ConfigurationService
import com.example.breathalyser_fyp.model.service.LogService
import com.example.breathalyser_fyp.screens.BreathalyserFYPViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
  configurationService: ConfigurationService,
  private val accountService: AccountService,
  logService: LogService
) : BreathalyserFYPViewModel(logService) {
  val showError = mutableStateOf(false)

  init {
    launchCatching { configurationService.fetchConfiguration() }
  }

  fun onAppStart(openAndPopUp: (String, String) -> Unit) {

    showError.value = false
    openAndPopUp(LOGIN_SCREEN, SPLASH_SCREEN)
  }

}
