import 'package:flutter/cupertino.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:tuple/tuple.dart';
import 'dart:math';
import 'dart:convert';

//simplified location
class Card {
  double x;
  double y;
  String label;
  Card(this.x, this.y, this.label);
}

class CardLocalizerFixedBoard {
  late int imageWidth;
  late int imageHeight;
  late List<Recognition> detections;
  double cardHeight = 1920; //TODO
  double cardWidth = 1080; //TODO
  double wiggleroom = 10; //TODO

  List<Recognition> centeredCoordinates = []; //center coordinate of each detected card
  List<Tuple2<double, double>> emptySpaces = []; //for use in findLocationsForCardsType1
  List<Recognition?> detectedLocations = []; //with spaces signified as null
  List<Recognition> detectedLocationsWithoutNull = []; //with spaces signified as recognitions with label E

  CardLocalizerFixedBoard({required this.imageHeight, required this.imageWidth, required this.detections}) {
    Tuple2<double, double> heightwidth = _findAverageHeightWidthOfcard();
    cardHeight = heightwidth.item1;
    cardWidth = heightwidth.item2;
    centeredCoordinates = _filterRecognitions(detections);
  }

  set spaceList(List<Tuple2<double, double>> emptySpaces) {
    this.emptySpaces = List<Tuple2<double, double>>.from(emptySpaces);
  }

  List<Recognition?> get resultAsList {
    return detectedLocations;
  }

  List<Recognition> get resultAsListNoNull {
    return detectedLocationsWithoutNull;
  }

  List<Recognition> _removeListFromList(List<Recognition> list1, List<Recognition> list2) {
    List<Recognition> finalList = [];
    for (Recognition recognition in list1) {
      if (!list2.contains(recognition)) {
        finalList.add(recognition);
      }
    }
    return finalList;
  }

  Recognition getRecognitionWithHighestConfidence(List<Recognition> detections) {
    Recognition highestConfidenceRecognition = detections[0];
    for (Recognition recognition in detections) {
      if (recognition.confidence > highestConfidenceRecognition.confidence) {
        highestConfidenceRecognition = recognition;
      }
    }
    return highestConfidenceRecognition;
  }

  String _getLabelFromNullableRecognition(Recognition? recognition) {
    if (recognition != null) {
      return recognition.label;
    } else {
      return "E";
    }
  }

  String createJsonStringFromResult() {
    Map<String, dynamic> mapToJson = {
      "deck": _getLabelFromNullableRecognition(detectedLocations[11]),
      "foundation1": _getLabelFromNullableRecognition(detectedLocations[0]),
      "foundation2": _getLabelFromNullableRecognition(detectedLocations[1]),
      "foundation3": _getLabelFromNullableRecognition(detectedLocations[2]),
      "foundation4": _getLabelFromNullableRecognition(detectedLocations[3]),
      "Tableu1": _getLabelFromNullableRecognition(detectedLocations[4]),
      "Tableu2": _getLabelFromNullableRecognition(detectedLocations[5]),
      "Tableu3": _getLabelFromNullableRecognition(detectedLocations[6]),
      "Tableu4": _getLabelFromNullableRecognition(detectedLocations[7]),
      "Tableu5": _getLabelFromNullableRecognition(detectedLocations[8]),
      "Tableu6": _getLabelFromNullableRecognition(detectedLocations[9]),
      "Tableu7": _getLabelFromNullableRecognition(detectedLocations[10]),
    };
    String json = jsonEncode(mapToJson);
    return json;
  }

  //uses the array that detects empty spaces
  void findLocationsForCardsType1() {
    List<Recognition> allCards = centeredCoordinates;
    List<Tuple2<double, double>> emptySlots = emptySpaces;

    List<Recognition> detectedRowCards;
    List<Tuple2<double, double>> rowEmptySpaces;
    List<Recognition?> rowWithSpaces;
    print("all cards \n");
    print(allCards);
    print("cardHeight \n");
    print(cardHeight);
    print("smallest card \n");
    Recognition smallestCard = allCards.reduce((value, element) => value.location.top < element.location.top ? value : element);
    print(smallestCard.location.top);
    for (int i = 0; i < 3; i++) {
      //get the cards for the row
      detectedRowCards = _getRowRecogntions(allCards, i);
      print("row " + i.toString() + "\n");
      print(detectedRowCards);
      //get empty spaces for the row
      rowEmptySpaces = _getEmptySlotsForRow(emptySlots, i);
      //remove from overall list
      allCards = _removeListFromList(allCards, detectedRowCards);
      emptySlots = _removeGapListFromGapList(emptySlots, rowEmptySpaces);
      //finish card list with its empty spaces represented as null
      rowWithSpaces = _completeRowType1(detectedRowCards, rowEmptySpaces);
      //add all to complete list
      for (Recognition? card in rowWithSpaces) {
        detectedLocations.add(card);
      }
    }
    detectedLocationsWithoutNull = _removeNullsFromRecognitionList(detectedLocations); //make an additional list that opholds null safety
  }

