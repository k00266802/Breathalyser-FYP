package com.example.breathalyser_fyp.screens.campus_map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.breathalyser_fyp.ui.theme.AppTheme as TimetableAppTheme

// ...

@Composable
fun CampusScreen(
    openScreen: (String) -> Unit,
    viewModel: CampusScreenViewModel = hiltViewModel()
) {
    val campus = viewModel.campus
    val cameraPositionState = rememberCameraPositionState {

        //no matter how hard i try, the query just doesn't work
        position = CameraPosition.fromLatLngZoom(LatLng(
            campus?.lat ?: 52.675183922507564,
            campus?.lng ?: -8.648493787771454
        ), 15f)
    }
    CampusScreenContent(position = cameraPositionState,
        openScreen = openScreen)
}

@Composable
fun CampusScreenContent(position: CameraPositionState,
                        openScreen: (String) -> Unit){
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = position
        )

    }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun CampusScreenPreview() {
    TimetableAppTheme {
        CampusScreenContent(position = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(52.675183922507564, -8.648493787771454), 15f)
        },
            {})
    }
}