/************************************************************
 * Name:  Santosh Paudel                                    *
 * Project:  Project 1 Konane                               *
 * Class:  Artificial Intelligence                          *
 * Date:  February 2, 2018                                  *
 ************************************************************/


package com.example.wills.konane;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.io.FileReader;
import java.util.HashMap;

import static android.view.View.VISIBLE;
import static android.widget.Toast.makeText;

/*This is a view class that represents Game Activity*/
public class MainActivity extends AppCompatActivity {

    Game game;
    Button turnSkipButton;

    /*At any given moment, there can only be two swaps.
      swap_source and swap_dest objects keep track of which two positions in the board are to be swapped
    */
    private static ImageView swap_source = null;
    private static ImageView swap_dest = null;
    

    //A flag to check if the source is selected for move
    private static boolean source_selected = false;

    //A flag to determine if a player is entitled for multiple moves
    private static boolean chain_move = false;
    
    //Id for the position of recently moved stone. It will be used for verifying chain movements (multiple movements)
    int destination_id;

    private static HashMap<Integer, Pair<Integer, Integer>> position_map = new HashMap<Integer, Pair<Integer, Integer>>();



    /*
        This function handles the swap of two stones. It uses global ImageView objects swap_source and swap_dest to determine source
        and destination of swap process. It checks if the source is selected (by taking advantage of global source_selected) flag. It makes use of
        global chain_move flag to determine if current move is a multiple move from the user. It checks if the user is moving the same stone during multiple moves (and not any
        other stones from the board)

        Parameter: View object
     */
    public void swap(View view)
    {

        ImageView stone = (ImageView) view;

        String current_stone_color = stone.getTag().toString();

        //Check if it's illegal player
        if(game.isLegalPlayer(current_stone_color) == false && swap_source == null){
            makeToast("Not Your Turn");
            return;
        }

        //If the source is not selected, first click should be the source
        if(source_selected == false)
        {
            swap_source = stone;

            //If the clicked field is empty, we perform no action
            if(stone.getDrawable() == null)
            {
                reset_select();
            }
            else{

                if(chain_move == true && swap_source.getId() != destination_id)
                {
                    makeToast("Wrong one selected");
                    reset_select();
                    return;
                }
                source_selected = true;
                swap_source.setBackgroundColor(Color.parseColor("#40F96E00"));
            }
        }
        else
        {
            swap_dest = (ImageView) view;

            Pair<Integer, Integer> source_pos = position_map.get(swap_source.getId()); //Position of the source in Pair of row and column
            Pair<Integer, Integer> dest_pos = position_map.get(swap_dest.getId()); //Position of the destination in Pair of row and col

            //Array to get back a list of middle stone row and column
            int[] middle_pos = new int[2];

            //check if the source and destination are swappable on the board
            boolean swappable;
            swappable = game.moveStone(source_pos.first, source_pos.second, dest_pos.first, dest_pos.second, swap_source.getTag().toString(), swap_dest.getTag().toString(), middle_pos);
            if (swappable == false)
            {
                reset_select();
                makeToast("Invalid Move");
                return;
            }


            if(swap_source.getTag().equals(game.board.BLACK_STONE))
            {
                swap_dest.setImageResource(R.drawable.black_stone);
            }
            else if(swap_source.getTag().equals(game.board.WHITE_STONE))
            {
                swap_dest.setImageResource(R.drawable.white_stone);
            }

            //Destination gets source tag
            //Source gets empty tag
            swap_dest.setTag(swap_source.getTag());
            swap_source.setTag(game.board.EMPTY_SPOT);
            swap_source.setImageResource(0);

            //The stone in the middle of source and destination should disappear

            //If the source and destination are in the same row, the removable lies in the middle column
            int middleId; //id of the middle stone (between source and destination
            int middle_row, middle_col; //positions of the middle_stone


            middle_row = middle_pos[0];
            middle_col = middle_pos[1];

            //get id of the middle stone
            middleId = getId(middle_row, middle_col);
            ImageView middle = findViewById(middleId);

            //set the middle stone Image to null
            middle.setImageResource(0);
            middle.setTag(game.board.EMPTY_SPOT);

            //update current player's score
            updateScore();


            //If the player cannot make another move (by same stone), switch player
            Pair<Integer, Integer> stone_pos = position_map.get(view.getId());
            if(!game.activePlayer.isAnotherMove(game.board, stone_pos.first, stone_pos.second)) {
                chain_move = false;
                switchPlayer();
            }
            else
            {
                turnSkipButton.setVisibility(View.VISIBLE);
                chain_move = true;
                destination_id = swap_dest.getId();
            }



            //reset all parameters
            reset_select();
        }

    }

    //This function iterates over the position_map and finds Id of Imageview of given Pair of row and column positions
    int getId(int middle_row, int middle_col)
    {
        for(Integer id: position_map.keySet())
        {
            Pair<Integer,Integer> middle_stone  = position_map.get(id);
            if(middle_stone.first == middle_row && middle_stone.second == middle_col)
            {
                return id;
            }
        }
        return 0;
    }

    /*This function updates player score on the screen*/
    private void updateScore()
    {
        game.activePlayer.incrementScore();
        TextView score_view = findViewById(game.activePlayer.getId());
        int score = game.activePlayer.getScore();
        score_view.setText(""+score);
    }

