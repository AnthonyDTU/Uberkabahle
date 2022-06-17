
import Table;

import java.util.ArrayList;
import java.util.List;

public class TableIO implements Table {

    private final List<List<Card>> piles = new ArrayList<>();           //List to maintain the seven bottom decks
    private final List<List<Card>> fundamentPiles = new ArrayList<>();        //List to maintain the four top decks.

    private List<Card> playerDeck_FaceUp = new ArrayList<>();
    private List<Card> playerDeck_FaceDown = new ArrayList<>();

    //private final ArrayList<Card> newDeck = new ArrayList<>();            //List used to instantiate all the cards
    private int complexSplitIndex;

    public void setComplexSplitIndex(int complexSplitIndex) {
        this.complexSplitIndex = complexSplitIndex;
    }
    private List<Card> convertStartTableString_ToCards(String table) {

        String[] cards = table.split(",");

        List<Card> toReturn = new ArrayList<>();

        if(cards.length != 7) {
            System.out.printf("The amount of cards when initializing the start table, has to be exactly 7. In this case there is " + cards.length + " cards");
            return toReturn;
        }

        String one = cards[0];
        String two = cards[1];
        String three = cards[2];
        String four = cards[3];
        String five = cards[4];
        String six = cards[5];
        String seven = cards[6];

        toReturn.add(stringToCardConverter(one));
        toReturn.add(stringToCardConverter(two));
        toReturn.add(stringToCardConverter(three));
        toReturn.add(stringToCardConverter(four));
        toReturn.add(stringToCardConverter(five));
        toReturn.add(stringToCardConverter(six));
        toReturn.add(stringToCardConverter(seven));

        for (int i = 0 ; i < 7 ; i++){
            toReturn.get(i).setBelongToPile(i);
        }
//        for (int i = 0 ; i < 7 ; i++){
//            System.out.println(toReturn.get(i));
//        }
        return toReturn;
    }

    public Card stringToCardConverter(String card){

        char type = card.charAt(0);
        int convertType;
        int color;
        int value;

        switch (type){
            case 'K' :
                convertType = 0;
                color = 0;
                break;
            case 'H' :
                convertType = 1;
                color = 1;
                break;
            case 'R' :
                convertType = 2;
                color = 1;
                break;
            case 'S' :
                convertType = 3;
                color = 0;
                break;
            case 'E' :
                convertType = -1;
                color = -1;
                return new Card(convertType, color, -1, false);
            default:
                System.out.println("Error in switch statement during converting String to Card");
                convertType = -1;
                color = -1;
        }

        if(card.length() == 2) {
            if(card.charAt(1)-48 == 0){
                value = 0;
            }
            else {
                value = (card.charAt(1) - 48) - 1;
            }
        }
        else
        {
            value = ((card.charAt(1) - 48)*10 + card.charAt(2)-48) - 1;
        }
    return new Card(convertType, color, value, true);
    }

    @Override
    public List<List<Card>> getFundamentPiles() {
        return fundamentPiles;
    }

    public List<Card> getAllFaceUpCards_fromAPile(int pile) {
        List<Card> faceUpList = new ArrayList<>();

        for (int i = 0 ; i < piles.get(pile).size() ; i++)
        {
            if (piles.get(pile).get(i).isFaceUp())
            {
                faceUpList.add(piles.get(pile).get(i));
            }
        }
        return faceUpList;
    }

    @Override
    public List<List<Card>> getAllFaceUpCards() {


        List<List<Card>> allFaceUpCards = new ArrayList<>();

        for (int i = 0 ; i < piles.size() ; i++)
        {
            allFaceUpCards.add(getAllFaceUpCards_fromAPile(i));
        }
        return allFaceUpCards;
    }

