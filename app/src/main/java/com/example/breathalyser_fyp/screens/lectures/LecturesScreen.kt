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

package com.example.breathalyser_fyp.screens.lectures

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
import com.example.breathalyser_fyp.model.Lecture
import com.example.breathalyser_fyp.ui.theme.AppTheme as TimetableAppTheme

@Composable
@ExperimentalMaterialApi
fun LecturesScreen(
  openScreen: (String) -> Unit,
  viewModel: LecturesViewModel = hiltViewModel()
) {
  val lectures = viewModel.lectures.collectAsStateWithLifecycle(emptyList())
  val options by viewModel.options

  LecturesScreenContent(
    lectures = lectures.value,
    options = options,
    onMapClick = viewModel::onMapClick,
    onSettingsClick = viewModel::onSettingsClick,
    openScreen = openScreen
  )

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun LecturesScreenContent(
  modifier: Modifier = Modifier,
  lectures: List<Lecture>,
  options: List<String>,
  onMapClick: ((String) -> Unit) -> Unit,
  onSettingsClick: ((String) -> Unit) -> Unit,
  openScreen: (String) -> Unit
) {
  Scaffold(
  ) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()) {
      ActionToolbar(
        title = AppText.lectures,
        modifier = Modifier.toolbarActions(),
        primaryActionIcon = AppIcon.ic_settings,
        primaryAction = { onSettingsClick(openScreen) },
        secondaryAction = { onMapClick(openScreen) },
        secondaryActionIcon = AppIcon.ic_map_24px
      )

      Spacer(modifier = Modifier.smallSpacer())
      val expandStates = remember(lectures) { lectures.map { false }.toMutableStateList() }
      Log.w("expandStates", expandStates.toString())

      LazyColumn {
        lectures.forEachIndexed { i, lecture ->
          val expanded = expandStates[i]
          item(key = i){
            LectureItem(
              lecture = lecture,
              options = options,
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
fun LecturesScreenPreview() {
  val lecture = Lecture(
    lectureName = "Lecture title",
    description = "Lecture Description"
  )

  val options = TaskActionOption.Companion.getOptions(hasEditOption = true)

  TimetableAppTheme {
    LecturesScreenContent(
      lectures = listOf(lecture),
      options = options,
      onMapClick = { },
      onSettingsClick = { },
      openScreen = { }
    )
  }
}
