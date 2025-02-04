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

package com.example.breathalyser_fyp.screens.bac_entries

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.breathalyser_fyp.R.drawable as AppIcon
import com.example.breathalyser_fyp.R.string as AppText
import com.example.breathalyser_fyp.common.composable.ActionToolbar
import com.example.breathalyser_fyp.common.ext.smallSpacer
import com.example.breathalyser_fyp.common.ext.toolbarActions
import com.example.breathalyser_fyp.model.BacReading
import com.example.breathalyser_fyp.resources
import com.example.breathalyser_fyp.ui.theme.BreathalyserAppTheme as TimetableAppTheme
import androidx.compose.runtime.getValue

@Composable
@ExperimentalMaterialApi
fun BacScreen(
  openScreen: (String) -> Unit,
  viewModel: BACViewModel = hiltViewModel()
) {
  val bacReadings by viewModel.bacEntries.collectAsStateWithLifecycle(emptyList())
  //val liveBacReadings by viewModel.liveBacReadings.collectAsStateWithLifecycle ()
  //val allReadings = remember(bacReadings, liveBacReadings) {
  //    liveBacReadings + bacReadings
  //  }
  val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

  val deviceName = resources().getString(AppText.device_name)

  BacScreenContent(
    bacReading = bacReadings,
    isConnected = isConnected,
    onToggleBluetooth = {viewModel.toggleBluetoothConnection(deviceName)},
    onSettingsClick = viewModel::onSettingsClick,
    openScreen = openScreen
  )

  LaunchedEffect(viewModel) {  }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun BacScreenContent(
  modifier: Modifier = Modifier,
  bacReading: List<BacReading>,
  isConnected: Boolean,
  onToggleBluetooth: () -> Unit,
  onSettingsClick: ((String) -> Unit) -> Unit,
  openScreen: (String) -> Unit
) {
  Scaffold(
  ) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()) {
      ActionToolbar(
        title = AppText.bac_readings,
        modifier = Modifier.toolbarActions(),
        primaryActionIcon = AppIcon.ic_settings,
        primaryAction = { onSettingsClick(openScreen) },
        secondaryAction = { onToggleBluetooth() },
        secondaryActionIcon = if (isConnected) AppIcon.ic_bluetooth_connected else AppIcon.ic_bluetooth
      )

      Spacer(modifier = Modifier.smallSpacer())
      val expandStates = remember(bacReading) { bacReading.map { false }.toMutableStateList() }
      Log.w("expandStates", expandStates.toString())

      LazyColumn {
        bacReading.forEachIndexed { i, bacReading ->
          val expanded = expandStates[i]
          item(key = i){
            BACItem(
              bacReading = bacReading,
              isExpanded = expanded,
              onExpandedChange = { expandStates[i] = it }
            )
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun BacScreenPreview() {
  val bacReading = BacReading(
    bacValue = 209
  )

  //val options = TaskActionOption.Companion.getOptions(hasEditOption = true)

  TimetableAppTheme {
    BacScreenContent(
      bacReading = listOf(bacReading, bacReading, bacReading),
      isConnected = true,
      onToggleBluetooth = { },
      onSettingsClick = { },
      openScreen = { }
    )
  }
}
