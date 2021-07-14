package com.adammcneilly.composepermissions

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adammcneilly.composepermissions.ui.theme.ComposePermissionsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePermissionsTheme {
                val cameraPermissionState = rememberPermissionState(
                    permission = android.Manifest.permission.CAMERA
                )

                Surface {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp),
                    ) {
                        when {
                            cameraPermissionState.hasPermission -> {
                                PermissionGrantedButton()
                            }
                            cameraPermissionState.hasBeenDeniedForever() -> {
                                PermissionDeniedButton()
                            }
                            else -> {
                                if (cameraPermissionState.shouldShowRationale) {
                                    PermissionRationale()
                                }

                                RequestPermissionButton(cameraPermissionState)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionRationale() {
        Text(
            "This is a rationale explaining why we need the camera permission. We " +
                    "are displaying this because the user has denied the permission once."
        )
    }

    @Composable
    private fun PermissionGrantedButton() {
        Button(onClick = { /*TODO*/ }) {
            Text("Camera permission granted.")
        }
    }

    @Composable
    private fun PermissionDeniedButton() {
        Button(onClick = { /*TODO*/ }) {
            Text("Camera permission denied for good - open settings.")
        }
    }

    @Composable
    private fun RequestPermissionButton(cameraPermissionState: PermissionState) {
        Button(
            onClick = {
                // This fails silently if permission is not in manifest.
                // Is this expected?
                cameraPermissionState.launchPermissionRequest()
            }
        ) {
            Text("Request camera permission.")
        }
    }
}

/**
 * If we should avoid showing a rationale, and we know the user has requested a permission before,
 * this means they have said deny and don't ask me again.
 */
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.hasBeenDeniedForever(): Boolean {
    return this.permissionRequested && !this.shouldShowRationale
}
