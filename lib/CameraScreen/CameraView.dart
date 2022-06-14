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
import 'package:uberkabahle/StartScreen/StartPage.dart';
import '../MoveScreen/MoveIndicater.dart';
import 'Widgets/MovesLabel.dart';
import 'Widgets/ScoreLabel.dart';
import 'Widgets/TimeLabel.dart';
import 'Widgets/ReturnButton.dart';
import 'Widgets/HintButton.dart';
import 'TensorFlow/Classifier.dart';
import 'TensorFlow/Recognition.dart';
import 'RecognizedImageView.dart';
import 'TensorImageViewer.dart';
import 'package:image/image.dart' as imageLib;

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

          // Display input image before classification:
          // ******************************************************

          // TensorImage tensorImage = TensorImage(TfLiteType.float32);
          // tensorImage = _classifier.processImageForRecognition(ImageConverter.convertFileToImage(imageFile));
          // Uint8List jpgTensorsImage = imageLib.encodeJpg(tensorImage.image) as Uint8List;

          // Navigator.of(context).push(
          //   MaterialPageRoute(
          //     builder: (context) => TensorImageViewer(
          //       imagebytes: jpgTensorsImage as Uint8List,
          //     ),
          //   ),
          // );

          RecognitionIsolateModel recognitionIsolateModel =
              RecognitionIsolateModel(ImageConverter.convertFileToImage(imageFile), _classifier.interpreter.address, _classifier.labels);

          List<Recognition> recognitions = await runRecognitionIsolateFunction(recognitionIsolateModel);

          setState(() {
            _isRecognizing = false;
          });

          // Display detected image with bounding boxes:
          // ******************************************************
          // Navigator.of(context).push(
          //   MaterialPageRoute(
          //     builder: (context) => RecognizedImageView(
          //       imageFile: imageFile,
          //       recognitions: recognitions,
          //     ),
          //   ),
          // );

          // Display detected image with bounding boxes:
          // ******************************************************
          Localizer localizer = Localizer();
          CardLocalizerFixedBoard cardLocalizer =
              CardLocalizerFixedBoard(AppSettings.inputImageSize.width.toInt(), AppSettings.inputImageSize.height.toInt(), recognitions);
          cardLocalizer.findLocationsForCardsType2();
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (context) => AdjustView(
                imageFile: imageFile,
                options: _classifier.labels,
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

  /// Navigates back to the [StartPage], when the back arrow is pressed
  void backButtonPressed() {
    Navigator.of(context).pop();
  }

  /// Dummy function for handling the testbutton press
  void incrementButtonPressed() {
    setState(() {
      numberOfMoves++;
    });
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
                      child: ReturnButton(onPressed: backButtonPressed),
                    ),
                    Container(
                      alignment: Alignment.bottomLeft,
                      margin: const EdgeInsets.all(20),
                      child: const TimeLabel(),
                    ),
                    Container(
                      alignment: Alignment.topRight,
                      margin: const EdgeInsets.all(20),
                      child: MovesLabel(
                        moves: numberOfMoves,
                      ),
                    ),
                    Container(
                      alignment: Alignment.bottomRight,
                      margin: const EdgeInsets.all(20),
                      child: ScoreLabel(
                        score: score,
                      ),
                    ),
                    Container(
                      alignment: Alignment.centerLeft,
                      margin: const EdgeInsets.all(20),
                      child: IconButton(
                        icon: const Icon(Icons.add),
                        onPressed: incrementButtonPressed,
                      ),
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
