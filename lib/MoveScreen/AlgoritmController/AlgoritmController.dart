import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:uberkabahle/AppSettings.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';

class AlgorithmController {
  static const backendAlgorithm = MethodChannel(AppSettings.methodChannelName);
  static bool isFirstMove = true;

  Future<List<SuggestedMove>> determineFirstMove(List<Recognition> sortedRecognitions) async {
    String cardConfigurationMessage = "";
    for (int index = 4; index < sortedRecognitions.length - 1; index++) {
      String label = translateLabelToDanish(sortedRecognitions[index].label);
      cardConfigurationMessage += (index != sortedRecognitions.length - 2) ? "$label," : "$label";
    }

    bool status = await initializeTableConfiguration(cardConfigurationMessage);

    if (status == true) {
      String suggestedMoves = await getNextMove();
      isFirstMove = false;
      return buildMoves(suggestedMoves);
    } else {
      return [];
    }
    return buildDebugMoves(sortedRecognitions);
  }

  Future<List<SuggestedMove>> determineNextMove(List<Recognition> sortedRecognitions) async {
    String cardConfigurationMessage = "";
    for (int index = 4; index < sortedRecognitions.length; index++) {
      String label = translateLabelToDanish(sortedRecognitions[index].label);
      cardConfigurationMessage += (index != sortedRecognitions.length - 1) ? "$label," : "$label";
    }
    bool status = await setRecognizedCards(cardConfigurationMessage);

    if (status == true) {
      String suggestedMoves = await getNextMove();
      isFirstMove = false;
      return buildMoves(suggestedMoves);
    } else {
      return [];
    }

    return buildDebugMoves(sortedRecognitions);
  }

  String translateLabelToDanish(String label) {
    label = label.replaceAll("K", "13");
    label = label.replaceAll("Q", "12");
    label = label.replaceAll("J", "11");
    label = label.replaceAll("A", "1");
    label = label.replaceAll("D", "R");
    label = label.replaceAll("C", "K");

    return label.substring(label.length - 1) + label.substring(0, label.length - 1);
  }

  String translateLabelToEnglish(String label) {
    label = label.replaceAll("R", "D");
    label = label.replaceAll("K", "C");
    label = label.replaceAll("13", "K");
    label = label.replaceAll("12", "Q");
    label = label.replaceAll("11", "J");
    label = label.replaceAll("1", "A");

    return label.substring(1, label.length) + label.substring(0, 1);
  }

  Future<String> getNextMove() async {
    String response = await backendAlgorithm.invokeMethod("getNextMove") as String;
    print("Just recieved $response from algoritm\n");
    return response;
  }

  Future<bool> setRecognizedCards(String cardConfigurationMessage) async {
    print("Just sent $cardConfigurationMessage to algorithm\n");
    bool status = await backendAlgorithm.invokeMethod("updateTable", {"data": cardConfigurationMessage});
    return status;
  }

  Future<bool> initializeTableConfiguration(String cardConfigurationMessage) async {
    print("Just initialized algortihm with $cardConfigurationMessage\n");
    bool status = await backendAlgorithm.invokeMethod("initTable", {"data": cardConfigurationMessage});
    return status;
  }

  List<SuggestedMove> buildMoves(String moves) {
    List<SuggestedMove> suggestedMoves = [];
    List<String> rawMoves = moves.split(';');
    rawMoves.removeLast();
    for (String move in rawMoves) {
      if (move == "0") {
        suggestedMoves.add(SuggestedMove("", 0, "", 0, true, false));
      } else {
        List<String> moveComponents = move.split(',');
        String moveCard = translateLabelToEnglish(moveComponents[0]);
        String toCard = translateLabelToEnglish(moveComponents[1]);
        int fromColumn = int.parse(moveComponents[2]);
        int toColumn = int.parse(moveComponents[3]);
        bool flipStack = false;
        bool solved = moveComponents[5] == "1";
        suggestedMoves.add(SuggestedMove(moveCard, fromColumn, toCard, toColumn, flipStack, solved));
      }
    }
    return suggestedMoves;
  }

  List<SuggestedMove> buildDebugMoves(List<Recognition> sortedRecognitions) {
    List<SuggestedMove> suggestedMoves = [];
    if (sortedRecognitions.length > 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0].label, 0, sortedRecognitions[0].label, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0].label, 0, sortedRecognitions[1].label, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1].label, 0, sortedRecognitions[0].label, 1, false, false));
      suggestedMoves.add(SuggestedMove(sortedRecognitions[1].label, 0, sortedRecognitions[1].label, 1, false, false));
    } else if (sortedRecognitions.length == 1) {
      suggestedMoves.add(SuggestedMove(sortedRecognitions[0].label, 0, sortedRecognitions[0].label, 1, false, false));
    } else {
      suggestedMoves.add(SuggestedMove("5C", 0, "5C", 1, false, false));
    }
    return suggestedMoves;
  }
}
