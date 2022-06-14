import 'dart:io';
import 'dart:math';
import 'dart:typed_data';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:tflite_flutter/tflite_flutter.dart';
import 'package:tflite_flutter_helper/tflite_flutter_helper.dart';
import 'package:image/image.dart' as imageLib;
import 'Utillities/NonMaxSuppression.dart';
import 'Recognition.dart';
import 'package:uberkabahle/AppSettings.dart';

class Classifier {
  // Constants

  static const int IMAGESIZE = 416;

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

  late Map<int, ByteBuffer> _outputBuffers = new Map<int, ByteBuffer>();
  late Map<int, TensorBuffer> _outputTensorBuffers = new Map<int, TensorBuffer>();
  late Map<int, String> _outputTensorNames = new Map<int, String>();

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
    TensorImage inputImage = processImageForRecognition(image);

    // TensorBuffers for output tensors
    TensorBuffer outputLocations = TensorBufferFloat(_outputShapes[0]);
    List<List<List<double>>> outputClassScores = List.generate(
        _outputShapes[1][0], (_) => List.generate(_outputShapes[1][1], (_) => List.filled(_outputShapes[1][2], 0.0), growable: false),
        growable: false);

    // Output map
    Map<int, Object> output = {
      0: outputLocations.buffer,
      1: outputClassScores,
    };

    // Run the detection
    _interpreter.runForMultipleInputs([inputImage.buffer], output);

    // Using bounding box utils for easy conversion of tensorbuffer to List<Rect>
    List<Rect> locations = BoundingBoxUtils.convert(
      tensor: outputLocations,
      boundingBoxAxis: 2,
      boundingBoxType: BoundingBoxType.UPPER_LEFT,
      coordinateType: CoordinateType.PIXEL,
      height: IMAGESIZE,
      width: IMAGESIZE,
    );

    List<Recognition> recognitions = [];
    // 10647?
    var gridWidth = _outputShapes[0][1];

    for (int i = 0; i < gridWidth; i++) {
      // Since we are given a list of scores for each class for
      // each detected Object, we are interested in finding the class
      // with the highest output score
      var maxClassScore = 0.00;
      var labelIndex = -1;

      for (int c = 0; c < _labels.length; c++) {
        // output[0][i][c] is the confidence score of c class
        if (outputClassScores[0][i][c] > maxClassScore) {
          labelIndex = c;
          maxClassScore = outputClassScores[0][i][c];
        }
      }

      // Get the label (card number) assosiated with the maximum score
      var label;
      if (labelIndex != -1) {
        label = _labels.elementAt(labelIndex);
      } else {
        label = null;
      }

      // Makes sure the confidence is above the
      // minimum threshold score for each object.
      if (maxClassScore > AppSettings.confidenceThreshold) {
        // inverse of rect
        // [locations] corresponds to the image size 416 X 416
        // inverseTransformRect transforms it our [inputImage]
        Rect rectAti = Rect.fromLTRB(
            max(0, locations[i].left), max(0, locations[i].top), min(IMAGESIZE.toDouble(), locations[i].right), min(IMAGESIZE.toDouble(), locations[i].bottom));

        Rect transformedRect = _imageProcessor.inverseTransformRect(rectAti, image.height, image.width);

        recognitions.add(
          Recognition(label: label, confidence: maxClassScore, location: transformedRect),
        );
      }
    }

    NonMaxSuppression nonMaxSuppression = NonMaxSuppression();
    List<Recognition> recognitionsNMS = nonMaxSuppression.PerformNonMaxSuppression(recognitions, _labels);

    recognitionsNMS.forEach((recognition) {});

    // Debug information about the recognized cards
    print("Recognitions lenght: ${recognitionsNMS.length}");
    recognitionsNMS.forEach((recognition) {
      print(recognition.toString() + "\n");
    });

    return recognitionsNMS;
  }
}
