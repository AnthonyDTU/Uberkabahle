//TODO Explain the algorithm
//Restriction state explanation


package com.example.uberkabahle.src.main.java.src;

import com.example.uberkabahle.src.main.java.src.Interfaces.RestrictionLevel;
import com.example.uberkabahle.src.main.java.src.Interfaces.Solver;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
import java.util.*;

public class Algorithm implements Solver {


    /*
    * IDE TIL STOCK ALGORITME
    *
    * - Undgå at komme i loop, hvor man ikke kan bruge de kort man får.
    *   Dette kan undgås ved, at (faceUp + FaceDown) % 3    !=   0
    *
    * - Før hver gang man trækker fra stock til foundation eller tablou, tjek om (faceUp + FaceDown - 1) % 3  =  0
    * - Hvis denne bliver 0 næste gang, kan man gennemgå hele bunken - måske
    *
    * */

    //TODO Tjek om bunken bliver vendt rigtigt

    private int cardFromPile;// = -10;
    private int cardToPile;// = -10;
    private int cardFromComplexPileIndex;
    private boolean complexMatch = false;
    private  boolean printTable;
    private boolean turnOverCard_playerDeck = false;
    private boolean firstTurn = true;
    //private boolean noNeedNextInput_algoritmClass = false;
    private int finalComplexPile;

    private int stockPileSize = 0;
    private int roundsToReturn = 0;
    private int currentRound = 0;

    private boolean enterModThreeState = false;

    List<List<Card>> tempPile = new ArrayList<>();
    List<List<Card>> sortedList = new ArrayList<>();
    Table table;

    RestrictionLevel restrictionLevel = RestrictionLevel.HIGH;

    public Algorithm(Table table){
        this.table = table;
    }

    public List<List<Card>> sortList(List<List<Card>> listToSort) {
        /*
         * @void
         * Takes a list and sort it in accenting order
         * -Create current and next index
         * -Compare the value of the current and next card.
         * -If next is smaller than current, swap it.
         * -Iterate through the list.
         * -Start over, if no swaps is done, set the boolean to false, and the loop is broken.
         * */
        boolean swapped = true;
        for (int i = 0 ; i < listToSort.size() ; i++){
            if (listToSort.get(i).isEmpty()){
                listToSort.remove(i);
                i--;
            }
        }
        while (swapped)
        {
            swapped = false;
            int current = 0;
            int next = 1;
            while (next < listToSort.size())
            {
                if(listToSort.get(current).isEmpty() || listToSort.get(next).isEmpty()){current++; next++; continue;}
                else if(listToSort.get(current).get(0).getValue() > listToSort.get(next).get(0).getValue())
                {
                    Collections.swap(listToSort, current, next);
                    swapped = true;
                }
                current++;
                next++;
            }
        }
        return listToSort;

       //List<List<Card>> sorted = listToSort.stream().sorted(Comparator.comparing(List<List<Card>>::get())).collect(Collectors.toList());
    }

    private void createSortedList_OfCards(){        //TODO Lav disse to finctioner createSortedList_OfCards() & sortList mere overskuelige
        /*
         * Initialize the temporary pile of cars and create a sorted list from low value to high value
         * */
        tempPile.clear();   //Clear any existing instance of the list
        tempPile.addAll(table.getAllFaceUpCards());
        sortedList = sortList(tempPile);
    }
    @Override
    public Match checkForAnyMatch() {

        if(checkForMatch_tablou_to_TopPile()){
            Match match = new Match(cardFromPile, cardToPile, true, false);
            if(table.getAllPiles().get(cardFromPile).size() < 2){
                match.setNoNextInput(true);
                return match;
            }
            if(table.getAllPiles().get(cardFromPile).get(table.getAllPiles().get(cardFromPile).size() - 2).isFaceUp()){
                match.setNoNextInput(true);
            }
            return match;
        }

        else if(checkForComplexMatch()){
            Match match = new Match(cardFromPile, cardToPile, true, true, cardFromComplexPileIndex, finalComplexPile);
            int index = 0;

            if(cardFromComplexPileIndex < 2){
                match.setNoNextInput(true);
                return match;
            }
            if (table.getAllPiles().get(cardFromPile).get(cardFromComplexPileIndex - 2).isFaceUp()){
                match.setNoNextInput(true);
            }
            return match;
        }

        else if(checkForMatchBottomPiles()){    //TODO denne funktions skaber lidt problemer
            int index = 0;
            Match match = new Match(cardFromPile, cardToPile, true, false);
            if(table.getAllPiles().get(cardFromPile).size() < 2){
                match.setNoNextInput(true);
                match.lastCardInPile = true;
                return match;
            }
            else if(table.getAllPiles().get(cardFromPile).get(0).isFaceUp()){
                match.setNoNextInput(true);
                match.setLastCardInPile(true);
            }
            return match;
        }


        else if(checkForKingMatch_FromTablou_ToEmptyPile()){
            return new Match(cardFromPile, cardToPile, true, false);
        }

        else if(checkForKingMatch_FromStack_ToEmptyPile()){
            Match match = new Match(cardFromPile, cardToPile, true, false);
            if(table.getPlayerDeck_FaceUp().size() == 1){
                match.setLastCardInPile(true);
                match.setNoNextInput(true);
                return match;
            }
            if(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 2).isFaceUp()){
                match.setNoNextInput(true);
            }
            return match;
        }

