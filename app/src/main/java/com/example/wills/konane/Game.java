package com.example.wills.konane;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;


/**
 * Created by Santosh on 2/10/2018.
 */

public class Game {
    private static String DEFAULT_SERILIZATION_FILE_NAME = "serialization.txt";
    private Board board;
    public Player player1 = new Player(true, board.BLACK_STONE, "Black");
    public Player player2 = new Player(false, board.WHITE_STONE, "White");
    public Player activePlayer = player1;
    private Boolean game_over = false;

    public ArrayList<TravelPath> minMaxRootValues = new ArrayList<>();

    Game(int boardSize){

        board = new Board(boardSize);
    }

    Board getBoardObj(){
        return board;
    }

    /*This function checks if the current player is legal
      Returns True if legal, false otherwise
    */
    public boolean isLegalPlayer(String current_stone_color) {
        if (current_stone_color.equals(activePlayer.getColor())) {
            return true;
        }
        return false;
    }


    /*This function returns true if the game is Over else false*/
    public boolean isGameOver() {
        return game_over;
    }


    /*This function switches player from active player to inactive player in this order:

        Does second player have any moves?
        No: Does current active player has moves ?
            YES: Keep

     */
    public Boolean switchPlayer() {
        Boolean player_switched = true;

        if (activePlayer == player1) {
            if (player2.hasValidMove(board) == false) {
                if (player1.hasValidMove(board) == false)
                    decideWinner();
                else
                    player_switched = false;

            } else {

                activePlayer = player2;
            }
        } else if (activePlayer == player2) {
            if (player1.hasValidMove(board) == false) {
                if (player2.hasValidMove(board) == false)
                    decideWinner();
                else
                    player_switched = false;
            } else {

                activePlayer = player1;
            }
        }

        return player_switched;
    }


    public void setUnvisited(Boolean[][] visited)
    {
        for(int i = 0; i<board.BOARD_SIZE; i++)
            for (int j = 0; j<board.BOARD_SIZE; j++)
                visited[i][j] = false;
    }


    /*This function resets board, player1 and player2 to their initial state.
      It sets player1 as currently active player
     */
    public void resetGame()
    {
        game_over = false;
        board.resetBoard();
        player1.reset();
        player2.reset();
        activePlayer = player1;
    }

    /*
        This function calls move function from board class. If the stone from source to destination is swappable, move (from board)
        returns true(false otherwise).

        RETURNS: true, if the stones are swappable. false otherwise
     */
    public Boolean moveStone(Cell source, Cell dest, Cell middle_positions[])
    {
        if (board.move(source, dest, middle_positions))
            return true;
        else
            return false;
    }


    /**
     * This function returns an arrayList of all the possible moves of stones of given stoneColor
     * @param stoneColor
     * @return
     */
    public ArrayList<TravelPath> getAllPossibleMoves(String stoneColor){
        
        ArrayList<TravelPath> travelPathArrayList = new ArrayList<>();

        //This arrayList will be used to store all possible moves of stone of stoneColor
        //in any particular game state. This array list is limited to the scope of this function
        ArrayList<Pair<Cell,Cell>> possibleMoves = new ArrayList<>();
        
        //We want to be able to backtrack and get the pats to all the children nodes after dfs is done
        //Using hashmap lets us do that by chaining as such: parentOf(child) = parent
        //                                                   parentOf(parent) = grandParent
        //                                                     ... (continue untill the root of the tree is found)
        HashMap<Cell,Cell> parentCells = new HashMap<>();

        for(int row=0; row<board.BOARD_SIZE; row++){
            for(int col=0; col<board.BOARD_SIZE; col++){

                if (board.BOARD[row][col].equals(stoneColor)){
                    Cell cell = new Cell(row, col, stoneColor);
                    dfsSearch(cell, possibleMoves, parentCells);

                    getPath(parentCells, possibleMoves, travelPathArrayList);
                    //clear parent cells hashmap
                    parentCells.clear();
                    possibleMoves.clear();
                }

            }
        }
        
        return travelPathArrayList;

    }