  //takes a list of cards and list of spaces and creates a list of cards and null signifying missing spaces
  //uses the array that contains locations for the empty spaces.
  List<Recognition?> _completeRowType1(List<Recognition> rowCards, List<Tuple2<double, double>> emptySpacesCards) {
    if (rowCards.isEmpty) {
      //no cards in row
      return [null, null, null, null];
    }

    //pre sort cards
    List<Recognition?> finalRow = [];
    rowCards.sort((a, b) => a.location.left.compareTo(b.location.left));

    if (rowCards.length == 4 && emptySpacesCards.isEmpty) {
      finalRow = rowCards;
      return finalRow;
    } else if (rowCards.length == 3 && emptySpacesCards.length == 1) {
      finalRow = rowCards;

      //if the empty space is before the first element insert at start
      if (emptySpacesCards[0].item1 < rowCards[0].location.left) {
        finalRow.insert(0, null);
        return finalRow;
      }
      //otherwise look for gaps inbetween
      int index = -1;
      for (int i = 0; i < (rowCards.length - 1); i++) {
        if (rowCards[i].location.left <= emptySpacesCards[0].item1 && rowCards[i + 1].location.left >= emptySpacesCards[0].item1) {
          index = i;
          break;
        }
      }
      //if no match then set at the end otherwise set after the index
      if (index == -1) {
        finalRow.add(null);
        return finalRow;
      } else {
        finalRow.insert(index + 1, null);
      }
      return finalRow;
    } else if (rowCards.length == 2 && emptySpacesCards.length == 2) {
      List<Tuple2<double, double>> gapsLeft = emptySpacesCards;

      //check if there are gaps before the first card, add them to final list and remove the them from possible gapsLeft list
      List<Tuple2<double, double>> gapsBeforeFirstCard = _amountOfgapsBeforeCard(rowCards[0], gapsLeft);
      if (gapsBeforeFirstCard.isNotEmpty) {
        _addThisManyNullsToList(finalRow, gapsBeforeFirstCard.length);
        gapsLeft = _removeGapListFromGapList(gapsLeft, gapsBeforeFirstCard);
      }

      //check gaps between cards
      List<Tuple2<double, double>> gapsBetweenCards;
      for (int i = 0; i < rowCards.length - 1; i++) {
        finalRow.add(rowCards[i]);
        gapsBetweenCards = _amountOfgapsBetweenTheseTwoCards(rowCards[i], rowCards[i + 1], gapsLeft);
        if (gapsBetweenCards.isNotEmpty) {
          _addThisManyNullsToList(finalRow, gapsBeforeFirstCard.length);
          gapsLeft = _removeGapListFromGapList(gapsLeft, gapsBetweenCards);
        }
      }

      //gaps after cards
      finalRow.add(rowCards[rowCards.length - 1]);
      if (gapsLeft.isNotEmpty) {
        _addThisManyNullsToList(finalRow, gapsLeft.length);
        gapsLeft = [];
      }
      return finalRow;
    } else if (rowCards.length == 1 && emptySpacesCards.length == 3) {
      List<Tuple2<double, double>> gapsLeft = emptySpacesCards;

      //gaps before the only card
      List<Tuple2<double, double>> gapsBeforeCard = _amountOfgapsBeforeCard(rowCards[0], gapsLeft);
      if (gapsBeforeCard.isNotEmpty) {
        _addThisManyNullsToList(finalRow, gapsBeforeCard.length);
        gapsLeft = _removeGapListFromGapList(gapsLeft, gapsBeforeCard);
      }

      //add card
      finalRow.add(rowCards[0]);

      //gaps after the only card, add the rest
      _addThisManyNullsToList(finalRow, gapsLeft.length);
      gapsLeft = [];

      return finalRow;
    } else {
      //fail scenario
      return [null, null, null, null];
    }
  }

