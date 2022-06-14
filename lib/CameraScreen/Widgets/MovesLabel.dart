import 'package:flutter/material.dart';

class MovesLabel extends Text {
  final int moves;
  const MovesLabel({required this.moves}) : super("Moves:\n$moves");

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Text(
      "Moves:\n$moves",
      textAlign: TextAlign.center,
      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
    );
  }
}
