import 'dart:async';
import 'package:flutter/material.dart';

class TimeLabel extends StatefulWidget {
  const TimeLabel({Key? key}) : super(key: key);

  @override
  State<TimeLabel> createState() => _TimeLabelState();
}

class _TimeLabelState extends State<TimeLabel> {
  String hoursStr = "00";
  String minutesStr = "00";
  String secondsStr = "00";

  late Stream<int> timerStream;
  late StreamSubscription<int> timerSubscription;

  @override
  void initState() {
    super.initState();
    _initializeTimer();
  }

  @override
  void dispose() {
    timerSubscription.cancel();
    super.dispose();
  }

  // Initializes the timer stream.
  void _initializeTimer() {
    timerStream = stopWatchStream();
    timerSubscription = timerStream.listen((int newTick) {
      setState(() {
        hoursStr = ((newTick / (60 * 60)) % 60).floor().toString().padLeft(2, '0');
        minutesStr = ((newTick / 60) % 60).floor().toString().padLeft(2, '0');
        secondsStr = (newTick % 60).floor().toString().padLeft(2, '0');
      });
    });
  }

  /// Stream for keeping a clock on the player.
  Stream<int> stopWatchStream() {
    late StreamController<int> streamController;
    late Timer timer;
    Duration timerInterval = Duration(seconds: 1);
    int counter = 0;

    void stopTimer() {
      timer.cancel();
      counter = 0;
      streamController.close();
    }

    void tick(_) {
      counter++;
      streamController.add(counter);
    }

    void startTimer() {
      timer = Timer.periodic(timerInterval, tick);
    }

    streamController = StreamController<int>(
      onListen: startTimer,
      onCancel: stopTimer,
      onResume: startTimer,
      onPause: stopTimer,
    );

    return streamController.stream;
  }

  @override
  Widget build(BuildContext context) {
    return Text(
      "Time:\n$hoursStr : $minutesStr : $secondsStr",
      textAlign: TextAlign.center,
      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
    );
  }
}
