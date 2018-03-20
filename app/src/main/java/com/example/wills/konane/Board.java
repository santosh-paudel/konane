/************************************************************
 * Name:  Santosh Paudel                                    *
 * Project:  Project 1 Konane                               *
 * Class:  Artificial Intelligence                          *
 * Date:  February 2, 2018                                  *
 ************************************************************/


package com.example.wills.konane;

import android.util.Pair;

import java.util.HashMap;
import java.util.Random;
import java.util.Stack;


public class Board {
    public static String BLACK_STONE = "B";
    public static String WHITE_STONE = "W";
    public static String EMPTY_SPOT = "E";


    //This is used as a default size of the board. Board size can change
    public static final int DEFAULT_BOARD_SIZE = 6;

    //This is an initial value of BOARD_SIZE. This might change if the user has specified board size in the constructor
    public static int BOARD_SIZE = DEFAULT_BOARD_SIZE;

    public static final int MAX_BOARD_SIZE = 10;

    public static String BOARD[][] = new String[MAX_BOARD_SIZE][MAX_BOARD_SIZE];

    //stores the pair of row and value of first movable stone of each color
    //This is done to avoid multiple repetitive calculation
    public static Pair<Integer,Integer> FIRST_MOVABLE_BLACK;
    public static Pair<Integer,Integer> FIRST_MOVABLE_WHITE;


    //Stores the value of first removed black stone
    private static Pair<Integer,Integer> firstRemovedBlack = new Pair<>(0,0);
    private static Pair<Integer,Integer> firstRemovedWhite = new Pair<>(0,0);

    /**
     * Constructor for Board Class. This class creates the board, removes the first black and white stone.
     * And stores the position of first movable black and white stone in FIRST_MOVABLE_BLACK and FIRST_MOVABLE_WHITE pair data structure
     * @param boardSize (Default value is DEFAULT_BOARD_SIZE)
     */
    Board(int boardSize)
    {

        //If boardSize provided is greater than the default (which is the lower limit) and smaller than the max
        //change it by calling setBoardSize(arg) function
        if(boardSize >= DEFAULT_BOARD_SIZE && boardSize <= MAX_BOARD_SIZE) {
            System.out.println("MAX BOARD SIZE "+boardSize);
            setBoardSize(boardSize);
        }
        createBoard();

        //First two stones have to be randomly removed from the board
        removeFirst();

        //When an object of Board class is created, we want the value of FIRST_MOVABLE_BLACK
        //and FIRST_MOVABLE_WHITE_ to be instantiated. Their value is updated in hasMoves function.
        //call that function to initialized the values.

        hasMoves(BLACK_STONE);
        hasMoves(WHITE_STONE);

    }

    public static Pair getFirstRemovedWhite(){
        return firstRemovedWhite;
    }

    public static Pair getFirstRemovedBlack(){
        return firstRemovedBlack;
    }

    public void createBoard(){

        String lastStone = "";
        for(int row = 0; row< BOARD_SIZE; row++){
            for(int col = 0; col< BOARD_SIZE; col++)
            {
                if(row == 0 && col == 0){
                    BOARD[row][col] = WHITE_STONE;
                    lastStone = WHITE_STONE;
                }
                else
                {
                    if(lastStone.equals(WHITE_STONE)){
                        BOARD[row][col] = BLACK_STONE;
                    }
                    else{
                        BOARD[row][col] = WHITE_STONE;
                    }
                    lastStone = BOARD[row][col];
                }
            }

            //now reset the lastStone to have a different color in the row
            if(lastStone.equals(BLACK_STONE))
                lastStone = WHITE_STONE;
            else
                lastStone = BLACK_STONE;

        }

    }

