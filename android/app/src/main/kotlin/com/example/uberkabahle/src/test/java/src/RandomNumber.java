package com.example.uberkabahle.src.test.java.src;

import java.util.Random;

public class RandomNumber {

    //Instance of Random class
    Random rand = new Random(); //instance of random class

    private int upperbound;

//Constructor
    public RandomNumber(int upperbound){this.upperbound = upperbound;}

//Functions
    public int getNewNumber()
    {
        return rand.nextInt(upperbound);
    }

    public void setUpperbound(int upperbound) {
        this.upperbound = upperbound;
        if(this.upperbound < 0){
            this.upperbound = 0;
        }
    }

    public int getUpperbound() {
        return upperbound;
    }
}
