import 'dart:io';
import 'package:flutter/material.dart';
import 'TensorFlow/Recognition.dart';
import 'TensorFlow/UIElements/BoundingBox.dart';
import 'Widgets/ReturnButton.dart';

class RecognizedImageView extends StatelessWidget {
  final File imageFile;
  final List<Recognition> recognitions;

  const RecognizedImageView({Key? key, required this.imageFile, required this.recognitions}) : super(key: key);

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
            child: boundingBoxes(recognitions),
          ),
          Container(
            alignment: Alignment.topLeft,
            margin: const EdgeInsets.all(20),
            child: ReturnButton(onPressed: () => {Navigator.of(context).pop()}),
          ),
        ]),
      ),
    );
  }

  Widget boundingBoxes(List<Recognition> recognitions) {
    return Stack(
      children: recognitions
          .map((recognition) => BoundingBox(
                recognition: recognition,
              ))
          .toList(),
    );
  }
}
