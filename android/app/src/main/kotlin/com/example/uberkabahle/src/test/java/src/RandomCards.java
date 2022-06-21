/*package com.example.uberkabahle.src.test.java.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomCards {

    public List<String> cards = new ArrayList<>();

    private void createListOCards(){

        String type;
        String value;
        String result = "";

        for (int i = 0 ; i < 4 ;  i++){
            for (int j = 0 ; j < 13 ; j++){
                int k = j;
                if(k > 0){k++;}
                switch (i)
                {
                    case 0 :
                        type = "K";
                        value = String.valueOf(k);
                        result = type+value;
                        break;
                    case 1 :
                        type = "H";
                        value = String.valueOf(k);
                        result = type+value;;
                        break;
                    case 2 :
                        type = "R";
                        value = String.valueOf(k);
                        result = type+value;
                        break;
                    case 3 :
                        type = "S";
                        value = String.valueOf(k);
                        result = type+value;;
                        break;
                }
               cards.add(result);
            }
        }
        Collections.shuffle(cards);
        System.out.println("");
    }

    public RandomCards(){
        createListOCards();
    }

    public String getStartTableString(){
        String result = "";
        for (int i = 0 ; i < 6 ; i++){
            result = result.concat(cards.get(0) + ",");
            cards.remove(0);
        }
        result = result.concat(cards.get(0));
        cards.remove(0);
        return result;
    }

    public String getNextCard(){
        if(cards.isEmpty()){
            return null;
        }
        String getCard = cards.get(0);
        cards.remove(0);
        return getCard;
    }
}*/
