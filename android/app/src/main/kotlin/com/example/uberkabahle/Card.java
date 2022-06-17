
package com.example.uberkabahle;

public class Card {

    public enum Colors {BLACK, RED};
    public enum Types {SPADE, CLUB, DIAMOND, HEART}

    private int type;
    private int color = -1;
    private int value = -1;
    boolean faceUp = false;

    private int belongToPile; //Which pile does the card belongs to.

// Constructors


    public Card() {
    }

    public Card(int type, int value)
    {
        /*
        * Check if the card is either a spade or a club, and assign the color to black if so
        * */
        this.value = value;
        this.type = type;

        if(Types.CLUB.ordinal() == type || Types.SPADE.ordinal() == type)
            color = Colors.BLACK.ordinal();
        else
            color = Colors.RED.ordinal();
    }

    public Card(int type, int color, int value, boolean faceUp, int belongToPile) {
        this.type = type;
        this.color = color;
        this.value = value;
        this.faceUp = faceUp;
        this.belongToPile = belongToPile;
    }

    public Card(int type, int color, int value, boolean faceUp) {
        this.type = type;
        this.color = color;
        this.value = value;
        this.faceUp = faceUp;
    }

    public Card(int value)
    {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public int getBelongToPile() {
        return belongToPile;
    }

    public void setBelongToPile(int belongToPile) {
        this.belongToPile = belongToPile;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    @Override
    public String toString() {
        return "Card{" +
                "type=" + type +
                ", color=" + color +
                ", value=" + value +
                ", faceUp=" + faceUp +
                ", belongToPile=" + belongToPile +
                '}';
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
