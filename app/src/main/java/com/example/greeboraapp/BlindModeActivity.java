package com.example.greeboraapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class BlindModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    ObjectAnimator textColorAnim;
    TextToSpeech TTS;
    String sentenceToSay;
    boolean on = false;
    ArrayList<String> command;
    HashMap<String, String> grades = new HashMap<>();
    private GestureDetector gesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blind_mode);

        grades.put("έναν", "έναν");
        grades.put("τρεις", "τρεις");
        grades.put("τέσσερις", "τέσσερις");
        grades.put("δεκατρείς", "δεκατρείς");
        grades.put("δεκατέσσερις", "δεκατέσσερις");

        gesture = new GestureDetector(new SwipeGestureDetector());
        TextView txt = findViewById(R.id.swipeLeft);

        textColorAnim = ObjectAnimator.ofInt(txt, "flicker", Color.BLACK, Color.TRANSPARENT);
        textColorAnim.setDuration(1000);
        textColorAnim.setEvaluator(new ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    command = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                }
                break;
        }

        if (command != null) {
            TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        Locale localeToUse = new Locale("el_GR");
                        TTS.setPitch((float) 0.8);
                        TTS.setLanguage(localeToUse);
                    }
                    if (command.get(0).equalsIgnoreCase("Ενεργοποίηση") || command.get(0).equalsIgnoreCase("Άνοιξε")) {
                        if (!on) {
                            sentenceToSay = "Το κλιματιστικό ενεργοποιήθηκε.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            on = true;
                        }
                    } else if (command.get(0).equalsIgnoreCase("Απενεργοποίηση") || command.get(0).equalsIgnoreCase("Κλείσε")) {
                        if (on) {
                            sentenceToSay = "Το κλιματιστικό απενεργοποιήθηκε.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            on = false;
                        }
                    } else if (command.get(0).toLowerCase().contains("αύξησε") || command.get(0).toLowerCase().contains("αύξηση") || command.get(0).toLowerCase().contains("πάνω")) {
                        ArrayList<String> st = new ArrayList<>();
                        int temp = 0;
                        String tempStr = "";
                        for (int i = 0; i < command.get(0).split(" ").length; i++) {
                            st.add(command.get(0).split(" ")[i]);
                        }
                        for (int i = 0; i < st.size(); i++) {
                            try {
                                temp = Integer.parseInt(st.get(i));
                                break;
                            } catch (Exception e) {
                            }
                            if (grades.containsKey(st.get(i))) {
                                tempStr = grades.get((st.get(i)));
                                break;
                            }
                        }
                        if (on) {
                            if(!tempStr.equals("")) {
                                sentenceToSay = "Αύξηση κατά " + tempStr + " βαθμούς Κελσίου.";
                            } else {
                                sentenceToSay = "Αύξηση κατά " + temp + " βαθμούς Κελσίου.";
                            }
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gesture.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeft() {
        finish();
        Intent myIntent = new Intent(BlindModeActivity.this, GiantModeActivity.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(myIntent);
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "el_GR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInit(int status) {
    }

    // Private class for gestures
    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    BlindModeActivity.this.onLeft();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures");
            }
            return false;
        }
    }
}
