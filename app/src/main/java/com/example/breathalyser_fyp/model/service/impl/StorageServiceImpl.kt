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

package com.example.breathalyser_fyp.model.service.impl

import com.example.breathalyser_fyp.model.BacReading
import com.example.breathalyser_fyp.model.service.AccountService
import com.example.breathalyser_fyp.model.service.StorageService
import com.example.breathalyser_fyp.model.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await

class StorageServiceImpl @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: AccountService
  ) : StorageService {

  private val collection get() = firestore.collection(BAC_READING_COLLECTION)
    .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

  @OptIn(ExperimentalCoroutinesApi::class)
  override val bacReadings: Flow<List<BacReading>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        firestore
          .collection(BAC_READING_COLLECTION)
          .whereEqualTo(USER_ID_FIELD, user.id)
          .orderBy(TIMESTAMP_FIELD, Query.Direction.ASCENDING)
          .dataObjects()
      }


  override suspend fun getBacReadings(lectureId: String): BacReading? =
    firestore.collection(BAC_READING_COLLECTION).document(lectureId).get().await().toObject()

  override suspend fun save(bacReading: BacReading): String =
    trace(SAVE_BAC_READING_TRACE) {
     val updatedTask = bacReading.copy(userId = auth.currentUserId)
      firestore.collection(BAC_READING_COLLECTION).add(updatedTask).await().id
    }

  override suspend fun update(bacReading: BacReading): Unit =
    trace(UPDATE_BAC_READING_TRACE) {
      firestore.collection(BAC_READING_COLLECTION).document(bacReading.id).set(bacReading).await()
    }

  override suspend fun delete(lectureId: String) {
    firestore.collection(BAC_READING_COLLECTION).document(lectureId).delete().await()
  }
  

  companion object {
    private const val USER_ID_FIELD = "userId"
    private const val TIMESTAMP_FIELD = "timestamp"
    private const val BAC_READING_COLLECTION = "bacReadings"
    private const val SAVE_BAC_READING_TRACE = "saveBacReading"
    private const val UPDATE_BAC_READING_TRACE = "updateBacReading"
  }
}
