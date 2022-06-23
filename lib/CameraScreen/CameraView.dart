import 'dart:async';
import 'dart:io';
import 'dart:isolate';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import 'package:uberkabahle/AdjustScreen/AdjustView.dart';
import 'package:uberkabahle/AppSettings.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/RecognitionIsolateController.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Utillities/CardlocalizerFixedBoard.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Utillities/ImageConverter.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Utillities/Localizer.dart';
import 'package:uberkabahle/CameraScreen/Widgets/TimeLabel.dart';
import 'Widgets/TimeLabel.dart';
import 'Widgets/ReturnButton.dart';
import 'Widgets/HintButton.dart';
import 'TensorFlow/Classifier.dart';
import 'TensorFlow/Recognition.dart';
//import 'package:image/image.dart' as imageLib;

class CameraView extends StatefulWidget {
  const CameraView({
    Key? key,
  }) : super(key: key);

  @override
  State<CameraView> createState() => CameraViewState();
}

class CameraViewState extends State<CameraView> {
  late CameraController _camera;
  bool _cameraInitialized = false;
  bool _isRecognizing = false;

  ResolutionPreset imageResolution = ResolutionPreset.veryHigh;

  int numberOfMoves = 0;
  int score = 0;

  late Classifier _classifier;
  late RecognitionIsolateController _recognitionIsolateController;

  @override
  void initState() {
    super.initState();

    _initializeCamera();
    _initializeTFLiteClassifier();
    _initializeIsolate();
  }

  @override
  void dispose() {
    _camera.dispose();
    super.dispose();
  }

  /// Initializes the camera system on the phone.
  /// It creates the camera using the specified settings, and also sets the
  /// [CameraViewConfigurationData] class accordingly. This class contains
  /// information about the relation between the image and the screen.
  void _initializeCamera() async {
    // Get Cameras
    List<CameraDescription> cameras = await availableCameras();

    // Create Camera Controller, with settings.
    _camera = CameraController(cameras[0], imageResolution, imageFormatGroup: ImageFormatGroup.yuv420, enableAudio: false);

    // Initialize Camera
    _camera.initialize().then((_) async {
      // Configure the Camera data for the localization of the detected objects.
      AppSettings.setImageSize(imageResolution);
      AppSettings.setPreviewSize(MediaQuery.of(context).size);
      AppSettings.calculateRatio();

      setState(() {
        _cameraInitialized = true;
      });
    });
  }

  /// Initializes a new [Classifier]. The Classifier is the class which handles
  /// the actual image recognition on either an image or a file contatining an image
  void _initializeTFLiteClassifier() async {
    _classifier = Classifier();
  }

  /// Initialize a new [Isolate]. An Isolate is a seperate thread, which can run
  /// on a different core. normal async functions are still single threaded in dart.
  void _initializeIsolate() async {
    // Spawn a new isolate
    _recognitionIsolateController = RecognitionIsolateController();
    await _recognitionIsolateController.start();
  }

  /// Executed when the [HintButton] is pressed. This checks if a recognition
  /// process is running, and if not, it takes a picture with the camera (no stream)
  /// It then utilizes the [Classifier] to run image recognition on that picture,
  /// and navigates to the [RecognizedImageView] for the user to see the results
  void hintButtonPressed() async {
    try {
      if (_camera.value.isInitialized) {
        if (!_isRecognizing) {
          setState(() {
            _isRecognizing = true;
          });

          final image = await _camera.takePicture();
          HapticFeedback.heavyImpact();
          File imageFile = File(image.path);

          RecognitionIsolateModel recognitionIsolateModel =
              RecognitionIsolateModel(ImageConverter.convertFileToImage(imageFile), _classifier.interpreter.address, _classifier.labels);

          List<Recognition> recognitions = await runRecognitionIsolateFunction(recognitionIsolateModel);

          setState(() {
            _isRecognizing = false;
          });

          // Display detected image with bounding boxes:
          // ******************************************************
          Localizer localizer = Localizer();
          CardLocalizerFixedBoard cardLocalizer = CardLocalizerFixedBoard(
            imageHeight: AppSettings.inputImageSize.height.toInt(),
            imageWidth: AppSettings.inputImageSize.width.toInt(),
            detections: recognitions,
          );
          cardLocalizer.findLocationsForCardsType2();
          print(cardLocalizer.createJsonStringFromResult());
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (context) => AdjustView(
                imageFile: imageFile,
                options: _classifier.labels,
                //sortedRecognitions: localizer.filterRecognitions(recognitions),
                sortedRecognitions: cardLocalizer.resultAsListNoNull,
              ),
            ),
          );
        }
      }
    } catch (e) {
      print(e);
    }
  }

  /// Shows a confirm dialog, before navigating back to the startscreen.
  /// If the user cancel the app stays in the cameraview.
  Future<void> _showConfirmReturnDialog() async {
    return showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Warning!'),
          content: SingleChildScrollView(
            child: ListBody(
              children: const <Widget>[
                Text('Going back to the start screen resets the game progress,\nand you will have to start over!\n'),
                Text('Are you sure you want to continue?'),
              ],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('Cancel'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
            TextButton(
              child: const Text('Continue'),
              onPressed: () {
                Navigator.of(context).pop();
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  /// When wanting to run image recognition on a new image (when running on image stream)
  /// this function is used to pass data to the [Isolate], where the recognition will run.
  /// It is also here the main process waits (asyncronously) for a response from that
  /// isolate.
  Future<List<Recognition>> runRecognitionIsolateFunction(RecognitionIsolateModel recognitionIsolateModel) async {
    ReceivePort receivePortFromIsolate = ReceivePort();
    recognitionIsolateModel.sendPortToMain = receivePortFromIsolate.sendPort;

    _recognitionIsolateController.sendPortToIsolate.send(recognitionIsolateModel);
    List<Recognition> recognitions = await receivePortFromIsolate.first;
    return recognitions;
  }

  // The build function, whcich renders the UI
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: (_cameraInitialized)
              ? Stack(
                  children: [
                    Container(
                      alignment: Alignment.center,
                      child: AspectRatio(
                        aspectRatio: _camera.value.aspectRatio,
                        child: CameraPreview(_camera),
                      ),
                    ),
                    Positioned(
                      child: Center(
                        child: BackdropFilter(
                          filter: ImageFilter.blur(sigmaX: 3, sigmaY: 3),
                          child: Center(
                            child: Container(
                              width: AppSettings.previewSizeHeight,
                              height: AppSettings.previewSizeHeight,
                              decoration: BoxDecoration(
                                backgroundBlendMode: BlendMode.clear,
                                color: Colors.white.withOpacity(0.5),
                                border: BoxBorder.lerp(
                                    Border.all(color: Color.fromARGB(255, 0, 0, 0), width: 2), Border.all(color: Color.fromARGB(255, 0, 0, 0), width: 1), .1),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Container(
                      alignment: Alignment.centerRight,
                      margin: const EdgeInsets.all(20),
                      child: HintButton(onPressed: hintButtonPressed),
                    ),
                    Container(
                      alignment: Alignment.topLeft,
                      margin: const EdgeInsets.all(20),
                      child: ReturnButton(onPressed: _showConfirmReturnDialog),
                    ),
                    Container(
                      alignment: Alignment.bottomLeft,
                      margin: const EdgeInsets.all(20),
                      child: const TimeLabel(),
                    ),
                    Container(
                      alignment: Alignment.center,
                      child: (_isRecognizing) ? const CircularProgressIndicator() : null,
                    ),
                  ],
                )
              : const CircularProgressIndicator()),
    );
  }
}
