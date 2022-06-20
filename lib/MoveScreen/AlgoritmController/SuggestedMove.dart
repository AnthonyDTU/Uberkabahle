import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';

class SuggestedMove {
  String moveCard;
  String toCard;
  int fromColumn;
  int toColumn;
  bool flipStack;
  bool solved;

  SuggestedMove(this.moveCard, this.fromColumn, this.toCard, this.toColumn, this.flipStack, this.solved);
}
