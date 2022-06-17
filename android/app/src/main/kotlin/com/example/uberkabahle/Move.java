
package com.example.uberkabahle;
public interface Move {

    void turnOverNewCard_PlayerDeck(Card newCard);
    void moveComplexPile(int fromPile, int fromIndex, int toPile);
    void moveCard_OrPile(Match match);

    //void insertNextCardFromInput(Match match);

    public boolean getIsStockPileIsEmpty();

}
