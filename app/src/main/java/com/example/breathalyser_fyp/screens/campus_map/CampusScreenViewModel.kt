
package com.example.breathalyser_fyp.screens.campus_map

import com.example.breathalyser_fyp.model.Campus

import com.example.breathalyser_fyp.model.service.ConfigurationService
import com.example.breathalyser_fyp.model.service.LogService
import com.example.breathalyser_fyp.model.service.StorageService
import com.example.breathalyser_fyp.screens.BreathalyserFYPViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CampusScreenViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : BreathalyserFYPViewModel(logService) {

  var campus: Campus? = Campus()

  init {
    launchCatching{
      campus = storageService.getCampus()
    }
  }

}
