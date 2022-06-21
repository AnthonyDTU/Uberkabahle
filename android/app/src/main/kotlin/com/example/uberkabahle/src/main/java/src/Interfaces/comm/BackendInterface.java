package com.example.uberkabahle.src.main.java.src.Interfaces.comm;

public interface BackendInterface {
    /**
     * @param cardsString 7 cards comma (,) separated from the start table of the 7 top cards in tableau.
     */
    public void initStartTable(String cardsString);

    /**
     * @return comma (,) separated string, where first two elements represent fromPile and toPile respectively. Third and fourth element represent a complex split move,
     * where the third represents the split index of a given fromPile and the fourth represents destination foundation pile. If multiple moves are detected with no required input, they will be separated with (;)
     */
    public String getNextMove();

    /**
     * @param cardsString 8 cards comma (,) separated, e.g. (H7, K9, etc..). First seven represent tableau, and the 8'th represent talon. If no card is detected in a given pile, 'e' should be passed
     * @return Newly turned card is returned - if no difference is found, null is returned
     */
    public String updateTable(String cardsString);

}
