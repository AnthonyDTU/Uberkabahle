package com.example.uberkabahle.src.test.java.src;

import com.example.uberkabahle.src.main.java.src.Algorithm;
import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
import com.example.uberkabahle.src.main.java.src.Match;
import com.example.uberkabahle.src.main.java.src.Mover;
import com.example.uberkabahle.src.main.java.src.TableIO;
import org.junit.jupiter.api.Test;
//import org.testng.annotations.Test;

import static org.testng.Assert.*;


class MoverTest {

    @Test
    void foundationToTablou(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H5,K13,R13,R13,H13,R13");
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K2"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K3"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K4"));
        table.getPlayerDeck_FaceUp().add(table.stringToCardConverter("H3"));
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch(), "Assert that match is found");
        assertTrue(match.getFromPile() > 6, "Assert that the match is from foundation to tablou");
        move.moveCard_OrPile(match);
    }

    @Test
    void StockToTablou(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);

        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K12");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isNoNextInput(), "Assert that next input is needed");
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
//Test end-pile cases.
        //Two faceup cards four facedown cards -> match
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        for (int i = 0 ; i < 20 ; i++){
            table.getPlayerDeck_FaceDown().remove(0);
        }
        table.getPlayerDeck_FaceUp().add(table.stringToCardConverter("H7"));
        table.getPlayerDeck_FaceUp().add(table.stringToCardConverter("H12"));
        match = algorithm.checkForAnyMatch();
        move.moveCard_OrPile(match);
    }


    @Test
    void turnOverNewCard_PlayerDeck() {
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        for(int i = 0 ; i < 8 ; i++) {
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch(), "Assert no match");
            match.nextPlayerCard = table.stringToCardConverter("K13");
            move.moveCard_OrPile(match);
        }
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert no match");
        assertTrue(match.isNoNextInput());
    //Check when 2 cards left at the end
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert no match");
        match.nextPlayerCard = table.stringToCardConverter("K12");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch());
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        for(int i = 0 ; i < 7 ; i++) {
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch(), "Assert no match");
            match.nextPlayerCard = table.stringToCardConverter("K13");
            move.moveCard_OrPile(match);
        }
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert no match");
        assertFalse(match.isNoNextInput());
        match.nextPlayerCard = table.stringToCardConverter("K9");
        move.moveCard_OrPile(match);
        assertEquals(8,table.getPlayerDeck_FaceUp().get(2).getValue(), "Assert the correct cards has been inserted");
        assertEquals(3, table.getPlayerDeck_FaceUp().size(), "Assert the size of stock is correct");
        for(int i = 0 ; i < 6 ; i++) {
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch(), "Assert no match");
            match.nextPlayerCard = table.stringToCardConverter("K9");
            move.moveCard_OrPile(match);
        }
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert no match");
        match.nextPlayerCard = table.stringToCardConverter("K8");
        move.moveCard_OrPile(match);
        assertEquals(-1, table.getPlayerDeck_FaceUp().get(0).getValue() , "Assert first card is still facedown");
        assertEquals(12, table.getPlayerDeck_FaceUp().get(1).getValue(), "Assert that second card is moved correctly");
        assertEquals(7, table.getPlayerDeck_FaceUp().get(2).getValue(), "Assert third card is moved correctly");
    //Another test, with 1 card left at the end
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert no match");
        match.nextPlayerCard = table.stringToCardConverter("K12");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch());
        match.nextPlayerCard = table.stringToCardConverter("H11");
        move.moveCard_OrPile(match);
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        for (int i = 0 ; i < 7 ; i++) {
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch(), "Assert that there is no match");
            assertFalse(match.isStockPileIsEmpty());
            match.nextPlayerCard = table.stringToCardConverter("K13");
            move.moveCard_OrPile(match);
        }
        for (int i = 0 ; i < 7 ; i++) {
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch(), "Assert that there is no match");
            assertFalse(match.isStockPileIsEmpty());
            match.nextPlayerCard = table.stringToCardConverter("K9");
            move.moveCard_OrPile(match);
        }
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch(), "Assert that there is no match");
        match.nextPlayerCard = table.stringToCardConverter("K9");
        move.moveCard_OrPile(match);
        System.out.printf("");
    //Test near game-end (small stock pile)
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        for (int i = 0 ; i < 21 ; i++){
            table.getPlayerDeck_FaceDown().remove(0);
        }
        for (int i = 0 ; i < 3 ; i++) {
            table.getPlayerDeck_FaceDown().get(i).setFaceUp(true);
            table.getPlayerDeck_FaceDown().get(i).setBelongToPile(11);
            table.getPlayerDeck_FaceDown().get(i).setColor(1);
            table.getPlayerDeck_FaceDown().get(i).setValue((i+1)*3);
            table.getPlayerDeck_FaceDown().get(i).setType(i);
        }
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isNoNextInput(), "Assert that next input is known");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();

    //End game cases:
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        for (int i = 0 ; i < 24 ; i++){
            table.getPlayerDeck_FaceDown().remove(0);
        }
        table.getPlayerDeck_FaceDown().add(table.stringToCardConverter("E"));
        table.getPlayerDeck_FaceDown().add(table.stringToCardConverter("K13"));
        match = algorithm.checkForAnyMatch();
        move.moveCard_OrPile(match);
        System.out.println("");
    }

    @Test
    void lastCardInStockIsMatch(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H13,K13,R13,R13,H13,R13");
        for (int i = 0 ; i < 7 ; i++){
            match = algorithm.checkForAnyMatch();
            assertFalse(match.isMatch());
            match.nextPlayerCard = table.stringToCardConverter("K13");
            move.moveCard_OrPile(match);
        }
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isMatch());
        match.nextPlayerCard = table.stringToCardConverter("S0");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch());
        match.nextPlayerCard = table.stringToCardConverter("K9");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
    }

    @Test
    void moveFromTablouToFoundation_withInput(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H13,K13,R0,R13,H13,R13");
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isNoNextInput());
        match.nextPlayerCard = table.stringToCardConverter("H9");
        move.moveCard_OrPile(match);
        assertEquals(3, table.getAllPiles().get(3).size(), "Assert correct size of pile");
        assertEquals(8, table.getAllPiles().get(3).get(2).getValue(), "Assert that the correct card is inserted");
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("H0");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        //Test no next input
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("H0,H13,K13,R13,R13,H13,R13");
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isNoNextInput());
        move.moveCard_OrPile(match);
    }

    @Test
    void tablou_foundation(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H0,S0,R0,K0,H2,S2,R2");
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isNoNextInput());
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertFalse(match.isNoNextInput());
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("S0"));
    }

    @Test
    void moveKing_fromStock_ToEmptyPile(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("K12,H12,K6,R12,R6,H6,R6");
        table.getAllPiles().get(4).clear();
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch(), "Assert match");
        assertEquals(4, match.getToPile(), "Assert to pile");
        assertEquals(11, match.getFromPile(), "Assert from pile");
        match.nextPlayerCard = table.stringToCardConverter("K6");
        move.moveCard_OrPile(match);
    }

    @Test
    void moveKing_fromTablou_ToEmptyPile(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("K12,H12,K6,R12,R6,H6,R6");
    }
    @Test
    void moveFromTablou_ToTablou(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("K12,H12,K12,R12,R7,H12,K6");
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);
        table.initStartTable("K12,H12,K12,K13,R7,H12,K11");
        table.getAllPiles().get(3).clear();
        table.getAllPiles().get(4).clear();
        table.getAllPiles().get(5).clear();
        table.getAllPiles().get(3).add(table.stringToCardConverter("K13"));
        table.getAllPiles().get(3).get(table.getAllPiles().get(3).size() - 1).setBelongToPile(3);
        table.getAllPiles().get(3).add(table.stringToCardConverter("R12"));
        table.getAllPiles().get(3).get(table.getAllPiles().get(3).size() - 1).setBelongToPile(3);
        match = algorithm.checkForAnyMatch();
    }
    @Test
    void complexMatch(){
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("K6,S6,K12,R12,R12,H12,R12");
        table.getAllPiles().get(0).add(table.stringToCardConverter("H5"));
        table.getAllPiles().get(0).add(table.stringToCardConverter("S4"));
        table.getAllPiles().get(0).add(table.stringToCardConverter("H3"));
        table.getAllPiles().get(0).add(table.stringToCardConverter("S2"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K0"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K2"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K3"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K4"));
        table.getFundamentPiles().get(0).add(table.stringToCardConverter("K5"));
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isComplex());
        assertTrue(match.isNoNextInput());
        match.nextPlayerCard = table.stringToCardConverter("K3");
        move.moveCard_OrPile(match);
    }
}