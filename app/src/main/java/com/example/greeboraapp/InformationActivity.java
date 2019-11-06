package com.example.greeboraapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class InformationActivity extends AppCompatActivity {
    TextToSpeech TTS;
    ArrayList<TextToSpeech> TTSs = new ArrayList<>();
    String sentenceToSay;
    boolean firstTime = true;
    boolean soundOn = false;
    int animateSound = 0;
    TextView text_view;
    private GestureDetector gesture;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        gesture = new GestureDetector(new InformationActivity.SwipeGestureDetector());

        final ImageButton sound = findViewById(R.id.note);

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
                            if (soundOn) {
                                soundOn = false;
                                sound.setImageResource(R.drawable.ic_sound_off);
                            } else {
                                soundOn = true;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    animateSound = 0;
                                    while (soundOn) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (animateSound == 0) {
                                                    sound.setImageResource(R.drawable.ic_sound_on_1);
                                                } else if (animateSound == 1) {
                                                    sound.setImageResource(R.drawable.ic_sound_on_2);
                                                } else {
                                                    sound.setImageResource(R.drawable.ic_sound_on);
                                                }
                                                if (!TTS.isSpeaking()) {
                                                    soundOn = false;
                                                    sound.setImageResource(R.drawable.ic_sound_off);
                                                }
                                            }
                                        });
                                        try {
                                            Thread.sleep(1000);
                                            animateSound++;
                                            if (animateSound == 3) {
                                                animateSound = 0;
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();

                            sentenceToSay = "Για την ενεργοποίηση του κλιματιστικού πείτε: 'Ενεργοποίηση', ή 'Άνοιξε'..."
                                    + "Για την απενεργοποίηση του κλιματιστικού πείτε: 'Απενεργοποίηση', ή 'Κλείσε'..."
                                    + "Για την αύξηση θερμοκρασίας πείτε: 'Αύξησε', ή 'Πάνω', ή 'Ανέβα', ή 'Ανέβασε', και τον αριθμό των βαθμών..."
                                    + "Για την μείωση θερμοκρασίας πείτε: 'Μείωσε', ή 'Κάτω', ή 'Κατέβα', ή 'Κατέβασε',  και τον αριθμό των βαθμών..."
                                    + "Για αλλαγή θερμοκρασίας σε συγκεκριμένο αριθμό πείτε: 'Θερμοκρασία', και τον αριθμό των βαθμών..."
                                    + "Για αλλαγή της λειτουργίας πείτε: 'Λειτουργία', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Ψυχρή', 'Αφύγρανση', 'Ανεμιστήρας', ή 'Θερμή'..."
                                    + "Για αλλαγή της ανάκλισης πείτε: 'Ανάκλιση', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Πάνω', 'Μέση', 'Κάτω', ή 'Ολική'..."
                                    + "Για αλλαγή της ταχύτητας πείτε: 'Ταχύτητα', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Χαμηλή', 'Μεσαία', ή 'Υψηλή'..."
                                    + "Για ενεργοποίηση χρονοδιακόπτη πείτε: 'Ενεργοποίηση Χρονοδιακόπτη', ή 'Άνοιξε Χρονοδιακόπτη', ακολουθούμενη από τα λεπτά που επιθυμείτε να είναι σε λειτουργία το κλιματιστικό..."
                                    + "Για την απενεργοποίηση του χρονοδιακόπτη πείτε: 'Απενεργοποίηση Χρονοδιακόπτη', ή 'Κλείσε Χρονοδιακόπτη'..."
                                    + "Για ενεργοποίηση ή απενεργοποίηση αδρανοποίησης πείτε: 'Αδρανοποίηση'..."
                                    + "Για ενεργοποίηση ή απενεργοποίηση ιονισμού πείτε: 'Ιονισμός', ή 'Καθαρισμός'..."
                                    + "Για ενημέρωση σχετικά με την κατάσταση του κλιματιστικού πείτε: 'Ενημέρωση'...";

                            if (firstTime) {
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                TTSs.add(TTS);
                                firstTime = false;
                            } else {
                                if (!TTS.isSpeaking()) {
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    TTSs.add(TTS);
                                } else {
                                    onStopTalking();
                                }
                            }
                        }
                    }
                });
            }
        });


        text_view = findViewById(R.id.text_view);
        text_view.setText((Html.fromHtml("<p>1. Για την ενεργοποίηση του κλιματιστικού: <b>Ενεργοποίηση</b> ή <b>Άνοιξε</b>.</p>"
                + "<p>2. Για την απενεργοποίηση του κλιματιστικού: <b>Απενεργοποίηση</b> ή <b>Κλείσε</b>.</p>"
                + "<p>3. Για την αύξηση θερμοκρασίας: <b>Αύξησε</b> ή <b>Πάνω</b> ή <b>Ανέβα</b> ή <b>Ανέβασε</b> και τον αριθμό των βαθμών.</p>"
                + "<p>4. Για την μείωση θερμοκρασίας: <b>Μείωσε</b> ή <b>Κάτω</b> ή <b>Κατέβα</b> ή <b>Κατέβασε</b> και τον αριθμό των βαθμών.</p>"
                + "<p>5. Για αλλαγή θερμοκρασίας σε συγκεκριμένους βαθμούς: <b>Θερμοκρασία</b> και τον αριθμό των βαθμών.</p>"
                + "<p>6. Για αλλαγή της λειτουργίας: <b>Λειτουργία</b> ακολουθούμενη από μια εκ των παρακάτω λέξεων, <b>Αυτόματη</b> ή <b>Ψυχρή</b> ή <b>Αφύγρανση</b> ή <b>Ανεμιστήρας</b> ή <b>Θερμή</b>.</p>"
                + "<p>7. Για αλλαγή της ανάκλισης: <b>Ανάκλιση</b> ακολουθούμενη από μια εκ των παρακάτω λέξεων, <b>Πάνω</b> ή <b>Μέση</b> ή <b>Κάτω</b> ή <b>Ολική</b>.</p>"
                + "<p>8. Για αλλαγή της ταχύτητας: <b>Ταχύτητα</b> ακολουθούμενη από μια εκ των παρακάτω λέξεων, <b>Αυτόματη</b> ή <b>Χαμηλή</b> ή <b>Μεσαία</b> ή <b>Υψηλή</b>.</p>"
                + "<p>9. Για ενεργοποίηση χρονοδιακόπτη: <b>Ενεργοποίηση Χρονοδιακόπτη</b> ή <b>Άνοιξε Χρονοδιακόπτη</b> ακολουθούμενη από τα λεπτά που επιθυμεί ο χρήστης να είναι σε λειτουργία το κλιματιστικό.</p>"
                + "<p>10. Για την απενεργοποίηση του χρονοδιακόπτη: <b>Απενεργοποίηση Χρονοδιακόπτη</b> ή <b>Κλείσε Χρονοδιακόπτη</b>.</p>"
                + "<p>11. Για ενεργοποίηση ή απενεργοποίηση αδρανοποίησης: <b>Αδρανοποίηση</b>.</p>"
                + "<p>12. Για ενεργοποίηση ή απενεργοποίηση ιονισμού: <b>Ιονισμός</b> ή <b>Καθαρισμός</b>.</p>"
                + "<p>13. Για ενημέρωση σχετικά με την κατάσταση του κλιματιστικού: <b>Ενημέρωση</b>.</p>")));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gesture.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeft() {
        onStopTalking();
        finish();
        Intent myIntent = new Intent(InformationActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(myIntent);
    }

    private void onStopTalking() {
        for (int i = 0; i < TTSs.size(); i++) {
            if (TTSs.get(i).isSpeaking()) {
                TTSs.get(i).stop();
                TTSs.get(i).shutdown();
            }
        }
    }

    @Override
    public void onPause() {
        onStopTalking();
        super.onPause();
        this.finish();
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
