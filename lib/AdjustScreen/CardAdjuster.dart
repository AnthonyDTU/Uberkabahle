import 'package:flutter/material.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';

class CardAdjuster extends StatefulWidget {
  final List<String> options;
  final Recognition recognition;
  const CardAdjuster({required this.options, required this.recognition, Key? key}) : super(key: key);

  @override
  State<CardAdjuster> createState() => _CardAdjusterState(options: options, recognition: recognition);
}

class _CardAdjusterState extends State<CardAdjuster> {
  late List<String> options;
  late Recognition recognition;

  _CardAdjusterState({required this.options, required this.recognition});

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: recognition.renderLocation.left,
      top: recognition.renderLocation.top,
      // width: 100,
      // height: 40,
      // Postition Here
      // ...
      // ...
      child: DropdownButton(
        value: recognition.label,
        items: options.map(buildMenuItem).toList(),
        onChanged: (value) => setState(() => recognition.label = value as String),
      ),
    );
  }

  DropdownMenuItem<String> buildMenuItem(String text) => DropdownMenuItem(
      value: text,
      child: Text(
        text,
        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
      ));
}
