package com.example.wills.konane;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


/**
 * Created by Santosh on 2/10/2018.
 */

public class Game {
    public static String SERILIZATION_FILE_NAME = "serialization.txt";
    public Board board = new Board();
    public Player player1 = new Player(true, board.BLACK_STONE, "BLACK");
    public Player player2 = new Player(false, board.WHITE_STONE, "WHITE");
    public Player activePlayer = player1;
    private Boolean game_over = false;


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

    public void dfsSearch(String stoneColor, ArrayList<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> possibleMoves) {
        Boolean[][] visited = new Boolean[6][6];

        populateArray(visited);
        int row, col;


        //We want to begin search from the first possible move of the stone of "stoneColor" (from parameter)
        //Board class automatically updates the first possible move of black and white stone both
        //so we can easily grab that
        if(stoneColor.equals(board.BLACK_STONE)){
            row = board.FIRST_MOVABLE_BLACK.first;
            col = board.FIRST_MOVABLE_BLACK.second;
        }
        else{
            row = board.FIRST_MOVABLE_WHITE.first;
            col = board.FIRST_MOVABLE_WHITE.second;
        }

        Stack<Pair<Integer,Integer>> s = new Stack<>();
        Pair<Integer,Integer> start = new Pair <>(row,col);


        s.push(start);

        while(s.empty() == false) {
            //an empty Pair data structure (that contains another pair of integers) to contain north, east, south, west moves
            Pair<Pair<Integer,Integer>,Pair<Integer,Integer>> moves = null;

            Pair <Integer, Integer> current_stone = s.peek();
            s.pop();

            if (visited[current_stone.first][current_stone.second] == false) {
                visited[current_stone.first][current_stone.second] = true;

                //You only need to add the neighbors (north, east,south, west) to the hashMap if the current
                //stone has same color as the one in the parameter
                if (board.BOARD[current_stone.first][current_stone.second] == stoneColor) {
                    Pair <Integer, Integer> west = board.getPositionWest(current_stone.first, current_stone.second);
                    Pair <Integer, Integer> south = board.getPositionSouth(current_stone.first, current_stone.second);
                    Pair <Integer, Integer> east = board.getPositionEast(current_stone.first, current_stone.second);
                    Pair <Integer, Integer> north = board.getPositionNorth(current_stone.first, current_stone.second);

                    //If the position is not garbage (i.e = -1) and if the position is not already visited,
                    //then push the position to stack

                    if (west.first != -1) {
                        //System.out.println("West "+west.first+west.second);
                        moves = new Pair <>(current_stone, west);
                        possibleMoves.add(moves);
                    }
                    if (south.first != -1) {
                        //System.out.println("South "+south.first+south.second);
                        moves = new Pair <>(current_stone,south);
                        possibleMoves.add(moves);
                    }
                    if (east.first != -1) {
                        //System.out.println("East "+east.first+east.second);
                        moves = new Pair <>(current_stone,east);
                        possibleMoves.add(moves);
                    }
                    if (north.first != -1) {
                        //System.out.println("North "+north.first+north.second);
                        moves = new Pair <>(current_stone,north);
                        possibleMoves.add(moves);
                    }

                }
            }

            int current_row = current_stone.first;
            int current_col = current_stone.second;

            //push the element below the current element onto the stack
            if(current_row+1 <6 && visited[current_row+1][current_col] == false) {
                //if current_col is equal to 5 (i.e the end), start pushing from the beginning (i.e from column 0)
                if(current_col == 5)
                    s.push(new Pair <>(current_row + 1, 0));
                else
                    s.push(new Pair <>(current_row+1, current_col));
            }

            //Push the element on the right of current element onto the stack
            if(current_col+1 < 6 && visited[current_row][current_col+1] == false)
                s.push(new Pair <>(current_row,current_col+1));
        }

    }


