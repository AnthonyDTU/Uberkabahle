import 'package:camera/camera.dart';
import 'package:flutter/cupertino.dart';

enum AvaliableModels {
  m416,
  m512,
}

class AppSettings {
  static const methodChannelName = "BackendChannel";

  static const modelToUse = AvaliableModels.m512;
  static const Size cardLayout = Size(4, 3);

  static const String modelFile_416 = "card_aug_v1_best-416.tflite";
  static const String modelFile_512 = "card_aug_v1_best-512.tflite";
  static const String labelFile = "card_aug_v1.names";

  static const int processedImageSize = 512;

  static const double confidenceThreshold = 0.5;
  static const double NMSOverlayThreshold = 0.6;

  static double witdhRatio = 0;
  static double heightRatio = 0;
  static double previewSizeHeight = 0;
  static double previewSizeWidth = 0;
  static Size inputImageSize = Size(0, 0);

  static Size get previewSize => Size(previewSizeWidth, previewSizeHeight);
  static double imagePreviewHorizontalOffset = 0;

  static void setImageSize(ResolutionPreset resolutionPreset) {
    switch (resolutionPreset) {
      case ResolutionPreset.low:
        inputImageSize = const Size(320, 240);
        break;

      case ResolutionPreset.medium:
        inputImageSize = const Size(720, 480);
        break;

      case ResolutionPreset.high:
        inputImageSize = const Size(1280, 720);
        break;

      case ResolutionPreset.veryHigh:
        inputImageSize = const Size(1920, 1080);
        break;

      case ResolutionPreset.ultraHigh:
        inputImageSize = const Size(3840, 2160);
        break;

      case ResolutionPreset.max:
        inputImageSize = const Size(3840, 2160);
        break;
    }
  }

  static void setPreviewSize(Size _screenSize) {
    previewSizeHeight = _screenSize.height;
    previewSizeWidth = _screenSize.height * (16 / 9);
    imagePreviewHorizontalOffset = (_screenSize.width - previewSizeWidth) / 2;
  }

  static void calculateRatio() {
    heightRatio = previewSizeHeight / inputImageSize.height;
    witdhRatio = previewSizeWidth / inputImageSize.width;
  }
}
