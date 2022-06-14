import 'package:flutter/material.dart';

class ScoreLabel extends Text {
  final int score;
  const ScoreLabel({required this.score}) : super("Score:\n$score");

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Text(
      "Score\n$score",
      textAlign: TextAlign.center,
      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
    );
  }
}
