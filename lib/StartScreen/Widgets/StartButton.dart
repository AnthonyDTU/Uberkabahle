import 'package:flutter/material.dart';

class StartButton extends StatelessWidget {
  final VoidCallback onPressed;

  const StartButton({required this.onPressed, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: onPressed,
      child: SizedBox(
        width: 150,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Text("New Game"),
            SizedBox(width: 30),
            Icon(Icons.arrow_circle_right_outlined),
          ],
        ),
      ),
    );
  }
}
