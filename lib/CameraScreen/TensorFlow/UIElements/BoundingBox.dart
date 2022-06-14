import 'package:flutter/material.dart';
import '../Recognition.dart';

class BoundingBox extends StatelessWidget {
  final Recognition recognition;

  const BoundingBox({required this.recognition}) : super();

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: recognition.renderLocation.left,
      top: recognition.renderLocation.top,
      width: recognition.renderLocation.width,
      height: recognition.renderLocation.height,
      child: Container(
        width: recognition.renderLocation.width,
        height: recognition.renderLocation.height,
        decoration: BoxDecoration(border: Border.all(color: Colors.red, width: 3), borderRadius: BorderRadius.all(Radius.circular(2))),
        child: Align(
          alignment: Alignment.topLeft,
          child: FittedBox(
            child: Container(
              color: Colors.red,
              child: Row(
                mainAxisSize: MainAxisSize.max,
                children: <Widget>[
                  Text(recognition.label),
                  Text(" " + recognition.confidence.toStringAsFixed(2)),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
