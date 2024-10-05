//
//  PermissionController.swift
//  Kok
//
//  Created by 이상현 on 10/5/24.
//

import Foundation
import AVFoundation
import Photos

class PermissionController {
    static func isCameraPermissionGranted() -> Bool {
        let cameraAuthStatus = AVCaptureDevice.authorizationStatus(for: .video)
        
        switch cameraAuthStatus {
        case .authorized:
            return true
        case .notDetermined, .denied, .restricted:
            return false
        @unknown default:
            fatalError("Unknown authorization status")
        }
    }
    
    static func requestCameraPermission() {
        AVCaptureDevice.requestAccess(for: .video) { response in
            if response {
                print("Camera access granted")
            } else {
                print("Camera access denied")
            }
        }
    }
    
    static func isPhotoLibraryPermissionGranted() -> Bool {
        let photoAuthStatus = PHPhotoLibrary.authorizationStatus()
        
        switch photoAuthStatus {
        case .authorized, .limited:
            return true
        case .notDetermined, .denied, .restricted:
            return false
        @unknown default:
            fatalError("Unknown authorization status")
        }
    }

    static func requestPhotoLibraryPermission() {
        PHPhotoLibrary.requestAuthorization { status in
            switch status {
            case .authorized:
                print("Photo library access granted")
            case .denied:
                print("Photo library access denied")
            case .restricted:
                print("Photo library access restricted")
            case .notDetermined:
                print("Photo library access not determined")
            case .limited:
                print("Photo library access limited")
            @unknown default:
                fatalError()
            }
        }
    }
}