        else if(checkForMatch_playerDeck()) {
            Match match = new Match(cardFromPile, cardToPile, true, false);
            if(table.getPlayerDeck_FaceUp().size() > 1){
                if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 2 ).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 2){
                if (table.getPlayerDeck_FaceDown().get(1).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 1){
                if (table.getPlayerDeck_FaceDown().get(0).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 0){
                //TODO handle empty stock pile condition here
            }
            else if(table.getPlayerDeck_FaceUp().size() == 1){
                match.setNoNextInput(true);
                match.setLastCardInPile(true);
            }
//            else if(table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() == 2){
//                if (table.getPlayerDeck_FaceDown().get(1).isFaceUp()) {
//                    match.setNoNextInput(true);
//                }
//            }
//            else if(table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() == 2){
//                if (table.getPlayerDeck_FaceDown().get(1).isFaceUp()) {
//                    match.setNoNextInput(true);
//                }
//            }
            return match;
        }

        else if (checkForFoundationToTableauMatch()){
            Match match = new Match(cardFromPile, cardToPile, true, false);
            match.setNoNextInput(true);
            return match;
        }

    //If nothing of above apply, then we need to turn three new cards from stock.
        else {
            Match match = new Match(11, -1, false, false);
        //If we are at the end of the pile
            if(table.getPlayerDeck_FaceUp().size() > 2 && table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if (table.getPlayerDeck_FaceUp().size() == 2 && table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceUp().get(0).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() > 2 && table.getPlayerDeck_FaceDown().size() == 2){
                if(table.getPlayerDeck_FaceUp().get(0).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 2 && table.getPlayerDeck_FaceDown().size() == 2){
                if(table.getPlayerDeck_FaceUp().get(0).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() == 2){
                match.setNoNextInput(true);
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 2){
                if(table.getPlayerDeck_FaceDown().get(1).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() > 2 && table.getPlayerDeck_FaceDown().size() == 1){
                if(table.getPlayerDeck_FaceUp().get(1).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 2 && table.getPlayerDeck_FaceDown().size() == 1){
                if(table.getPlayerDeck_FaceUp().get(1).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() == 1){
                match.setNoNextInput(true);
            }
            else if(table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 1){
                if(table.getPlayerDeck_FaceDown().get(0).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() >= 3 && table.getPlayerDeck_FaceDown().size() == 0){
                if(table.getPlayerDeck_FaceUp().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
            else if(table.getPlayerDeck_FaceUp().size() < 3 && table.getPlayerDeck_FaceDown().size() == 0){
                match.setNoNextInput(true);
            }

            if (isStockPile_ModThree_EqualsToZero()){
                if (roundsToReturn == 0){
                    stockPileSize = table.getPlayerDeck_FaceUp().size() + table.getPlayerDeck_FaceDown().size();
                    roundsToReturn = stockPileSize/3;
                }
                currentRound++;
                if(currentRound == roundsToReturn){
                    restrictionLevel = RestrictionLevel.LOW;
                }
            }
            else {
                currentRound = 0;
                roundsToReturn = 0;
                restrictionLevel = RestrictionLevel.HIGH;
            }
            return match;
        }
    }

    private boolean allStockCardAreKnown() {
        for (int i = 0 ; i < table.getPlayerDeck_FaceUp().size() ; i++){
            if (!table.getPlayerDeck_FaceUp().get(i).isFaceUp()){
                return false;
            }
        }
        for (int i = 0 ; i < table.getPlayerDeck_FaceDown().size() ; i++){
            if (!table.getPlayerDeck_FaceDown().get(i).isFaceUp()){
                return false;
            }
        }
        return true;
    }

    private boolean nextStockCardIsKnown() {
        if (table.getPlayerDeck_FaceDown().size() > 2) {
            if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()) {
                return true;
            }
        }
        else if (table.getPlayerDeck_FaceDown().size() == 2) {
            return table.getPlayerDeck_FaceUp().get(0).isFaceUp();
        }
        else if (table.getPlayerDeck_FaceDown().size() == 1 && table.getPlayerDeck_FaceUp().size() > 1) {
            return table.getPlayerDeck_FaceUp().get(1).isFaceUp();
        }
        else if (table.getPlayerDeck_FaceDown().size() == 0) {
            return table.getPlayerDeck_FaceUp().get(2).isFaceUp();
        }
        return false;
    }

    private boolean checkForKingMatch_FromStack_ToEmptyPile() {
        //We try to match the king, with a already known card on the tablou or stock.
        //If restriction level is low, then we move the king no matter what.
        if (!table.getPlayerDeck_FaceUp().isEmpty()) {
            if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getValue() == 12) {
                Card king = table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1);
                //Find out if there is an empty pile
                for (int i = 0; i < 7; i++) {
                    if (table.getAllPiles().get(i).isEmpty()) {
                        if (restrictionLevel == RestrictionLevel.HIGH) {
                            if (findMatchForKing(king)) {
                                cardFromPile = 11;
                                cardToPile = i;
                                return true;
                            }
                        }
                        else{
                            restrictionLevel = RestrictionLevel.HIGH;
                            cardFromPile = 11;
                            cardToPile = i;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean findMatchForKing(Card king) {
        int validValue = 11;
        int validColor = 0;
        if (king.getColor() == 0){
            validColor = 1;
        }
        //Start by looking in the tablou pile for a match
        for (int i = 0 ; i < 7 ; i++){
            for (int j = 0 ; j < table.getAllPiles().get(i).size() ; j++){
                if (!table.getAllPiles().get(i).get(j).isFaceUp()){
                    continue;
                }
                if(table.getAllPiles().get(i).get(j).getValue() != validValue || table.getAllPiles().get(i).get(j).getColor() != validColor){
                    break;
                }
                else{
                    return true;
                }
            }
        }
        //Now look in the stack if there is a match.
        for (int i = 0 ; i < table.getPlayerDeck_FaceDown().size() ; i++){
            if(table.getPlayerDeck_FaceDown().get(i).getValue() == validValue && table.getPlayerDeck_FaceDown().get(i).getColor() == validColor){
                return true;
            }
        }
        for (int i = 0 ; i < table.getPlayerDeck_FaceUp().size() ; i++){
            if(table.getPlayerDeck_FaceUp().get(i).getValue() == validValue && table.getPlayerDeck_FaceUp().get(i).getColor() == validColor){
                return true;
            }
        }
        return false;
    }

    private boolean checkForKingMatch_FromTablou_ToEmptyPile() {

        //Find out if there is an empty pile
        for (int i = 0 ; i < 7 ; i++){
            if (table.getAllPiles().get(i).isEmpty()){
                //Find if there is a suitable king to move
                for (int j = 0 ; j < 7 ; j++){
                    //First look if the bottom faceup card is a king
                    if(j == i){continue;}
                    for (int k = 0 ; k < table.getAllPiles().get(j).size() ; k++){
                        if (table.getAllPiles().get(j).size() < 1){continue;} //If there is no facedown cards underneath the king, we don't move it
                        if (table.getAllPiles().get(j).get(k).getValue() == 12){
                            if(k == 0){continue;}
                            cardFromPile = j;
                            cardToPile = i;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private boolean checkForMatchBottomPiles() {

        createSortedList_OfCards(); //Re-init the temp pile, so that we get the cards back in
        while (!sortedList.isEmpty())
        {
//            if(checkKingCondition()){return true;};
//            createSortedList_OfCards();
            int validValue = 0;
            int validColor = 0;
            if(!sortedList.get(0).isEmpty()) {
                validValue = (sortedList.get(0).get(0).getValue()) + 1;
                if (sortedList.get(0).get(0).getColor() == 0) {
                    validColor = 1;
                }
            }
            for (int j = 0 ; j < sortedList.size(); j++)
            {
                if(sortedList.get(j).isEmpty()){continue;}
                if (sortedList.get(0).isEmpty()){continue;}
                if(sortedList.get(j).get(sortedList.get(j).size() - 1).getColor() == validColor && sortedList.get(j).get(sortedList.get(j).size() - 1).getValue() == validValue){

                    cardFromPile = sortedList.get(0).get(0).getBelongToPile();
                    cardToPile = sortedList.get(j).get(0).getBelongToPile();
                    return true;
                }
            }
            sortedList.remove(0);
        }
        return false;

    }

    private boolean checkForMatch_playerDeck() {
//Check for match in top piles,
        if(table.getPlayerDeck_FaceUp().size() == 0){return false;}
        for (int i = 0; i < 4; i++) {
            if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getValue() == table.getTopCard_fromFundamentStack(i).getValue() + 1 && table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getType() == table.getTopCard_fromFundamentStack(i).getType()) {
                cardFromPile = 11;
                cardToPile = i + 7;
                if(table.getPlayerDeck_FaceUp().size() > 1){
                    if(!table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 2).isFaceUp()){
                        //TODO implement noNextInput here
                    }
                }
                return true;
            }
        }
//Check for match tablou piles
        for (int i = 0; i < table.getAllPiles().size(); i++) {
           // if(table.getTopCard_PlayerDeck().getValue() == 1) {break;}  //The algorithm don't want to place a two on a tablou pile, as it is then locked
            if(restrictionLevel == RestrictionLevel.HIGH) {
                if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getValue() == 1) {
                    break;
                }
            }
            if(table.getPile(i).size() == 0){continue;}
            if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getValue() + 1 == table.getPile(i).get(table.getPile(i).size() - 1).getValue() && table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getColor() != table.getPile(i).get(table.getPile(i).size() - 1).getColor()) {
                cardFromPile = 11;
                cardToPile = i;
                return true;
            }
        }

//Check for king to move onto empty pile
//        if(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).getValue() == 12 && checkForAnyEmptyPile()) {
//            if(kingHasMatch(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1)))
//            {
//                cardFromPile = 11;
//                for(int i = 0 ; i < 7 ; i++){
//                    if(table.getAllPiles().get(i).isEmpty()){
//                        cardToPile = i;
//                    }
//                }
//                return true;
//            }
//        }
        return false;
    }

//    private boolean kingHasMatch(Card king) {   //TODO maybe improve this
//        int validValue = 11;
//        int validColor = 0;
//
//        if(king.getColor() == 0)
//        {
//            validColor = 1;
//        }
//
//        for (int i = 0 ; i < table.getAllPiles().size() ; i++)
//        {
//            if(table.getBottomFaceUpCard_FromPile(i).getColor() == validColor && table.getBottomFaceUpCard_FromPile(i).getValue() == validValue)
//            {
//                return true;
//            }
//        }
//        for (int i = 0 ; i < table.getPlayerDeck_FaceUp().size() ; i++){
//            if(!table.getPlayerDeck_FaceUp().get(i).isFaceUp()){continue;}
//            if(table.getPlayerDeck_FaceUp().get(i).getValue() == validValue && table.getPlayerDeck_FaceUp().get(i).getColor() == validColor){return true;}
//        }
//        for (int i = 0 ; i < table.getPlayerDeck_FaceDown().size() ; i++){
//            if(!table.getPlayerDeck_FaceDown().get(i).isFaceUp()){continue;}
//            if(table.getPlayerDeck_FaceDown().get(i).getValue() == validValue && table.getPlayerDeck_FaceDown().get(i).getColor() == validColor){return true;}
//        }
//        return false;
//    }
//
//    private boolean checkForAnyEmptyPile() {
//        for (int i = 0 ; i < 7 ; i++) {
//            if (table.getAllPiles().get(i).size() == 0) {
//                return true;
//            }
//        }
//        return false;
//    }

    private boolean checkForMatch_tablou_to_TopPile() {
        createSortedList_OfCards();
        //Check for simple match only at the top card in a pile
        while (!sortedList.isEmpty())
        {
            for (int j = 0 ; j < 4 ; j++)
            {
                if(sortedList.get(0).isEmpty()){continue;}
                if(sortedList.get(0).get(sortedList.get(0).size() - 1).getValue() == table.getTopCard_fromFundamentStack(j).getValue() + 1
                        && sortedList.get(0).get(sortedList.get(0).size() - 1).getType() == table.getTopCard_fromFundamentStack(j).getType())
                {
                    cardFromPile = sortedList.get(0).get(0).getBelongToPile();
                    cardToPile = 7 + j;
                    return true;
                }
            }
            sortedList.remove(0);
        }
        //Check for complex match where the algorithm breaks a pile up to make a match
        //return checkForComplexMatch();
        return false;
    }

    private boolean indexCanSplit(Card card) {
        //createSortedList_OfCards();
        int validValue = card.getValue() + 1;
        int validColor = 0;
        if(card.getColor() == 0){validColor = 1;}
        for(int i = 0 ; i < table.getAllPiles().size() ; i++)
        {
            //System.out.println("Test2");
            //if(sortedList.get(0).isEmpty())
            if(table.getPile(i).isEmpty())
            {
                continue;
            }
            if (table.getPile(i).get(table.getPile(i).size() - 1).getValue() == validValue && table.getPile(i).get(table.getPile(i).size() - 1).getColor() == validColor)
            {
                cardToPile = i;
                return true;
            }
        }
        return false;
    }

    private boolean checkForComplexMatch() {
        /*
         * This function will see, if there is any open-faced card that can be a potential match in the foundation piles.
         * If so, then it will also check, if it is possible to move the card on top of that card, to somewhere else, in order for
         * the card to become free.
         *
         * - Store the valid value and type with respect to foundation pile
         * - Run through all the cards, but skip the face-down cards
         * - If a card matches the value stored, check if the pile can be split with the indexCanSplit() function
         * - If indexCanSplit() returns true, store the relevant index and piles, and return true-
         * */
        for (int i = 0 ; i < 4 ; i++)   //Check all four fundament pile
        {
            if(table.getFundamentPiles().get(i).size() < 2){continue;}
            int validValue = table.getFundamentPiles().get(i).get(table.getFundamentPiles().get(i).size() - 1).getValue() + 1;
            int validType = table.getFundamentPiles().get(i).get(table.getFundamentPiles().get(i).size() - 1).getType();

            for(int j = 0 ; j < table.getAllPiles().size() ; j++)
            {
                for (int k = 0 ; k < table.getPile(j).size() ; k++)
                {
                    if(!table.getPile(j).get(k).isFaceUp()){continue;}
                    if(table.getPile(j).get(k).getValue() == validValue && table.getPile(j).get(k).getType() == validType)
                    {
                        if(indexCanSplit(table.getPile(j).get(k+1))) //See if we can move the card on top of the indexed card
                        {
                            finalComplexPile = i;
                            cardFromPile = j;
                            cardFromComplexPileIndex = k + 1;
                            table.setComplexSplitIndex(cardFromComplexPileIndex);
                            complexMatch = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isStockPile_ModThree_EqualsToZero(){
        int totalCardsInFaceUp_AndFaceDown = table.getPlayerDeck_FaceDown().size() + table.getPlayerDeck_FaceUp().size();
        return totalCardsInFaceUp_AndFaceDown % 3 == 0;
    }

    private boolean checkForFoundationToTableauMatch(){
        createSortedList_OfCards();

        for (int i = 0; i < 4; i++){
            Card tmpFundamentalCard = table.getFundamentPiles().get(i).get(table.getFundamentPiles().get(i).size() - 1);
            for (int j = 0; j < 7; j++){
                if (!table.getAllPiles().get(j).isEmpty()){
                    Card tmpTableauCard = table.getAllPiles().get(j).get(table.getAllPiles().get(j).size() - 1);
                    if (tmpFundamentalCard.getValue() == tmpTableauCard.getValue() - 1 && tmpFundamentalCard.getColor() != tmpTableauCard.getColor()){
                        for (int g = 0; g < 7; g++){
                            if (table.getAllPiles().get(g).size() > 1){
                                Card secondTmpTableauCard = table.getAllPiles().get(g).get(table.getAllPiles().get(g).size() - 1);
                                if (secondTmpTableauCard.getValue() == tmpTableauCard.getValue() - 2 && secondTmpTableauCard.getColor() == tmpTableauCard.getColor()){
                                    if (table.getAllPiles().get(g).get(table.getAllPiles().get(g).size() - 1) != null && !table.getAllPiles().get(g).get(table.getAllPiles().get(g).size() - 2).isFaceUp()){
                                        cardFromPile = i + 7;
                                        cardToPile = j;
                                        return true;
                                    }
                                }

                                for (int h = 0; h < table.getAllPiles().get(g).size(); h++){
                                    secondTmpTableauCard = table.getAllPiles().get(g).get(h);
                                    if (secondTmpTableauCard.getValue() == tmpTableauCard.getValue() - 2 && secondTmpTableauCard.getColor() != tmpTableauCard.getColor()){
                                        if (table.getAllPiles().get(g).get(h) != null && !table.getAllPiles().get(g).get(h).isFaceUp()){
                                            cardFromPile = i + 7;
                                            cardToPile = j;
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        return false;
    }

    public RestrictionLevel getRestrictionState() {
        return restrictionLevel;
    }
}
