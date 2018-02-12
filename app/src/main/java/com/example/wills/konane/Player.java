/************************************************************
 * Name:  Santosh Paudel                                    *
 * Project:  Project 1 Konane                               *
 * Class:  Artificial Intelligence                          *
 * Date:  February 2, 2018                                  *
 ************************************************************/

package com.example.wills.konane;



public class Player {

    //player id is necessary for UI manipulation
    private int player_id; //all players have unique id's
    private boolean isActive;
    private int score = 0;
    private String stone_color; //the stone color that the player picked
    private Boolean hasWon = false;
    private String name;

    //constructor
    Player(boolean active, String color, String player_name) {

        isActive = active;
        stone_color = color;
        name = player_name;
    }

    //get player's name
    public String getName(){ return name;}

    public void setScore(int playerScore){ score = playerScore; }

    public void incrementScore()
    {
        score++;
    }

    public int getScore()
    {
        return score;
    }

    public String getColor()
    {
        return stone_color;
    }

    public boolean hasValidMove (Board board)
    {
        return board.hasMoves(stone_color);
    }

    public boolean isAnotherMove(Board board, int row, int col){ return board.isFurtherMove(row,col);}

    public void reset()
    {

        score = 0;
        hasWon = false;
    }

    public Boolean hasWon() {return hasWon;}

    public void setWinner(Boolean winningState) {hasWon = winningState;}

    public Boolean isWinner(){ return hasWon;}

    public void setId(int id){player_id = id;}

    public int getId(){return player_id;}

}
