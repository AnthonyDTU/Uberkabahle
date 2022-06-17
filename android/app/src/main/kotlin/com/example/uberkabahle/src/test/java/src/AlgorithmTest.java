//package src;
//
//import org.junit.jupiter.api.Test;
//import src.Interfaces.Move;
//import src.Interfaces.Table;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AlgorithmTest {
//
//    //TODO Test for king to match empty pile
//    @Test
//    void sortList() {
//
//        int actualValue;
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//
//        List<List<Card>> underTest = new ArrayList<>();
//
//        List<Card> pileOne = new ArrayList<>();
//        List<Card> pileTwo = new ArrayList<>();
//        List<Card> pileThree = new ArrayList<>();
//        List<Card> pileFour = new ArrayList<>();
//        List<Card> pileFive = new ArrayList<>();
//        List<Card> pileSix = new ArrayList<>();
//        List<Card> pileSeven = new ArrayList<>();
//
//        pileOne.add(new Card(1,1,12,true,0));
//        pileTwo.add(new Card(1,1,7,true,1));
//        pileThree.add(new Card(1,1,2,true,2));
//        pileFour.add(new Card(1,1,6,true,3));
//
//        pileFive.add(new Card(1,1,8,true,4));
//        pileFive.add(new Card(0,0,7,true,4));
//        pileFive.add(new Card(1,1,6,true,4));
//        pileFive.add(new Card(0,0,5,true,4));
//        pileFive.add(new Card(1,1,4,true,4));
//        pileFive.add(new Card(0,0,3,true,4));
//        pileFive.add(new Card(1,1,2,true,4));
//
//        pileSix.add(new Card(1,1,11,true,5));
//        pileSeven.add(new Card(1,1,3,true,6));
//
//        underTest.add(pileOne);
//        underTest.add(pileTwo);
//        underTest.add(pileThree);
//        underTest.add(pileFour);
//        underTest.add(pileFive);
//        underTest.add(pileSix);
//        underTest.add(pileSeven);
//
//        List<List<Card>> sortedList = algorithm.sortList(underTest);
//
//        actualValue = sortedList.get(0).get(0).getValue();
//        assertEquals(2, actualValue);
//
//        actualValue = sortedList.get(1).get(0).getValue();
//        assertEquals(3, actualValue);
//
//        actualValue = sortedList.get(2).get(0).getValue();
//        assertEquals(6, actualValue);
//
//        actualValue = sortedList.get(3).get(0).getValue();
//        assertEquals(7, actualValue);
//
//        actualValue = sortedList.get(4).get(0).getValue();
//        assertEquals(8, actualValue);
//
//        actualValue = sortedList.get(5).get(0).getValue();
//        assertEquals(11, actualValue);
//
//        actualValue = sortedList.get(6).get(0).getValue();
//        assertEquals(12, actualValue);
//
//    }
//    @Test
//    void endOfPlayerPileTest(){
//    //Test when the player deck is divisible with 3
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//    //Init start table with no match
//        table.initStartTable("H7,S10,H10,S2,K10,S12,H2");
//        Match match = algorithm.checkForAnyMatch();
//        boolean hasMatch = match.match;
//        assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//        match.nextPlayerCard = table.stringToCardConverter("H12");
//        move.insertNextCardFromInput(match);
//    //Rerun Algorithm with the new no-match until supposed index 21
//        for (int i = 0 ; i < 7 ; i++) {
//            match = algorithm.checkForAnyMatch();
//            hasMatch = match.match;
//            assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//            match.nextPlayerCard = table.stringToCardConverter("H12");
//            move.insertNextCardFromInput(match);
//        }
//        //int actual = table.getPlayerDeckIndex();
//        //assertEquals(23, actual, "Assert that the player deck index is 23");
//    //Now we get one more match, so the playerPile should start over
//        match = algorithm.checkForAnyMatch();
//        hasMatch = match.match;
//        assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//        match.nextPlayerCard = table.stringToCardConverter("H12");
//        move.insertNextCardFromInput(match);
//        //actual = table.getPlayerDeckIndex();
//        //assertEquals(2, actual, "Assert that the player deck index is now at 2");
//
//    //Test when the player deck is NOT divisible with 3
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        //Init start table with no match
//        table.initStartTable("H7,S10,H10,S2,K10,S12,H2");
//        match = algorithm.checkForAnyMatch();
//        hasMatch = match.match;
//        assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//        match.nextPlayerCard = table.stringToCardConverter("H0"); //Get rid of one card in the player deck by adding an ace to match
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        hasMatch = match.match;
//        assertTrue(hasMatch, "Assert that there is match");
//        match.nextPlayerCard = table.stringToCardConverter("H12");
//        move.insertNextCardFromInput(match);
//
//    //Rerun Algorithm with the new no-match until supposed index 21
//        for (int i = 0 ; i < 7 ; i++) {
//            match = algorithm.checkForAnyMatch();
//            hasMatch = match.match;
//            assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//            match.nextPlayerCard = table.stringToCardConverter("H12");
//            move.insertNextCardFromInput(match);
//        }
//        //actual = table.getPlayerDeckIndex();
//        //assertEquals(22, actual, "Assert that the player deck index is 22");
//    //Rerun again, this time there will be two cards left at the end of the player deck
//        for (int i = 0 ; i < 7 ; i++) {
//            match = algorithm.checkForAnyMatch();
//            hasMatch = match.match;
//            assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//            match.nextPlayerCard = table.stringToCardConverter("K12");
//            move.insertNextCardFromInput(match);
//        }
//    //Now the index is pointing at the last possible card in the player pile, with two cards left
//        match = algorithm.checkForAnyMatch();
//        hasMatch = match.match;
//        assertFalse(hasMatch, "Assert that there is no match, and therefore a player card move is recommended");
//        match.nextPlayerCard = table.stringToCardConverter("R12");
//        move.insertNextCardFromInput(match);
//        System.out.println("test");
//
//    }
//    @Test
//    void checkForEmptyTablouPile(){
//
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        Match match;
//    //Init start table with one ace
//        table.initStartTable("H7,S10,H10,S2,K10,S12,S0");
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("K12");
//        move.insertNextCardFromInput(match);
//        assertTrue(match.lastCardInPile, "Assert that the boolean lastCardInPile from Match object is set to true");
//        assertEquals(0, table.getAllPiles().get(6).size(), "Assert that the pile is still empty, after trying to insert a card in it");
//    }
//    @Test
//    void checkFromTablouPile_ToTablouPile() {
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        table.initStartTable("H7,S10,H9,S2,K10,S13,R13");
//        Match match = algorithm.checkForAnyMatch();
//        assertEquals(2, match.fromPile);
//        assertEquals(1, match.toPile);
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H11");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match");
//        assertEquals(1, match.fromPile);
//        assertEquals(2, match.toPile, "Assert that it is the correct toPile");
//        move.moveCard_OrPile(match);
//        System.out.printf("");
//
//    //Check a complicated match
//
//
//
//    }
//    @Test
//    void checkFromTablouPile_ToFoundation(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        table.initStartTable("H7,S10,H9,S2,K10,S0,R10");
//        Match match = algorithm.checkForAnyMatch();
//        assertEquals(5, match.fromPile);
//        assertEquals(10, match.toPile);
//    }
//    @Test
//    void checkForNoMatchCondition(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        table.initStartTable("H7,S10,H12,S2,K10,H2,R10");
//        Match match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//    }
//    @Test
//    void checkFromPlayerCard_ToFoundation(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Match match;
//        Move move = new Mover(table);
//        table.initStartTable("H7,S10,H12,S3,K10,H3,R10");
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that there is no match");
//    //Insert ace in player deck
//        match.nextPlayerCard = table.stringToCardConverter("S0");
//        move.insertNextCardFromInput(match);
//    //Run the algorithm again
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match from player deck to foundation");
//        assertEquals(11, match.fromPile, "Assert that the match is from player deck");
//        assertEquals(10, match.toPile, "Assert that it is the correct toPile in match");
//        move.moveCard_OrPile(match);
//        match = algorithm.checkForAnyMatch();
//    //Insert S2 and check if it still matches
//        match.nextPlayerCard = table.stringToCardConverter("S2");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        assertEquals(11, match.fromPile, "Assert that the match is from player deck");
//        assertEquals(10, match.toPile, "Assert that it is the correct toPile in match");
//    //Check if the lsat card from the player deck is also a match
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        table.initStartTable("H7,S10,H12,S3,K10,H3,R10");
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that there is no match");
//        //Insert ace in player deck
//        match.nextPlayerCard = table.stringToCardConverter("S0");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H0");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Check for match");
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("K0");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//
//
//    }
//    @Test
//    void checkFromPlayerDeck_ToTablou(){
//    //Init table to no match
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Match match;
//        Move move = new Mover(table);
//        table.initStartTable("H7,S10,H12,S2,K10,H2,R10");
//        match = algorithm.checkForAnyMatch();
//    //Insert card in player deck
//        match.nextPlayerCard = table.stringToCardConverter("S6");
//        move.insertNextCardFromInput(match);
//    //Run algoritm again again
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//    //Try to insert the same card again
//        match.nextPlayerCard = table.stringToCardConverter("S6");
//        move.insertNextCardFromInput(match);
//    //Now se what the algorithm does
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Check that the algorithm cannot move to cards underneath other cards");
//    //Try match with K9
//        match.nextPlayerCard = table.stringToCardConverter("K9");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Check if the algorithm found a match");
//        assertEquals(11, match.fromPile, "Assert that the match is from the player pile");
//        assertEquals(6, match.toPile, "Assert that the match is to the correct pile");
//        assertFalse(match.complex, "Assert that the match is NOT a complex match");
//    }
//    @Test
//    void testForKingMatch_FromTablouPile_ToEmptyTablouPile(){
//
//    //First test with simple match
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        Match match;
//        table.initStartTable("H13,H3,H3,S3,K11,H3,K2");
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(table.getAllPiles().get(6).isEmpty(), "Assert that the pile is empty");
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match on empty pile 6 from king in pile 0");
//        assertEquals(0, match.fromPile, "Assert that it is the correct fromPile");
//        assertEquals(6, match.toPile, "Assert that it is the correct toPile");
//    //Second test, with a more complicated match eg. a larger pile to move.
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        table.initStartTable("H13,H3,H3,S3,K11,S12,K3");
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H11");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(table.getAllPiles().get(5).isEmpty(), "Assert that pile 5 is actually empty");
//        match.nextPlayerCard = table.stringToCardConverter("S0");
//        assertTrue(table.getAllPiles().get(5).isEmpty(), "Assert that pile 5 is actually STILL empty after trying to insert an ace in the empty pile");
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match");
//        assertEquals(0, match.fromPile, "Assert correct fromPile");
//        assertEquals(5, match.toPile, "Assert correct toPile");
//    //Check that a king is not moved, if there is no faceup cards on top of it
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        table.initStartTable("H3,H3,H3,S3,H11,H12,K13");
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("K10");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//        System.out.printf("");
//
//    }
//    @Test
//    void testForKingMatch_FromStack_ToEmptyTablouPile(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        Match match;
//        table.initStartTable("K12,H3,H3,S3,K11,H3,K2");
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(table.getAllPiles().get(6).isEmpty(), "Assert that the pile is empty, even that we tried to insert an ace");
//    //First check is with a mathc-queen in the tablou pile.
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that there is no match");
//        match.nextPlayerCard = table.stringToCardConverter("H13");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match");
//        assertEquals(11, match.fromPile, "Assert that the fromPile is correct");
//        assertEquals(6, match.toPile, "Assert that the toPile");
//    //Next check with a non-match queen in the tablou pile
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        table.initStartTable("H12,H3,H3,S3,H11,H3,K2"); //Change the queen K to a queen H
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//        match.nextPlayerCard = table.stringToCardConverter("H13");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that there is no match because there is no matching queen on the table. ");
//    //Next check if the match is in the stack.
//        //Start by adding some different cards to the playerPile.
//        table = new TableIO();
//        algorithm = new Algorithm(table);
//        move = new Mover(table);
//        table.initStartTable("H11,S6,S6,S6,H11,R4,S3"); //Change the queen K to a queen H
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(table.getAllPiles().get(6).isEmpty(), "Assert that the pile is empty");
//
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//        match.nextPlayerCard = table.stringToCardConverter("R12");//Insert a non-match queen
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert no match");
//        match.nextPlayerCard = table.stringToCardConverter("R13");
//
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that the queen in the stack is not a match");
//
//        match.nextPlayerCard = table.stringToCardConverter("S13");  //Insert a king that should have m match in the stack
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert if a match is found in the stack, and therefor the king should be moved.");
//        //match.nextPlayerCard = table.stringToCardConverter("");
//
//
//
//    }
//    @Test
//    void checkForComplexMatch(){
//        Table table = new TableIO();
//        Move move = new Mover(table);
//        Algorithm algorithm = new Algorithm(table);
//        Match match;
//        table.initStartTable("H7,S10,H12,H4,K10,R4,R10");
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match, "Assert that there is no match");
//        match.nextPlayerCard = table.stringToCardConverter("S3");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match, "Assert that there is a match from the stack");
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H0");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H2");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//        match = algorithm.checkForAnyMatch(); //Run again as the pile is now empty
//        match.nextPlayerCard = table.stringToCardConverter("H3");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("S13");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.complex, "Assert that a complex match has been found");
//    }
//    @Test
//
//    void checkNoNextInputFlag_TablouToTablou(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        Match match;
//        table.initStartTable("K5,H8,H8,S8,H8,H8,H4");
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//        assertTrue(match.lastCardInPile);
//        if(!match.lastCardInPile || !match.noNextInput) {
//            match.nextPlayerCard = table.stringToCardConverter("H0");
//            move.insertNextCardFromInput(match);
//        }
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//        assertTrue(match.lastCardInPile);
//        if(!match.lastCardInPile || !match.noNextInput) {
//            match.nextPlayerCard = table.stringToCardConverter("H12");
//            move.insertNextCardFromInput(match);
//        }
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        assertEquals(8, match.toPile, "Assert correct toPile");
//        assertEquals(11, match.fromPile, "Assert correct fromPile");
//
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H2");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//
//        move.moveCard_OrPile(match);
//
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(match.isNoNextInput());
//    }
//    @Test
//    void testForNoNextInputFlag_StockToTablou(){
//        Table table = new TableIO();
//        Algorithm algorithm = new Algorithm(table);
//        Move move = new Mover(table);
//        Match match;
//        table.initStartTable("H8,H8,H8,H8,H8,H8,H8");
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//
//        match.nextPlayerCard = table.stringToCardConverter("K13");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertFalse(match.match);
//        match.nextPlayerCard = table.stringToCardConverter("S7");
//        move.insertNextCardFromInput(match);
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("H6");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        assertTrue(match.match);
//        move.moveCard_OrPile(match);
//        match.nextPlayerCard = table.stringToCardConverter("S5");
//        move.insertNextCardFromInput(match);
//
//        match = algorithm.checkForAnyMatch();
//        move.moveCard_OrPile(match);
//        assertTrue(match.noNextInput);
//
//        System.out.printf("");
//    }
//}