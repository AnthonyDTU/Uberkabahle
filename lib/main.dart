import 'StartScreen/StartPage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setEnabledSystemUIOverlays([]);
  //SystemChrome.setEnabledSystemUIMode(SystemUiMode.)
  SystemChrome.setPreferredOrientations([DeviceOrientation.landscapeLeft])
      .then((value) => runApp(MaterialApp(theme: ThemeData.dark(), home: const StartPage())));

  runApp(
    MaterialApp(theme: ThemeData.dark(), home: const StartPage()),
  );
}
