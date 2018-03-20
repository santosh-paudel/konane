/************************************************************
 * Name:  Santosh Paudel                                    *
 * Project:  Project 1 Konane                               *
 * Class:  Artificial Intelligence                          *
 * Date:  February 2, 2018                                  *
 ************************************************************/


package com.example.wills.konane;


import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.makeText;

/*This is a view class that represents Game Activity*/
public class MainActivity extends AppCompatActivity {

    //This activity has one or more fragments associated with it. We need a fragment manager to
    //send and received data to and from those fragments
    BoardSizeDialogueFragment boardSizeDialogueFragment = new BoardSizeDialogueFragment();
    FragmentManager fragmentManager = getFragmentManager();

    int LOAD_FROM_FILE_REQUEST_CODE = 5;
    int EXTERNAL_STORAGE_WRITE_PERMISSION_CODE = 10;

    public Game game;
    public Button turnSkipButton;

    //Numeric value to keep track of the number of hints asked by the user
    public static int hintNumber = 0;

    //Global variable to represent depth cut off
    //default depth cut off value is 5
    public static int plyCutOff = -1;

    //At any given time, if the user clicks hint, only two imageview object can have blink animation.
    //That object should be globally accessible to start and end animations from any functions.
    ImageView sourceBlink = null;
    ImageView destBlink = null;




    //Every users (black and white) have a a set of possible moves in their turn. This hashmap keeps track of their possible moves
    //public static HashMap<Pair<Integer,Integer>, ArrayList<Pair<Integer,Integer>>> possibleMoves = new HashMap <>();
    ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer,Integer>>> possibleMoves = new ArrayList <>();

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
    int MultiJumpSourceId;

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

        //if there are any blinking animations, clear them first
        clearBlinkEffect();
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

                if(chain_move == true && swap_source.getId() != MultiJumpSourceId)
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

            //source and destination Cell objects
            Cell source = new Cell(source_pos.first, source_pos.second, swap_source.getTag().toString());
            Cell dest = new Cell(dest_pos.first, dest_pos.second, swap_dest.getTag().toString());

            //Array to get back a list of middle stone row and column
            Cell[] middle_pos = new Cell[1];

            //check if the source and destination are swappable on the board
            boolean swappable;
            swappable = game.moveStone(source, dest, middle_pos);
            if (swappable == false)
            {
                reset_select();
                makeToast("Invalid Move");
                return;
            }


            if(swap_source.getTag().equals(game.getBoardObj().BLACK_STONE))
            {
                swap_dest.setImageResource(R.drawable.black_stone);
            }
            else if(swap_source.getTag().equals(game.getBoardObj().WHITE_STONE))
            {
                swap_dest.setImageResource(R.drawable.white_stone);
            }

            //Destination gets source tag
            //Source gets empty tag
            swap_dest.setTag(swap_source.getTag());
            swap_source.setTag(game.getBoardObj().EMPTY_SPOT);
            swap_source.setImageResource(0);

            //The stone in the middle of source and destination should disappear

            //If the source and destination are in the same row, the removable lies in the middle column
            int middleId; //id of the middle stone (between source and destination
            int middle_row, middle_col; //positions of the middle_stone


            middle_row = middle_pos[0].getRow();
            middle_col = middle_pos[0].getCol();

            //get id of the middle stone
            middleId = getId(middle_row, middle_col);
            ImageView middle = findViewById(middleId);

            //set the middle stone Image to null
            middle.setImageResource(0);
            middle.setTag(game.getBoardObj().EMPTY_SPOT);

            //update current player's score
            updateScore();

            //reset Any hints because older hints may not be relevant anymore after the user has moved a stone
            resetHints();


            //If the player cannot make another move (by same stone), switch player
            Pair<Integer, Integer> stone_pos = position_map.get(view.getId());
            if(!game.activePlayer.isAnotherMove(game.getBoardObj(), stone_pos.first, stone_pos.second)) {
                chain_move = false;
                switchPlayer();
            }
            else
            {
                turnSkipButton.setVisibility(View.VISIBLE);
                chain_move = true;
                resetHints();
                MultiJumpSourceId = swap_dest.getId();
            }

