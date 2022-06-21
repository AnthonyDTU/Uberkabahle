package com.example.uberkabahle.src.test.java.src;

import com.example.uberkabahle.src.main.java.src.*;
import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
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
        assertTrue(refference.get(0).equals(sortedList.get(6)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(1).equals(sortedList.get(4)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(2).equals(sortedList.get(5)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(3).equals(sortedList.get(2)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(4).equals(sortedList.get(3)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(5).equals(sortedList.get(0)), "Assert that the sorted list is correct sorted");
        assertTrue(refference.get(6).equals(sortedList.get(1)), "Assert that the sorted list is correct sorted");
    }

    @Test
    void getRestrictionState() {
    }

    @Test
    void isSolitaireSolved() {
    }
}