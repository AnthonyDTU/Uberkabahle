package src;

import Move;
import Table;

public class Communicator {

    Table table;
    Algorithm algorithm;
    Match match;
    Move move;

    public void initStartTable(String cardsString){
        table = new TableIO();
        move = new Mover(table);
        algorithm = new Algorithm(table);
        table.initStartTable(cardsString);
    }

    public int[] getNextMove(){
        match = algorithm.checkForAnyMatch();

        int[] ret = new int[3];

        if (match.match){
            move.moveCard_OrPile(match);
            ret[0] = match.fromPile;
            ret[1] = match.toPile;
            ret[2] = match.complexIndex;
        }

        return ret;
    }

    public String updateTable(String cardsString){
        String[] cardSplit = cardsString.split(",");
        Card cardDif;

        for (int i = 0; i < cardSplit.length; i++){
            if (!cardSplit[i].equals("e")){
                if (i == 7){
                    Card tmpCard = null;
                    if (!table.getPlayerDeck_FaceUp().isEmpty()){
                        tmpCard = table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1);
                    }
                    Card tmpCard1 = table.stringToCardConverter(cardSplit[i]);
                    if (table.getPlayerDeck_FaceUp().isEmpty() || (tmpCard.getValue() != tmpCard1.getValue() &&
                            tmpCard.getType() != tmpCard1.getType())){
                        cardDif = table.stringToCardConverter(cardSplit[i]);
                        match.nextPlayerCard = cardDif;
                        move.insertNextCardFromInput(match);
                        table.printTable();
                        return String.valueOf(cardDif.getValue());
                    }
                }
                else {
                    Card tmpCard = table.getAllPiles().get(i).get(table.getAllPiles().get(i).size() - 1);
                    Card tmpCard1 = table.stringToCardConverter(cardSplit[i]);
                    if (tmpCard.getValue() != tmpCard1.getValue() && tmpCard.getType() != tmpCard1.getType()){
                        cardDif = table.stringToCardConverter(cardSplit[i]);
                        match.nextPlayerCard = cardDif;
                        move.insertNextCardFromInput(match);
                        table.printTable();
                        return String.valueOf(cardDif.getValue());
                    }
                }
            }
        }

        return "No board difference";
    }

}