  //version that uses recognitions instead of cards
  List<Recognition?> _completeRowType2(List<Recognition> rowCards) {
    if (rowCards.isEmpty) {
      //no cards in row
      return [null, null, null, null];
    }

    //pre sort cards
    List<Recognition?> finalRow = [];
    rowCards.sort((a, b) => a.location.left.compareTo(b.location.left));

    if (rowCards.length == 4) {
      return rowCards;
    } else if (rowCards.length == 3) {
      finalRow = List<Recognition?>.from(rowCards);

      //first check if there a big gap between the cards
      //if not the empty space is at end or start choose the bigger one
      int index = -1;
      double currentDistanceBetweenCards;
      for (int i = 0; i < (rowCards.length - 1); i++) {
        currentDistanceBetweenCards = (rowCards[i].location.left - rowCards[i + 1].location.left).abs();
        if (currentDistanceBetweenCards > cardWidth * 1.5) {
          //there is a gap
          index = i;
          break;
        }
      }

      if (index != -1) {
        //found index in middle
        finalRow.insert(index + 1, null);
      } else {
        //either left or right
        if (rowCards[0].location.left > (imageWidth - rowCards[rowCards.length - 1].location.left)) {
          //left
          finalRow.insert(0, null);
        } else {
          //right
          finalRow.add(null);
        }
      }

      return finalRow;
    } else if (rowCards.length == 2) {
      finalRow = [];
      int gapsLeft = 4 - rowCards.length;

      //first check if there a big gap between the cards
      //for every gap found add a null, if you find a big gap add 2 nulls

      int index = -1;
      double currentDistanceBetweenCards;

      finalRow.add(rowCards[0]); //add card 1

      //add gaps
      /**/
      double distaceBetweenCards = (rowCards[0].location.left - rowCards[1].location.left).abs();

      if (distaceBetweenCards > (cardWidth * 2.5)) {
        finalRow.add(null);
        finalRow.add(null);
        gapsLeft = 0;
      } else if (distaceBetweenCards > (cardWidth * 1.5)) {
        finalRow.add(null);
        gapsLeft = 1;
      }
      /**/
      finalRow.add(rowCards[1]); //add card 2

      //if there is 1 gap left one of the sides that is bigger has that gap
      //if there is 2 gaps left one side either has both gaps or each has a gap each
      if (gapsLeft == 1) {
        //either left or right
        if (rowCards[0].location.left > (imageWidth - rowCards[rowCards.length - 1].location.left)) {
          //left
          finalRow.insert(0, null);
        } else {
          //right
          finalRow.add(null);
        }
        gapsLeft = 0;
      } else if (gapsLeft == 2) {
        //if both distances to the side are equal to 1 card distance's margin of error,
        //then either side has a gap
        double sideMargin = (rowCards[0].location.left - (imageWidth - rowCards[1].location.left));
        if (sideMargin.abs() < cardWidth) {
          finalRow.insert(0, null);
          finalRow.add(null);
        } else if (sideMargin < 0) {
          //bigger to the right
          finalRow.add(null);
        } else if (sideMargin > 0) {
          //bigger to the left
          finalRow.insert(0, null);
        }
        gapsLeft = 0;
      }

      return finalRow;
    } else if (rowCards.length == 1) {
      finalRow = [];
      int gapsLeft = 4 - rowCards.length;

      double distanceToLeft = rowCards[0].location.left;
      double distanceToRight = imageWidth - rowCards[0].location.left;
      double difference = distanceToLeft - distanceToRight;

      finalRow.add(rowCards[0]);
      //if negative, then right distance is bigger, meaning 2 or more spaces there
      if (difference < 0) {
        if (difference.abs() > cardWidth * 2.5) {
          //all 3 gaps are to the right
          finalRow.add(null);
          finalRow.add(null);
          finalRow.add(null);
        } else {
          //two gaps are to the right and 1 to the left
          finalRow.add(null);
          finalRow.add(null);
          finalRow.insert(0, null);
        }
        gapsLeft = 0;
      } else {
        //if postive, then left distance is bigger, meaning 2 or more spaces there
        if (difference.abs() > cardWidth * 2.5) {
          //all 3 gaps are to the left
          finalRow.insert(0, null);
          finalRow.insert(0, null);
          finalRow.insert(0, null);
        } else {
          //two gaps are to the left and 1 to the right
          finalRow.insert(0, null);
          finalRow.insert(0, null);
          finalRow.add(null);
        }
        gapsLeft = 0;
      }
      return finalRow;
    } else {
      return [null, null, null, null]; //failstate
    }
  }

