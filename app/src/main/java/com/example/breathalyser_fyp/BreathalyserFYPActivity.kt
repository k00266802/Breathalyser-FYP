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

package com.example.breathalyser_fyp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import dagger.hilt.android.AndroidEntryPoint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

// BreathalyserFYPActivity starts the first composable, which uses material cards that are still experimental.
// TODO: Update material dependency and experimental annotations once the API stabilizes.
@AndroidEntryPoint
@ExperimentalMaterialApi
class BreathalyserFYPActivity : AppCompatActivity() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request necessary permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT),
            1
        )

        setContent { BreathalyserFYP() }
        connectToDevice("Breathalyser") {data -> Log.w("b-data", data) }

    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(deviceName: String, onDataReceived: (String) -> Unit) {
        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName }
        if (device == null) {
            Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uuid = device.uuids.firstOrNull()?.uuid ?: UUID.randomUUID()
        val socket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(uuid)

        //bluetoothAdapter.cancelDiscovery()

        try {
            socket?.connect()
            Toast.makeText(this, "Connected to $deviceName", Toast.LENGTH_SHORT).show()

            val inputStream: InputStream? = socket?.inputStream
            val outputStream: OutputStream? = socket?.outputStream

            val buffer = ByteArray(1024)
            val bytes = inputStream?.read(buffer)
            val receivedMessage = bytes?.let { String(buffer, 0, it) } ?: ""

            onDataReceived(receivedMessage)

            socket?.close()
        } catch (e: Exception) {
            Log.e("Bluetooth", "Error connecting to device", e)
            Toast.makeText(this, "Error connecting to device", Toast.LENGTH_SHORT).show()
        }
    }
}


