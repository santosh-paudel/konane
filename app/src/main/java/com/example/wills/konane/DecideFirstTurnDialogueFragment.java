package com.example.wills.konane;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by santosh on 3/15/18.
 */

public class DecideFirstTurnDialogueFragment extends DialogFragment implements View.OnClickListener{

    TextView position1TextView;
    TextView position2TextView;

    int blackPositionTextView; //contains the id of textView that shows black position
    int selectedPositionTextView; //contains the id of textView that shows user selected position


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Which Of The Following Position has Black Cell");

        //set positive button
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MainActivity callingActivity = (MainActivity) getActivity();
                if(blackPositionTextView == selectedPositionTextView){
                    callingActivity.onPlayerDecided(true);
                }
                else{
                    callingActivity.onPlayerDecided(false);
                }

            }
        });

        builder.setView(R.layout.decide_first_turn_dialogue_fragment);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        //Set the dim amount of screen when the dialogue is displayed
        dialog.getWindow().setDimAmount(10);


        position1TextView = dialog.findViewById(R.id.black_position);
        position2TextView = dialog.findViewById(R.id.white_position);

        ArrayList<Integer> arrayListFirstRemovals = getArguments().getIntegerArrayList("decidePlayer");


        //user random integer to determine which of the two textView has black position
        Random random = new Random();
        int randomInt = random.nextInt(2)+1;

        if(randomInt == 1){
            //set black positions
            position1TextView.setText(arrayListFirstRemovals.get(0)+"X"+arrayListFirstRemovals.get(1));
            blackPositionTextView = position1TextView.getId();

            //set white positions
            position2TextView.setText(arrayListFirstRemovals.get(2)+"X"+arrayListFirstRemovals.get(3));
        }
        else{
            //set black position in the second textView
            position2TextView.setText(arrayListFirstRemovals.get(0)+"X"+arrayListFirstRemovals.get(1));
            blackPositionTextView = position2TextView.getId();

            //set white positions in the first textView
            position1TextView.setText(arrayListFirstRemovals.get(2)+"X"+arrayListFirstRemovals.get(3));
        }

        position1TextView.setOnClickListener(this);
        position2TextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        //now reset the other textView color
        position1TextView.setBackgroundColor(Color.WHITE);
        position2TextView.setBackgroundColor(Color.WHITE);

        v.setBackgroundColor(Color.parseColor("#FFA045"));

        selectedPositionTextView = v.getId();

    }



}
