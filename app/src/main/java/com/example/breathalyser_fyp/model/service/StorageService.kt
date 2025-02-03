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

package com.example.breathalyser_fyp.model.service

import com.example.breathalyser_fyp.model.Campus
import com.example.breathalyser_fyp.model.BacReading
import kotlinx.coroutines.flow.Flow

interface StorageService {
  val bacReadings: Flow<List<BacReading>>
  suspend fun getBacReadings(lectureId: String): BacReading?
  suspend fun save(bacReading: BacReading): String
  suspend fun update(bacReading: BacReading)
  suspend fun delete(lectureId: String)
}
