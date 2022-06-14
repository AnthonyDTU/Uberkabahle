import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';

class Localizer {
  /// Creates one [Recognition] for each recognized label, based on an average location of all labels of that kind.
  ///
  List<Recognition> filterRecognitions(List<Recognition> recognitions) {
    Map<String, List<Recognition>> sortedRecognitions = Map<String, List<Recognition>>();

    // Map all the recognitions made, into groups
    recognitions.forEach((recognition) {
      if (sortedRecognitions.containsKey(recognition.label)) {
        sortedRecognitions[recognition.label]!.add(recognition);
      } else {
        sortedRecognitions.putIfAbsent(recognition.label, () => []);
        sortedRecognitions[recognition.label]!.add(recognition);
      }
    });

    List<Recognition> filteredRecognitions = [];

    List<String> recognizedLabels = sortedRecognitions.keys.toList();
    recognizedLabels.forEach((label) {
      // Check that atleast two recognitions of this label has been made
      if (sortedRecognitions[label]!.length >= 2) {
        // Initialize calculation variables
        int count = 0;
        double averageY = 0;
        double averageX = 0;
        double confidence = 0;

        // Sum up all the recognitongs in the group
        sortedRecognitions[label]!.forEach((recognition) {
          averageX += recognition.location.left;
          averageY += recognition.location.top;
          confidence += recognition.confidence;
          count++;
        });

        // Calculate average of recognition group
        averageX /= count;
        averageY /= count;
        confidence /= count;

        // Create new recognition symbolizing the average
        Recognition newRecognition = Recognition(label: label, confidence: confidence, location: Rect.fromLTWH(averageX, averageY, 0, 0));
        filteredRecognitions.add(newRecognition);
      }
    });
    filteredRecognitions = determineRelativePosition(filteredRecognitions);
    return filteredRecognitions;
  }

  List<Recognition> determineRelativePosition(List<Recognition> filteredRecognitions) {
    // Init Variables
    List<Recognition> locallyPlacedRecognitions = [];
    int xCount = 0;
    int yCount = 0;

    filteredRecognitions.forEach((recognition) {
      bool itemAdded = false;

      if (locallyPlacedRecognitions.isEmpty) {
        locallyPlacedRecognitions.add(recognition);
      } else {
        for (int index = 0; index < locallyPlacedRecognitions.length; index++) {
          if (locallyPlacedRecognitions[index].location.left > recognition.location.left) {
            locallyPlacedRecognitions.insert(index, recognition);
            itemAdded = true;
            break;
          }
        }

        if (!itemAdded) {
          locallyPlacedRecognitions.add(recognition);
        }
      }
    });

    return locallyPlacedRecognitions;
  }
}