  //Doesnt use the array that contains empty spaces
  //
  void findLocationsForCardsType2() {
    List<Recognition> allCards = centeredCoordinates;

    List<Recognition> detectedRowCards;
    List<Recognition?> rowWithSpaces;

    Recognition smallestCard = allCards.reduce((value, element) => value.location.top < element.location.top ? value : element);
    print("whole list");
    print(allCards);
    print(smallestCard.location.top);
    for (int i = 0; i < 3; i++) {
      //get the cards for the row
      detectedRowCards = _getRowRecogntions(allCards, i);
      print("row " + i.toString() + "\n");
      print(detectedRowCards);
      //remove from overall list
      allCards = _removeListFromList(allCards, detectedRowCards);
      print("cards after removal");
      print(allCards);
      //finish card list with its empty spaces represented as null
      rowWithSpaces = _completeRowType2(detectedRowCards);
      print("row with spaces " + i.toString() + "\n");
      print(rowWithSpaces);
      //add all to complete list
      for (Recognition? card in rowWithSpaces) {
        detectedLocations.add(card);
      }
    }
    detectedLocationsWithoutNull = _removeNullsFromRecognitionList(detectedLocations); //make an additional list that opholds null safety
  }
////////////////////////////////////////////////////////////////////////////////////////

  List<Tuple2<double, double>> _removeGapListFromGapList(List<Tuple2<double, double>> gapList1, List<Tuple2<double, double>> gapList2) {
    List<Tuple2<double, double>> gapListToReturn = [];
    for (int i = 0; i < gapList1.length; i++) {
      if (!(gapList2.contains(gapList1[i]))) {
        gapListToReturn.add(gapList1[i]);
      }
    }
    return gapListToReturn;
  }

  void _addThisManyNullsToList(List<Recognition?> cards, int num) {
    for (int i = 0; i < num; i++) {
      cards.add(null);
    }
  }

  List<Tuple2<double, double>> _amountOfgapsBetweenTheseTwoCards(Recognition card1, Recognition card2, List<Tuple2<double, double>> possibleGaps) {
    List<Tuple2<double, double>> actualGaps = [];

    for (Tuple2<double, double> gap in possibleGaps) {
      if (card1.location.left <= gap.item1 && card2.location.left >= gap.item1) {
        actualGaps.add(gap);
      }
    }
    return actualGaps;
  }

  List<Tuple2<double, double>> amountOfgapsAfterCard(Card card, List<Tuple2<double, double>> possibleGaps) {
    List<Tuple2<double, double>> actualGaps = [];

    for (Tuple2<double, double> gap in possibleGaps) {
      if (card.x <= gap.item1) {
        actualGaps.add(gap);
      }
    }
    return actualGaps;
  }

  List<Tuple2<double, double>> _amountOfgapsBeforeCard(Recognition card, List<Tuple2<double, double>> possibleGaps) {
    List<Tuple2<double, double>> actualGaps = [];

    for (Tuple2<double, double> gap in possibleGaps) {
      if (card.location.left >= gap.item1) {
        actualGaps.add(gap);
      }
    }
    return actualGaps;
  }

  List<Recognition> _getRowRecogntions(List<Recognition> cards, int rowNum) {
    //first row is foundations and within 1 card height
    //third row is 1-4 tableus and within 2 card height
    //first row is 5-7 tableus and deck and within 3 card height

    double wiggleroom = 20;

    List<Recognition> cardsToReturn = [];
    for (Recognition card in centeredCoordinates) {
      if ((card.location.top >= (cardHeight * rowNum) - wiggleroom) && (card.location.top <= (cardHeight * (rowNum + 1)) + wiggleroom)) {
        cardsToReturn.add(card);
      }
    }
    return cardsToReturn;
  }

  List<Tuple2<double, double>> _getEmptySlotsForRow(List<Tuple2<double, double>> emptySlots, int rowNum) {
    List<Tuple2<double, double>> emptySlotsToReturn = [];
    for (Tuple2<double, double> emptySlot in emptySlots) {
      if ((emptySlot.item2 >= (cardHeight * rowNum)) && (emptySlot.item2 <= (cardHeight * rowNum + 1))) {
        emptySlotsToReturn.add(emptySlot);
      }
    }
    return emptySlotsToReturn;
  }

