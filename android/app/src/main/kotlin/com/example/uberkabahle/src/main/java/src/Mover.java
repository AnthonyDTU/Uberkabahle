package com.example.uberkabahle.src.main.java.src;

import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
import com.example.uberkabahle.src.main.java.src.Interfaces.Table;

import java.util.ArrayList;
import java.util.List;

public class Mover implements Move {

    Table table;
    public Mover(Table table){
        this.table = table;
    }
    @Override
    public void moveCard_OrPile(Match match) {

//If there is a complex match - no next input
        if (match.complex && (match.noNextInput || match.lastCardInPile)) {
            //First step tablou to tablou
            List<Card> cardsToMove = new ArrayList<>();

            for (int i = match.complexIndex ; i < table.getAllPiles().get(match.fromPile).size(); i++) {
                cardsToMove.add(table.getAllPiles().get(match.fromPile).get(i));
            }


            for (int i = 0 ; i < cardsToMove.size() ; i++){
                table.getAllPiles().get(match.fromPile).remove(match.complexIndex);
            }

            table.getAllPiles().get(match.toPile).addAll(cardsToMove);

            table.getFundamentPiles().get(match.getComplexFinalFoundationPile()).add(table.getAllPiles().get(match.fromPile).get(table.getAllPiles().get(match.fromPile).size() - 1));
            table.getFundamentPiles().get(match.complexFinalFoundationPile).get(table.getFundamentPiles().get(match.complexFinalFoundationPile).size() - 1).setBelongToPile(match.complexFinalFoundationPile + 7);
            table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);

            int type = table.getFundamentPiles().get(match.complexFinalFoundationPile).get(table.getFundamentPiles().get(match.complexFinalFoundationPile).size() - 1).getType();
        }
        else if(match.complex){
//If there is a complex match - next input
            List<Card> cardsToMove = new ArrayList<>();

            for (int i = match.complexIndex ; i < table.getAllPiles().get(match.fromPile).size(); i++) {
                cardsToMove.add(table.getAllPiles().get(match.fromPile).get(i));
            }
            table.getAllPiles().get(match.fromPile).removeAll(cardsToMove);

            for (int i = 0 ; i < cardsToMove.size() ; i++){
                cardsToMove.get(i).setBelongToPile(match.complexFinalFoundationPile + 7);
            }
            table.getAllPiles().get(match.toPile).addAll(cardsToMove);
            table.getFundamentPiles().get(match.getComplexFinalFoundationPile()).add(table.getAllPiles().get(match.fromPile).get(table.getAllPiles().get(match.fromPile).size() - 1));
            table.getFundamentPiles().get(match.complexFinalFoundationPile).get(table.getFundamentPiles().get(match.complexFinalFoundationPile).size() - 1).setBelongToPile(match.complexFinalFoundationPile + 7);
            table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);

            //Insert new card
            match.nextPlayerCard.setBelongToPile(match.fromPile);
            table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);
            table.getAllPiles().get(match.fromPile).add(match.nextPlayerCard);
        }

//If there is a match, from foundation to tablou - no next input
        else if(match.match && match.fromPile > 6 && match.fromPile < 11 && match.noNextInput){
        //Copy from foundation to tablou
            table.getAllPiles().get(match.toPile).add(table.getFundamentPiles().get(match.fromPile - 7).get(table.getFundamentPiles().get(match.fromPile - 7).size() - 1));
            table.getAllPiles().get(match.toPile).get(table.getAllPiles().get(match.toPile).size() - 1).setBelongToPile(match.toPile);
        //Delete from foundation
            table.getFundamentPiles().get(match.fromPile - 7).remove(table.getFundamentPiles().get(match.fromPile - 7).size() - 1);
        //Move from player deck to tablou
            table.getAllPiles().get(match.toPile).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
            table.getAllPiles().get(match.toPile).get(table.getAllPiles().get(match.toPile).size() - 1).setBelongToPile(match.toPile);
        //Delete from player deck.
            table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
        }

//If there is a match, from foundation to tablou - next input
        else if(match.match && match.fromPile > 6 && match.fromPile < 11){
        //Copy from foundation to tablou
            table.getAllPiles().get(match.toPile).add(table.getFundamentPiles().get(match.fromPile - 7).get(table.getFundamentPiles().get(match.fromPile - 7).size() - 1));
            table.getAllPiles().get(match.toPile).get(table.getAllPiles().get(match.toPile).size() - 1).setBelongToPile(match.toPile);
        //Delete from foundation
            table.getFundamentPiles().get(match.fromPile - 7).remove(table.getFundamentPiles().get(match.fromPile - 7).size() - 1);
        //Move from player deck to tablou
            table.getAllPiles().get(match.toPile).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
            table.getAllPiles().get(match.toPile).get(table.getAllPiles().get(match.toPile).size() - 1).setBelongToPile(match.toPile);
            //Delete from player deck.
            table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
            table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
        //Add input to stock.
            match.nextPlayerCard.setBelongToPile(11);
            table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);
        }

