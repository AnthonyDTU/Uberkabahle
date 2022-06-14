import 'dart:io';
import 'dart:typed_data';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:tflite_flutter_helper/tflite_flutter_helper.dart';
import 'TensorFlow/Recognition.dart';
import 'TensorFlow/UIElements/BoundingBox.dart';
import 'Widgets/ReturnButton.dart';
import 'package:image/image.dart' as imageLib;
import 'dart:ui' as UI;

class TensorImageViewer extends StatelessWidget {
  //final imageLib.Image image;
  final Uint8List imagebytes;

  const TensorImageViewer({Key? key, required this.imagebytes}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Stack(children: <Widget>[
          Container(
            alignment: Alignment.center,
            child: Image.memory(
              imagebytes,
              width: 416,
              height: 416,
            ),
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
}
