import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';

class ReturnButton extends IconButton {
  @override
  final VoidCallback onPressed;

  const ReturnButton({required this.onPressed})
      : super(onPressed: onPressed, icon: const Icon(Icons.backspace));

  @override
  Widget build(BuildContext context) {
    // Android:
    if (defaultTargetPlatform == TargetPlatform.android) {
      return IconButton(
        onPressed: onPressed,
        icon: const Icon(Icons.arrow_back),
        color: Colors.grey,
      );
    }
    // iOS:
    else {
      return IconButton(
        onPressed: onPressed,
        icon: const Icon(Icons.arrow_back_ios),
        color: Colors.grey,
      );
    }
  }
}