//IF THERE IS NO MATCH, AND WE NEED 3 NEW CARDS - WITH INPUT
        else if (!match.match && !match.noNextInput) {
//            First check speciual end stock rule
//            if((table.getPlayerDeck_FaceDown().size() + table.getPlayerDeck_FaceUp().size()) == 3){
//                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
//                table.getPlayerDeck_FaceUp().clear();
//                table.getPlayerDeck_FaceUp().addAll(table.getPlayerDeck_FaceDown());
//                table.getPlayerDeck_FaceDown().clear();
//            }
            if (table.getPlayerDeck_FaceDown().size() > 2) {
                for (int i = 0; i < 2; i++) {
                    table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                    table.getPlayerDeck_FaceDown().remove(0);
                }
                match.nextPlayerCard.setBelongToPile(match.fromPile);
                table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);
                table.getPlayerDeck_FaceDown().remove(0);
            }
            else if ((table.getPlayerDeck_FaceUp().size() > 2 && table.getPlayerDeck_FaceDown().size() < 3) ||
                     (table.getPlayerDeck_FaceUp().size() == 2 && table.getPlayerDeck_FaceDown().size() > 0) ||
                     (table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() > 1))
            {
                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
                table.getPlayerDeck_FaceUp().clear();
                for (int i = 0; i < 2 ; i++) {
                    table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                    table.getPlayerDeck_FaceDown().remove(0);
                }
                match.nextPlayerCard.setBelongToPile(match.fromPile);
                table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);
                table.getPlayerDeck_FaceDown().remove(0);
            }
            else if (table.getPlayerDeck_FaceUp().size() == 1 && table.getPlayerDeck_FaceDown().size() == 1){
                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
                table.getPlayerDeck_FaceUp().clear();
                table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                table.getPlayerDeck_FaceDown().remove(0);
                match.nextPlayerCard.setBelongToPile(match.fromPile);
                table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);
                table.getPlayerDeck_FaceDown().remove(0);
            }
            else if (table.getPlayerDeck_FaceUp().size() == 0 && table.getPlayerDeck_FaceDown().size() == 1){
                table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                table.getPlayerDeck_FaceDown().clear();
            }
        }
//If there is no match, and we need 3 new cards - no next input
        else if(/*!match.lastCardInPile && */match.noNextInput && !match.match){
            //First check speciual end stock rule
            if((table.getPlayerDeck_FaceDown().size() + table.getPlayerDeck_FaceUp().size()) == 3){
                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
                table.getPlayerDeck_FaceUp().clear();
                table.getPlayerDeck_FaceUp().addAll(table.getPlayerDeck_FaceDown());
                table.getPlayerDeck_FaceDown().clear();
            }
            if (table.getPlayerDeck_FaceDown().size() > 2) {
                for (int i = 0; i < 3; i++) {
                    table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                    table.getPlayerDeck_FaceDown().remove(0);
                }
            }
            else if(table.getPlayerDeck_FaceDown().size() == 0 && table.getPlayerDeck_FaceUp().size() > 2){
                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
                table.getPlayerDeck_FaceUp().clear();
                for (int i = 0 ; i < 3 ; i++){
                    table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                    table.getPlayerDeck_FaceDown().remove(0);
                }
            }
            else if(table.getPlayerDeck_FaceDown().size() == 2 && table.getPlayerDeck_FaceUp().size() >= 1){
                table.getPlayerDeck_FaceDown().addAll(table.getPlayerDeck_FaceUp());
                table.getPlayerDeck_FaceUp().clear();
                for (int i = 0 ; i < 3 ; i++){
                    table.getPlayerDeck_FaceUp().add(table.getPlayerDeck_FaceDown().get(0));
                    table.getPlayerDeck_FaceDown().remove(0);
                }
            }
            else if(table.getPlayerDeck_FaceDown().size() <= 2 && table.getPlayerDeck_FaceDown().size() >= 1 && table.getPlayerDeck_FaceUp().size() == 0){
                table.getPlayerDeck_FaceUp().addAll(table.getPlayerDeck_FaceDown());
                table.getPlayerDeck_FaceDown().clear();
            }
            else if (table.getPlayerDeck_FaceDown().size() == 0 && table.getPlayerDeck_FaceUp().size() == 0){
                //TODO implement this
            }

        }
