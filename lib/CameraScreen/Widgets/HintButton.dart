import 'package:flutter/material.dart';

class HintButton extends FloatingActionButton {
  final VoidCallback onPressed;

  const HintButton({required this.onPressed}) : super(onPressed: onPressed);

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      child: const Icon(Icons.lightbulb),
    );
  }
}
