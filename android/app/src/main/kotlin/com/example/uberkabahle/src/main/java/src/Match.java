package com.example.uberkabahle.src.main.java.src;

public class Match {

    int fromPile;
    int toPile;
    Card fromCard;

    Card toCard;
    boolean match;

    boolean complex = false;
    int complexIndex;
    int complexFinalFoundationPile;
    public Card nextPlayerCard;

    boolean flipStack;

    boolean solved;



    public Match(int fromPile, int toPile, boolean match, boolean complex, Card fromCard, Card toCard) {
        this.fromPile = fromPile;
        this.toPile = toPile;
        this.match = match;
        this.complex = complex;
        this.fromCard = fromCard;
        this.toCard = toCard;
    }





    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public Card getFromCard() {
        return fromCard;
    }

    public Card getToCard() {
        return toCard;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public int getComplexIndex() {
        return complexIndex;
    }

    public void setComplexIndex(int complexIndex) {
        this.complexIndex = complexIndex;
    }

    public Card getNextPlayerCard() {
        return nextPlayerCard;
    }

    public void setNextPlayerCard(Card nextPlayerCard) {
        this.nextPlayerCard = nextPlayerCard;
    }

    boolean lastCardInPile = false;

    boolean noNextInput = false;

    boolean stockPileIsEmpty = false;

    //boolean nextPlayerDeckCardIsKnown;

    public Match(boolean match) {
        this.match = match;
    }

    public Match(int fromPile, int toPile, boolean match, boolean complex, int complexIndex) {
        this.fromPile = fromPile;
        this.toPile = toPile;
        this.match = match;
        this.complex = complex;
        this.complexIndex = complexIndex;
    }

    public Match(int fromPile, int toPile, boolean match, boolean complex) {
        this.fromPile = fromPile;
        this.toPile = toPile;
        this.match = match;
        this.complex = complex;
    }

    public Match(int fromPile, int toPile, boolean match, boolean complex, int complexIndex, int complexFinalFoundationPile, Card fromCard, Card toCard) {
        this.fromPile = fromPile;
        this.toPile = toPile;
        this.match = match;
        this.complex = complex;
        this.complexIndex = complexIndex;
        this.complexFinalFoundationPile = complexFinalFoundationPile;
        this.fromCard = fromCard;
        this.toCard = toCard;
    }

    public int getFromPile() {
        return fromPile;
    }

    public void setFromPile(int fromPile) {
        this.fromPile = fromPile;
    }

    public int getToPile() {
        return toPile;
    }

    public void setToPile(int toPile) {
        this.toPile = toPile;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public boolean isLastCardInPile() {
        return lastCardInPile;
    }

    public void setLastCardInPile(boolean lastCardInPile) {
        this.lastCardInPile = lastCardInPile;
    }

    public int getComplexFinalFoundationPile() {
        return complexFinalFoundationPile;
    }

    public void setComplexFinalFoundationPile(int complexFinalFoundationPile) {
        this.complexFinalFoundationPile = complexFinalFoundationPile;
    }

    public boolean isNoNextInput() {
        return noNextInput;
    }

    public void setNoNextInput(boolean noNextInput) {
        this.noNextInput = noNextInput;
    }

    @Override
    public String toString() {
        return "Match{" +
                "fromPile=" + fromPile +
                ", toPile=" + toPile +
                ", match=" + match +
                ", complex=" + complex +
                ", complexIndex=" + complexIndex +
                ", complexFinalFoundationPile=" + complexFinalFoundationPile +
                ", nextPlayerCard=" + nextPlayerCard +
                ", lastCardInPile=" + lastCardInPile +
                ", noNextInput=" + noNextInput +
                '}';
    }

    public boolean isStockPileIsEmpty() {
        return stockPileIsEmpty;
    }

    public void setStockPileIsEmpty(boolean stockPileIsEmpty) {
        this.stockPileIsEmpty = stockPileIsEmpty;
    }
}