    public void bfsSearch(String stoneColor, ArrayList<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> possibleMoves)
    {
        Boolean[][] visited = new Boolean[6][6];

        populateArray(visited);
        int row, col;


        //We want to begin search from the first possible move of the stone of "stoneColor" (from parameter)
        //Board class automatically updates the first possible move of black and white stone both
        //so we can easily grab that
        if(stoneColor.equals(board.BLACK_STONE)){
            row = board.FIRST_MOVABLE_BLACK.first;
            col = board.FIRST_MOVABLE_BLACK.second;
        }
        else{
            row = board.FIRST_MOVABLE_WHITE.first;
            col = board.FIRST_MOVABLE_WHITE.second;
        }

        Queue<Pair<Integer,Integer>> q = new LinkedList <>();
        Pair<Integer,Integer> start = new Pair <>(row,col);

        visited[start.first][start.second] = true;

        q.add(start);

        while(!q.isEmpty())
        {
            Pair<Integer,Integer> current_stone = q.peek();
            q.remove();

            Pair<Pair<Integer,Integer>,Pair<Integer,Integer>> moves = null;

            //You only need to add the neighbors (north, east,south, west) to the hashMap if the current
            //stone has same color as the one in the parameter
            if (board.BOARD[current_stone.first][current_stone.second] == stoneColor) {
                Pair <Integer, Integer> west = board.getPositionWest(current_stone.first, current_stone.second);
                Pair <Integer, Integer> south = board.getPositionSouth(current_stone.first, current_stone.second);
                Pair <Integer, Integer> east = board.getPositionEast(current_stone.first, current_stone.second);
                Pair <Integer, Integer> north = board.getPositionNorth(current_stone.first, current_stone.second);

                //If the position is not garbage (i.e = -1) and if the position is not already visited,
                //then push the position to stack

                if (west.first != -1) {
                    //System.out.println("West "+west.first+west.second);
                    moves = new Pair <>(current_stone, west);
                    possibleMoves.add(moves);
                }
                if (south.first != -1) {
                    //System.out.println("South "+south.first+south.second);
                    moves = new Pair <>(current_stone,south);
                    possibleMoves.add(moves);
                }
                if (east.first != -1) {
                    //System.out.println("East "+east.first+east.second);
                    moves = new Pair <>(current_stone,east);
                    possibleMoves.add(moves);
                }
                if (north.first != -1) {
                    //System.out.println("North "+north.first+north.second);
                    moves = new Pair <>(current_stone,north);
                    possibleMoves.add(moves);
                }

            }

            int current_row = current_stone.first;
            int current_col = current_stone.second;


            //Push the element on the right of current element onto the stack
            if(current_col+1 < 6 && visited[current_row][current_col+1] == false) {
                q.add(new Pair <>(current_row, current_col + 1));
                visited[current_row][current_col+1] = true;
            }

            //push the element below the current element onto the stack
            if(current_row+1 <6 && visited[current_row+1][current_col] == false) {
                //if current_col is equal to 5 (i.e the end), start pushing from the beginning (i.e from column 0)
                q.add(new Pair <>(current_row + 1, current_col));
                visited[current_row+1][current_col] = true;

            }

        }

    }

    



    public void populateArray(Boolean[][] visited)
    {
        for(int i=0; i<board.NUM_BLOCKS; i++)
            for (int j=0; j<board.NUM_BLOCKS;j++)
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
    public Boolean moveStone(int source_row, int source_col, int dest_row, int dest_col, String source_color, String dest_color, int middle_positions[])
    {
        if (board.move(source_row, source_col, dest_row, dest_col, source_color, dest_color, middle_positions))
            return true;
        else
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
            File fileOut = new File(applicationContext.getFilesDir(), SERILIZATION_FILE_NAME);

            FileOutputStream fileOutputStream = new FileOutputStream(fileOut);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            //write the score of black stone
            bufferedWriter.write(player1.getName()+": "+player1.getScore());
            bufferedWriter.newLine();

            //write the score of white stone
            bufferedWriter.write(player2.getName()+": "+player2.getScore());
            bufferedWriter.newLine();

            bufferedWriter.write("Board:");
            bufferedWriter.newLine();

            for(int i=0; i< board.NUM_BLOCKS; i++)
            {
                String buffer = "";
                for(int j=0; j<board.NUM_BLOCKS;j++)
                {
                    buffer += board.BOARD[i][j]+" ";
                }
                bufferedWriter.write(buffer);
                bufferedWriter.newLine();
            }

            String nextPlayer = "Next Player: "+ activePlayer.getName();
            bufferedWriter.write(nextPlayer);


            bufferedWriter.close();
            successful_save = true;


        } catch (Exception e) {
            e.printStackTrace();
            successful_save = false;
        }

        return  successful_save;
    }



    /*This function reads the file named SERILIZATION_FILE_NAME defined in Game class
      and loads the state of the last saved game. The game state is loaded from the following format:
          Black: 6
          White: 4
          Board:
          B W B W B W
          W B O B O B
          B W O W B W
          O B O O O O
          B W B O O W
          W B W O O B
          Next Player: White

      RETURNS: True, if the game is loaded successfully. False otherwise.
     */
    public boolean loadGame(Context applicationContext)
    {

        Boolean successful_load = false;
        try{
            File inFile = new File(applicationContext.getFilesDir()+"/"+SERILIZATION_FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(inFile));

            String buffer;
            int line_counter = 0;
            int rowNum = 0;
            while(true)
            {
                buffer = bufferedReader.readLine();

                if(buffer == null)
                    break;

                if (line_counter == 0 || line_counter == 1)
                {
                    if(line_counter == 0)
                        player1.setScore(Integer.parseInt(buffer.split(": ")[1]));
                    else if(line_counter == 1)
                        player2.setScore(Integer.parseInt(buffer.split(": ")[1]));
                }
                else if(line_counter >= 3 && line_counter <= 8)
                {
                    String[] row = buffer.split(" ");
                    for(int i=0; i<6; i++)
                    {
                        board.BOARD[rowNum][i] = row[i];
                    }
                    rowNum++;
                }

                if(line_counter == 9)
                {
                    String playerName = buffer.split(": ")[1].trim();
                    if(player1.getName().equals(playerName)){
                        activePlayer = player1;}
                    else
                        activePlayer = player2;
                }
                line_counter++;
            }
            successful_load = true;
        }catch (Exception e)
        {
            System.out.println("************************************");
            System.out.println(e.toString());
            e.printStackTrace();
            successful_load = false;
        }
        return successful_load;
    }


}
