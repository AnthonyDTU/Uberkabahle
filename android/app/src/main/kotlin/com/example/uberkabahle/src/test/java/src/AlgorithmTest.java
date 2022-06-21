package com.example.uberkabahle.src.test.java.src;

import com.example.uberkabahle.src.main.java.src.*;
import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
import com.example.uberkabahle.src.main.java.src.Interfaces.RestrictionLevel;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmTest {
    @Test
    void sortList() {
    //Test with no empty piles
        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H5,K12,R3,R4,H0,R2");
        List<List<Card>> refference = new ArrayList<>(table.getAllPiles());
        List<List<Card>> sortedList = algorithm.sortList(table.getAllPiles());
        assertEquals(refference.get(0), sortedList.get(6), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(1), sortedList.get(4), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(2), sortedList.get(5), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(3), sortedList.get(2), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(4), sortedList.get(3), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(5), sortedList.get(0), "Assert that the sorted list is correct sorted");
        assertEquals(refference.get(6), sortedList.get(1), "Assert that the sorted list is correct sorted");

    //Test with empty piles
        table = new TableIO();
        algorithm = new Algorithm(table);
        move = new Mover(table);;
        table.initStartTable("H13,H5,K12,R3,R4,H0,R2");
        table.getAllPiles().get(4).clear();
        table.getAllPiles().get(5).clear();
        List<List<Card>> refferenceTwo = new ArrayList<>(table.getAllPiles());
        List<List<Card>> sortedListTwo = algorithm.sortList(table.getAllPiles());
        assertEquals(refferenceTwo.get(0), sortedListTwo.get(4), "Assert that the sorted list is correct sorted");
        assertEquals(refferenceTwo.get(1), sortedListTwo.get(2), "Assert that the sorted list is correct sorted");
        assertEquals(refferenceTwo.get(2), sortedListTwo.get(3), "Assert that the sorted list is correct sorted");
        assertEquals(refferenceTwo.get(3), sortedListTwo.get(1), "Assert that the sorted list is correct sorted");
        assertEquals(refferenceTwo.get(6), sortedListTwo.get(0), "Assert that the sorted list is correct sorted");
        assertEquals(5, sortedListTwo.size(), "Assert correct size of sorted list with empty piles");
    }

    @Test
    void restrictionState() {

        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H13,H13,H13,H13,H13,R13");
        assertSame(algorithm.getRestrictionState(), RestrictionLevel.HIGH, "Assert init restriction level is HIGH");
        for(int i = 0 ; i < 16 ; i++) {
            match = algorithm.checkForAnyMatch();
            match.nextPlayerCard = table.stringToCardConverter("K13");
            move. moveCard_OrPile(match);
        }
        assertSame(algorithm.getRestrictionState(), RestrictionLevel.HIGH, "Assert init restriction level is HIGH");
        match = algorithm.checkForAnyMatch();
        match.nextPlayerCard = table.stringToCardConverter("K13");
        move.moveCard_OrPile(match);
        assertSame(algorithm.getRestrictionState(), RestrictionLevel.LOW, "Assert init restriction level is LOW");
    }

    @Test
    void isSolitaireSolved() {
        Table table = new TableIO();
        table.initStartTable("H0,H0,H0,H0,H0,H0,H0"); // Random start table
        Algorithm algorithm = new Algorithm(table);

        for (int i = 0; i < 13; i++){
            for (int j = 0; j < 4; j++){
                switch (j){
                    case 0:
                        table.getFundamentPiles().get(j).add(table.stringToCardConverter("H" + i));
                        break;
                    case 1:
                        table.getFundamentPiles().get(j).add(table.stringToCardConverter("K" + i));
                        break;
                    case 2:
                        table.getFundamentPiles().get(j).add(table.stringToCardConverter("R" + i));
                        break;
                    case 3:
                        table.getFundamentPiles().get(j).add(table.stringToCardConverter("S" + i));
                        break;
                }
            }
        }
        assertTrue(algorithm.isSolitaireSolved());
    }

    @Test
    void matchFromTableauToTableau(){

        Table table = new TableIO();
        Algorithm algorithm = new Algorithm(table);
        Move move = new Mover(table);
        Match match;
        table.initStartTable("H13,H5,K12,K3,K4,H12,K2");
        match = algorithm.checkForAnyMatch();
        assertTrue(match.isMatch(), "Assert that match is found");
        assertEquals(4, match.getFromPile(), "Assert fromPile is correct");
        assertEquals(1, match.getToPile(), "Assert toPile is correct");
    }
}
