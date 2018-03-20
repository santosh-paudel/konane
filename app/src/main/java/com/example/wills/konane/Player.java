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
    private int score = 0;
    private String stone_color; //the stone color that the player picked
    private Boolean hasWon = false;
    private String name;
    private Boolean isComputer;


    //constructor
    Player(boolean active, String color, String player_name) {

        stone_color = color;
        name = player_name;
    }

    public void setComputer(Boolean computer) {
        isComputer = computer;
    }

    public Boolean isComputer() {
        return isComputer;
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

    /**
     * This function returns if the player has any valid moves based on the color of the stone
     * @param board
     * @return
     */
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

    public void setId(int id){player_id = id;}

    public int getId(){return player_id;}

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);

        Player player_obj = (Player) obj;
        if(this.getColor().equals(player_obj.getColor()))
            return true;

        return false;
    }
}
