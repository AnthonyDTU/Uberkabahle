import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:uberkabahle/AppSettings.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';

class AlgorithmController {
  static const backendAlgorithm = MethodChannel(AppSettings.methodChannelName);

  Future<List<SuggestedMove>> determinNextMove(List<Recognition> sortedRecognitions) async {
    String cardConfigurationMessage = "";
    for (int index = 4; index < sortedRecognitions.length; index++) {
      cardConfigurationMessage += "${sortedRecognitions[index].label},";
    }
    List<int> suggestedMoveInt = [];
    initializeTableConfiguration(cardConfigurationMessage).then((value) => {suggestedMoveInt = getNextMove()});

    return buildDebugMoves(sortedRecognitions);
  }

  List<int> getNextMove() {
    return backendAlgorithm.invokeMethod("getNextMove") as List<int>;
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
