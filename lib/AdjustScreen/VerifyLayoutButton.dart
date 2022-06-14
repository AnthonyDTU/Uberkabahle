import 'package:flutter/material.dart';

import 'package:flutter/material.dart';

class VerifyLayoutButton extends FloatingActionButton {
  final VoidCallback onPressed;

  const VerifyLayoutButton({required this.onPressed}) : super(onPressed: onPressed);

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(onPressed: onPressed, child: const Icon(Icons.monetization_on_sharp));
  }
}
