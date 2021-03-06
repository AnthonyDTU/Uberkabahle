import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:uberkabahle/MoveScreen/AlgoritmController/SuggestedMove.dart';

class MoveIndicator extends StatelessWidget {
  final SuggestedMove suggestedMove;
  final VoidCallback nextMoveHandler;
  const MoveIndicator({required this.suggestedMove, required this.nextMoveHandler, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BackdropFilter(
      filter: ImageFilter.blur(sigmaX: 3, sigmaY: 3),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: (suggestedMove.flipStack)
                ? [
                    // *****************************************
                    // Flip Stack
                    // *****************************************
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        SizedBox(
                          width: 150,
                          child: Column(
                            children: [
                              Image.asset(
                                "assets/cards/drawFromPile.png",
                              ),
                              const Text(
                                "Flip Stack",
                                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                              ),
                            ],
                          ),
                        ),
                        GestureDetector(
                          onTap: nextMoveHandler,
                          child: const Icon(
                            Icons.arrow_right_alt_rounded,
                            size: 80,
                          ),
                        ),
                      ],
                    ),
                  ]
                :
                // *****************************************
                // Is Solved
                // *****************************************
                (suggestedMove.solved)
                    ? [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            GestureDetector(
                              onTap: nextMoveHandler,
                              child: SizedBox(
                                width: 150,
                                child: Column(
                                  children: [
                                    Image.asset(
                                      "assets/Throphy.png",
                                    ),
                                    const Text(
                                      "Solved!",
                                      style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ],
                        ),
                      ]
                    : [
                        // *****************************************
                        // Regular move
                        // *****************************************
                        SizedBox(
                          width: 150,
                          child: Column(
                            children: [
                              Image.asset(
                                "assets/cards/${suggestedMove.moveCard}.png",
                              ),
                              const SizedBox(
                                height: 20,
                              ),
                              (suggestedMove.fromColumn <= 6)
                                  ? Text(
                                      "Tableau ${suggestedMove.fromColumn + 1}",
                                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                                    )
                                  : Text(
                                      (suggestedMove.fromColumn == 11) ? "Stack" : "Foundation ${suggestedMove.fromColumn - 6}",
                                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                                    ),
                            ],
                          ),
                        ),
                        const SizedBox(width: 30),
                        GestureDetector(
                          onTap: nextMoveHandler,
                          child: const Icon(
                            Icons.arrow_right_alt_rounded,
                            size: 80,
                          ),
                        ),
                        const SizedBox(width: 50),
                        SizedBox(
                          width: 150,
                          child: Column(
                            children: [
                              Image.asset(
                                "assets/cards/${suggestedMove.toCard}.png",
                              ),
                              const SizedBox(
                                height: 20,
                              ),
                              (suggestedMove.toColumn <= 6)
                                  ? Text(
                                      "Tableau ${suggestedMove.toColumn + 1}",
                                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                                    )
                                  : Text(
                                      "Foundation ${suggestedMove.toColumn - 6}",
                                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                                    ),
                            ],
                          ),
                        ),
                      ],
          ),
        ],
      ),
    );
  }
}
