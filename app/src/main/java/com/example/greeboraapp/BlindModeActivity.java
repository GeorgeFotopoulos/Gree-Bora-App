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
import java.util.Map;

public class BlindModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    ObjectAnimator swipeLeftAnim, swipeRightAnim;
    TextToSpeech TTS;
    String sentenceToSay, modeStr = "ψυχρή", swingStr = "ολική", fanStr = "αυτόματη";
    ArrayList<String> command;
    HashMap<Integer, String> grades = new HashMap<>();
    int modeChoice = -1, swingChoice = -1, fanChoice = -1, currentTemp = 21;
    boolean timer = false, sleep = false, ionization = false, on = false;
    GestureDetector gesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blind_mode);

        grades.put(1, "έναν");
        grades.put(3, "τρεις");
        grades.put(4, "τέσσερις");
        grades.put(13, "δεκατρείς");
        grades.put(14, "δεκατέσσερις");
        grades.put(23, "εικοσιτρείς");
        grades.put(24, "εικοσιτέσσερις");

        gesture = new GestureDetector(new BlindModeActivity.SwipeGestureDetector());
        TextView swipeLeft = findViewById(R.id.swipeLeft);
        TextView swipeRight = findViewById(R.id.swipeRight);

        swipeLeftAnim = ObjectAnimator.ofInt(swipeLeft, "textColor", Color.BLACK, Color.TRANSPARENT);
        swipeLeftAnim.setDuration(1000);
        swipeLeftAnim.setEvaluator(new ArgbEvaluator());
        swipeLeftAnim.setRepeatCount(ValueAnimator.INFINITE);
        swipeLeftAnim.setRepeatMode(ValueAnimator.REVERSE);
        swipeLeftAnim.start();

        swipeRightAnim = ObjectAnimator.ofInt(swipeRight, "textColor", Color.BLACK, Color.TRANSPARENT);
        swipeRightAnim.setDuration(1000);
        swipeRightAnim.setEvaluator(new ArgbEvaluator());
        swipeRightAnim.setRepeatCount(ValueAnimator.INFINITE);
        swipeRightAnim.setRepeatMode(ValueAnimator.REVERSE);
        swipeRightAnim.start();
    }

    private void readCommand() {
        if (command != null) {
            TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        Locale localeToUse = new Locale("el_GR");
                        TTS.setPitch((float) 0.9);
                        TTS.setLanguage(localeToUse);
                    }
                    sentenceToSay = "";
                    for (int j = 0; j < command.size(); j++) {
                        if ((command.get(j).toLowerCase().contains("απενεργοποίησ") && !command.get(j).toLowerCase().contains(" ")) || (command.get(j).toLowerCase().contains("απενεργοποίησ") && command.get(j).toLowerCase().contains("κλιματιστικ"))) {
                            if (on) {
                                on = false;
                                sentenceToSay = "Το κλιματιστικό απενεργοποιήθηκε.";
                            } else {
                                sentenceToSay = "Το κλιματιστικό βρίσκεται ήδη εκτός λειτουργίας.";
                            }
                            command.removeAll(command);
                        } else if ((command.get(j).toLowerCase().contains("ενεργοποίησ") && !command.get(j).toLowerCase().contains(" ")) || (command.get(j).toLowerCase().contains("ενεργοποίησ") && command.get(j).toLowerCase().contains("κλιματιστικ"))) {
                            if (!on) {
                                on = true;
                                sentenceToSay = "Το κλιματιστικό ενεργοποιήθηκε.";
                            } else {
                                sentenceToSay = "Το κλιματιστικό βρίσκεται ήδη σε λειτουργία.";
                            }
                            command.removeAll(command);
                        }
                    }
                    if (on) {
                        for (int j = 0; j < command.size(); j++) {
                            if (command.get(j).toLowerCase().contains("απενεργοποίησ") || command.get(j).toLowerCase().contains("κλείσε")) {
                                if (command.get(j).toLowerCase().contains("χρονοδιακόπτη")) {
                                    if (timer) {
                                        timer = false;
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("αδρανοποίηση")) {
                                    if (sleep) {
                                        sleep = false;
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("ιονισμ") || command.get(j).toLowerCase().contains("καθαρισμ")) {
                                    if (ionization) {
                                        ionization = false;
                                        sentenceToSay = "Η λειτουργία ιονισμού απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία ιονισμού είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                }
                            } else if (command.get(j).toLowerCase().contains("ενεργοποίησ") || command.get(j).toLowerCase().contains("άνοιξε")) {
                                if (command.get(j).toLowerCase().contains("χρονοδιακόπτη")) {
                                    if (!timer) {
                                        timer = true;
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη είναι ήδη ενεργή.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("αδρανοποίηση")) {
                                    if (!sleep) {
                                        sleep = true;
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης ενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης είναι ήδη ενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("ιονισμ") || command.get(j).toLowerCase().contains("καθαρισμ")) {
                                    if (!ionization) {
                                        ionization = true;
                                        sentenceToSay = "Η λειτουργία ιονισμού ενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία ιονισμού είναι ήδη ενεργοποιημένη.";
                                    }
                                    break;
                                }
                            } else if (command.get(j).toLowerCase().contains("αύξησε") || command.get(j).toLowerCase().contains("ανέβασε") || command.get(j).toLowerCase().contains("αύξηση") || command.get(j).toLowerCase().contains("ανέβα") || command.get(j).toLowerCase().contains("πάνω")) {
                                ArrayList<String> splitCommand = new ArrayList<>();
                                int temp = 0;
                                String tempStr = "";
                                for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                    splitCommand.add(command.get(j).split(" ")[i]);
                                }
                                for (int i = 0; i < splitCommand.size(); i++) {
                                    try {
                                        temp = Integer.parseInt(splitCommand.get(i));
                                        break;
                                    } catch (Exception e) {
                                    }
                                    if (grades.containsKey(splitCommand.get(i))) {
                                        tempStr = grades.get(i);
                                        break;
                                    }
                                }
                                if (!tempStr.equals("")) {
                                    if (tempStr.equalsIgnoreCase("μηδέν")) {
                                        sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                    } else if (tempStr.equalsIgnoreCase("ένα") || tempStr.equalsIgnoreCase("έναν")) {
                                        sentenceToSay = "Η θερμοκρασία αυξήθηκε κατά " + tempStr + " βαθμό Κελσίου.";
                                        currentTemp += 1;
                                    } else {
                                        sentenceToSay = "Η θερμοκρασία αυξήθηκε κατά " + tempStr + " βαθμούς Κελσίου.";
                                        currentTemp += Integer.parseInt(tempStr);
                                    }
                                } else {
                                    if (temp == 0) {
                                        sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                    } else if (temp == 1) {
                                        sentenceToSay = "Η θερμοκρασία αυξήθηκε κατά " + temp + " βαθμό Κελσίου.";
                                        currentTemp += temp;
                                    } else {
                                        sentenceToSay = "Η θερμοκρασία αυξήθηκε κατά " + temp + " βαθμούς Κελσίου.";
                                        currentTemp += temp;
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("μείωσε") || command.get(j).toLowerCase().contains("κατέβασε") || command.get(j).toLowerCase().contains("μείωση") || command.get(j).toLowerCase().contains("κατέβα") || command.get(j).toLowerCase().contains("κάτω")) {
                                ArrayList<String> splitCommand = new ArrayList<>();
                                int temp = -1;
                                for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                    splitCommand.add(command.get(j).split(" ")[i]);
                                }
                                for (int i = 0; i < splitCommand.size(); i++) {
                                    for (Map.Entry<Integer, String> entry : grades.entrySet()) {
                                        if (entry.getValue().equalsIgnoreCase(splitCommand.get(i))) {
                                            temp = entry.getKey();
                                            break;
                                        }
                                    }
                                    if (temp == -1) {
                                        try {
                                            temp = Integer.parseInt(splitCommand.get(i));
                                            break;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (temp >= 0) {
                                    if (temp == 0) {
                                        sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                    } else if (temp == 1) {
                                        sentenceToSay = "Η θερμοκρασία μειώθηκε κατά ένα βαθμό Κελσίου.";
                                    } else {
                                        if (grades.containsKey(temp)) {
                                            sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + grades.get(temp) + " βαθμούς Κελσίου.";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + temp + " βαθμούς Κελσίου.";
                                        }
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("λειτουργία")) {
                                if (command.get(j).toLowerCase().contains("αυτόματη")) {
                                    if (modeChoice != 0) {
                                        modeStr = "αυτόματη";
                                        sentenceToSay = "Αυτόματη λειτουργία ενεργή.";
                                        modeChoice = 0;
                                    } else {
                                        sentenceToSay = "Η αυτόματη λειτουργία είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("ψυχρή")) {
                                    if (modeChoice != 1) {
                                        modeStr = "ψυχρή";
                                        sentenceToSay = "Ψυχρή λειτουργία ενεργή.";
                                        modeChoice = 1;
                                    } else {
                                        sentenceToSay = "Η ψυχρή λειτουργία είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("αφύγρανση")) {
                                    if (modeChoice != 2) {
                                        modeStr = "αφύγρανσης";
                                        sentenceToSay = "Λειτουργία αφύγρανσης ενεργή.";
                                        modeChoice = 2;
                                    } else {
                                        sentenceToSay = "Η λειτουργία αφύγρανσης είναι ηδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("ανεμιστήρα")) {
                                    if (modeChoice != 3) {
                                        modeStr = "ανεμιστήρα";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα ενεργή.";
                                        modeChoice = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα είναι ηδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("θερμή")) {
                                    if (modeChoice != 4) {
                                        modeStr = "θερμή";
                                        sentenceToSay = "Θερμή λειτουργία ενεργή.";
                                        modeChoice = 4;
                                    } else {
                                        sentenceToSay = "Η θερμή λειτουργία είναι ηδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ανάκλιση")) {
                                if (command.get(j).toLowerCase().contains("ολική")) {
                                    if (swingChoice != 0) {
                                        swingStr = "ολική";
                                        sentenceToSay = "Λειτουργία ολικής ανάκλισης ενεργή.";
                                        swingChoice = 0;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ολικής ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("κάτω")) {
                                    if (swingChoice != 1) {
                                        swingStr = "κάτω";
                                        sentenceToSay = "Λειτουργία κάτω ανάκλισης ενεργή.";
                                        swingChoice = 1;
                                    } else {
                                        sentenceToSay = "Η λειτουργία κάτω ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("μέση")) {
                                    if (swingChoice != 2) {
                                        swingStr = "μέση";
                                        sentenceToSay = "Λειτουργία μέσης ανάκλισης ενεργή.";
                                        swingChoice = 2;
                                    } else {
                                        sentenceToSay = "Η λειτουργία μέσης ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("πάνω")) {
                                    if (swingChoice != 3) {
                                        swingStr = "πάνω";
                                        sentenceToSay = "Λειτουργία πάνω ανάκλισης ενεργή.";
                                        swingChoice = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία πάνω ανάκλισης είναι ήδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ταχύτητα")) {
                                if (command.get(j).toLowerCase().contains("αυτόματη")) {
                                    if (fanChoice != 0) {
                                        fanStr = "αυτόματη";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα ενεργή.";
                                        fanChoice = 0;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("χαμηλή")) {
                                    if (fanChoice != 1) {
                                        fanStr = "χαμηλή";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα ενεργή.";
                                        fanChoice = 1;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("μεσαία")) {
                                    if (fanChoice != 2) {
                                        fanStr = "μεσαία";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε μεσαία ταχύτητα ενεργή.";
                                        fanChoice = 2;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε μεσαία ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("υψηλή")) {
                                    if (fanChoice != 3) {
                                        fanStr = "υψηλή";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε υψηλή ταχύτητα ενεργή.";
                                        fanChoice = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε υψηλή ταχύτητα είναι ήδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ενημέρωση")) {
                                if (modeStr.equalsIgnoreCase("ανεμιστήρα") || modeStr.equalsIgnoreCase("αφύγρανσης")) {
                                    sentenceToSay = "Η θερμοκρασία είναι στους " + currentTemp + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε λειτουργία " + modeStr + ", με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                } else {
                                    sentenceToSay = "Η θερμοκρασία είναι στους " + currentTemp + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                }
                                if (sleep) {
                                    sentenceToSay += "Η λειτουργία ύπνου είναι ενεργή..";
                                }
                                if (ionization) {
                                    if (sleep) {
                                        sentenceToSay += "Οι λειτουργίες ύπνου και ιονισμού είναι ενεργές..";
                                    } else {
                                        sentenceToSay += "Η λειτουργία ιονισμού είναι ενεργή..";
                                    }
                                }
                                if (timer) {
                                    if (!sleep && !ionization) {
                                        sentenceToSay += "Ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα..";
                                    } else {
                                        sentenceToSay += " και ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα..";
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (!sentenceToSay.equals("")) {
                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        sentenceToSay = "";
                        command.removeAll(command);
                    } else {
                        if (on) {
                            sentenceToSay = "Η εντολή που δώσατε δεν αναγνωρίστηκε! Επαναλάβετε...";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            while (TTS.isSpeaking()) {
                            }
                            findViewById(R.id.miccommand).performClick();
                        } else {
                            sentenceToSay = "";
                        }
                    }
                }
            });
        }
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
        readCommand();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gesture.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onRight() {
        finish();
        Intent myIntent = new Intent(BlindModeActivity.this, InformationActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(myIntent);
    }

    private void onLeft() {
        finish();
        Intent myIntent = new Intent(BlindModeActivity.this, ButtonModeActivity.class);
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

                if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    BlindModeActivity.this.onRight();
                }

                if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    BlindModeActivity.this.onLeft();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures1");
            }
            return false;
        }
    }

}
