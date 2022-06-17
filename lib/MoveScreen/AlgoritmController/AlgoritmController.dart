import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:uberkabahle/AppSettings.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';

class AlgorithmController {
  static const backendAlgorithm = MethodChannel(AppSettings.methodChannelName);

  Future<List<SuggestedMove>> determineFirstMove(List<Recognition> sortedRecognitions) async {
    String cardConfigurationMessage = "";
    for (int index = 4; index < sortedRecognitions.length - 1; index++) {
      String convertedLabel = sortedRecognitions[index].label;
      convertedLabel = convertedLabel.replaceAll("K", "13");
      convertedLabel = convertedLabel.replaceAll("Q", "12");
      convertedLabel = convertedLabel.replaceAll("J", "11");
      convertedLabel = convertedLabel.replaceAll("A", "1");
      convertedLabel = convertedLabel.replaceAll("D", "R");
      convertedLabel = convertedLabel.replaceAll("C", "K");
      String newLabel = convertedLabel.substring(convertedLabel.length - 1) + convertedLabel.substring(0, convertedLabel.length - 1);

      cardConfigurationMessage += (index != sortedRecognitions.length - 2) ? "${newLabel}," : "${newLabel}";
    }

    bool status = await initializeTableConfiguration(cardConfigurationMessage);
    String suggestedMove = await getNextMove();
    return buildDebugMoves(sortedRecognitions);
  }

  Future<List<SuggestedMove>> determineNextMove(List<Recognition> sortedRecognitions) async {
    String cardConfigurationMessage = "";
    for (int index = 4; index < sortedRecognitions.length; index++) {
      cardConfigurationMessage += "${sortedRecognitions[index].label},";
    }
    bool status = await setRecognizedCards(cardConfigurationMessage);
    String suggestedMove = await getNextMove();
    return buildDebugMoves(sortedRecognitions);
  }

  Future<String> getNextMove() async {
    String response = await backendAlgorithm.invokeMethod("getNextMove") as String;
    return response;
  }

  Future<bool> setRecognizedCards(String cardConfigurationMessage) async {
    bool status = await backendAlgorithm.invokeMethod("updateTable", {"data": cardConfigurationMessage});
    return status;
  }

  Future<bool> initializeTableConfiguration(String cardConfigurationMessage) async {
    bool status = await backendAlgorithm.invokeMethod("initTable", {"data": cardConfigurationMessage});
    return status;
  }

  List<SuggestedMove> buildDebugMoves(List<Recognition> sortedRecognitions) {
    List<SuggestedMove> suggestedMoves = [];
    if (sortedRecognitions.length > 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0], 0, sortedRecognitions[0], 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0], 0, sortedRecognitions[1], 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1], 0, sortedRecognitions[0], 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1], 0, sortedRecognitions[1], 1, false, false));
    } else if (sortedRecognitions.length == 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0], 0, sortedRecognitions[0], 1, false, false));
    } else {
      suggestedMoves.add(SuggestedMove(
          Recognition(label: "5C", confidence: 0, location: Rect.zero), 0, Recognition(label: "5C", confidence: 0, location: Rect.zero), 1, false, false));
    }
    return suggestedMoves;
  }
}
