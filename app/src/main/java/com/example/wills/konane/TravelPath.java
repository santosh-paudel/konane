package com.example.wills.konane;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by coffee on 3/18/18.
 */

public class TravelPath implements Comparable<TravelPath>{
    private Cell source;
    private Cell destination;
    private int score;
    private int miniMaxValue;

    ArrayList<Cell> path = new ArrayList<>();

    TravelPath(Cell source, Cell destination){
        //add source to path
        this.source = source;
        this.destination = destination;
    }

    public ArrayList<Cell> getPath() {

        //add destination to path
        return path;
    }

    public int getMiniMaxValue() {
        return miniMaxValue;
    }

    public void setMiniMaxValue(int miniMaxValue) {
        this.miniMaxValue = miniMaxValue;
    }

    public void setPath(ArrayList<Cell> path) {
        //score is always 1 less than the total number of cells in our path
        score = path.size()-1;
        this.path = path;
    }

    public Cell getSource() {
        return source;
    }


    public Cell getDestination() {
        return destination;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        super.toString();
        String path = "(";
        if(this.getPath().size() != 0){
            for(Cell cell: this.getPath()){
                path += cell.getRow()+","+cell.getCol()+" -> ";
            }
        }
        path+=")";

        return path;
    }

    @Override
    public int compareTo(@NonNull TravelPath o) {
        if(this.getScore() > o.getScore())
            return 1;
        if(this.getScore() < o.getScore())
            return -1;
        return 0;
    }

    /**This function returns which colored stone is being moved in the current
     * travelpath. It is always the color of the source that matters
     * @return
     */
    public String getCurrentColor(){
        return source.getColor();
    }

}
