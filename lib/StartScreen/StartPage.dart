import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/AlgoritmController.dart';
import 'package:uberkabahle/StartScreen/Widgets/StartButton.dart';
import 'Widgets/TitleLabel.dart';
import '../CameraScreen/CameraView.dart';

class StartPage extends StatefulWidget {
  const StartPage({
    Key? key,
  }) : super(key: key);

  @override
  State<StartPage> createState() => _StartPageState();
}

class _StartPageState extends State<StartPage> {
  void _startPressed() async {
    AlgorithmController.isFirstMove = true;

    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const CameraView()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Stack(
          alignment: Alignment.center,
          children: <Widget>[
            Positioned(child: Image.asset("assets/StartScreenGraphics.png")),
            Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const TitleLabel(text: 'Uber Kabahle'),
                SizedBox(height: 10),
                StartButton(onPressed: _startPressed),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
