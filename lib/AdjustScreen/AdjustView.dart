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
  final File imageFile;

  _AdjustViewState({required this.imageFile, required this.options, required this.sortedRecognitions, Key? key});

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
    return Stack(
      children: sortedRecognitions
          .map((recognition) => CardAdjuster(
                options: options,
                recognition: recognition,
              ))
          .toList(),
    );
  }
}

// class AdjustView extends StatelessWidget {
//   final List<Recognition> recognitions;
//   final List<String> options;
//   final File imageFile;

//   const AdjustView({required this.imageFile, required this.options, required this.recognitions, Key? key}) : super(key: key);

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: Center(
//         child: Stack(children: <Widget>[
//           Container(
//             alignment: Alignment.center,
//             child: Image.file(imageFile),
//           ),
//           Container(
//             alignment: Alignment.center,
//             child: getAdjustmentWidgets(),
//           ),
//           Container(
//             alignment: Alignment.topLeft,
//             margin: const EdgeInsets.all(20),
//             child: ReturnButton(onPressed: () => {Navigator.of(context).pop()}),
//           ),
//         ]),
//       ),
//     );
//   }

//   Stack getAdjustmentWidgets() {
//     return Stack(
//       children: recognitions
//           .map((recognition) => CardAdjuster(
//                 options: options,
//                 recognition: recognition,
//               ))
//           .toList(),
//     );
//   }
// }