//If there is a match and the next input is attached
        else if (match.match && !match.noNextInput) {
            //If match from stock pile
            if (match.fromPile == 11) {
                if (match.toPile < 7) {

                    //
                    table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).setBelongToPile(match.toPile);
                    table.getAllPiles().get(match.toPile).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                    table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);

                    table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
                    match.nextPlayerCard.setBelongToPile(match.fromPile);
                    table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);
                }
                else {
                    table.getFundamentPiles().get(match.toPile - 7).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                    table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
                    table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
                    table.getFundamentPiles().get(match.toPile - 7).get(table.getFundamentPiles().get(match.toPile - 7).size() - 1).setBelongToPile(match.toPile);
                    match.nextPlayerCard.setBelongToPile(match.toPile);
                    table.getPlayerDeck_FaceUp().add(match.nextPlayerCard);

                }
            }
            //If there is a match from the tablou to tablou
            else if(match.fromPile < 7 && match.toPile < 7) {
                //First move the cards from pile to pile
                List<Card> cardsToMove = new ArrayList<>();
                for (int i = 0 ; i < table.getAllPiles().get(match.fromPile).size() ; i++){
                    if(table.getAllPiles().get(match.fromPile).get(i).isFaceUp()) {
                        cardsToMove.add(table.getAllPiles().get(match.fromPile).get(i));
                        table.getAllPiles().get(match.fromPile).remove(i);
                        i--;
                    }
                }
                for (int i = 0 ; i < cardsToMove.size() ; i++){
                    cardsToMove.get(i).setBelongToPile(match.toPile);
                }
                table.getAllPiles().get(match.toPile).addAll(cardsToMove);
                //Flip the next cards
                table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);
                table.getAllPiles().get(match.fromPile).add(match.nextPlayerCard);
                table.getAllPiles().get(match.fromPile).get(table.getAllPiles().get(match.fromPile).size() -1).setBelongToPile(match.fromPile);
            }
            //If there is a match from tablou to foundation
            else if(match.fromPile < 7){
                //Copy from tablou to foundation
//                table.printTable();
//                System.out.println("Match: " + match.fromPile + ", " + match.toPile);
                table.getFundamentPiles().get(match.toPile - 7).add(table.getAllPiles().get(match.fromPile).get(table.getAllPiles().get(match.fromPile).size() - 1));
                table.getFundamentPiles().get(match.toPile - 7).get(table.getFundamentPiles().get(match.toPile - 7).size() - 1).setBelongToPile(match.toPile);
                //Remove the two next cards (we know that we need an input)
                table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);
                table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);
                match.nextPlayerCard.setBelongToPile(match.fromPile);
                table.getAllPiles().get(match.fromPile).add(match.nextPlayerCard);
            }
        }
    //If there is a match and the next input is NOT needed
        else if (match.match) {
            //Match from stock to tablou
            if (match.fromPile == 11 && match.toPile < 7) {
                if(table.getPlayerDeck_FaceUp().size() > 1){
                    table.getAllPiles().get(match.toPile).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                    table.getAllPiles().get(match.toPile).get(table.getAllPiles().get(match.toPile).size() - 1).setBelongToPile(match.toPile);
                    table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
                }
                else if  (table.getPlayerDeck_FaceUp().size() == 1) {
                    table.getPlayerDeck_FaceUp().get(0).setBelongToPile(match.toPile);
                    table.getAllPiles().get(match.toPile).add(table.getPlayerDeck_FaceUp().get(0));
                    table.getPlayerDeck_FaceUp().clear();
                }
            }
            //Match from tablou to foundation
            else if(match.fromPile < 7 && match.toPile > 6){
                table.getFundamentPiles().get(match.toPile - 7).add(table.getAllPiles().get(match.fromPile).get(table.getAllPiles().get(match.fromPile).size() - 1));
                table.getAllPiles().get(match.fromPile).remove(table.getAllPiles().get(match.fromPile).size() - 1);
                table.getFundamentPiles().get(match.toPile - 7).get(table.getFundamentPiles().get(match.toPile - 7).size() - 1).setBelongToPile(match.toPile);
            }
            //Match from tablou to tablou
            else if(match.fromPile < 7){
                for(int i = 0 ; i < table.getAllPiles().get(match.fromPile).size() ; i++) {
                    table.getAllPiles().get(match.fromPile).get(i).setBelongToPile(match.toPile);
                }
                table.getAllPiles().get(match.toPile).addAll(table.getAllPiles().get(match.fromPile));
                table.getAllPiles().get(match.fromPile).clear();
            }
            //Stock to tablou
            //Stock to foundation
            else{
                table.getFundamentPiles().get(match.toPile - 7).add(table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1));
                table.getFundamentPiles().get(match.toPile - 7).get(table.getFundamentPiles().get(match.toPile - 7).size() - 1).setBelongToPile(match.toPile);
                table.getPlayerDeck_FaceUp().remove(table.getPlayerDeck_FaceUp().size() - 1);
            }
        }
    }
    private void checkIfNextCard_InStockPile_IsKnown(Match match) {
        if (table.getPlayerDeck_FaceUp().size() > 0) {
            if (table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1).isFaceUp()) {
                match.setNoNextInput(true);
            }
            else if(table.getPlayerDeck_FaceDown().size() > 2){
                if (table.getPlayerDeck_FaceDown().get(2).isFaceUp()){
                    match.setNoNextInput(true);
                }
            }
        }
    }
}