    public void setBoardSize(int size){
        BOARD_SIZE = size;
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
    boolean move(Cell source, Cell dest, Cell[] middle_pos) {

        //Return flag
        boolean swappable = false;

        if(source.getRow() == dest.getRow())
        {
            //The stones should just by one field
            if(Math.abs(source.getCol()-dest.getCol()) == 2 ) {

                //The stones have to be of different colors and the destination should be empty
                if(source.getColor().equals(dest.getColor()) == false && dest.getColor().equals(EMPTY_SPOT)) {


                    //Erase the middle one
                    if(dest.getCol() > source.getCol() )
                    {
                        if (BOARD[source.getRow()][source.getCol()+1].equals(EMPTY_SPOT) == false) {
                            BOARD[source.getRow()][source.getCol() + 1] = EMPTY_SPOT;
                            middle_pos[0] = new Cell(source.getRow(), source.getCol()+1, EMPTY_SPOT);
                            swappable = true;
                        }

                    }
                    else
                    {

                        if(BOARD[source.getRow()][source.getCol()-1].equals(EMPTY_SPOT) == false){
                            BOARD[source.getRow()][source.getCol()-1] = EMPTY_SPOT;
                            middle_pos[0] = new Cell(source.getRow(), source.getCol()-1, EMPTY_SPOT);
                            swappable = true;
                        }
                    }
                    BOARD[dest.getRow()][dest.getCol()] = BOARD[source.getRow()][source.getCol()];
                    BOARD[source.getRow()][source.getCol()] = EMPTY_SPOT;
                }
            }
        }
        else if(source.getCol() == dest.getCol())
        {
            if(Math.abs(source.getRow()-dest.getRow()) ==2) {

                if(source.getColor().equals(dest.getColor()) == false && dest.getColor().equals(EMPTY_SPOT)){
                    //Erase the middle one
                    if(dest.getRow() > source.getRow())
                    {
                        if (BOARD[source.getRow()+1][source.getCol()].equals(EMPTY_SPOT) == false){
                            BOARD[source.getRow()+1][source.getCol()] = EMPTY_SPOT;
                            middle_pos[0] = new Cell(source.getRow()+1, source.getCol(), EMPTY_SPOT);
                            swappable = true;
                        }

                    }
                    else {
                        if (BOARD[source.getRow()-1][source.getCol()].equals(EMPTY_SPOT) == false) {
                            BOARD[source.getRow()-1][source.getCol()] = EMPTY_SPOT;
                            middle_pos[0] = new Cell(source.getRow()-1, source.getCol(), EMPTY_SPOT);
                            swappable = true;
                        }
                    }
                    BOARD[dest.getRow()][dest.getCol()] = BOARD[source.getRow()][source.getCol()];
                    BOARD[source.getRow()][source.getCol()] = EMPTY_SPOT;
                }
            }
        }
        //printTable();
        return swappable;
    }

    /*This function checks if any stone of given stone_color has any moves left. If it can be moved,
      it sets the has_moves flag to true and updates the FIRST_MOVABLE_BLACK or FIRST_MOVABLE_WHITE
      depending on the value of the parameter;

     PARAMETERS: stone_color: "W", "B" or "E" of the stone whose possible moves should be checked

     RETURNS: true if there are any available moves. false otherwise
     */
   public boolean hasMoves(String stone_color)
   {
       boolean has_moves = false;

       for(int i = 0; i< BOARD_SIZE; i++)
       {
           for(int j = 0; j< BOARD_SIZE; j++)
           {
               if (BOARD[i][j].equals(stone_color) && has_moves !=true)
               {
                   has_moves = isFurtherMove(i,j);

                   if(stone_color.equals(BLACK_STONE))
                       FIRST_MOVABLE_BLACK = new Pair <>(i,j);

                   if(stone_color.equals(WHITE_STONE))
                       FIRST_MOVABLE_WHITE = new Pair <>(i,j);
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
       if(col+2 < BOARD_SIZE && BOARD[row][col+2].equals(EMPTY_SPOT) && !BOARD[row][col+1].equals(EMPTY_SPOT)) {
           can_move = true;
       }
       else if(col-2 >= 0 && BOARD[row][col-2].equals(EMPTY_SPOT) && !BOARD[row][col-1].equals(EMPTY_SPOT)) {
           can_move = true;
       }

       else if(row-2 >= 0 && BOARD[row-2][col].equals(EMPTY_SPOT) && !BOARD[row-1][col].equals(EMPTY_SPOT)) {
           can_move = true;
       }
       else if(row+2 < BOARD_SIZE && BOARD[row+2][col].equals(EMPTY_SPOT) && !BOARD[row+1][col].equals(EMPTY_SPOT))
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

        firstRemovedWhite = new Pair<>(white_row,white_col);


        do {
            black_row = rand.nextInt(5);
            black_col = rand.nextInt(5);
        }while(BOARD[black_row][black_col].equals("W"));

        firstRemovedBlack = new Pair<>(black_row,black_col);

        BOARD[white_row][white_col] = EMPTY_SPOT;
        BOARD[black_row][black_col] = EMPTY_SPOT;
    }

    /*This function calculates the maximum distance a stone (node) can travel from a point.
      It uses depth first search to achieve that.

      PARAMETERS: Cell object
                  maxDestination: an Array (of two elements) that should carry back the row and col value of the farthest stone
                  needDest: a flag (true or false) that is used to determine if we want maxDestination array should
                  also contain the farthest distant cell object when the function goes out of scope.

     */
    public int getMaxMoves(final Cell stone, Cell[] maxDesination, Boolean needDest)
    {
        Boolean[][] visited = new  Boolean[6][6];
        for(int i=0; i<6; i++)
            for(int j=0; j<6; j++){
                visited[i][j] = false;
            }

        
        Stack<Cell> s = new Stack <>();
        s.push(stone);

        HashMap<Cell, Integer> dist = new HashMap <>();
        dist.put(stone, 0);


        Cell lastNode = stone;

        /**
         * Temporarily remove the startingCell from the board. This is done so that in dfs
         * if we came in circles in the grid, we can still visit the starting node
         * Example:
         * (Start) ---> Visit
         *    ^          |
         *    |          |
         *    |          v
         *  Visit  <--- Visit
         */
        BOARD[stone.getRow()][stone.getCol()] = EMPTY_SPOT;


        while(!s.empty()) {
            Cell current_cell = s.peek();
            s.pop();

            if (visited[current_cell.getRow()][current_cell.getCol()] == false && current_cell.equals(lastNode) == false) {
                visited[current_cell.getRow()][current_cell.getCol()] = true;
            }

            //get all possible neighbors of the current stone
            Cell west = this.getPositionWest(current_cell);
            Cell south = this.getPositionSouth(current_cell);
            Cell east = this.getPositionEast(current_cell);
            Cell north = this.getPositionNorth(current_cell);

            //If the position is not garbage (i.e = -1) and if the position is not already visited,
            //then push the position to stack

            //we also need to check if each directed is visited (because we don't want to visit the parent we just came from
            if (west != null && visited[west.getRow()][west.getCol()] == false) {
                //System.out.println("West "+west.first+west.second);
                dist.put(west, dist.get(current_cell) + 1);
                s.push(west);
            }
            if (south != null && visited[south.getRow()][south.getCol()] == false) {
                //System.out.println("South "+south.first+south.second);
                dist.put(south, dist.get(current_cell) + 1);
                s.push(south);
            }
            if (east != null && visited[east.getRow()][east.getCol()] == false) {
                //System.out.println("East "+east.first+east.second);
                dist.put(east, dist.get(current_cell) + 1);
                s.push(east);
            }
            if (north != null && visited[north.getRow()][north.getCol()] == false) {
                //System.out.println("North "+north.first+north.second);
                dist.put(north, dist.get(current_cell) + 1);
                s.push(north);
            }

        }

        //restore the origin color of startingCell in the board
        BOARD[stone.getRow()][stone.getCol()] = stone.getColor();
        int max_dist = -1;
        for(Cell key: dist.keySet())
        {
            if(dist.get(key) > max_dist)
            {
                max_dist = dist.get(key);
                if(needDest == true) {
                    maxDesination[0] = key;
                }
            }
        }
        return max_dist;
    }




    public static Cell getPositionEast(Cell cell)
    {
        Cell east;

        //If it's possible to move to the right, get the coordinate to the right
        if(cell.getCol()+2 < BOARD_SIZE && BOARD[cell.getRow()][cell.getCol()+2].equals(EMPTY_SPOT) && !BOARD[cell.getRow()][cell.getCol()+1].equals(EMPTY_SPOT)) {
            east = new Cell(cell.getRow(), cell.getCol()+2, cell.getColor());
            return east;
        }

        return null;
    }

    public static Cell getPositionSouth(Cell cell) {

        Cell south;
        //If it's possible to move to the top, get the coordinate to the top
        if (cell.getRow() + 2 < BOARD_SIZE && BOARD[cell.getRow() + 2][cell.getCol()].equals(EMPTY_SPOT) && !BOARD[cell.getRow() + 1][cell.getCol()].equals(EMPTY_SPOT)) {
            south = new Cell(cell.getRow() + 2, cell.getCol(), cell.getColor());
            return south;
        }
        return null;
    }

    public static Cell getPositionWest(Cell cell)
    {
        Cell west;

        //If it's possible to move to the right, get the coordinates to the right
        if(cell.getCol()-2 >= 0 && BOARD[cell.getRow()][cell.getCol()-2].equals(EMPTY_SPOT) && !BOARD[cell.getRow()][cell.getCol()-1].equals(EMPTY_SPOT)) {
            west = new Cell(cell.getRow(), cell.getCol()-2, cell.getColor());
            return west;
        }
        return null;
    }

    public static Cell getPositionNorth(Cell cell)
    {
        Cell north;
        if(cell.getRow()-2 >= 0 && BOARD[cell.getRow()-2][cell.getCol()].equals(EMPTY_SPOT) && !BOARD[cell.getRow()-1][cell.getCol()].equals(EMPTY_SPOT)) {
            north = new Cell(cell.getRow()-2, cell.getCol(), cell.getColor());
            return north;
        }
        return null;
    }

    //print board for debugging purpose
    public void printTable()
    {
        for(int i=0; i<BOARD_SIZE; i++)
        {
            for(int j=0; j<BOARD_SIZE; j++)
            {
                System.out.print(BOARD[i][j]+" ");
            }
            System.out.println("");
        }
    }


    /*Reset the board to initial state*/
    public void resetBoard()
    {
        for(int i = 0; i< BOARD_SIZE; i++)
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

    /**
     * Restures the current board state to the restorePoint passed in the parameter
     * @param restorePoint
     */
    public void restoreBoard(String[][] restorePoint){
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                BOARD[row][col] = restorePoint[row][col];
            }
        }
    }

    public void makeCopy(String[][] emptyArray){
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                emptyArray[row][col] = BOARD[row][col];
            }
        }
    }
}
