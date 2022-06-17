package com.example.uberkabahle.src.test.java.src;

import com.example.uberkabahle.src.main.java.src.*;
import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunSimulation {

    @Test
    void testManyGames() {
        String lastMove = "";
        int amountOfGamesToRun = 1000;
        int maximumHandsPrGame = 500;
        int totalMovesTaken = 0;
        int currentMovesTaken = 0;
        int gamesWon = 0;
        int gamesLost = 0;
        boolean printTable = false;

        for (int k = 0; k < amountOfGamesToRun; k++) {
            Table table = new TableIO();
            Algorithm algorithm = new Algorithm(table);
            Move move = new Mover(table);
            List<Card> cards = new ArrayList<>();
            for (int i = 0 ; i < 4 ;  i++){
                for (int j = 0 ; j < 13 ; j++){
                    Card newCard = new Card();
                    newCard.setValue(j);
                    switch (i)
                    {
                        case 0 :
                            newCard.setColor(0);
                            newCard.setType(0);
                            break;
                        case 1 :
                            newCard.setColor(1);
                            newCard.setType(1);
                            break;
                        case 2 :
                            newCard.setColor(1);
                            newCard.setType(2);
                            break;
                        case 3 :
                            newCard.setColor(0);
                            newCard.setType(3);
                            break;
                    }
                    cards.add(newCard);
                }
            }
            Collections.shuffle(cards);
            String startTable = "";
            for (int i = 0; i < 7; i++) {
                Card card = cards.get(0);
                int type = card.getType();
                switch (type) {
                    case 0 : startTable += "K";
                        break;
                    case 1 : startTable += "H";
                        break;
                    case 2 : startTable += "R";
                        break;
                    case 3 : startTable += "S";
                        break;
                }
                if (card.getValue() != 0) startTable += card.getValue()+1;
                else startTable += card.getValue();
                if (i != 6) startTable += ",";
                cards.remove(0);
            }
            table.initStartTable(startTable);
            Match match;
            currentMovesTaken = 0;
            for (int i = 0 ; i < maximumHandsPrGame ; i++) {

                if(printTable) {
                    table.printTable();
                }
                if(printTable) {
                    System.out.println("**** Round " + (i + 1) + " ****");
                }
                int total = 0;
                for (int j = 0; j < 4; j++) total += table.getFundamentPiles().get(j).size();
                if (total >= 52) {
                    gamesWon++;
                    break;
                }
                currentMovesTaken++;

                for (int j = 0; j < table.getAllPiles().size(); j++) {
                    if (table.getAllPiles().get(j).size() != 0 && !table.getAllPiles().get(j).get(table.getAllPiles().get(j).size() - 1).isFaceUp() && table.getAllPiles().get(j).get(table.getAllPiles().get(j).size() - 1).getValue() != -1) {
                        table.getAllPiles().get(j).get(table.getAllPiles().get(j).size() - 1).setFaceUp(true);
                    }
                }
                if (table.getPlayerDeck_FaceUp().size() != 0 && table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size()-1).getValue() != -1 && !table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size()-1).isFaceUp()) table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size()-1).setFaceUp(true);

                int unknownCards = 0;
                for (int j = 0; j < 7; j++) {
                    for (int l = 0; l < table.getPile(j).size(); l++) {
                        if (!table.getPile(j).get(l).isFaceUp()) unknownCards++;
                    }
                }
                for (int j = 0; j < table.getPlayerDeck_FaceDown().size(); j++) {
                    if (!table.getPlayerDeck_FaceDown().get(j).isFaceUp()) unknownCards++;
                }
                for (int j = 0; j < table.getPlayerDeck_FaceUp().size(); j++) {
                    if (!table.getPlayerDeck_FaceUp().get(j).isFaceUp()) unknownCards++;
                }

//                if (unknownCards != cards.size()) {
//                    System.out.print("");
//                }
//
//                if (cards.size() == 0) {
//                    System.out.printf("");
//                }

                int totalCardsInTablou = table.getAllPiles().get(0).size() + table.getAllPiles().get(1).size() + table.getAllPiles().get(2).size() + table.getAllPiles().get(3).size() + table.getAllPiles().get(4).size() + table.getAllPiles().get(5).size() + table.getAllPiles().get(6).size();
                int totalCardsInFoundation = table.getFundamentPiles().get(0).size() + table.getFundamentPiles().get(1).size() + table.getFundamentPiles().get(2).size() + table.getFundamentPiles().get(3).size() - 4;
                int totalCardsInStock = table.getPlayerDeck_FaceUp().size() + table.getPlayerDeck_FaceDown().size();

                int totalCardsInGame = totalCardsInFoundation + totalCardsInStock + totalCardsInTablou;

                if (totalCardsInGame != 52){
                    //              System.out.printf("");
                }
//                if(table.getFundamentPiles().get(0).get(table.getFundamentPiles().get(0).size() - 1).getBelongToPile() < 7 ||
//                        table.getFundamentPiles().get(1).get(table.getFundamentPiles().get(1).size() - 1).getBelongToPile() < 7 ||
//                        table.getFundamentPiles().get(2).get(table.getFundamentPiles().get(2).size() - 1).getBelongToPile() < 7 ||
//                        table.getFundamentPiles().get(3).get(table.getFundamentPiles().get(3).size() - 1).getBelongToPile() < 7){
//                    System.out.printf("");
//                }

                match = algorithm.checkForAnyMatch();
                if(match.getToPile() == 11){
        //            System.out.println("");
                }
                if (cards.size() == 0){
   //                 System.out.printf("");
                }
                if (!match.isMatch()){
      //              System.out.printf("");
                }
      //          System.out.println("");

                int cardsBefore = cards.size();

                if (match.isComplex() && (match.isNoNextInput() || match.isLastCardInPile())){
                    if(printTable){
                        System.out.println("Complex match, first move from pile " + match.getFromPile() + " at index " + match.getComplexIndex() + " to tablou pile " + match.getToPile());
                        System.out.println("After that, move the card at tablou pile " + match.getFromPile() + " to foundation pile " + match.getComplexFinalFoundationPile());}
                    move.moveCard_OrPile(match);
                }
                else if (match.isComplex() && !match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("Complex match, first move from pile " + match.getFromPile() + " at index " + match.getComplexIndex() + " to tablou pile " + match.getToPile());
                        System.out.println("After that, move the card at tablou pile " + match.getFromPile() + " to foundation pile " + match.getComplexFinalFoundationPile());
                        System.out.println("Last trun over the facedown card in tablou " + match.getFromPile() + " and enter value:");
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                    if(printTable) {
                        table.printTable();
                    }
                }
                //No match - Turn card from player pile - next input
                else if(match.getFromPile() == 11 && !match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()){
                    if(printTable) {
                        System.out.println("Turn over three new cards in the stock pile");
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                }
                //No match - Turn card from player pile - no next input
                else if(match.getFromPile() == 11 && !match.isMatch() && match.isNoNextInput() && !match.isLastCardInPile()) {
                    if (printTable){
                        System.out.println("Turn over three new cards in the stock pile");
                    }
                    move.moveCard_OrPile(match);
                    //System.out.printf("The next card you turn over is known and is: " + table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                }
                //Match from player pile to tablou - next input
                else if(match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                }
                //Match from stock to foundation - next input
                else if(match.getFromPile() == 11 && match.getToPile() >= 7 && match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                }
                //Match from stock to foundation - no next input
                else if(match.getFromPile() == 11 && match.getToPile() > 6 && match.isMatch() && match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from stock to tablou - next input
                else if(match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput()){
                    if (printTable) {
                        System.out.println("Move from stock to tablou pile: " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from stock to tablou - no next input
                else if(match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch()){
                    if(printTable) {
                        System.out.println("Move from stock to tablou pile: " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                    //System.out.println("The next card in stock pile is known: " + table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                }
                //Match from tablou to foundation - no next input
                else if(match.getFromPile() < 7 && match.getToPile() > 6 && match.isMatch() && match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to foundation - next input
                else if(match.getFromPile() < 7 && match.getToPile() > 6 && match.isMatch() && !match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to toblou - next input
                else if(match.getFromPile() < 7 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    Card card = cards.get(0);
                    cards.remove(0);
                    card.setFaceUp(true);
                    match.nextPlayerCard = card;
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to tablou - no next input
                else if(match.getFromPile() < 7 && match.getToPile() < 7 && match.isMatch() && match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                else {
                    System.out.printf("Meeeh");
                }

            if (table.getFundamentPiles().get(0).size() == 14 && table.getFundamentPiles().get(1).size() == 14 && table.getFundamentPiles().get(2).size() == 14 && table.getFundamentPiles().get(3).size() == 14) {
                gamesWon++;
                break;
            }
            if (i == maximumHandsPrGame - 1){
                gamesLost++;}
            }
        }
        System.out.println("******************************************************************************************************\n");
        System.out.println("Test result in " + amountOfGamesToRun + " games there was " + (int) gamesWon + " games won " + (int) gamesLost + " games lost");
//        System.out.println("That is a win rate on " + df.format(winRatio) + "%\n");
//        System.out.println("Average hands needed for a win: " + df.format(averageMovesForAWin));
//        System.out.println("Maximum hands for a win is: " + maximumMovesForAWin + " hands");
//        System.out.println("Minimum hands for a win is: " + minimumMovesForAWin + " hands\n");
//        System.out.println("******************************************************************************************************\n");
    }
}
