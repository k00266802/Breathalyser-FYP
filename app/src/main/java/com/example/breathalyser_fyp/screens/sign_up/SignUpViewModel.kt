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

package com.example.breathalyser_fyp.screens.sign_up

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.breathalyser_fyp.BAC_ENTRIES_SCREEN
import com.example.breathalyser_fyp.SETTINGS_SCREEN
import com.example.breathalyser_fyp.SIGN_UP_SCREEN
import com.example.breathalyser_fyp.common.ext.isValidEmail
import com.example.breathalyser_fyp.common.ext.isValidPassword
import com.example.breathalyser_fyp.common.ext.passwordMatches
import com.example.breathalyser_fyp.common.snackbar.SnackbarManager
import com.example.breathalyser_fyp.model.service.AccountService
import com.example.breathalyser_fyp.model.service.LogService
import com.example.breathalyser_fyp.screens.BreathalyserFYPViewModel
import com.example.breathalyser_fyp.R.string as AppText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : BreathalyserFYPViewModel(logService) {
  var uiState = mutableStateOf(SignUpUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onRepeatPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(repeatPassword = newValue)
  }

  fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      Log.e("Sign up","Email error")
      return
    }

    if (!password.isValidPassword()) {
      SnackbarManager.showMessage(AppText.password_error)
      Log.e("Sign up","Password error")
      return
    }

    if (!password.passwordMatches(uiState.value.repeatPassword)) {
      SnackbarManager.showMessage(AppText.password_match_error)
      Log.e("Sign up","Password match error")
      return
    }

    Log.w("Sign up", "Linking account")
    launchCatching {
      accountService.linkAccount(email, password)
      openAndPopUp(BAC_ENTRIES_SCREEN, SIGN_UP_SCREEN)
    }
  }
}
