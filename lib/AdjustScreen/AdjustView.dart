import 'package:flutter/material.dart';
import 'package:uberkabahle/AdjustScreen/CardAdjuster.dart';
import 'package:uberkabahle/AdjustScreen/VerifyLayoutButton.dart';
import 'dart:io';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/CameraScreen/Widgets/ReturnButton.dart';
import 'package:uberkabahle/MoveScreen/MoveView.dart';

class AdjustView extends StatefulWidget {
  final List<Recognition> sortedRecognitions;
  final List<String> options;
  final File imageFile;

  const AdjustView({required this.imageFile, required this.options, required this.sortedRecognitions, Key? key}) : super(key: key);

  @override
  State<AdjustView> createState() => _AdjustViewState(imageFile: imageFile, options: options, sortedRecognitions: sortedRecognitions);
}

class _AdjustViewState extends State<AdjustView> {
  late List<Recognition> sortedRecognitions;
  final List<String> options;
  late List<String> newOptions = [];

  List<String> possibleLocation = ["F1", "F2", "F3", "F4", "T1", "T2", "T3", "T4", "T5", "T6", "T7", "Stack"];

  final File imageFile;

  _AdjustViewState({required this.imageFile, required this.options, required this.sortedRecognitions, Key? key}) {
    newOptions.addAll(options);
    newOptions.add("e");
  }

  void verifyLayoutButtonPressed() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => MoveView(
          sortedRecognitions: sortedRecognitions,
          imageFile: imageFile,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Stack(children: <Widget>[
          Container(
            alignment: Alignment.center,
            child: Image.file(imageFile),
          ),
          Container(
            alignment: Alignment.center,
            child: getAdjustmentWidgets(),
          ),
          Container(
            alignment: Alignment.topLeft,
            margin: const EdgeInsets.all(20),
            child: ReturnButton(onPressed: () => {Navigator.of(context).pop()}),
          ),
          Container(
            alignment: Alignment.centerRight,
            margin: const EdgeInsets.all(20),
            child: VerifyLayoutButton(onPressed: verifyLayoutButtonPressed),
          ),
        ]),
      ),
    );
  }

  Stack getAdjustmentWidgets() {
    List<CardAdjuster> adjustmentWidgets = [];
    int index = 0;

    for (Recognition recognition in sortedRecognitions) {
      if (recognition.label != 'e') {
        adjustmentWidgets.add(CardAdjuster(options: newOptions, location: possibleLocation[index], recognition: recognition));
      }
      index++;
    }

    return Stack(children: adjustmentWidgets);
  }
}
