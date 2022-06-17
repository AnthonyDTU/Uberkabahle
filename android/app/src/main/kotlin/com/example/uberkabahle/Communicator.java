package com.example.uberkabahle;
public class Communicator {

    Table table;
    Algorithm algorithm;
    Match match;
    Move move;

    StringBuilder retMove = new StringBuilder();

    public void initStartTable(String cardsString){
        table = new TableIO();
        move = new Mover(table);
        algorithm = new Algorithm(table);
        table.initStartTable(cardsString);
    }

    public String getNextMove(){
        match = algorithm.checkForAnyMatch();

        if (match.match){
            retMove.append(String.valueOf(match.getFromPile())).append(",");
            retMove.append(String.valueOf(match.getToPile())).append(",");
            retMove.append(String.valueOf(match.complexIndex)).append(",");
            retMove.append(String.valueOf(match.getComplexFinalFoundationPile())).append(";");
        }
        if (match.noNextInput){
            move.moveCard_OrPile(match);
            getNextMove();
        }


        return String.valueOf(retMove);
    }

    public String updateTable(String cardsString){
        retMove = new StringBuilder();
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
                        if (tmpCard.getValue() != tmpCard1.getValue() || tmpCard.getType() != tmpCard1.getType() &&
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

