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
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.breathalyser_fyp.SETTINGS_SCREEN

import com.example.breathalyser_fyp.model.BacReading
import com.example.breathalyser_fyp.model.service.ConfigurationService
import com.example.breathalyser_fyp.model.service.LogService
import com.example.breathalyser_fyp.model.service.StorageService
import com.example.breathalyser_fyp.screens.BreathalyserFYPViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BACViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val configurationService: ConfigurationService
) : BreathalyserFYPViewModel(logService) {
    val bacEntries = storageService.bacReadings

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null

    private val _liveBacReadings = MutableStateFlow<List<BacReading>>(emptyList())
    val liveBacReadings: StateFlow<List<BacReading>> = _liveBacReadings

    // UI States
    private val _isConnected = MutableStateFlow<Boolean>(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _receivedData = MutableLiveData<String>()
    val receivedData: LiveData<String> = _receivedData


    fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

    fun toggleBluetoothConnection(deviceName: String) {
        viewModelScope.launch(Dispatchers.IO) {  // ✅ Runs in background
            if (_isConnected.value == true) {
                disconnectDevice()
            } else {
                connectToDevice(deviceName)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceName: String) {
        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName }
        if (device == null) {
            _receivedData.postValue("Device '$deviceName' not found")
            return
        }

        val uuid = device.uuids.firstOrNull()?.uuid ?: UUID.randomUUID()
        val socket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(uuid)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socket?.connect()
                bluetoothSocket = socket
                _isConnected.value = true
                startListeningForData(socket)
            } catch (e: Exception) {
                _isConnected.value = false
                _receivedData.postValue("Error connecting to device: ${e.message}")
            }
        }
    }

    // Disconnect from Bluetooth device
    fun disconnectDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bluetoothSocket?.close()
                bluetoothSocket = null
                _isConnected.value = false
            } catch (e: IOException) {
                _receivedData.postValue("Error disconnecting from device: ${e.message}")
            }
        }
    }

    // Start listening for incoming data
    private fun startListeningForData(socket: BluetoothSocket?) {
        val inputStream: InputStream? = socket?.inputStream
        val buffer = ByteArray(1024)
        while (_isConnected.value == true) {
            try {
                val bytes = inputStream?.read(buffer) ?: break
                val receivedMessage = String(buffer, 0, bytes)
                val newBacValue = receivedMessage

                Log.w("preprocessedBac", "bluetooth data: $newBacValue")
                if("BAC_" !in newBacValue){
                    break
                }

                var processedBacValue = newBacValue.split("BAC_")[1].toInt()

                val newBacReading = BacReading(
                    bacValue = processedBacValue,
                    timestamp = Timestamp.now(),
                )

                _liveBacReadings.value = _liveBacReadings.value + newBacReading


                launchCatching {
                    storageService.save(newBacReading)
                }

            } catch (e: Exception) {
                Log.e("Bluetooth Error", e.stackTraceToString())
                _receivedData.postValue("Error reading from device: ${e.message}")
                break
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        disconnectDevice()
    }


//  private fun onFlagTaskClick(lecture: BacReading) {
//    launchCatching { storageService.update(lecture.copy(flag = !lecture.flag)) }
//  }

    private fun onDeleteTaskClick(bacReading: BacReading) {
        launchCatching { storageService.delete(bacReading.id) }
    }
}
