/************************************************************
 * Name:  Santosh Paudel                                    *
 * Project:  Project 1 Konane                               *
 * Class:  Artificial Intelligence                          *
 * Date:  February 2, 2018                                  *
 ************************************************************/


package com.example.wills.konane;

import android.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Board {
    public static String BLACK_STONE = "B";
    public static String WHITE_STONE = "W";
    public static String EMPTY_SPOT = "E";
    public static String BOARD[][] = { {"B","W","B","W","B","W"},
            {"W","B","W","B","W","B"},
            {"B","W","B","W","B","W"},
            {"W","B","W","B","W","B"},
            {"B","W","B","W","B","W"},
            {"W","B","W","B","W","B"}};
    public static int NUM_BLOCKS = 6;

    //constructor for Board class
    Board()
    {
        //First two stones have to be randomly removed from the board
        removeFirst();
    }

    /*
    This function checks if the move from source to destination is valid. If it is valid, it should return true (false otherwise)
    Paramters:
    source_row: row value of the stone that should be moved
    source_col: column value of the stones that should be moved
    dest_row: row value of the position where the source stone should be moved
    dest_col: column value of the position where the stone should be moved
    dest_color: color of the destination position ("E" for empty "W" for white "B" for black)
    middle_pos: an empty array passed by the calling function. This array will carry back the position of the stone
                between source and destination

    Pseudocode:
        if source is in same row as destination:
            move source to source_col+2 or source_col-2 (depending on position)
            swap source and destination
            empty the middle position
            store middle_row and middle_col to middle_pos array
            swappable = true

    if source is in same column as destination:
        move source to source_row +2 or source_row -2 (depending on position)
        swap source and destination
        empty the middle position
        store middle_row and middle_col to middle_pos array
        swappable = false


     return swappable

     */
    boolean move(Integer source_row, Integer source_col, Integer dest_row, Integer dest_col, String source_color, String dest_color, int[] middle_pos) {

        //Return flag
        boolean swappable = false;

        if(source_row == dest_row)
        {
            //The stones should just by one field
            if(Math.abs(source_col-dest_col) == 2 ) {

                //The stones have to be of different colors and the destination should be empty

                if(source_color.equals(dest_color) == false && dest_color.equals(EMPTY_SPOT)) {


                    //Erase the middle one

                    if(dest_col > source_col )
                    {
                        if (BOARD[source_row][source_col+1].equals(EMPTY_SPOT) == false) {
                            BOARD[source_row][source_col + 1] = EMPTY_SPOT;
                            middle_pos[0] = source_row;
                            middle_pos[1] = source_col+1;
                            swappable = true;
                        }

                    }
                    else
                    {

                        if(BOARD[source_row][source_col-1].equals(EMPTY_SPOT) == false){
                            BOARD[source_row][source_col-1] = EMPTY_SPOT;
                            middle_pos[0] = source_row;
                            middle_pos[1] = source_col-1;
                            swappable = true;
                        }
                    }
                    BOARD[dest_row][dest_col] = BOARD[source_row][source_col];
                    BOARD[source_row][source_col] = EMPTY_SPOT;
                }
            }
        }
        else if(source_col == dest_col)
        {
            if(Math.abs(source_row-dest_row) ==2) {

                if(source_color.equals(dest_color) == false && dest_color.equals(EMPTY_SPOT)){
                    //Erase the middle one
                    if(dest_row > source_row)
                    {
                        if (BOARD[source_row+1][source_col].equals(EMPTY_SPOT) == false){
                            BOARD[source_row+1][source_col] = EMPTY_SPOT;
                            middle_pos[0] = source_row+1;
                            middle_pos[1] = source_col;
                            swappable = true;
                        }

                    }
                    else {
                        if (BOARD[source_row-1][source_col].equals(EMPTY_SPOT) == false) {
                            BOARD[source_row-1][source_col] = EMPTY_SPOT;
                            middle_pos[0] = source_row-1;
                            middle_pos[1] = source_col;
                            swappable = true;
                        }
                    }
                    BOARD[dest_row][dest_col] = BOARD[source_row][source_col];
                    BOARD[source_row][source_col] = EMPTY_SPOT;
                }


            }
        }

        //printTable();
        return swappable;
    }

    /*This function checks if any stone of given stone_color has any moves left

    parameter: stone_color: "W", "B" or "E" of the stone whose possible moves should be checked

    for each_row in NUM_BLOCKS:
        for each column in NUM_BLOCKS:
            If BOARD[row][col] has stone_color:
                if isFurther(row,col) == true:
                    has_moves = true
                    exit out of both loop

     return hasmove


     */
   public boolean hasMoves(String stone_color)
   {
       boolean has_moves = false;

       for(int i=0; i<NUM_BLOCKS; i++)
       {
           for(int j=0; j<NUM_BLOCKS; j++)
           {
               if (BOARD[i][j].equals(stone_color) && has_moves !=true)
               {
                   has_moves = isFurtherMove(i,j);
               }
           }

           if(has_moves == true)
               break;
       }
       return has_moves;
   }

   /*this checks, if the stone in row and col and move further
    Parameters:
        row = row value of the stone whose move should be checked
        col = column value of the stone whose move should be checked

    Pseudocode:
        if stone can move right:
            cam_move = true
        else if stone can move left:
            cam_move = true
        else if stone can move up:
            can_move = true
        else if stone can move down:
            can_move = true



        return canmove

    */
   public boolean isFurtherMove(int row, int col)
   {
       boolean can_move = false;
       //Check if it can move right
       if(col+2 < NUM_BLOCKS && BOARD[row][col+2].equals(EMPTY_SPOT) && !BOARD[row][col+1].equals(EMPTY_SPOT)) {
           can_move = true;
       }
       else if(col-2 >= 0 && BOARD[row][col-2].equals(EMPTY_SPOT) && !BOARD[row][col-1].equals(EMPTY_SPOT)) {
           can_move = true;
       }

       else if(row-2 >= 0 && BOARD[row-2][col].equals(EMPTY_SPOT) && !BOARD[row-1][col].equals(EMPTY_SPOT)) {
           can_move = true;
       }
       else if(row+2 < NUM_BLOCKS && BOARD[row+2][col].equals(EMPTY_SPOT) && !BOARD[row+1][col].equals(EMPTY_SPOT))
           can_move = true;

       return  can_move;
   }

    /*This function removes the first white and first black stone from the board randomly*/
    private void removeFirst()
    {
        int white_row, white_col;
        int black_row, black_col;

        Random rand = new Random();
        do {
            white_row = rand.nextInt(5);
            white_col = rand.nextInt(5);
        }while(BOARD[white_row][white_col].equals("B"));



        do {
            black_row = rand.nextInt(5);
            black_col = rand.nextInt(5);
        }while(BOARD[black_row][black_col].equals("W"));

        BOARD[white_row][white_col] = EMPTY_SPOT;
        BOARD[black_row][black_col] = EMPTY_SPOT;

    }

    public void getPossibleMoves(int stoneRow, int stoneCol, Pair<Integer, Integer>[] movesContainer)
    {
        //check if it's possible to move in column
    }


    //print board for debugging purpose
    public void printTable()
    {
        for(int i=0; i<6; i++)
        {
            for(int j=0; j<6; j++)
            {
                System.out.print(BOARD[i][j]+" ");
            }
            System.out.println("");
        }
    }


    /*Reset the board to initial state*/
    public void resetBoard()
    {
        for(int i=0; i<NUM_BLOCKS; i++)
        {

            if(i%2 == 0) {

                String[] arr = {"B","W","B","W","B","W"};
                BOARD[i] = arr;
            }
            else {
                String[] arr = {"W", "B", "W", "B", "W", "B"} ;
                BOARD[i] = arr;
            }
        }

        removeFirst();
    }
}