    //resets source and destination selection
    private void reset_select()
    {
        if (swap_source != null)
            swap_source.setBackgroundColor(Color.WHITE);
        swap_source = null;
        swap_dest = null;
        source_selected = false;
    }

    public void skipTurn(View view)
    {
        chain_move = false;
        reset_select();
        switchPlayer();
    }



    /*This function calls switchPlayer function from game class.
       switchPlayer (from game class) performs necessary checks to see if it
       is possible to switch player and returns true (or false if it not possible).m

       This function then checks if there are any winners. If there are, it calls declareWinnder() function
     */
    private void switchPlayer()
    {
        String currentPlayerName = game.activePlayer.getName();

        if (game.switchPlayer() == false)
            makeToast("Another player has no moves. "+ currentPlayerName+ ", play again!");

        highlightPlayer(game.activePlayer.getColor());

        if(game.isGameOver() == true)
            declareWinner();

        turnSkipButton.setVisibility(View.INVISIBLE);
    }

    /*This function checks if any player has won or if there's a draw.
      It makes linear layout appear with a congratulations message
     */
    void declareWinner()
    {
        String winner_msg = "none";
        TextView winner_board = findViewById(R.id.winning_board);

        if(game.player1.isWinner() == true)
            winner_msg = game.player1.getName()+" has Won!";
        else if(game.player2.isWinner() == true)
            winner_msg = game.player2.getName()+" has Won";
        else
            winner_msg = "It's a draw";

        winner_board.setText(winner_msg);

        LinearLayout layout = findViewById(R.id.final_layout);
        layout.setVisibility(VISIBLE);
    }

    /*This function resets all parameters to get the game back to ready state!
     */
    void replay(View view)
    {
        LinearLayout layout = findViewById(R.id.final_layout);
        layout.setVisibility(View.INVISIBLE);
        game.resetGame();
        initializeGame();
    }

    void save(View view)
    {
        boolean successful_save = game.saveGame(getApplicationContext());

        if(successful_save == false)
            makeToast("Could not save file");
        else
            makeToast("Saved!");

    }

    void load(View view)
    {
        boolean successful_load;
        successful_load = game.loadGame(getApplicationContext());

        if(successful_load == false)
            makeToast("Game could not be loaded");
        else
            reset_select();
            initializeGame();
    }

    //Make a Toast appear on screen with the message given in parameter
    private void makeToast(String message)
    {
        Toast toat = (Toast) makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toat.show();
    }


    /*
        This function:
            - Populates the grid with black and white stones
            - Sets tags for all those stones ("B" for black, "W" for white, "E" for Empty)
    */
    public void initializeGame()
    {
        GridLayout grid = findViewById(R.id.konane_grid);

        int child_index = 0; //to identify children of the grid layout


        //loop through the board array and initialize the grid layout with
        //appropriate color of stones
        for(int i=0; i<game.board.BOARD.length; i++)
        {
            for(int j=0; j<game.board.BOARD[i].length; j++)
            {
                String stone = game.board.BOARD[i][j];
                ImageView image = (ImageView) grid.getChildAt(child_index);

                if(stone.equals(game.board.BLACK_STONE))
                {
                    image.setImageResource(R.drawable.black_stone);
                }
                else if(stone.equals(game.board.WHITE_STONE))
                {
                    image.setImageResource(R.drawable.white_stone);
                }
                else if(stone.equals(game.board.EMPTY_SPOT))
                {
                    image.setImageResource(0);
                }

                //Put the Imageview ids and their equivalent locaiton in board array in position_map hashmap
                //for O(1) reference
                position_map.put(image.getId(), new Pair<>(i,j));

                //All the ImaveViews need to be given tags based on the color of the stones they are given
                image.setTag(game.board.BOARD[i][j]);
                child_index++;
            }

        }

        //Initialize player 1 score as 0
        TextView player1_score = findViewById(R.id.player1_score);
        player1_score.setText(String.valueOf(game.player1.getScore()));

        TextView player2_score = findViewById(R.id.player2_score);
        player2_score.setText(String.valueOf(game.player2.getScore()));

        //Highlight the active player on the screen
        highlightPlayer(game.activePlayer.getColor());


        //Give Each Player player ID
        //player1.PLAYER_ID = findViewById(R.id.player1_score).getId();
        //player2.PLAYER_ID = findViewById(R.id.player2_score).getId();
        game.player1.setId(findViewById(R.id.player1_score).getId());
        game.player2.setId(findViewById(R.id.player2_score).getId());
    }

    /*This function highlights active player in the TextView (User Interface)*/
    void highlightPlayer(String player_color)
    {
        if(player_color == game.board.BLACK_STONE)
        {
            TextView player1_label = findViewById(R.id.player1_label);
            player1_label.setBackgroundColor(Color.parseColor("#FF8E3A"));

            TextView player2_label = findViewById(R.id.player2_label);
            player2_label.setBackgroundColor(Color.WHITE);
        }
        else if(player_color == game.board.WHITE_STONE)
        {
            TextView player2_label = findViewById(R.id.player2_label);
            player2_label.setBackgroundColor(Color.parseColor("#FF8E3A"));

            TextView player1_label = findViewById(R.id.player1_label);
            player1_label.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game = new Game();

        turnSkipButton = findViewById(R.id.skipTurn);

        initializeGame();
    }
}
