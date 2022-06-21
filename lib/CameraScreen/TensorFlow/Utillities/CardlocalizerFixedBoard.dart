import 'package:flutter/cupertino.dart';
import 'package:uberkabahle/CameraScreen/TensorFlow/Recognition.dart';
import 'package:tuple/tuple.dart';
import 'dart:math';
import 'dart:convert';

//small class used as signifier for size of a card

class Coordinate {
  final double x;
  final double y;
  Coordinate({required this.x, required this.y});
}

class CardLocalizerFixedBoard {
  final int imageWidth;
  final int imageHeight;
  final List<Recognition> detections;
  double cardHeight = 0;
  double cardWidth = 0;
  double wiggleroom = 20; //extends the detection range of each row

  List<Recognition> centeredCoordinates = []; //center coordinate of each detected card
  List<Coordinate> emptySpaces = []; //for use in findLocationsForCardsType1
  List<Recognition?> detectedLocations = []; //with spaces signified as null
  List<Recognition> detectedLocationsWithoutNull = []; //with spaces signified as recognitions with label e

  CardLocalizerFixedBoard({required this.imageHeight, required this.imageWidth, required this.detections}) {
    Size cardSize = _findAverageHeightWidthOfcard();
    cardHeight = cardSize.width;
    cardWidth = cardSize.height;
    centeredCoordinates = _filterRecognitions(detections);
  }

  set spaceList(List<Tuple2<double, double>> emptySpaces) {
    for (Tuple2<double, double> emptySpace in emptySpaces) {
      this.emptySpaces.add(Coordinate(x: emptySpace.item1, y: emptySpace.item2));
    }
  }

  List<Recognition?> get resultAsList {
    return detectedLocations;
  }

  List<Recognition> get resultAsListNoNull {
    return detectedLocationsWithoutNull;
  }

  bool isRecogInList(List<Recognition> list1, Recognition recog) {
    bool isItHere = false;
    for (Recognition recognition in list1) {
      if (recognition.label == recog.label) {
        isItHere = true;
        break;
      }
    }
    return isItHere;
  }

  List<Recognition> _removeListFromList(List<Recognition> list1, List<Recognition> list2) {
    List<Recognition> finalList = [];
    for (Recognition recognition in list1) {
      if (!isRecogInList(list2, recognition)) {
        finalList.add(recognition);
      }
    }
    return finalList;
  }

