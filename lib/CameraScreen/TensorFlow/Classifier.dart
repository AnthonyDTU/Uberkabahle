import 'dart:math';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:tflite_flutter/tflite_flutter.dart';
import 'package:tflite_flutter_helper/tflite_flutter_helper.dart';
import 'package:image/image.dart' as imageLib;
import 'Utillities/NonMaxSuppression.dart';
import 'Recognition.dart';
import 'package:uberkabahle/AppSettings.dart';

/// This class [Classifier] is responsible for running the imagerecognition model
/// It is heavily influenced, but very modified, by this guide:
/// https://medium.com/@am15hg/real-time-object-detection-using-new-tensorflow-lite-flutter-support-ea41263e801d
/// as well as the related example project, from the author of the plugin, on object detection
/// using Tensorflow lite, found on Github:
/// https://github.com/TexMexMax/object_detection_flutter
///
class Classifier {
  // Constants

  // Model fields
  late Interpreter _interpreter;
  bool interpreterInitialized = false;

  late List<String> _labels;
  bool labelsLoaded = false;

  // Image preproccessing object
  late ImageProcessor _imageProcessor;
  bool _imageProcessorInitialized = false;

  // Output data fields
  /// Shapes of output tensors
  late List<List<int>> _outputShapes = [];

  /// Types of output tensors
  late List<TfLiteType> _outputTypes = [];

  // TensorBuffers for output tensors
  late TensorBuffer _outputLocations;
  late List<List<List<double>>> _outputClassScores;
  // Output map
  late Map<int, Object> _output;

  /// Gets the interpreter instance
  Interpreter get interpreter => _interpreter;

  /// Gets the loaded labels
  List<String> get labels => _labels;

  /// Gets the image processor
  ImageProcessor get imageProcessor => _imageProcessor;

  Classifier() {
    loadModel();
    loadLabels();
    interpreterInitialized = true;
  }

  Classifier.fromInterpreter({required Interpreter interpreter, required List<String> labels}) {
    _interpreter = interpreter;
    _labels = labels;
    interpreterInitialized = true;
    allocateTensors();
  }

  /// Initializes and [Interpreter] with the avaliable model
  void loadModel() async {
    try {
      if (AppSettings.modelToUse == AvaliableModels.m416) {
        _interpreter = await Interpreter.fromAsset(
          AppSettings.modelFile_416,
          options: InterpreterOptions()..threads = 16,
        );
      } else if (AppSettings.modelToUse == AvaliableModels.m512) {
        _interpreter = await Interpreter.fromAsset(
          AppSettings.modelFile_512,
          options: InterpreterOptions()..threads = 16,
        );
      }

      allocateTensors();
    } catch (e) {
      print("Error while creating interpreter: $e");
    }
  }

  /// Loads the labels assosiated with the model
  void loadLabels() async {
    try {
      // Load the labels associated with the model
      _labels = await FileUtil.loadLabels("assets/${AppSettings.labelFile}");

      // Tell the rest of the system, that the labels has been loaded.
      labelsLoaded = true;
    } catch (e) {
      print("Error while loading labels: $e");
    }
  }

  /// Initializes the [ImageProcessor]
  /// This is used on the tensor image, to make it ready for the TFLite framework,
  /// in the function [processImageForRecognition].
  void initializeImageProcessor(int padSize) {
    try {
      if (AppSettings.modelToUse == AvaliableModels.m416) {
        _imageProcessor = ImageProcessorBuilder()
            .add(ResizeWithCropOrPadOp(padSize, padSize))
            .add(ResizeOp(416, 416, ResizeMethod.NEAREST_NEIGHBOUR))
            .add(NormalizeOp(1.0, 250.0)) // REALLY IMPORTANT!
            .build();
      } else if (AppSettings.modelToUse == AvaliableModels.m512) {
        _imageProcessor = ImageProcessorBuilder()
            .add(ResizeWithCropOrPadOp(padSize, padSize))
            .add(ResizeOp(512, 512, ResizeMethod.NEAREST_NEIGHBOUR))
            .add(NormalizeOp(1.0, 250.0)) // REALLY IMPORTANT!
            .build();
      }

      _imageProcessorInitialized = true;
    } catch (e) {
      print("Error initializing the image preprocessor: $e");
    }
  }

