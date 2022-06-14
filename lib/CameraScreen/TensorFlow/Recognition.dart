import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import '../../AppSettings.dart';
import 'UIElements/BoundingBox.dart';

class Recognition implements Comparable<Recognition> {
  late String label;
  final double confidence;
  late Rect location;

  Recognition({
    required this.label,
    required this.confidence,
    required this.location,
  });

  BoundingBox getBoundingBox() {
    return BoundingBox(recognition: this);
  }

  /// Returns bounding box rectangle corresponding to the
  /// displayed image on screen
  ///
  /// This is the actual location where rectangle is rendered on
  /// the screen
  Rect get renderLocation {
    // ratioX = screenWidth / imageWidth
    double ratioX = AppSettings.witdhRatio;

    // ratioY = sceenHeight / imageHeight
    double ratioY = AppSettings.heightRatio;

    double transLeft = max(0.1, location.left * ratioX) + AppSettings.imagePreviewHorizontalOffset;
    double transTop = max(0.1, location.top * ratioY);
    double transWidth = min(location.width * ratioX, AppSettings.previewSize.width);
    double transHeight = min(location.height * ratioY, AppSettings.previewSize.height);

    Rect transformedRect = Rect.fromLTWH(transLeft, transTop, transWidth, transHeight);
    return transformedRect;
  }

  @override
  String toString() {
    return 'Recognition(label: $label, score: ${(confidence * 100).toStringAsPrecision(3)}, x: ${location.left} y: ${location.top} width: ${location.width} height: ${location.height})';
  }

  @override
  int compareTo(Recognition other) {
    if (this.confidence == other.confidence) {
      return 0;
    } else if (this.confidence > other.confidence) {
      return -1;
    } else {
      return 1;
    }
  }
}
