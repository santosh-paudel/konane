package com.example.wills.konane;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by Santosh on 3/14/2018.
 */

public class BoardSizeDialogueFragment extends DialogFragment{

    //This is listetenr object for NumPickerPicker
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());


        final String[] displayValues = {"6","8","10"};

        //minimum value is the first value of displayValues
        numberPicker.setMinValue(0);

        //maximum value is the last value of displayValues
        numberPicker.setMaxValue(displayValues.length-1);

        //these are the values displayed on numbPicker
        numberPicker.setDisplayedValues(displayValues);

        //default value
        numberPicker.setValue(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Your Board Size");

        //set positive button
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity callingActivity = (MainActivity) getActivity();
                //call the callback function after value has been changed and "Done" is clicked
                callingActivity.onBoardSizePicked(displayValues[numberPicker.getValue()]);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "Board will be default size",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(numberPicker);

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
