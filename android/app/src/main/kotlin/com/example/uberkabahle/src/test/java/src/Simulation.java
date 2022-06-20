package com.example.uberkabahle.src.test.java.src;


import com.example.uberkabahle.src.main.java.src.Algorithm;
import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
import com.example.uberkabahle.src.main.java.src.Match;
import com.example.uberkabahle.src.main.java.src.Mover;
import com.example.uberkabahle.src.main.java.src.TableIO;

public class Simulation {

    //Run the simulation from this main method
    public static void main(String[] args) {
        Simulation simulation = new Simulation(1000, 800, false);
        TestResult testResult = simulation.runSimulation();
        System.out.println(testResult);
    }
    int numberOfGames;
    int maximumNumberOfHandsInEachGame;
    int handsLost = 0;
    int handsWon = 0;
    boolean printTable;
    private Simulation(int numberOfGames, int maximumNumberOfHandsInEachGame, boolean printTable) {
        this.numberOfGames = numberOfGames;
        this.maximumNumberOfHandsInEachGame = maximumNumberOfHandsInEachGame;
        this.printTable = printTable;
    }
    private TestResult runSimulation() {
        for (int i = 0; i < numberOfGames; i++) {
            Table table = new TableIO();
            Algorithm algorithm = new Algorithm(table);
            Move move = new Mover(table);
            Match match;
            src.RandomCards randomCards = new src.RandomCards();
            table.initStartTable(randomCards.getStartTableString());

            for (int j = 0; j < maximumNumberOfHandsInEachGame; j++) {
                match = algorithm.checkForAnyMatch();
                if (match.isComplex() && (match.isNoNextInput() || match.isLastCardInPile())) {
                    if (printTable) {
                        System.out.println("Complex match, first move from pile " + match.getFromPile() + " at index " + match.getComplexIndex() + " to tablou pile " + match.getToPile());
                        System.out.println("After that, move the card at tablou pile " + match.getFromPile() + " to foundation pile " + match.getComplexFinalFoundationPile());
                    }
                    move.moveCard_OrPile(match);
                }
                else if (match.isComplex() && !match.isNoNextInput()) {
                    if (printTable) {
                        System.out.println("Complex match, first move from pile " + match.getFromPile() + " at index " + match.getComplexIndex() + " to tablou pile " + match.getToPile());
                        System.out.println("After that, move the card at tablou pile " + match.getFromPile() + " to foundation pile " + match.getComplexFinalFoundationPile());
                        System.out.println("Last turn over the facedown card in tablou " + match.getFromPile() + " and enter value:");
                    }

                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                    if (printTable) {
                        table.printTable();
                    }
                }

            //Match from foundation to tablou - no next input
                else if(match.isMatch() && match.getFromPile() > 6 && match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("Move from foundation " + match.getFromPile() + " to tablou " + match.getToPile());
                        System.out.println("After that move the card from talon to tablo " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }

            //Match from foundation to tablou - next input
                else if(match.isMatch() && match.getFromPile() > 6 && !match.isNoNextInput()){
                    if(printTable) {
                        System.out.println("Move from foundation " + match.getFromPile() + " to tablou " + match.getToPile());
                        System.out.println("After that move the card from talon to tablo " + match.getToPile());
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }

            //No match - Turn card from player pile - next input - Not last card in faceUp
                else if (match.getFromPile() == 11 && !match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("Turn over three new cards in the stock pile");
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }
                //No match - Turn card from player pile - no next input
                else if (match.getFromPile() == 11 && !match.isMatch() && match.isNoNextInput() && !match.isLastCardInPile()) {
                    if (printTable) {
                        System.out.println("Turn over three new cards in the stock pile");
                    }
                    move.moveCard_OrPile(match);
                    //System.out.printf("The next card you turn over is known and is: " + table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                }
                //Match from stock pile to tablou - next input
                else if (match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("move from " + match.getFromPile() + " to tableau" + match.getToPile());
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }
                //Match from stock to foundation - next input
                else if (match.getFromPile() == 11 && match.getToPile() >= 7 && match.isMatch() && !match.isNoNextInput() && !match.isLastCardInPile()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("move from stock to foundation pile: " + match.getToPile());
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }
                //Match from stock to foundation - no next input
                else if (match.getFromPile() == 11 && match.getToPile() > 6 && match.isMatch() && match.isNoNextInput()) {
                    if (printTable) {
                        System.out.println("move from stock to foundation" + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from stock to tablou - next input
                else if (match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("Move from stock to tablou pile: " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from stock to tablou - no next input
                else if (match.getFromPile() == 11 && match.getToPile() < 7 && match.isMatch()) {
                    if (printTable) {
                        System.out.println("Move from stock to tablou pile: " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                    //System.out.println("The next card in stock pile is known: " + table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                }
                //Match from tablou to foundation - no next input
                else if (match.getFromPile() < 7 && match.getToPile() > 6 && match.isMatch() && match.isNoNextInput()) {
                    if (printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to foundation - next input
                else if (match.getFromPile() < 7 && match.getToPile() > 6 && match.isMatch() && !match.isNoNextInput()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to toblou - next input
                else if (match.getFromPile() < 7 && match.getToPile() < 7 && match.isMatch() && !match.isNoNextInput()) {
                    if (printTable) {
                        table.printTable();
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    match.nextPlayerCard = table.stringToCardConverter(randomCards.getNextCard());
                    match.nextPlayerCard.setFaceUp(true);
                    move.moveCard_OrPile(match);
                }
                //Match from tablou to tablou - no next input
                else if (match.getFromPile() < 7 && match.getToPile() < 7 && match.isMatch() && match.isNoNextInput()) {
                    if (printTable) {
                        System.out.println("move from " + match.getFromPile() + " to " + match.getToPile());
                    }
                    move.moveCard_OrPile(match);
                } else {
                    System.out.printf("Meeeh");
                }

                if (table.getFundamentPiles().get(0).size() == 14 && table.getFundamentPiles().get(1).size() == 14 &&
                        table.getFundamentPiles().get(2).size() == 14 && table.getFundamentPiles().get(3).size() == 14) {
                    handsWon++;
                    break;
                }
                if (j == maximumNumberOfHandsInEachGame - 1){
                    handsLost++;
                }
            }
        }
        return new TestResult(handsWon, handsLost);
    }
}


