import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';

class CardAdjuster extends StatefulWidget {
  final List<String> options;
  final String location;
  final Recognition recognition;
  const CardAdjuster({required this.options, required this.location, required this.recognition, Key? key}) : super(key: key);

  @override
  State<CardAdjuster> createState() => _CardAdjusterState(options: options, locatioan: location, recognition: recognition);
}

class _CardAdjusterState extends State<CardAdjuster> {
  final List<String> options;
  final String locatioan;
  final Recognition recognition;

  _CardAdjusterState({required this.options, required this.locatioan, required this.recognition});

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: recognition.renderLocation.left - 30,
      top: recognition.renderLocation.top - 30,
      child: Container(
        decoration: BoxDecoration(
          backgroundBlendMode: BlendMode.darken,
          color: Colors.grey.withOpacity(0.80),
          borderRadius: const BorderRadius.all(
            Radius.circular(10),
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Stack(
              children: [
                Text(
                  locatioan,
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    //color: Color.fromARGB(255, 13, 95, 163),
                    foreground: Paint()
                      ..style = PaintingStyle.stroke
                      ..strokeWidth = 1
                      ..color = Colors.white,
                  ),
                ),
                Text(
                  locatioan,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: Color.fromARGB(255, 13, 95, 163),
                  ),
                ),
              ],
            ),
            DropdownButton(
              value: recognition.label,
              items: options.map(buildMenuItem).toList(),
              onChanged: (value) => setState(() => recognition.label = value as String),
            ),
          ],
        ),
      ),
    );
  }

  DropdownMenuItem<String> buildMenuItem(String text) => DropdownMenuItem(
        value: text,
        child: Stack(
          children: [
            Text(
              text,
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                //color: Color.fromARGB(255, 13, 95, 163),
                foreground: Paint()
                  ..style = PaintingStyle.stroke
                  ..strokeWidth = 1
                  ..color = Colors.white,
              ),
            ),
            Text(
              text,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Color.fromARGB(255, 13, 95, 163),
              ),
            ),
          ],
        ),
      );
}
