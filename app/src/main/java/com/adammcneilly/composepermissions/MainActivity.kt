package com.adammcneilly.composepermissions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adammcneilly.composepermissions.ui.theme.ComposePermissionsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePermissionsTheme {
                val cameraStoragePermissionState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )

                Surface {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp),
                    ) {
                        when {
                            cameraStoragePermissionState.allPermissionsGranted -> {
                                PermissionGrantedButton()
                            }
                            cameraStoragePermissionState.hasBeenDeniedForever() -> {
                                PermissionDeniedButton(cameraStoragePermissionState.revokedPermissionNames())
                            }
                            else -> {
                                if (cameraStoragePermissionState.shouldShowRationale) {
                                    PermissionRationale(cameraStoragePermissionState.revokedPermissionNames())
                                }

                                RequestPermissionButton(cameraStoragePermissionState)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionRationale(revokedPermissionNames: String) {
        Text(
            "This is a rationale explaining why we need the following permissions: $revokedPermissionNames. We " +
                    "are displaying this because the user has denied the permission once."
        )
    }

    @Composable
    private fun PermissionGrantedButton() {
        Button(onClick = { /*TODO*/ }) {
            Text("Camera and storage permissions granted.")
        }
    }

    @Composable
    private fun PermissionDeniedButton(revokedPermissionNames: String) {
        Button(
            onClick = {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                )
            }
        ) {
            Text("The $revokedPermissionNames permission(s) was denied - open settings.")
        }
    }

    @Composable
    private fun RequestPermissionButton(cameraPermissionState: MultiplePermissionsState) {
        Button(
            onClick = {
                // This fails silently if permission is not in manifest.
                // Is this expected?
                cameraPermissionState.launchMultiplePermissionRequest()
            }
        ) {
            Text("Request camera and/or storage permission.")
        }
    }
}

/**
 * If we should avoid showing a rationale, and we know the user has requested a permission before,
 * this means they have said deny and don't ask me again.
 *
 * On app startup this will be false, it's not until the user requests a permission at least once
 * that we can determine if they've denied it before.
 */
@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.hasBeenDeniedForever(): Boolean {
    return this.permissionRequested && !this.shouldShowRationale
}

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.revokedPermissionNames(): String {
    return this.revokedPermissions.joinToString { permissionState ->
        permissionState.permission.removePrefix("android.permission.")
    }
}
