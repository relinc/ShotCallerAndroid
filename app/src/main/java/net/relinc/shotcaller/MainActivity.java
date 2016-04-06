package net.relinc.shotcaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText prevStrainRate, prevLength, prevPressure, nextStrainRate, nextLength, nextPressure;
    private Button btnClear, btnCalculate;
    private ArrayList<EditText> prev = new ArrayList<EditText>();
    private ArrayList<EditText> next = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.mainwrapper));
        prevStrainRate = (EditText) findViewById(R.id.prevstrainrate);
        prevLength = (EditText) findViewById(R.id.prevlength);
        prevPressure = (EditText) findViewById(R.id.prevpressure);
        prev.add(prevStrainRate);
        prev.add(prevLength);
        prev.add(prevPressure);
        nextStrainRate = (EditText) findViewById(R.id.nextstrainrate);
        nextLength = (EditText) findViewById(R.id.nextlength);
        nextPressure = (EditText) findViewById(R.id.nextpressure);
        next.add(nextStrainRate);
        next.add(nextLength);
        next.add(nextPressure);
        btnClear = (Button) findViewById(R.id.clear);
        btnCalculate = (Button) findViewById(R.id.calculate);
        btnClear.setOnClickListener(this);
        btnCalculate.setOnClickListener(this);
        getSupportActionBar().setTitle("Shot Caller");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
                if(fieldsValid()) {
                    if(nextPressure.getText().toString().trim().length() > 0 && nextLength.getText().toString().trim().length() > 0)
                        calculateStrainRate();
                    else if(nextPressure.getText().toString().trim().length() > 0 && nextStrainRate.getText().toString().trim().length() > 0)
                        calculateStrikerBarLength();
                    else if(nextStrainRate.getText().toString().trim().length() > 0 && nextLength.getText().toString().trim().length() > 0)
                        calculatePressure();
                }
                break;
            case R.id.clear:
                for(EditText e : prev) {
                    e.setText(null);
                    e.setHintTextColor(Color.parseColor("#808080"));
                }
                for(EditText e : next) {
                    e.setText(null);
                    e.setHintTextColor(Color.parseColor("#808080"));
                }
                break;
        }
    }

    private boolean fieldsValid() {
        boolean valid = true;
        String message = "";
        for(EditText e : prev) {
            if(e.getText().toString().trim().length() == 0) {
                e.setHintTextColor(Color.RED);
                valid = false;
            } else {
                e.setHintTextColor(Color.parseColor("#808080"));
            }
        }
        if(!valid) {
            message += "Please fill ALL three fields in the \"Previous Shot\" Column \n\n";
        }
        int nextFieldsFilled = 0;
        for(EditText e : next) {
            if(e.getText().toString().trim().length() != 0) {
                nextFieldsFilled++;
            }
            e.setHintTextColor(Color.parseColor("#808080"));
        }
        if(nextFieldsFilled < 2) {
            valid = false;
            for (EditText e : next) {
                if (e.getText().toString().trim().length() == 0) {
                    e.setHintTextColor(Color.RED);
                }
            }
            message += "Please fill EXACTLY two of the three fields in the \"Next Shot\" Column \n\n";
        }
        if(nextFieldsFilled == 3) {
            valid = false;
            message += "Please fill ONLY two the three fields in the \"Next Shot\" Column \n\n";
        }

        if(!valid) {
            message = message.substring(0, message.length()-1);
            showAlertDialog(message);
        }

        return valid;
    }

    private void calculateStrainRate(){
        try {
            double previousStrainRate = Double.parseDouble(prevStrainRate.getText().toString());
            double previousStrikerBarLength = Double.parseDouble(prevLength.getText().toString());
            double previousPressure = Double.parseDouble(prevPressure.getText().toString());
            double nextStrikerBarLength = Double.parseDouble(nextLength.getText().toString());
            double nextPressureValue = Double.parseDouble(nextPressure.getText().toString());
            double newStrainRate = previousStrainRate * Math.sqrt(nextPressureValue / previousPressure * previousStrikerBarLength / nextStrikerBarLength);
            nextStrainRate.setText(round(newStrainRate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateStrikerBarLength(){
        try {
            double previousStrainRate = Double.parseDouble(prevStrainRate.getText().toString());
            double previousStrikerBarLength = Double.parseDouble(prevLength.getText().toString());
            double previousPressure = Double.parseDouble(prevPressure.getText().toString());
            double nextStrainRateValue = Double.parseDouble(nextStrainRate.getText().toString());
            double nextPressureValue = Double.parseDouble(nextPressure.getText().toString());
            double newStrikerBarLength = nextPressureValue / previousPressure * previousStrikerBarLength / Math.pow(nextStrainRateValue / previousStrainRate, 2);
            nextLength.setText(round(newStrikerBarLength));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculatePressure(){
        try {
            double previousStrainRate = Double.parseDouble(prevStrainRate.getText().toString());
            double previousStrikerBarLength = Double.parseDouble(prevLength.getText().toString());
            double previousPressure = Double.parseDouble(prevPressure.getText().toString());
            double nextStrainRateValue = Double.parseDouble(nextStrainRate.getText().toString());
            double nextStrikerBarLength = Double.parseDouble(nextLength.getText().toString());
            double newPressure = Math.pow(nextStrainRateValue / previousStrainRate, 2) * previousPressure / previousStrikerBarLength * nextStrikerBarLength;
            nextPressure.setText(round(newPressure));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String round (double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }


    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Validation Failed")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void setupUI(View view) {
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