    /**
     * This function gets the path of traversal between any two points in the grid. parentCell stores pair(source_cell, destination_cell)
     * and possiblePaths stores the path of traversal (child as key and parent as value). Using parentCells and possiblePaths are can
     * retrieve the actual path of traversal
     * @param parentCells Contains hashmap that crudely represents path of traversal from parent to children nodes
     * @param possiblePaths Contains source and destination pair list
     * @param travelPathArrayList
     */
    public void getPath(HashMap<Cell,Cell> parentCells, ArrayList<Pair<Cell,Cell>> possiblePaths, ArrayList<TravelPath> travelPathArrayList) {

        //This arraylist stores the retrieved path from parent to children
        ArrayList<ArrayList<Cell>> path = new ArrayList<>();

        //backtrack in the hashmap to find the parent node. This process lets us find the path of traversal
        for (int i = 0; i < possiblePaths.size(); i++) {

            //A single hashmap may have multiple paths. To stores each of these paths, we need an array
            ArrayList<Cell> partialPath = new ArrayList<>();
            //get the destination of the last element from possiblePaths. That's where we'll getting the path from
            Cell cell = possiblePaths.get(i).second;
            while (parentCells.containsKey(cell)) {
                Cell parent = parentCells.get(cell);
                //System.out.print("("+cell.getRow()+","+cell.getCol()+")"+" ---> ");
                partialPath.add(cell);
                cell = parent;
            }
            //Parent is left out from the above loop
            partialPath.add(cell);

            //Path is stored in reversed order as we are backtracking from the child to parent
            Collections.reverse(partialPath);
            //add each of those individual path to the array
            path.add(partialPath);
        }


        //At this point, we have path in the following format: [[Cell(0,2),Cell(0,4)],[Cell(0,2),Cell(0,4),Cell(0,6)]...]
        for (int i = 0; i < path.size(); i++) {

            //we need to add each partial path to our travelPath
            ArrayList<Cell> partialPath = path.get(i);

            //source
            Cell source = partialPath.get(0);

            //destination
            Cell dest = partialPath.get(partialPath.size() - 1);

            TravelPath travelPath = new TravelPath(source, dest);
            travelPath.setPath(partialPath);
            travelPathArrayList.add(travelPath);

        }
    }


    public void dfsSearch(Cell startingCell, ArrayList<Pair<Cell,Cell>> possibleMoves, HashMap<Cell,Cell> parentOf) {

        Boolean[][] visited = new Boolean[board.BOARD_SIZE][board.BOARD_SIZE];
        setUnvisited(visited);

        Stack <Cell> s = new Stack <>();
        s.push(startingCell);
        
        Cell lastNode = startingCell;

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
          board.BOARD[startingCell.getRow()][startingCell.getCol()] = board.EMPTY_SPOT;


        while (s.empty() == false) {
            //an empty Pair data structure (that contains another pair of integers) to contain north, east, south, west moves
            Pair <Cell, Cell> next_move = null;

            Cell current_cell = s.peek();
            s.pop();

            //If the current Node has not already been visited and
            //If current Node is the last node we visited (we don't immediately want to mark our starting node as visited because we might eventually
                                                          //come back to it through dfs
            //mark it as visited
            if (visited[current_cell.getRow()][current_cell.getCol()] == false && current_cell.equals(lastNode) == false) {
                visited[current_cell.getRow()][current_cell.getCol()] = true;

                next_move = new Pair <>(startingCell,current_cell);
                possibleMoves.add(next_move);

            }


            //get all possible neighbors of the current stone
            Cell west = board.getPositionWest(current_cell);
            Cell south = board.getPositionSouth(current_cell);
            Cell east = board.getPositionEast(current_cell);
            Cell north = board.getPositionNorth(current_cell);

            //If the position is not garbage (i.e = -1) and if the position is not already visited,
            //then push the position to stack
            if (west != null && visited[west.getRow()][west.getCol()] == false) {

                //We don't want to re-visit the parent node again from the child node
                if(!isChild(current_cell, west, parentOf)) {
                    s.add(west);
                    parentOf.put(west,current_cell);
                }

            }
            if (south != null && visited[south.getRow()][south.getCol()] == false) {
                if(!isChild(current_cell, south, parentOf)) {
                    s.add(south);
                    parentOf.put(south,current_cell);
                }
            }
            if (east != null && visited[east.getRow()][east.getCol()] == false) {
                if(!isChild(current_cell, east, parentOf)) {
                    s.add(east);
                    parentOf.put(east,current_cell);
                }
            }
            if (north != null && visited[north.getRow()][north.getCol()] == false) {
                if(!isChild(current_cell, north, parentOf)) {
                    s.add(north);
                    parentOf.put(north,current_cell);
                }
            }

            lastNode = current_cell;
        }

        //restore the origin color of startingCell in the board
        board.BOARD[startingCell.getRow()][startingCell.getCol()] = startingCell.getColor();
    }

    boolean isChild(Cell childNode, Cell parentNode,  HashMap<Cell,Cell> parentOf){
        try{
            if(parentOf.get(childNode).equals(parentNode))
                return true;
        }catch (NullPointerException e){

        }
        return false;
    }



    void decideWinner()
    {
        if(player1.getScore() > player2.getScore())
            player1.setWinner(true);
        else if(player1.getScore() < player2.getScore())
            player2.setWinner(true);

        game_over = true;
    }

