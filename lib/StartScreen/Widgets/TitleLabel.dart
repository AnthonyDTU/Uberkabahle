import 'dart:ui';

import 'package:flutter/material.dart';

class TitleLabel extends StatelessWidget {
  const TitleLabel({Key? key, required this.text}) : super(key: key);

  final String text;

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: const TextStyle(fontSize: 60, color: Colors.blue, fontWeight: FontWeight.w500),
    );
  }
}
