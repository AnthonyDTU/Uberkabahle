import 'dart:isolate';
import 'package:tflite_flutter/tflite_flutter.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'Classifier.dart';
import 'package:image/image.dart' as imageLib;

class RecognitionIsolateController {
  late Isolate _isolate;
  ReceivePort _receivePortFromIsolate = ReceivePort();
  late SendPort _sendPortToIsolate;

  SendPort get sendPortToIsolate => _sendPortToIsolate;

  Future<void> start() async {
    _isolate = await Isolate.spawn<SendPort>(recognize, _receivePortFromIsolate.sendPort);
    _sendPortToIsolate = await _receivePortFromIsolate.first;
  }
}

void recognize(SendPort sendPortToMain) async {
  ReceivePort reveivePortFromMain = ReceivePort();
  sendPortToMain.send(reveivePortFromMain.sendPort);

  await for (final RecognitionIsolateModel data in reveivePortFromMain) {
    Classifier classifier = Classifier.fromInterpreter(
      interpreter: Interpreter.fromAddress(data.interpreterAddress),
      labels: data.labels,
    );

    List<Recognition> recognitions = classifier.runImageRecognition(data.image);

    data.sendPortToMain.send(recognitions);
  }
}

class RecognitionIsolateModel {
  int interpreterAddress;
  imageLib.Image image;
  List<String> labels;
  late SendPort sendPortToMain;

  RecognitionIsolateModel(this.image, this.interpreterAddress, this.labels);
}