  List<Recognition> getRecognitionsOfThisType(Recognition recognition) {
    List<Recognition> typeOfCardList = [];

    for (Recognition detection in detections) {
      if (detection.label == recognition.label) {
        typeOfCardList.add(detection);
      }
    }
    return typeOfCardList;
  }
/*//works with 4 corner cards
  Tuple2<double, double> findAverageHeightWidthOfcard() {
    List<Tuple2<double, double>> sizes = [];
    List<Recognition> cardsThatHaveGottenTheirHeightWidth = [];

    for (Recognition detection in detections) {
      if (!CardIsInList(cardsThatHaveGottenTheirHeightWidth, detection)) {
        sizes.add(findHeightAndWidthOfCard(detection));
      }
    }

    //remove outliers 0
    List<Tuple2<double, double>> currentSizeList = [];
    for (Tuple2<double, double> size in sizes) {
      if (size.item1 != 0 && size.item2 != 0) {
        currentSizeList.add(size);
      }
    }
    sizes = currentSizeList;

    //remove outlier that is double the size of the smallest
    currentSizeList = [];
    double smallestHeightInList = smallestHeight(sizes);
    double smallestWidthInList = smallestWidth(sizes);
    for (Tuple2<double, double> size in sizes) {
      if (!(size.item1 > smallestHeightInList * 2) && !(size.item2 > smallestWidthInList * 2)) {
        currentSizeList.add(size);
      }
    }
    sizes = currentSizeList;
    int amountOfItems = sizes.length;
    double height = 0;
    double width = 0;
    for (Tuple2<double, double> size in sizes) {
      height = height + size.item1;
      width = width + size.item2;
    }
    height = height / amountOfItems;
    width = width / amountOfItems;

    return Tuple2<double, double>(height, width);
  }
*/

  Tuple2<double, double> _findAverageHeightWidthOfcard() {
    List<Tuple2<double, double>> sizes = [];
    List<Recognition> cardsThatHaveGottenTheirHeightWidth = [];

    for (Recognition detection in detections) {
      if (!_cardIsInList(cardsThatHaveGottenTheirHeightWidth, detection)) {
        sizes.add(_findHeightAndWidthOfCard(detection));
      }
    }

    //remove outliers 0
    List<Tuple2<double, double>> currentSizeList = [];
    for (Tuple2<double, double> size in sizes) {
      if (size.item1 != 0 && size.item2 != 0) {
        currentSizeList.add(size);
      }
    }

    sizes = currentSizeList;
    //remove outlier that is double the size of the average
    currentSizeList = [];
    double averageHeight = sizes.fold(0.0, (previousValue, element) => (previousValue as double) + element.item1) / sizes.length;
    double averageWidth = sizes.fold(0.0, (previousValue, element) => (previousValue as double) + element.item2) / sizes.length;
    for (Tuple2<double, double> size in sizes) {
      if (!(size.item1 > averageHeight * 2) && !(size.item2 > averageWidth * 2)) {
        currentSizeList.add(size);
      }
    }
    sizes = currentSizeList;
    //remove outlier that is half the size of the average
    currentSizeList = [];
    for (Tuple2<double, double> size in sizes) {
      if (!(size.item1 < averageHeight / 2) && !(size.item2 < averageWidth / 2)) {
        currentSizeList.add(size);
      }
    }

    sizes = currentSizeList;
    int amountOfItems = sizes.length;
    double height = 0;
    double width = 0;
    for (Tuple2<double, double> size in sizes) {
      height = height + size.item1;
      width = width + size.item2;
    }
    height = height / amountOfItems;
    width = width / amountOfItems;

    return Tuple2<double, double>(height, width);
  }

  double smallestWidth(List<Tuple2<double, double>> sizes) {
    double smallestWidth = sizes[0].item2;
    for (Tuple2<double, double> size in sizes) {
      if (size.item2 < smallestWidth) {
        smallestWidth = size.item2;
      }
    }
    return smallestWidth;
  }

  double smallestHeight(List<Tuple2<double, double>> sizes) {
    double smallestHeight = sizes[0].item1;
    for (Tuple2<double, double> size in sizes) {
      if (size.item1 < smallestHeight) {
        smallestHeight = size.item1;
      }
    }
    return smallestHeight;
  }

  bool _cardIsInList(List<Recognition> cards, Recognition card) {
    bool itIsThere = false;
    for (Recognition detection in cards) {
      if (card.label == detection.label) {
        itIsThere = true;
        break;
      }
    }
    return itIsThere;
  }

  // Tuple2<double, double> findHeightAndWidthOfCard(Recognition recognition) {
  //   double height = 0;
  //   double width = 0;
  //   List<Recognition> corners = [];

  //   for (Recognition detection in detections) {
  //     if (recognition.label == detection.label) {
  //       corners.add(detection);
  //     }
  //   }

