import 'dart:ui';
import 'package:uberkabahle/AppSettings.dart';

import '../Recognition.dart';
import 'package:collection/collection.dart';

class NonMaxSuppression {
  List<Recognition> PerformNonMaxSuppression(List<Recognition> recognitions, List<String> labels) {
    List<Recognition> nmsList = <Recognition>[];

    for (int k = 0; k < labels.length; k++) {
      // 1.find max confidence per class
      PriorityQueue<Recognition> pq = new HeapPriorityQueue<Recognition>();
      for (int i = 0; i < recognitions.length; ++i) {
        if (recognitions[i].label == labels[k]) {
          // Changed from comparing #th class to class to string to string
          pq.add(recognitions[i]);
        }
      }

      // 2.do non maximum suppression
      while (pq.length > 0) {
        // insert detection with max confidence
        List<Recognition> detections = pq.toList(); //In Java: pq.toArray(a)
        Recognition max = detections[0];
        nmsList.add(max);
        pq.clear();
        for (int j = 1; j < detections.length; j++) {
          Recognition detection = detections[j];
          Rect b = detection.location;
          if (boxIou(max.location, b) < AppSettings.NMSOverlayThreshold) {
            pq.add(detection);
          }
        }
      }
    }

    return nmsList;
  }

  double boxIou(Rect a, Rect b) {
    return boxIntersection(a, b) / boxUnion(a, b);
  }

  double boxIntersection(Rect a, Rect b) {
    double w = overlap((a.left + a.right) / 2, a.right - a.left, (b.left + b.right) / 2, b.right - b.left);
    double h = overlap((a.top + a.bottom) / 2, a.bottom - a.top, (b.top + b.bottom) / 2, b.bottom - b.top);
    if ((w < 0) || (h < 0)) {
      return 0;
    }
    double area = (w * h);
    return area;
  }

  double boxUnion(Rect a, Rect b) {
    double i = boxIntersection(a, b);
    double u = ((((a.right - a.left) * (a.bottom - a.top)) + ((b.right - b.left) * (b.bottom - b.top))) - i);
    return u;
  }

  double overlap(double x1, double w1, double x2, double w2) {
    double l1 = (x1 - (w1 / 2));
    double l2 = (x2 - (w2 / 2));
    double left = ((l1 > l2) ? l1 : l2);
    double r1 = (x1 + (w1 / 2));
    double r2 = (x2 + (w2 / 2));
    double right = ((r1 < r2) ? r1 : r2);
    return right - left;
  }
}