  String _getLabelFromNullableRecognition(Recognition? recognition) {
    if (recognition != null) {
      return recognition.label;
    } else {
      return "e";
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

  //uses the array with detections of empty spaces
  void findLocationsForCardsType1() {
    List<Recognition> allCards = centeredCoordinates;
    List<Coordinate> emptySlots = emptySpaces;

    List<Recognition> detectedRowCards;
    List<Coordinate> rowEmptySpaces;
    List<Recognition?> rowWithSpaces;
    // print("all cards \n");
    // print(allCards);
    // print("cardHeight \n");
    // print(cardHeight);
    // print("smallest card \n");
    Recognition smallestCard = allCards.reduce((value, element) => value.location.top < element.location.top ? value : element);
    print(smallestCard.location.top);
    for (int i = 0; i < 3; i++) {
      //get the cards for the row
      detectedRowCards = _getRowRecogntions(allCards, i);
      // print("row " + i.toString() + "\n");
      // print(detectedRowCards);
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

  //Doesnt use the list that contains empty spaces
  void findLocationsForCardsType2() {
    List<List<Recognition?>> rowList = [[], [], []];
    List<Recognition> allCards = centeredCoordinates;

    List<Recognition> detectedRowCards;
    Recognition smallestCard = allCards.reduce((value, element) => value.location.top < element.location.top ? value : element);
    // print("whole list");
    // detectedLocationsPrint(allCards);
    // print(smallestCard.location.top);
    for (int i = 0; i < 3; i++) {
      //get the cards for the row
      detectedRowCards = _getRowRecogntions(allCards, i);
      // print("row " + i.toString() + "\n");
      // detectedLocationsPrint(detectedRowCards);
      //remove from overall list
      allCards = _removeListFromList(allCards, detectedRowCards);
      // print("rest of cards after removal");
      // detectedLocationsPrint(allCards);
      //finish card list with its empty spaces represented as null
      rowList[i] = _completeRowType2(detectedRowCards);
      // print("row with spaces " + i.toString() + "\n");
      // detectedLocationsPrint(rowList[i]);
    }

    //if there are leftovers find a row with all e's and add them there
    if (allCards.isNotEmpty && allCards.length <= 4) {
      for (int i = 0; i < 3; i++) {
        if (isRowEmpty(rowList[i])) {
          rowList[i] = _completeRowType2(allCards);
        }
      }
    }

    //add all rows to overall List
    for (int i = 0; i < 3; i++) {
      detectedLocations.addAll(rowList[i]);
    }

    detectedLocationsWithoutNull = _removeNullsFromRecognitionList(detectedLocations); //make an additional list that opholds null safety
    // print("final list");
    // detectedLocationsPrint(detectedLocationsWithoutNull);
  }

  bool isRowEmpty(List<Recognition?> row) {
    bool empty = true;
    for (int i = 0; i < 4; i++) {
      if (row[i] != null) {
        empty = false;
      }
    }
    return empty;
  }

  void detectedLocationsPrint(List<Recognition?> list) {
    for (var recognition in list) {
      print("[ " + _getLabelFromNullableRecognition(recognition) + " ]");
    }
  }

  //takes a list of cards and list of spaces and creates a list of cards and null signifying missing spaces
  //uses the array that contains locations for the empty spaces.
  List<Recognition?> _completeRowType1(List<Recognition> rowCards, List<Coordinate> emptySpacesCards) {
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
      if (emptySpacesCards[0].x < rowCards[0].location.left) {
        finalRow.insert(0, null);
        return finalRow;
      }
      //otherwise look for gaps inbetween
      int index = -1;
      for (int i = 0; i < (rowCards.length - 1); i++) {
        if (rowCards[i].location.left <= emptySpacesCards[0].x && rowCards[i + 1].location.left >= emptySpacesCards[0].x) {
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
      List<Coordinate> gapsLeft = emptySpacesCards;

      //check if there are gaps before the first card, add them to final list and remove the them from possible gapsLeft list
      List<Coordinate> gapsBeforeFirstCard = _amountOfgapsBeforeCard(rowCards[0], gapsLeft);
      if (gapsBeforeFirstCard.isNotEmpty) {
        _addThisManyNullsToList(finalRow, gapsBeforeFirstCard.length);
        gapsLeft = _removeGapListFromGapList(gapsLeft, gapsBeforeFirstCard);
      }

      //check gaps between cards
      List<Coordinate> gapsBetweenCards;
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
      List<Coordinate> gapsLeft = emptySpacesCards;

      //gaps before the only card
      List<Coordinate> gapsBeforeCard = _amountOfgapsBeforeCard(rowCards[0], gapsLeft);
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

  //This version find the spacing of the row without a list of empty locations
  List<Recognition?> _completeRowType2(List<Recognition> rowCards) {
    double oneCardGapDistance = cardWidth * 1;
    double twoCardGapDistance = cardWidth * 2;

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
        if (currentDistanceBetweenCards > oneCardGapDistance) {
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

      if (distaceBetweenCards > twoCardGapDistance) {
        finalRow.add(null);
        finalRow.add(null);
        gapsLeft = 0;
      } else if (distaceBetweenCards > oneCardGapDistance) {
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
          finalRow.add(null);
        } else if (sideMargin > 0) {
          //bigger to the left
          finalRow.insert(0, null);
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
        if (difference.abs() > twoCardGapDistance) {
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
        if (difference.abs() > twoCardGapDistance) {
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

  List<Coordinate> _removeGapListFromGapList(List<Coordinate> gapList1, List<Coordinate> gapList2) {
    List<Coordinate> gapListToReturn = [];
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

  List<Coordinate> _amountOfgapsBetweenTheseTwoCards(Recognition card1, Recognition card2, List<Coordinate> possibleGaps) {
    List<Coordinate> actualGaps = [];

    for (Coordinate gap in possibleGaps) {
      if (card1.location.left <= gap.x && card2.location.left >= gap.x) {
        actualGaps.add(gap);
      }
    }
    return actualGaps;
  }

  List<Coordinate> _amountOfgapsBeforeCard(Recognition card, List<Coordinate> possibleGaps) {
    List<Coordinate> actualGaps = [];

    for (Coordinate gap in possibleGaps) {
      if (card.location.left >= gap.x) {
        actualGaps.add(gap);
      }
    }
    return actualGaps;
  }

  List<Recognition> _getRowRecogntions(List<Recognition> cards, int rowNum) {
    //first row is foundations and within 1 card height
    //third row is 1-4 tableus and within 2 card height
    //first row is 5-7 tableus and deck and within 3 card height

    double screenPart = imageHeight / 3;

    // print("third of screen: " + screenPart.toString());
    // print("card height: " + cardHeight.toString());
    // print("card width: " + cardWidth.toString());

    List<Recognition> cardsToReturn = [];
    for (Recognition card in cards) {
      if ((card.location.top >= (screenPart * rowNum) - wiggleroom) && (card.location.top <= (screenPart * (rowNum + 1)) + wiggleroom)) {
        cardsToReturn.add(card);
      }
    }

    //trim end if too many card get recognized for row;
    if (cardsToReturn.length > 4) {
      cardsToReturn.sort((a, b) => a.location.top.compareTo(b.location.top));
      while (cardsToReturn.length > 4) {
        cardsToReturn.removeLast();
      }
    }
    return cardsToReturn;
  }

  List<Coordinate> _getEmptySlotsForRow(List<Coordinate> emptySlots, int rowNum) {
    List<Coordinate> emptySlotsToReturn = [];
    for (Coordinate emptySlot in emptySlots) {
      if ((emptySlot.y >= (cardHeight * rowNum)) && (emptySlot.y <= (cardHeight * rowNum + 1))) {
        emptySlotsToReturn.add(emptySlot);
      }
    }
    return emptySlotsToReturn;
  }

  Size _findAverageHeightWidthOfcard() {
    List<Size> sizes = [];
    List<Recognition> cardsThatHaveGottenTheirHeightWidth = [];

    //there are no cards
    if (detections.isEmpty) {
      return Size(0, 0);
    }

    //otherwise continue
    int symbolCornerAmount = _amountOfSymbolCorners(detections); //find amount of corners and change strategya accordingly

    for (Recognition detection in detections) {
      if (!_cardIsInList(cardsThatHaveGottenTheirHeightWidth, detection)) {
        sizes.add(_findHeightAndWidthOfCard(detection, symbolCornerAmount));
        cardsThatHaveGottenTheirHeightWidth.add(detection);
      }
    }

    //remove outliers 0
    List<Size> currentSizeList = [];
    for (Size size in sizes) {
      if (size.height != 0 && size.width != 0) {
        currentSizeList.add(size);
      }
    }

    if (symbolCornerAmount == 2) {
      double averageHeight = sizes.fold(0.0, (previousValue, element) => (previousValue as double) + element.height) / sizes.length;
      double averageWidth = sizes.fold(0.0, (previousValue, element) => (previousValue as double) + element.width) / sizes.length;
      sizes = currentSizeList;
      currentSizeList = [];
      //remove outlier that is double the size of the average
      for (Size size in sizes) {
        if (!(size.height > averageHeight * 2) && !(size.width > averageWidth * 2)) {
          currentSizeList.add(size);
        }
      }
      sizes = currentSizeList;
      currentSizeList = [];
      //remove outlier that is half the size of the average
      for (Size size in sizes) {
        if (!(size.height < averageHeight / 2) && !(size.width < averageWidth / 2)) {
          currentSizeList.add(size);
        }
      }
    } else {
      sizes = currentSizeList;
      double smallestHeightInList = _smallestHeight(sizes);
      double smallestWidthInList = _smallestWidth(sizes);
      currentSizeList = [];
      //remove outlier that is double the size of the smallest
      currentSizeList = [];
      for (Size size in sizes) {
        if (!(size.height > smallestHeightInList * 2) && !(size.width > smallestWidthInList * 2)) {
          currentSizeList.add(size);
        }
      }
    }

    sizes = currentSizeList;
    int amountOfItems = sizes.length;
    double height = 0;
    double width = 0;
    for (Size size in sizes) {
      height = height + size.height;
      width = width + size.width;
    }
    height = height / amountOfItems;
    width = width / amountOfItems;

    return Size(width, height);
  }

  //used to find the maximum amount of corners to find out the type of card //Either 2 or 4
  int _amountOfSymbolCorners(List<Recognition> recognitions) {
    Map<String, List<Recognition>> sortedRecognitions = Map<String, List<Recognition>>();

    // Map all the recognitions made, into groups
    for (var recognition in recognitions) {
      if (sortedRecognitions.containsKey(recognition.label)) {
        sortedRecognitions[recognition.label]!.add(recognition);
      } else {
        sortedRecognitions.putIfAbsent(recognition.label, () => []);
        sortedRecognitions[recognition.label]!.add(recognition);
      }
    }

    bool hasFourCorners = false;
    for (var key in sortedRecognitions.keys) {
      if (sortedRecognitions[key]!.length >= 4) {
        hasFourCorners = true;
        break;
      }
    }
    return hasFourCorners ? 4 : 2;
  }

  double _smallestWidth(List<Size> sizes) {
    double smallestWidth = sizes[0].width;
    for (Size size in sizes) {
      if (size.width < smallestWidth) {
        smallestWidth = size.width;
      }
    }
    return smallestWidth;
  }

  double _smallestHeight(List<Size> sizes) {
    double smallestHeight = sizes[0].height;
    for (Size size in sizes) {
      if (size.height < smallestHeight) {
        smallestHeight = size.height;
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

  //works with 2 corners
  Size _findHeightAndWidthOfCard(Recognition recognition, int symbolCornerAmount) {
    double maxHeight = 0;
    double maxWidth = 0;
    List<Recognition> corners = [];

    for (Recognition detection in detections) {
      if (recognition.label == detection.label) {
        corners.add(detection);
      }
    }

    if ((symbolCornerAmount == 2 && corners.length < 2) || (symbolCornerAmount == 4 && corners.length < 3)) {
      return Size(0, 0);
    }

    double currentHeight;
    //find height, biggest difference in y
    for (Recognition corner1 in corners) {
      for (Recognition corner2 in corners) {
        currentHeight = (corner1.location.top - corner2.location.bottom).abs();
        if (currentHeight > maxHeight) {
          maxHeight = currentHeight;
        }
      }
    }

    double currentWidth;
    //find width, biggest difference in x
    for (Recognition corner1 in corners) {
      for (Recognition corner2 in corners) {
        currentWidth = (corner1.location.left - corner2.location.right).abs();
        if (currentWidth > maxWidth) {
          maxWidth = currentWidth;
        }
      }
    }
    return Size(maxWidth, maxHeight);
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

    //remove detections that are too far from other with same label, ie it shouldnt be too far from all other detections and the list to be removed from should be a minimum of 4 corners as otherwise we would remove too many corners

    //make a copy of map we will make changes to
    Map<String, List<Recognition>> copyOfMap = Map<String, List<Recognition>>.from(sortedRecognitions);

    bool distanceExceeded = true;
    for (String key in recognizedLabels) {
      if (sortedRecognitions.length >= 4) {
        for (Recognition recognition1 in sortedRecognitions[key]!) {
          for (Recognition recognition2 in sortedRecognitions[key]!) {
            if (recognition1.location.left > (recognition2.location.left + cardWidth * 2) ||
                recognition1.location.left < (recognition2.location.left - cardWidth * 2) ||
                recognition1.location.top < (recognition2.location.top - cardHeight * 2) ||
                recognition1.location.top > (recognition2.location.top + cardHeight * 2)) {
              distanceExceeded = true;
            } else {
              distanceExceeded = false; //if just 1 is not too far away we are looking at wrong element
              break;
            }
          }
          if (distanceExceeded) {
            //remove from map
            copyOfMap[key]!.remove(recognition1);
            break;
          }
        }
      }
    }
    sortedRecognitions = copyOfMap;

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

        //method 2 of calculating average

        // Create new recognition symbolizing the average
        Recognition newRecognition = Recognition(label: label, confidence: confidence, location: Rect.fromLTWH(averageX, averageY, 0, 0));
        filteredRecognitions.add(newRecognition);
      }
    });
    //determineRelativePosition(filteredRecognitions);
    return filteredRecognitions;
  }

  //detectedLocation list without nulls to placate nullsafety, empty spaces are repressenteed as recognition objects with label e
  List<Recognition> _removeNullsFromRecognitionList(List<Recognition?> detectedLocations) {
    List<Recognition> recognitionListWithoutNull = [];

    for (Recognition? recognition in detectedLocations) {
      if (recognition != null) {
        recognitionListWithoutNull.add(recognition);
      } else {
        recognitionListWithoutNull.add(Recognition(label: "e", confidence: 0, location: Rect.fromLTWH(0, 0, 0, 0)));
      }
    }
    return recognitionListWithoutNull;
  }
}