  //   if (corners.length <= 2) {
  //     return Tuple2<double, double>(0, 0);
  //   }

  //   double currentHeight;
  //   //find height, biggest difference in y
  //   for (Recognition corner1 in corners) {
  //     for (Recognition corner2 in corners) {
  //       currentHeight = (corner1.location.top - corner2.location.bottom).abs();
  //       if (currentHeight > height) {
  //         height = currentHeight;
  //       }
  //     }
  //   }

  //   double currentWidth;
  //   //find width, biggest difference in x
  //   for (Recognition corner1 in corners) {
  //     for (Recognition corner2 in corners) {
  //       currentWidth = (corner1.location.left - corner2.location.right).abs();
  //       if (currentWidth > width) {
  //         width = currentWidth;
  //       }
  //     }
  //   }
  //   return Tuple2<double, double>(height, width);
  // }

  //works with 2 corners
  Tuple2<double, double> _findHeightAndWidthOfCard(Recognition recognition) {
    double height = 0;
    double width = 0;
    List<Recognition> corners = [];

    for (Recognition detection in detections) {
      if (recognition.label == detection.label) {
        corners.add(detection);
      }
    }

    if (corners.length < 2) {
      return Tuple2<double, double>(0, 0);
    }

    double currentHeight;
    //find height, biggest difference in y
    for (Recognition corner1 in corners) {
      for (Recognition corner2 in corners) {
        currentHeight = (corner1.location.top - corner2.location.bottom).abs();
        if (currentHeight > height) {
          height = currentHeight;
        }
      }
    }

    double currentWidth;
    //find width, biggest difference in x
    for (Recognition corner1 in corners) {
      for (Recognition corner2 in corners) {
        currentWidth = (corner1.location.left - corner2.location.right).abs();
        if (currentWidth > width) {
          width = currentWidth;
        }
      }
    }
    return Tuple2<double, double>(height, width);
  }

  List<Card> recognitionListToCardList(List<Recognition> recognitionList) {
    List<Card> cardList = [];
    for (int i = 0; i < recognitionList.length; i++) {
      cardList.add(Card(recognitionList[i].location.left, recognitionList[i].location.top, recognitionList[i].label));
    }
    return cardList;
  }

  /// Creates one [Recognition] for each recognized label, based on an average location of all labels of that kind.
  ///
  List<Recognition> _filterRecognitions(List<Recognition> recognitions) {
    Map<String, List<Recognition>> sortedRecognitions = Map<String, List<Recognition>>();

    // Map all the recognitions made, into groups
    recognitions.forEach((recognition) {
      if (sortedRecognitions.containsKey(recognition.label)) {
        sortedRecognitions[recognition.label]!.add(recognition);
      } else {
        sortedRecognitions.putIfAbsent(recognition.label, () => []);
        sortedRecognitions[recognition.label]!.add(recognition);
      }
    });

    List<Recognition> filteredRecognitions = [];

    List<String> recognizedLabels = sortedRecognitions.keys.toList();
    recognizedLabels.forEach((label) {
      // Check that atleast two recognitions of this label has been made
      if (sortedRecognitions[label]!.length >= 2) {
        // Initialize calculation variables
        int count = 0;
        double averageY = 0;
        double averageX = 0;
        double confidence = 0;

        // Sum up all the recognitongs in the group
        sortedRecognitions[label]!.forEach((recognition) {
          averageX += recognition.location.left;
          averageY += recognition.location.top;
          confidence += recognition.confidence;
          count++;
        });

        // Calculate average of recognition group
        averageX /= count;
        averageY /= count;
        confidence /= count;

        // Create new recognition symbolizing the average
        Recognition newRecognition = Recognition(label: label, confidence: confidence, location: Rect.fromLTWH(averageX, averageY, 0, 0));
        filteredRecognitions.add(newRecognition);
      }
    });
    //determineRelativePosition(filteredRecognitions);
    return filteredRecognitions;
  }

  //detectedLocation list without nulls

  List<Recognition> _removeNullsFromRecognitionList(List<Recognition?> detectedLocations) {
    List<Recognition> recognitionListWithoutNull = [];

    for (Recognition? recognition in detectedLocations) {
      if (recognition != null) {
        recognitionListWithoutNull.add(recognition);
      } else {
        recognitionListWithoutNull.add(Recognition(label: "E", confidence: 0, location: Rect.fromLTWH(0, 0, 0, 0)));
      }
    }
    return recognitionListWithoutNull;
  }
}