    /*This function stores the current game state in a file in internal storage of android device
      It stores the state in the following format:
          Black: 6
          White: 4
          board size: 6
          Board:
          B W B W B W
          W B O B O B
          B W O W B W
          O B O O O O
          B W B O O W
          W B W O O B
          Next Player: White

    RETURNS: true, if the file is successfully read and the game state is stored. false otherwise

     */
    public Boolean saveGame(Context applicationContext)
    {
        Boolean successful_save;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File fileOut = new File(dir, DEFAULT_SERILIZATION_FILE_NAME);

            FileOutputStream fileOutputStream = new FileOutputStream(fileOut);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            //write the score of black stone
            bufferedWriter.write(player1.getName()+": "+player1.getScore());
            bufferedWriter.newLine();

            //write the score of white stone
            bufferedWriter.write(player2.getName()+": "+player2.getScore());
            bufferedWriter.newLine();

            bufferedWriter.write("board size: "+board.BOARD_SIZE);
            bufferedWriter.newLine();

            bufferedWriter.write("Board:");
            bufferedWriter.newLine();

            for(int i = 0; i< board.BOARD_SIZE; i++)
            {
                String buffer = "";
                for(int j = 0; j<board.BOARD_SIZE; j++)
                {
                    buffer += board.BOARD[i][j]+" ";
                }
                bufferedWriter.write(buffer);
                bufferedWriter.newLine();
            }

            String nextPlayer = "Next Player: "+ activePlayer.getName();
            bufferedWriter.write(nextPlayer);
            bufferedWriter.newLine();

            if(player1.isComputer() == true){
                bufferedWriter.write("Human: "+player2.getName());
            }else
                bufferedWriter.write("Human: "+player1.getName());




            bufferedWriter.close();
            successful_save = true;


        } catch (Exception e) {
            System.out.println("#########");
            System.out.println("####  "+e);
            successful_save = false;
        }

