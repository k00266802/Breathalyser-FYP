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

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breathalyser_fyp.model.Lecture
import java.text.SimpleDateFormat



@Composable
@ExperimentalMaterialApi
fun LectureItem(
  lecture: Lecture,
  options: List<String>,
  isExpanded: Boolean,
  onExpandedChange: (Boolean) -> Unit
)
 {

  Card(
    backgroundColor = MaterialTheme.colors.background,
    modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
      .clickable { onExpandedChange(!isExpanded) }
  ) {
    Log.w("isExpanded", isExpanded.toString())
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = lecture.lectureName, style = MaterialTheme.typography.subtitle1)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(text = SimpleDateFormat("dd/MM/yyyy").format(lecture.startTime).toString(), fontSize = 14.sp)
          Text(text = SimpleDateFormat("HH:mm").format(lecture.startTime).toString() + " - " + SimpleDateFormat("HH:mm").format(lecture.endTime).toString(), fontSize = 12.sp)
        }
        if(isExpanded){
          Text(text = lecture.description, style = MaterialTheme.typography.body1, fontSize = 14.sp)
          Text(text = lecture.room, style = MaterialTheme.typography.body1, fontSize = 10.sp)
        }
      }
    }
  }
}


