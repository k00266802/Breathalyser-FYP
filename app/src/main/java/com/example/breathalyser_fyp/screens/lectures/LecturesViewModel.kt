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

import androidx.compose.runtime.mutableStateOf
import com.example.breathalyser_fyp.CAMPUS_MAP_SCREEN
import com.example.breathalyser_fyp.SETTINGS_SCREEN

import com.example.breathalyser_fyp.model.Lecture
import com.example.breathalyser_fyp.model.service.ConfigurationService
import com.example.breathalyser_fyp.model.service.LogService
import com.example.breathalyser_fyp.model.service.StorageService
import com.example.breathalyser_fyp.screens.BreathalyserFYPViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LecturesViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : BreathalyserFYPViewModel(logService) {
  val options = mutableStateOf<List<String>>(listOf())

  val lectures = storageService.lectures

  fun loadTaskOptions() {
    val hasEditOption = configurationService.isShowTaskEditButtonConfig
    options.value = TaskActionOption.Companion.getOptions(hasEditOption)
  }

//  fun onTaskCheckChange(lecture: Lecture) {
//    launchCatching { storageService.update(lecture.copy(completed = !lecture.completed)) }
//  }

  fun onMapClick(openScreen: (String) -> Unit) = openScreen(CAMPUS_MAP_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)



//  private fun onFlagTaskClick(lecture: Lecture) {
//    launchCatching { storageService.update(lecture.copy(flag = !lecture.flag)) }
//  }

  private fun onDeleteTaskClick(lecture: Lecture) {
    launchCatching { storageService.delete(lecture.id) }
  }
}
