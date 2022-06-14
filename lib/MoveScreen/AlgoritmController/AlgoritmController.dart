import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:uberkabahle/AppSettings.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';
import 'package:http/http.dart' as http;

class AlgorithmController {
  static const backendAlgorithm = MethodChannel(AppSettings.methodChannelName);
  Future<List<SuggestedMove>> determinNextMove(List<Recognition?> sortedRecognitions) async {
    // Call API Functions in correct order, and build a suggested move here.
    //
    List<SuggestedMove> suggestedMoves = [];

    String cardConfigurationMessage = "";
    for (int index = 0; index < sortedRecognitions.length; index++) {
      if (sortedRecognitions == null) {
        cardConfigurationMessage += "null,";
      } else {
        cardConfigurationMessage += "${sortedRecognitions[index]!.label},";
      }
    }

    if (sortedRecognitions.length > 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0]!, 0, sortedRecognitions[0]!, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0]!, 0, sortedRecognitions[1]!, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1]!, 0, sortedRecognitions[0]!, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1]!, 0, sortedRecognitions[1]!, 1, false, false));
    } else if (sortedRecognitions.length == 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0]!, 0, sortedRecognitions[0]!, 1, false, false));
    } else {
      suggestedMoves.add(SuggestedMove(
          Recognition(label: "5C", confidence: 0, location: Rect.zero), 0, Recognition(label: "5C", confidence: 0, location: Rect.zero), 1, false, false));
    }
    return suggestedMoves;
  }

  Future<List<SuggestedMove>> fetchMoves() async {
    final response = await http.get(Uri.parse("http://someurl.com"));

    if (response.statusCode == 200) {
      return [];
    } else {
      throw Exception("Failed to fetch move from server");
    }
  }

  void setRecognizedCards(String cardConfigurationMessage) {
    backendAlgorithm.invokeMethod("set cards", {"cardConfiguration": cardConfigurationMessage, "param2": 1});
  }

  void initializeTableConfiguration(String cardConfigurationMessage) {
    backendAlgorithm.invokeMethod("initialize", {"data": cardConfigurationMessage});
  }
}
