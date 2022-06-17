
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

    public String[] getNextMove(){
        match = algorithm.checkForAnyMatch();

        String[] ret = new String[4];
        ret[0] = "0";
        ret[1] = "0";
        ret[2] = "0";
        ret[3] = "0";

        if (match.match){
            ret[0] = String.valueOf(match.getFromPile());
            ret[1] = String.valueOf(match.getToPile());
            ret[2] = String.valueOf(match.complexIndex);
            ret[3] = String.valueOf(match.getComplexFinalFoundationPile());
        }

        return ret;
    }

    public String updateTable(String cardsString){
        if (match.noNextInput){
            return null;
        }
        else {
            String[] cardSplit = cardsString.split(",");
            Card cardDif;

            for (int i = 0; i < cardSplit.length; i++){
                if (!cardSplit[i].equals("e")){
                    if (i == 7){
                        Card tmpCard = null;
                        if (!table.getPlayerDeck_FaceUp().isEmpty()){
                            tmpCard = table.getPlayerDeck_FaceUp().get(table.getPlayerDeck_FaceUp().size() - 1);
                            System.out.println(tmpCard.getValue());
                        }
                        Card tmpCard1 = table.stringToCardConverter(cardSplit[i]);
                        System.out.println(tmpCard1.getValue());
                        if (table.getPlayerDeck_FaceUp().isEmpty() || (tmpCard.getValue() != tmpCard1.getValue() ||
                                tmpCard.getType() != tmpCard1.getType()) && match.fromPile == tmpCard.getBelongToPile()){
                            cardDif = table.stringToCardConverter(cardSplit[i]);
                            cardDif.setFaceUp(true);
                            match.nextPlayerCard = cardDif;
                            move.moveCard_OrPile(match);
                            table.printTable();
                            return String.valueOf(cardDif.getValue());
                        }
                    }
                    else {
                        Card tmpCard = table.getAllPiles().get(i).get(table.getAllPiles().get(i).size() - 1);
                        Card tmpCard1 = table.stringToCardConverter(cardSplit[i]);
                        if (tmpCard.getValue() != tmpCard1.getValue() && tmpCard.getType() != tmpCard1.getType() &&
                                match.fromPile == tmpCard.getBelongToPile()){
                            cardDif = table.stringToCardConverter(cardSplit[i]);
                            cardDif.setFaceUp(true);
                            match.nextPlayerCard = cardDif;
                            move.moveCard_OrPile(match);
                            table.printTable();
                            return String.valueOf(cardDif.getValue());
                        }
                    }
                }
            }

            return "No board difference";
        }
    }
}

