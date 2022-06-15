package com.example.uberkabahle;

import java.util.List;

public interface Table {

    void initStartTable(String table);

    Card stringToCardConverter(String card);

    List<List<Card>> getFundamentPiles();

    List<List<Card>> getAllFaceUpCards();

    void printTable();

    Card getTopCard_fromFundamentStack(int i);

    //Card getTopCard_PlayerDeck();

    List<Card> getAllFaceUpCards_fromAPile(int pile);

    List<List<Card>> getAllPiles();

    List<Card> getPile(int pile);

    void setComplexSplitIndex(int cardFromPileIndex);

    public Card getBottomFaceUpCard_FromPile(int pile);

    public List<List<Card>> getPiles();

    public List<Card> getPlayerDeck_FaceUp();

    public void setPlayerDeck_FaceUp(List<Card> playerDeck_FaceUp);

    public List<Card> getPlayerDeck_FaceDown();

    public void setPlayerDeck_FaceDown(List<Card> playerDeck_FaceUp);

    //public int getPlayerDeckIndex();

//    public void setPlayerDeckIndex(int playerDeckIndex);

//    boolean checkIfNextCardIsKnown();



}