  /// Reads the [_outputShapes] and [_outputTypes] from the loaded model
  void allocateTensors() {
    // Get the output tensors from the loaded model.
    var outputTensors = _interpreter.getOutputTensors();

    // Get the output types and shapes from the output Tensors.
    outputTensors.forEach((tensor) {
      _outputShapes.add(tensor.shape);
      _outputTypes.add(tensor.type);
    });

    // TensorBuffers for output tensors
    _outputLocations = TensorBufferFloat(_outputShapes[0]);
    _outputClassScores = List.generate(
        _outputShapes[1][0], (_) => List.generate(_outputShapes[1][1], (_) => List.filled(_outputShapes[1][2], 0.0), growable: false),
        growable: false);

    // Output map
    _output = {
      0: _outputLocations.buffer,
      1: _outputClassScores,
    };
  }

  /// Processes an image, to make it ready for recognition.
  /// Converts an [imageLib.Image] to a [TensorImage].
  /// It also process the [TensorImage] with the image pre processor,
  /// which has been configured in [initializeImageProcessor], with the
  /// [ResizeWithCropOrPadOp], [ResizeOp] and [NormalizeOp] options
  TensorImage processImageForRecognition(imageLib.Image image) {
    try {
      TensorImage tensorImage = TensorImage(TfLiteType.float32);
      tensorImage.loadImage(image);
      if (!_imageProcessorInitialized) {
        initializeImageProcessor(min(image.width, image.height));
      }
      return _imageProcessor.process(tensorImage);
    } catch (e) {
      print("Image preprocessing failed: $e");
      return TensorImage(TfLiteType.float32);
    }
  }

  /// Run image recognition on [imageLib.Image] input.
  List<Recognition> runImageRecognition(imageLib.Image image) {
    // Process the image
    TensorImage inputImage = processImageForRecognition(image);

    // Run the detection
    _interpreter.runForMultipleInputs([inputImage.buffer], _output);

    // Map and filter the reults
    List<Recognition> recognitions = createRecognitionsFromOutput(Size(image.width.toDouble(), image.height.toDouble()));
    recognitions = _runNMS(recognitions);

    // Debug
    //_printRecognitions(recognitions);

    return recognitions;
  }

  List<Recognition> createRecognitionsFromOutput(Size inputImageSize) {
    // Using bounding box utils for easy conversion of tensorbuffer to List<Rect>
    List<Rect> locations = BoundingBoxUtils.convert(
      tensor: _outputLocations,
      boundingBoxAxis: 2,
      boundingBoxType: BoundingBoxType.UPPER_LEFT,
      coordinateType: CoordinateType.PIXEL,
      height: AppSettings.processedImageSize,
      width: AppSettings.processedImageSize,
    );

    List<Recognition> recognitions = [];
    var gridWidth = _outputShapes[0][1];

    for (int i = 0; i < gridWidth; i++) {
      // Since we are given a list of scores for each class for
      // each detected Object, we are interested in finding the class
      // with the highest output score
      var maxClassScore = 0.00;
      var labelIndex = -1;

      for (int c = 0; c < _labels.length; c++) {
        // output[0][i][c] is the confidence score of c class
        if (_outputClassScores[0][i][c] > maxClassScore) {
          labelIndex = c;
          maxClassScore = _outputClassScores[0][i][c];
        }
      }

      // Get the label (card number) assosiated with the maximum score
      var label;
      if (labelIndex != -1 && labelIndex < 52) {
        label = _labels.elementAt(labelIndex);
      } else {
        label = null;
      }

      // Makes sure the confidence is above the
      // minimum threshold score for each object.
      if (maxClassScore > AppSettings.confidenceThreshold) {
        // inverse of rect
        // [locations] corresponds to the image size 512x512
        // inverseTransformRect transforms it our [inputImage]
        Rect rectAti = Rect.fromLTRB(max(0, locations[i].left), max(0, locations[i].top), min(AppSettings.processedImageSize.toDouble(), locations[i].right),
            min(AppSettings.processedImageSize.toDouble(), locations[i].bottom));

        Rect transformedRect = _imageProcessor.inverseTransformRect(rectAti, inputImageSize.height.toInt(), inputImageSize.width.toInt());

        recognitions.add(
          Recognition(label: label, confidence: maxClassScore, location: transformedRect),
        );
      }
    }

    return recognitions;
  }

  /// Takes a lits of [Recognition] and performs Non Max Suppression on its items
  /// Returns the filtered list
  List<Recognition> _runNMS(List<Recognition> recognitions) {
    NonMaxSuppression nonMaxSuppression = NonMaxSuppression();
    return nonMaxSuppression.PerformNonMaxSuppression(recognitions, _labels);
  }

  /// Prints the recognitions
  void _printRecognitions(List<Recognition> recognitions) {
    // Debug information about the recognized cards
    print("Recognitions lenght: ${recognitions.length}");
    recognitions.forEach((recognition) {
      print(recognition.toString() + "\n");
    });
  }
}
