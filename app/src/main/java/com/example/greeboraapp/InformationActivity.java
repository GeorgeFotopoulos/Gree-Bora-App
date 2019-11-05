package com.example.greeboraapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.util.Locale;

public class InformationActivity extends AppCompatActivity {
    TextToSpeech TTS;
    String sentenceToSay;
    long firstClickDown = 0;
    private GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        gesture = new GestureDetector(new InformationActivity.SwipeGestureDetector());

        final ImageButton noteDisp = findViewById(R.id.note);
        noteDisp.setVisibility(View.VISIBLE);


        findViewById(R.id.note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);

                            sentenceToSay = "Για την ενεργοποίηση του κλιματιστικού πείτε: 'Ενεργοποίηση', ή 'Άνοιξε'..."
                                    + "Για την απενεργοποίηση του κλιματιστικού πείτε: 'Απενεργοποίηση', ή 'Κλείσε'..."
                                    + "Για την αύξηση θερμοκρασίας πείτε: 'Αύξησε', ή 'Πάνω', και τον αριθμό των βαθμών..."
                                    + "Για την μείωση θερμοκρασίας πείτε: 'Μείωσε', ή 'Κάτω', και τον αριθμό των βαθμών..."
                                    + "Για αλλαγή της λειτουργίας πείτε: 'Λειτουργία', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Ψυχρή', 'Αφύγρανση', 'Ανεμιστήρας', ή 'Θερμή'..."
                                    + "Για αλλαγή της ανάκλισης πείτε: 'Ανάκλιση', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Πάνω', 'Μέση', 'Κάτω', ή 'Ολική'..."
                                    + "Για αλλαγή της ταχύτητας πείτε: 'Ταχύτητα', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Χαμηλή', 'Μεσαία', ή 'Υψηλή'..."
                                    + "Για ενεργοποίηση χρονοδιακόπτη πείτε: 'Ενεργοποίηση Χρονοδιακόπτη', ή 'Άνοιξε Χρονοδιακόπτη', ακολουθούμενη από τα λεπτά που επιθυμείτε να είναι σε λειτουργία το κλιματιστικό..."
                                    + "Για την απενεργοποίηση του χρονοδιακόπτη πείτε: 'Απενεργοποίηση Χρονοδιακόπτη', ή 'Κλείσε Χρονοδιακόπτη'..."
                                    + "Για ενεργοποίηση ή απενεργοποίηση αδρανοποίησης πείτε: 'Αδρανοποίηση'..."
                                    + "Για ενημέρωση σχετικά με την κατάσταση του κλιματιστικού πείτε: 'Ενημέρωση'..."
                                    + "Για ενεργοποίηση ή απενεργοποίηση ιονισμού πείτε: 'Ιονισμός', ή 'Καθαρισμός'...";

                            firstClickDown = System.currentTimeMillis();
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                public void run() {
                                    if (System.currentTimeMillis() - firstClickDown >= 950) {
                                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    }
                                }
                            }, 1000);

                        }
                    }
                });
            }
        });
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
        Intent myIntent = new Intent(InformationActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(myIntent);
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
                    InformationActivity.this.onLeft();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures2");
            }
            return false;
        }
    }
}