            //reset all parameters
            reset_select();
        }

    }

    /*This function resets the array named possibleMoves and resets hintsNumber to 0.
      possibleMoves contain a list of possible hints (or moves) for any player that clicks hints

      RETURNS: This function does not return anything
    */


    public void resetHints()
    {
        hintNumber = 0;
        possibleMoves.clear();

        //clear blinking effect if there are any
        clearBlinkEffect();
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
        clearBlinkEffect();
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
    /*This function checks if any player has won or if there's a draw.
      It makes linear layout appear with a congratulations message
     */
    void declareWinner()
    {
        String winner_msg = "none";

        if(game.player1.hasWon() == true)
            winner_msg = game.player1.getName()+" has Won!";
        else if(game.player2.hasWon() == true)
            winner_msg = game.player2.getName()+" has Won";
        else
            winner_msg = "It's a draw";

        makeToast(winner_msg);

    }

    /*This function resets all parameters to get the game back to ready state!
     */
    void replay(View view)
    {
        game.resetGame();
        initializeGame(game.getBoardObj().BOARD_SIZE, true);
    }

    void saveGame()
    {
        boolean successful_save = false;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_WRITE_PERMISSION_CODE);
        }else {
            successful_save = game.saveGame(getApplicationContext());
        }

        if(successful_save == false)
            makeToast("Could not save file");
        else
            makeToast("Saved!");

    }

    void loadGame(Uri fileName)
    {
        boolean successful_load;

        successful_load = game.loadGameFromFile(getApplicationContext(), fileName);

        if(successful_load == false)
            makeToast("Game could not be loaded");
        else
            reset_select();

        game.getBoardObj().printTable();
        initializeGame(game.getBoardObj().BOARD_SIZE, false);
    }

    public void loadGameFromFile(){

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, LOAD_FROM_FILE_REQUEST_CODE);

    }

    void blinkEffect()
    {
        if(sourceBlink != null && destBlink != null) {

            Animation sourceAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            sourceBlink.startAnimation(sourceAnimation);

            Animation destAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            destBlink.startAnimation(destAnimation);
        }


    }

    void clearBlinkEffect()
    {
        if(sourceBlink !=null && destBlink !=null)
        {
            sourceBlink.clearAnimation();
            destBlink.clearAnimation();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == LOAD_FROM_FILE_REQUEST_CODE) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                loadGame(uri);
            }
        }
    }


    void hint(View view)
    {

        if(game.activePlayer.equals(game.player2)){
            makeToast("player 2 is playing");

            MiniMaxAlgorithmTask mtask = new MiniMaxAlgorithmTask();
            mtask.execute(game.player2);

        }else
        {
            makeToast("Player 2 only");
        }
//        System.out.println("Hint");
//        ArrayList<TravelPath> travelPaths = game.getAllPossibleMoves("W");
//
//        for(int i=0; i<travelPaths.size(); i++){
//            ArrayList<Cell> cells = travelPaths.get(i).getPath();
//            for(Cell cell:cells){
//                System.out.print(cell.getRow()+","+cell.getCol()+" -> ");
//            }
//            System.out.println("");
//        }
//
//        System.out.println("MINIMAX");
//        game.minimax(0, 5, game.player1);


    }

    //Make a Toast appear on screen with the message given in parameter
    private void makeToast(String message)
    {
        Toast toat = (Toast) makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toat.show();
    }


    /*When we assign dimensions programmatically, it is assigned in pixel value
      But we want the dimension in dp since px won't be same in high and low pixel
      density devices.

      RETURNS: integer pixel
     */
    public int convertDpToPx(int dp){

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        return Math.round(dp*metrics.density);
    }


    /**
     * Konane grid has label on the top and on the left. The labels number the grid's row and column.
     * @param dimension
     * @param boardSize
     */
    public void updateGridLabel(int dimension, int boardSize){

        LinearLayout leftLabel = findViewById(R.id.gridLabelLeft);
        LinearLayout topLabel = findViewById(R.id.gridLabelTop);

        //first remove all views from the layouts
        leftLabel.removeAllViews();
        topLabel.removeAllViews();

        //set left label
        int marginOffset = 0;
        for(int i=0; i<boardSize; i++){
            TextView textView = new TextView(getApplicationContext());
            textView.setText(String.valueOf(i));
            textView.setHeight(dimension + marginOffset);
            textView.setGravity(Gravity.BOTTOM);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;

            leftLabel.addView(textView);

            marginOffset +=2;
        }

        //set top label
        marginOffset = 0;
        for(int i=0; i<boardSize; i++){
            TextView textView = new TextView(getApplicationContext());
            textView.setText(String.valueOf(i));
            textView.setWidth(dimension + marginOffset);
            textView.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;

            topLabel.addView(textView);

            marginOffset +=2;
        }
    }

    public void decidePlayer(){

        //Use Bundle to carry data to the fragment
        Bundle bundle = new Bundle();
        Pair<Integer,Integer> blackPosition = game.getBoardObj().getFirstRemovedBlack();
        Pair<Integer,Integer> whitePosition = game.getBoardObj().getFirstRemovedWhite();

        ArrayList<Integer> firstRemoved = new ArrayList<>();

        //Add black positions first and then white to the arraylist
        firstRemoved.add(blackPosition.first);
        firstRemoved.add(blackPosition.second);
        firstRemoved.add(whitePosition.first);
        firstRemoved.add(whitePosition.second);

        bundle.putIntegerArrayList("decidePlayer",firstRemoved);

        //Run the FragmentDialogue to get user input
        DecideFirstTurnDialogueFragment decideFirstTurnDialogueFragment = new DecideFirstTurnDialogueFragment();
        decideFirstTurnDialogueFragment.setArguments(bundle);
        decideFirstTurnDialogueFragment.show(fragmentManager,"Turn");
    }


    /*
        This function:
            - Populates the grid with black and white stones
            - Sets tags for all those stones ("B" for black, "W" for white, "E" for Empty)
    */
    public void initializeGame(int boardSize, Boolean createNewBoard)
    {
        //Initialize Game Class
        if(createNewBoard == true)
            game = new Game(boardSize);

        //clear position map (necessary when loading)
        position_map.clear();




        GridLayout grid = findViewById(R.id.konane_grid);
        //first remove all contents from gridLayout if there are any
        grid.removeAllViews();

        grid.setColumnCount(boardSize);
        grid.setRowCount(boardSize);

        //imageVieSize in dp
        int imageViewSize = getStoneDimension(boardSize);

        //loop through the board array and initialize the grid layout with
        //appropriate color of stones
        for(int row=0; row<game.getBoardObj().BOARD_SIZE; row++)
        {
            for(int col=0; col<game.getBoardObj().BOARD_SIZE; col++)
            {
                String stone = game.getBoardObj().BOARD[row][col];
                //ImageView image = (ImageView) grid.getChildAt(child_index);
                ImageView image = new ImageView(getApplicationContext());

                if(stone.equals(game.getBoardObj().BLACK_STONE))
                {
                    image.setImageResource(R.drawable.black_stone);
                }
                else if(stone.equals(game.getBoardObj().WHITE_STONE))
                {
                    image.setImageResource(R.drawable.white_stone);
                }
                else if(stone.equals(game.getBoardObj().EMPTY_SPOT))
                {
                    image.setImageResource(0);
                }


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swap(v);
                    }
                });

                image.setBackgroundColor(Color.WHITE);



                //Put the Imageview ids and their equivalent locaiton in board array in position_map hashmap
                //for O(1) reference
                int _id = View.generateViewId();
                image.setId(_id);
                position_map.put(_id, new Pair<>(row,col));

                //All the ImaveViews need to be given tags based on the color of the stones they are given
                image.setTag(game.getBoardObj().BOARD[row][col]);

                GridLayout.LayoutParams gparams = new GridLayout.LayoutParams();
                //set row of imageview in the parent gridLayout
                gparams.rowSpec = GridLayout.spec(row);
                //set column of imageview in the parent gridLayout
                gparams.columnSpec = GridLayout.spec(col);

                //set height of imageView
                gparams.height = imageViewSize;

                //set width of imageView
                gparams.width = imageViewSize;

                //set gravity of imageView
                gparams.setGravity(Gravity.CENTER);



                int margin = convertDpToPx(1);
                gparams.setMargins(margin,margin,margin,margin);


                grid.addView(image,gparams);
            }

        }

        //The grid layout (or the game board) has an associated label on it's left and top
        //the label labels the index or row and column.
        //Update the label when the board is initialized
        updateGridLabel(imageViewSize, boardSize);

        //Initialize player 1 score as 0
        TextView player1_score = findViewById(R.id.player1_score);
        player1_score.setText(String.valueOf(game.player1.getScore()));

        TextView player2_score = findViewById(R.id.player2_score);
        player2_score.setText(String.valueOf(game.player2.getScore()));

        //Highlight the active player on the screen
        highlightPlayer(game.activePlayer.getColor());


        //Give Each Player player ID
        game.player1.setId(findViewById(R.id.player1_score).getId());
        game.player2.setId(findViewById(R.id.player2_score).getId());

    }

    /*This function highlights active player in the TextView (User Interface)*/
    void highlightPlayer(String player_color)
    {
        if(player_color == game.getBoardObj().BLACK_STONE)
        {
            TextView player1_label = findViewById(R.id.player1_label);
            player1_label.setBackgroundColor(Color.parseColor("#FF8E3A"));

            TextView player2_label = findViewById(R.id.player2_label);
            player2_label.setBackgroundColor(Color.WHITE);
        }
        else if(player_color == game.getBoardObj().WHITE_STONE)
        {
            TextView player2_label = findViewById(R.id.player2_label);
            player2_label.setBackgroundColor(Color.parseColor("#FF8E3A"));

            TextView player1_label = findViewById(R.id.player1_label);
            player1_label.setBackgroundColor(Color.WHITE);
        }
    }

    /*Inflate the items on menu bar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.game_state_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.saveGame:
                saveGame();
                break;
            case R.id.loadFromFile:
                loadGameFromFile();
                break;
            case R.id.settings:
                PlyAndPrunSettingFragment plyAndPrunSettingFragment = new PlyAndPrunSettingFragment();
                plyAndPrunSettingFragment.show(fragmentManager, "ply_and_prun_settings");
                break;


            default:
                return false;
        }
        return true;
    }

    public void onPlyAndPruneSet(int plyCutOff, Boolean isPrune){
        Toast.makeText(getApplicationContext(), "Ply "+plyCutOff, Toast.LENGTH_SHORT).show();;
    }

    /**
     * This function launches a Dialogue Fragment buiild on BoardSizeDialogueFragment Class.
     * The purpose of the fragment is to make the user pick board esize
     */
    public void getBoardDimensions(){

        boardSizeDialogueFragment = new BoardSizeDialogueFragment();
        boardSizeDialogueFragment.show(fragmentManager, "board size");
    }

    /**
     * This function will return the approximate stoneSize for a given boardSize
     * This is done in order to render all the stones in the screen realstate
     * (As the size of the board increase, the size of the stones have to decrease
     *  for them all to fit on the screen)
     *
     *Size of the stone is calculated by an observation:
     *Observation: When the board size is 6, stone size of 50dp is a perfect setup
     *Conclusion: For a board size of n, stone size of (50dp*6)/n should be a perfect setup
     *
     * @param boardSize
     */
    public int getStoneDimension(int boardSize){

        if (boardSize == 6)
            return convertDpToPx(50);

        else {
            int stoneSize = convertDpToPx(50);
            return Math.round((stoneSize*6)/boardSize);
        }

    }

    /**
     *This is a callback function trigged from the fragment class. Upon triggered,
     * it initializes the game with the user picked board size
     * @param number
     */
    public void onBoardSizePicked(String number){
        Toast.makeText(getApplicationContext(),"Number "+number,Toast.LENGTH_SHORT).show();
        initializeGame(Integer.parseInt(number), true);

        //A fragment thatf lets the player guess which of first two removed stones are black
        //if the player guesses right, they're Black player. Elase computer is a black player
        decidePlayer();
    }

    public void onPlayerDecided(Boolean isCorrectlyPicked){
        TextView player1_label = findViewById(R.id.player1_label);
        TextView player2_label = findViewById(R.id.player2_label);
        if(isCorrectlyPicked == true){

            //update the label on UI
            player1_label.setText("BLACK (H)");
            player2_label.setText("WHITE (AI)");

            //If the user answers correctly in the fragment, player1 is human, player2 is not
            game.player1.setComputer(false);
            game.player2.setComputer(true);

            Toast.makeText(getApplicationContext(),"You are now BLACK Player",Toast.LENGTH_SHORT).show();
        }
        else{

            //update the label on UI
            player1_label.setText("BLACK (AI)");
            player2_label.setText("WHITE (H)");

            //If the user answers incorrectly in fragment, player2 is human and player1 is AI
            game.player1.setComputer(true);
            game.player2.setComputer(false);

            Toast.makeText(getApplicationContext(),"You are now WHITE Player",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == EXTERNAL_STORAGE_WRITE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveGame();
            }
            else{
                makeToast("Sorry, can't load file");
            }
        }
    }

    void minimaxCallback(TravelPath bestPath){
        System.out.println(bestPath.toString());
        System.out.println("minimax value "+bestPath.getMiniMaxValue());
        System.out.println("__________");

        Button AIPermissionButton = findViewById(R.id.AIPermissionButton);
        AIPermissionButton.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        turnSkipButton = findViewById(R.id.skipTurn);
        getBoardDimensions();
    }

    /**
     * Minmax algorithm takes a long time to execute. Trying to run the algorithm within any view functions
     * throws badtokenException. Running the algorithm in background in each user's turn is a better alternative.
     */
    public class MiniMaxAlgorithmTask extends AsyncTask<Player, Void, TravelPath>{

        @Override
        protected TravelPath doInBackground(Player... players) {
            game.callMinimax(3, 0, game.player2);
            TravelPath bestPath = game.getBestMove();

            return bestPath;
        }

        @Override
        protected void onPostExecute(TravelPath travelPath) {
            super.onPostExecute(travelPath);
            minimaxCallback(travelPath);
        }
    }
}


