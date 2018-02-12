package com.example.wills.konane;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


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
    public boolean isLegalPlayer(String current_stone_color)
    {
        if(current_stone_color.equals(activePlayer.getColor()))
        {
            return true;
        }
        return false;
    }

    /*This function returns true if the game is Over else false*/
    public boolean isGameOver(){return game_over;}


    /*This function switches player from active player to inactive player in this order:

        Does second player have any moves?
        No: Does current active player has moves ?
            YES: Keep

     */
    public Boolean switchPlayer()
    {
        Boolean player_switched = true;

        if (activePlayer == player1)
        {
            if(player2.hasValidMove(board) == false)
            {
                if(player1.hasValidMove(board) == false)
                    decideWinner();
                else
                    player_switched = false;

            }
            else {

                activePlayer = player2;
            }
        }
        else if(activePlayer == player2)
        {
            if(player1.hasValidMove(board) == false)
            {
                if (player2.hasValidMove(board) == false)
                    decideWinner();
                else
                    player_switched = false;
            }
            else {

                activePlayer = player1;
            }
        }

        return player_switched;
    }

//    public void dfsSearch(String stoneColor)
//    {
//        for(int row=0; row<board.NUM_BLOCKS; row++)
//        {
//            for(int )
//        }
//    }

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
                System.out.println(buffer);

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
        board.printTable();
        return successful_load;
    }




}