    @Override
    public void printTable() {

        for (int i = 0 ; i < piles.size(); i++)
        {
            System.out.println("Face up cards: " + getAllFaceUpCards_fromAPile(i) + " Cards in pile: " + piles.get(i).size());
        }

        System.out.println("\n");

        for (int i = 0 ; i < 4 ; i++)
        {
            int size = fundamentPiles.get(i).size()-1;
            System.out.println("Fundamental Cards: " + fundamentPiles.get(i) + " Cards in pile: " + size);
        }
        System.out.println("\n");
        if (playerDeck_FaceUp.size() != 0) {
            System.out.println("Player deck top card: " + playerDeck_FaceUp.get(playerDeck_FaceUp.size() - 1) + " Cards in faceUp pile: " + playerDeck_FaceUp.size()  + "  cards in faceDown pile " + playerDeck_FaceDown.size());
        }
        else
        {
            System.out.println("Player deck top card: No cards are turned over   Cards in pile face pile: " + playerDeck_FaceUp.size() + " cards in faceDown pile: " + playerDeck_FaceDown.size());
        }

        System.out.println("\n************************************************************\n");

    }

    @Override
    public void initStartTable(String table) {

//INIT SEVEN PILES
        List<Card> cards = convertStartTableString_ToCards(table);
        for (int i = 0; i < 7; i++) {
            piles.add(new ArrayList<>());
        }

        int j = 7;
        for (int n = 0; n < 7; n++){
            for (int i = 0; i < j; i++) {
                Card card = new Card();
                piles.get(i).add(card);
                piles.get(i).get(n).setBelongToPile(i);
            }
            j--;
        }
        //Turn over the first card in all the 7 piles
        for (int i = 0 ; i < piles.size() ; i++)
        {
            piles.get(i).get(piles.get(i).size() - 1).setType(cards.get(i).getType());
            piles.get(i).get(piles.get(i).size() - 1).setValue(cards.get(i).getValue());
            piles.get(i).get(piles.get(i).size() - 1).setColor(cards.get(i).getColor());
            piles.get(i).get(piles.get(i).size() - 1).setFaceUp(true);
        }

//INIT TOP PILES
        for (int i = 0; i < 4; i++) {
            fundamentPiles.add(i, new ArrayList<>());
            fundamentPiles.get(i).add(new Card(-1));
            fundamentPiles.get(i).get(0).setType(i);
            fundamentPiles.get(i).get(0).setBelongToPile(i+7);
        }
//INIT PLAYER DECK
        for(int i = 0 ; i < 24 ; i++){
            Card cardToAdd = new Card(-1, -1, -1, false, 11);
            playerDeck_FaceDown.add(cardToAdd);
        }
    }

    public Card getTopCard_fromFundamentStack(int stack) {
        return fundamentPiles.get(stack).get(fundamentPiles.get(stack).size() - 1);
    }



    public List<List<Card>> getAllPiles()
    {
        return piles;
    }

    @Override
    public List<Card> getPile(int i) {
        return piles.get(i);
    }

    public Card getBottomFaceUpCard_FromPile(int pile) {
        if(!piles.get(pile).isEmpty())
        {
            for (int i = 0 ; i < piles.get(pile).size() ; i++)
            {
                if(piles.get(pile).get(i).isFaceUp())
                {
                    return piles.get(pile).get(i);
                }
            }
            return piles.get(pile).get(0);
        }
        else return new Card(-10, -10, -12, false, pile);       //TODO working on better king decision in this area
    }

    public List<List<Card>> getPiles() {
        return piles;
    }

    public List<Card> getPlayerDeck_FaceUp() {
        return playerDeck_FaceUp;
    }

    public void setPlayerDeck_FaceUp(List<Card> playerDeck_FaceUp) {
        this.playerDeck_FaceUp = playerDeck_FaceUp;
    }


    public List<Card> getPlayerDeck_FaceDown() {
        return playerDeck_FaceDown;
    }

    public void setPlayerDeck_FaceDown(List<Card> playerDeck_FaceDown) {
        this.playerDeck_FaceDown = playerDeck_FaceDown;
    }



}
