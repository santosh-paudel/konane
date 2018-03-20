package com.example.wills.konane;

import android.support.annotation.NonNull;

/**
 * Created by Santosh on 2/28/2018.
 * Cell object is necessary for Best First Search Traversal implmentation
 *
 */

public class Cell implements Comparable<Cell>{
    private int row;
    private int col;
    private String color; //B for black, W for white E for empty
    private int heuristicValue = 0;

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    Cell(@NonNull int r, @NonNull int c, String color){
        this.row = r;
        this.col = c;
        this.color = color;
    }


    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    public String getColor() {
        return color;
    }

    @Override
    public int compareTo(@NonNull Cell o) {
        //we are going to implement max heap so use < instead of >
        if (heuristicValue > o.getHeuristicValue()){
            return -1;
        }
        else if(heuristicValue < o.getHeuristicValue()){
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);

        if (obj == null)
            return false;

        Cell cellObj = (Cell) obj;

        if (this.getRow() == cellObj.getRow() && this.getCol() == cellObj.getCol())
            return true;
        return false;
    }
}