        return  successful_save;
    }

    public Boolean loadGameFromFile(Context context, Uri uri) {
        InputStream inputStream = null;
        Boolean successful_load = false;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));


            String buffer;
            int line_counter = 0;
            int rowNum = 0;
            while((buffer = reader.readLine()) != null)
            {

                if(buffer == null)
                    break;

                if (line_counter == 0 || line_counter == 1)
                {
                    if(line_counter == 0)
                        player1.setScore(Integer.parseInt(buffer.split(": ")[1]));
                    else if(line_counter == 1)
                        player2.setScore(Integer.parseInt(buffer.split(": ")[1]));
                }
                else if(line_counter == 2){
                    board.BOARD_SIZE = Integer.parseInt(buffer.split(": ")[1]);
                }
                //ignore line_counter 3 (we don't have anything to do with Board: )
                else if(line_counter > 3 && line_counter <= 3+board.BOARD_SIZE)
                {
                    String[] row = buffer.split(" ");
                    for(int i=0; i<board.BOARD_SIZE; i++)
                    {
                        if (row[i].equals("O"))
                            row[i] = "E";
                        board.BOARD[rowNum][i] = row[i];
                    }
                    rowNum++;
                }

                if(line_counter == 3+board.BOARD_SIZE+1)
                {
                    String playerName = buffer.split(": ")[1].trim();
                    if(playerName.equals("Black")){
                        activePlayer = player1;}
                    else
                        activePlayer = player2;
                }
                if(line_counter == 3+board.BOARD_SIZE+1){
                    String human = buffer.split(":")[1].trim();
                    if(human.equals(player1.getName())){
                        player2.setComputer(true);
                    }else
                        player1.setComputer(true);
                }
                line_counter++;
            }
            successful_load = true;

            inputStream.close();
        } catch (Exception e) {
            System.out.println(e.toString()+ "******");
            e.printStackTrace();
        }
        return successful_load;
    }

    /*This function returns the winner of the game. This function is used for minimax algorithm*/
    public Player getWinner(){

        if(player1.hasValidMove(board) == false && player2.hasValidMove(board) == false){

            if(player1.getScore() > player2.getScore())
                return player1;
            else if(player2.getScore() > player1.getScore())
                return player2;
        }
        return null;
    }

    public Player getComputerPlayer(){

        if(player1.isComputer() == true)
            return player1;

        return player2;

    }

    public void callMinimax(int ply,int depth, Player currentPlayer){
        minMaxRootValues.clear();
        int m = minimax(ply,depth, currentPlayer);


    }

    public TravelPath getBestMove(){
        TravelPath t = minMaxRootValues.get(0);
        int max_score = t.getScore();
        for(TravelPath tp: minMaxRootValues){
            if(tp.getScore() > max_score){
                t = tp;
                max_score = t.getScore();
            }
        }

        return t;
    }

    //This min max favors player1. needs changes. read comment
    public int minimax(int ply,int depth,  Player currentPlayer){

        //System.out.println("Depth "+depth);
        //board.printTable();

        //if current player is winning return 1
        if (getWinner() != null && getWinner().equals(currentPlayer)) {
            //System.out.println(currentPlayer.getName()+"("+currentPlayer.getColor()+") " +" return 1");
            return 1;
        }
        //if current player is not winning return -1
        if (getWinner() != null && getWinner().equals(currentPlayer) == false) {
            //System.out.println(currentPlayer.getName()+"("+currentPlayer.getColor()+") " +" return -1");
            return -1;
        }

        if (depth > ply){
            int minHeuristic = getMinMaxHeuristic();
            //System.out.println("Min Heuristic "+minHeuristic);
            return minHeuristic;
        }


        ArrayList<TravelPath> availableMoves = getAllPossibleMoves(currentPlayer.getColor());
        if (availableMoves.isEmpty())
        {
            //System.out.println("No available moves");
            return 0;
        }


        ArrayList<Integer> scores = new ArrayList<>();

        //Each recursion is a new change in game state. After coming
        //back from the recursion, we need to restore the game state's to it's
        //original state. Hence, we need to copy the board
        String[][] restorePointBoard = new String[board.BOARD_SIZE][board.BOARD_SIZE];

        //I'm not sure why this condition has to be checked. This seems to work after stepping through the debugger several hundred times
        //Any answers will be awarded a million dollars
        if(ply >= depth) {
            //copy board to restorePointBoard array
            board.makeCopy(restorePointBoard);
            //System.out.println("Saving restore point: ");
        }
        board.printTable();

        for (int i = 0; i < availableMoves.size(); i++) {

            TravelPath point = availableMoves.get(i);
            //System.out.println("Point: "+point);

            //change this line to currentPlayer.isComputer() later
            if (currentPlayer.equals(player2)) {
                //System.out.println("Moving player 2 "+point);
                move(point);

                int currentScore = minimax(ply,depth+1, player1);
                //System.out.println("current Score player 1: "+currentScore);
                scores.add(currentScore);
                //System.out.println("Scores: "+scores.toString());

                if(depth == 0){
                    //System.out.println("Depth 0 point "+point);
                    point.setMiniMaxValue(currentScore);
                    minMaxRootValues.add(point);
                }
            }else if(currentPlayer.equals(player1)){
                move(point);
                //System.out.println("Moving player1 "+point);
                int currentScore = minimax(ply,depth+1, player2);
                //System.out.println("current Score player 2: "+currentScore);

                scores.add(currentScore);

                //System.out.println("Scores: "+scores.toString());
            }
            board.restoreBoard(restorePointBoard);
            //System.out.println("Board Restored: ");
            board.printTable();
        }

        if(currentPlayer.equals(player2)){
            return Collections.max(scores);
        }
        return Collections.min(scores);
    }



    //Utility function for minimax Algorithm that calculates
    //it favors player2 (read minimax comments to fix this)
    public int getMinMaxHeuristic(){
        //Get all the possible moves of player1
        ArrayList<TravelPath> travelPathsPlayer1 = getAllPossibleMoves(player1.getColor());
        int player1MaxScore = Integer.MIN_VALUE;
        for(TravelPath t: travelPathsPlayer1){
            if(t.getScore() > player1MaxScore)
                player1MaxScore = t.getScore();
        }

        //System.out.println(player1.getName()+" Heuristic "+player1MaxScore);

        //Get all the possible moves of player2
        ArrayList<TravelPath> travelPathsPlayer2 = getAllPossibleMoves(player2.getColor());
        int player2MaxScore = Integer.MIN_VALUE;
        for(TravelPath t: travelPathsPlayer2){
            if(t.getScore() > player2MaxScore)
                player2MaxScore = t.getScore();
        }

        System.out.println(player2.getName()+" Heuristic "+player1MaxScore);

        return player2MaxScore - player1MaxScore;
    }

    /*Utility function for minimax algorithm and MainActivity
      This function moves stone from source to destination of Travelpath object and empties all the
      cells in between
     */
    public void move(TravelPath travelPath){

        Cell source = travelPath.getSource();
        Cell dest = travelPath.getDestination();

        //copy source to destination
        board.BOARD[dest.getRow()][dest.getCol()] = board.BOARD[source.getRow()][source.getCol()];

        //empty every cells except destination
        for(Cell each: travelPath.getPath()){

            if(!each.equals(dest)){
                board.BOARD[each.getRow()][each.getCol()] = board.EMPTY_SPOT;
            }
        }
    }

}
