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

import com.example.breathalyser_fyp.model.Campus
import com.example.breathalyser_fyp.model.Lecture
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

  private val collection get() = firestore.collection(LECTURE_COLLECTION)
    .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

  @OptIn(ExperimentalCoroutinesApi::class)
  override val lectures: Flow<List<Lecture>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        firestore
          .collection(LECTURE_COLLECTION)
          .whereEqualTo(USER_ID_FIELD, user.id)
          .orderBy(START_TIME_FIELD, Query.Direction.ASCENDING)
          .dataObjects()
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun getCampus(): Campus? =
    firestore.collection(CAMPUS_COLLECTION).whereEqualTo(USER_ID_FIELD, auth.currentUserId).get().await().first().toObject<Campus>()

  override suspend fun getLecture(lectureId: String): Lecture? =
    firestore.collection(LECTURE_COLLECTION).document(lectureId).get().await().toObject()

  override suspend fun save(lecture: Lecture): String =
    trace(SAVE_LECTURE_TRACE) {
     val updatedTask = lecture.copy(userId = auth.currentUserId)
      firestore.collection(LECTURE_COLLECTION).add(updatedTask).await().id
    }

  override suspend fun update(lecture: Lecture): Unit =
    trace(UPDATE_LECTURE_TRACE) {
      firestore.collection(LECTURE_COLLECTION).document(lecture.id).set(lecture).await()
    }

  override suspend fun delete(lectureId: String) {
    firestore.collection(LECTURE_COLLECTION).document(lectureId).delete().await()
  }
  

  companion object {
    private const val USER_ID_FIELD = "userId"
    private const val DESCRIPTION_FIELD = "description"
    private const val ROOM_FIELD = "room"
    private const val TEST = "wBNqRyQBTOCTXIwdmcZK"
    private const val LECTURE_NAME_FIELD = "lectureName"
    private const val START_TIME_FIELD = "startTime"
    private const val END_TIME_FIELD = "endTime"
    private const val LECTURE_COLLECTION = "lectures"
    private const val CAMPUS_COLLECTION = "campus"
    private const val SAVE_LECTURE_TRACE = "saveTask"
    private const val UPDATE_LECTURE_TRACE = "updateTask"
  }
}
