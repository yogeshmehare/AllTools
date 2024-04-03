package com.yogesh.alltools.utils

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class FlashlightHelper(private val context: Context) {

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    init {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        try {
            cameraId = cameraManager?.cameraIdList?.get(0)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun toggleFlashlight() {
        try {
            if (isFlashlightOn()) {
                turnOffFlashlight()
            } else {
                turnOnFlashlight()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun isFlashlightOn(): Boolean {
        return cameraManager?.getCameraCharacteristics(cameraId!!)?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }

    fun turnOnFlashlight() {
        cameraManager?.setTorchMode(cameraId!!, true)
    }

    fun turnOffFlashlight() {
        cameraManager?.setTorchMode(cameraId!!, false)
    }
}
