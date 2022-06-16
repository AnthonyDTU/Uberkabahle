import 'dart:io';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/AlgoritmController.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';
import 'package:uberkabahle/MoveScreen/MoveIndicater.dart';
import '../CameraScreen/Widgets/ReturnButton.dart';

class MoveView extends StatefulWidget {
  final File imageFile;
  final List<Recognition> sortedRecognitions;
  const MoveView({required this.imageFile, required this.sortedRecognitions, Key? key}) : super(key: key);

  @override
  State<MoveView> createState() => _MoveViewState(imageFile: imageFile, sortedRecognitions: sortedRecognitions);
}

class _MoveViewState extends State<MoveView> {
  final File imageFile;
  final List<Recognition> sortedRecognitions;
  late SuggestedMove suggestedMove;
  late List<SuggestedMove> suggestedMoves = [];
  bool movesDetermined = false;
  int moveIndex = 0;

  _MoveViewState({required this.imageFile, required this.sortedRecognitions});

  void navigateToCameraView() {
    Navigator.pop(context);
    Navigator.pop(context);
  }

  @override
  void initState() {
    super.initState();
    runAlgorithm();
  }

  void goToNextMove() {
    if (moveIndex < suggestedMoves.length - 1) {
      setState(() {
        moveIndex++;
      });
    } else {
      navigateToCameraView();
    }
  }

  void goToPreviousMove() {
    if (moveIndex > 0) {
      setState(() {
        moveIndex--;
      });
    }
  }

  void runAlgorithm() async {
    AlgorithmController algorithmController = AlgorithmController();
    suggestedMoves = await algorithmController.determinNextMove(sortedRecognitions);

    setState(() {
      movesDetermined = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Stack(
          children: <Widget>[
            Container(
              alignment: Alignment.center,
              child: Image.file(imageFile),
            ),
            (movesDetermined)
                ? Stack(
                    children: [
                      MoveIndicator(suggestedMove: suggestedMoves[moveIndex], nextMoveHandler: goToNextMove),
                      Container(
                        alignment: Alignment.bottomLeft,
                        margin: const EdgeInsets.all(20),
                        child: IconButton(
                          icon: const Icon(Icons.redo_rounded),
                          onPressed: goToPreviousMove,
                        ),
                      ),
                      Container(
                        alignment: Alignment.bottomCenter,
                        margin: const EdgeInsets.all(20),
                        child: Text(
                          "Move ${moveIndex + 1} of ${suggestedMoves.length}",
                          style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ],
                  )
                : BackdropFilter(
                    filter: ImageFilter.blur(sigmaX: 3, sigmaY: 3),
                    child: const Center(child: CircularProgressIndicator()),
                  ),
            Container(
              alignment: Alignment.topLeft,
              margin: const EdgeInsets.all(20),
              child: ReturnButton(
                  onPressed: () => {
                        Navigator.of(context).pop(),
                        Navigator.of(context).pop(),
                      }),
            ),
          ],
        ),
      ),
    );
  }
}
