package com.example.greeboraapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

    final int MAX_TEMP = 30, MIN_TEMP = 16;
    int modeCount = 2, swingCount = 1, fanCount = 1, temperatureShow = 0, temperatureReal = 21, minutesToCount = 0, pStatus = 0, countDown = 0;
    String sentenceToSay, modeStr = "ψυχρή", swingStr = "ολική", fanStr = "αυτόματη";
    boolean timerOn = false, sleepOn = false, cleanOn = false, on = false, stopped = false, hideMoreOptions, welcome = true, leaveNow = false;
    HashMap<Integer, String> grades = new HashMap<Integer, String>() {{
        put(1, "έναν");
        put(3, "τρεις");
        put(4, "τέσσερις");
        put(13, "δεκατρείς");
        put(14, "δεκατέσσερις");
        put(23, "εικοσιτρείς");
        put(24, "εικοσιτέσσερις");
    }};
    ObjectAnimator swipeLeftAnim, swipeRightAnim;
    Handler handler = new Handler();
    Thread timerThread = new Thread();
    ArrayList<String> command = new ArrayList<>();
    GestureDetector gesture;
    TextToSpeech TTS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blind_mode);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            on = intent.getExtras().getBoolean("onOff");
            modeCount = intent.getExtras().getInt("mode");
            swingCount = intent.getExtras().getInt("swing");
            fanCount = intent.getExtras().getInt("fan");
            sleepOn = intent.getExtras().getBoolean("sleep");
            timerOn = intent.getExtras().getBoolean("timer");
            if (timerOn) {
                minutesToCount = intent.getExtras().getInt("timerFull");
                countDown = intent.getExtras().getInt("timerCount");
                command.add("Ενεργοποίηση Χρονοδιακόπτη");
                timerOn = false;
                readCommand();
            }
            cleanOn = intent.getExtras().getBoolean("clean");
            hideMoreOptions = intent.getExtras().getBoolean("hide");
            welcome = intent.getExtras().getBoolean("welcome");
            temperatureReal = intent.getExtras().getInt("temperature");
        }

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


        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("el_GR");
                    TTS.setPitch((float) 0.9);
                    TTS.setLanguage(localeToUse);
                    if (welcome) {
                        TTS.speak("Καλωσορίσατε!", TextToSpeech.QUEUE_ADD, null);
                    } else {
                        TTS.speak("Φωνητική λειτουργία", TextToSpeech.QUEUE_ADD, null);
                    }

                }
            }
        });

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
                        if (command.get(j).toLowerCase().contains("λειτουργία")) {
                            if (command.get(j).toLowerCase().contains("κουμπι") || command.get(j).toLowerCase().contains("πλήκτρ")) {
                                onLeft();
                                sentenceToSay = " ";
                            }
                        }
                        if (command.get(j).toLowerCase().contains("οδηγίες") || command.get(j).toLowerCase().contains("βοήθεια")) {
                            sentenceToSay = " ";
                            onRight();
                        }

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
                                    if (timerOn) {
                                        timerOn = false;
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("αδρανοποίηση")) {
                                    if (sleepOn) {
                                        sleepOn = false;
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("ιονισμ") || command.get(j).toLowerCase().contains("καθαρισμ")) {
                                    if (cleanOn) {
                                        cleanOn = false;
                                        sentenceToSay = "Η λειτουργία ιονισμού απενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία ιονισμού είναι ήδη απενεργοποιημένη.";
                                    }
                                    break;
                                }
                            } else if (command.get(j).toLowerCase().contains("ενεργοποίησ") || command.get(j).toLowerCase().contains("άνοιξε")) {
                                if (command.get(j).toLowerCase().contains("χρονοδιακόπτη")) {
                                    if (!timerOn) {
                                        if (minutesToCount == 0) {
                                            ArrayList<String> splitCommand = new ArrayList<>();
                                            minutesToCount = -1;
                                            for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                                splitCommand.add(command.get(j).split(" ")[i]);
                                            }
                                            for (int i = 0; i < splitCommand.size(); i++) {

                                                if (minutesToCount == -1) {
                                                    try {
                                                        minutesToCount = Integer.parseInt(splitCommand.get(i));
                                                        pStatus = 0;
                                                        timerOn = true;
                                                        countDown = minutesToCount;
                                                        break;
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }

                                        } else {
                                            pStatus = minutesToCount - countDown;
                                            timerOn = true;
                                        }
                                        if (minutesToCount != -1) {

                                            //timerOn Start
                                            timerThread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    while (pStatus <= minutesToCount) {
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (leaveNow) {
                                                                    stopped = true;
                                                                    pStatus = pStatus + minutesToCount;
                                                                }
                                                            }
                                                        });
                                                        try {
                                                            timerThread.sleep(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        pStatus++;
                                                        countDown--;
                                                    }

                                                    if (!stopped) {
                                                        TTS.speak("Το κλιματιστικό απενεργοποιήθηκε.", TextToSpeech.QUEUE_ADD, null);
                                                        on = false;
                                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                                        } else {
                                                            v.vibrate(500);
                                                        }
                                                    } else {
                                                        stopped = false;
                                                    }
                                                    timerOn = false;

                                                }
                                            });
                                            timerThread.start();
                                            //Loader End
                                            sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε για " + minutesToCount + " λεπτά";
                                        }
                                    } else {
                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη είναι ήδη ενεργή.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("αδρανοποίηση")) {
                                    if (!sleepOn) {
                                        sleepOn = true;
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης ενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία αδρανοποίησης είναι ήδη ενεργοποιημένη.";
                                    }
                                    break;
                                } else if (command.get(j).toLowerCase().contains("ιονισμ") || command.get(j).toLowerCase().contains("καθαρισμ")) {
                                    if (!cleanOn) {
                                        cleanOn = true;
                                        sentenceToSay = "Η λειτουργία ιονισμού ενεργοποιήθηκε.";
                                    } else {
                                        sentenceToSay = "Η λειτουργία ιονισμού είναι ήδη ενεργοποιημένη.";
                                    }
                                    break;
                                }
                            } else if (command.get(j).toLowerCase().contains("αύξησ") || command.get(j).toLowerCase().contains("ανέβα") || command.get(j).toLowerCase().contains("πάνω")) {
                                ArrayList<String> splitCommand = new ArrayList<>();
                                temperatureShow = -1;
                                for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                    splitCommand.add(command.get(j).split(" ")[i]);
                                }
                                for (int i = 0; i < splitCommand.size(); i++) {
                                    for (Map.Entry<Integer, String> entry : grades.entrySet()) {
                                        if (entry.getValue().equalsIgnoreCase(splitCommand.get(i))) {
                                            temperatureShow = entry.getKey();
                                            break;
                                        }
                                    }
                                    if (temperatureShow == -1) {
                                        try {
                                            temperatureShow = Integer.parseInt(splitCommand.get(i));
                                            break;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (temperatureShow >= 0 && (temperatureReal + temperatureShow <= MAX_TEMP)) {
                                    temperatureReal = temperatureReal + temperatureShow;
                                    if (temperatureShow == 0) {
                                        sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                    } else {
                                        if (grades.containsKey(temperatureReal)) {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + grades.get(temperatureReal) + " βαθμούς Κελσίου.";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + temperatureReal + " βαθμούς Κελσίου.";
                                        }
                                    }
                                    break;
                                } else if (temperatureShow >= 0 && (temperatureReal + temperatureShow > MAX_TEMP)) {
                                    temperatureReal = MAX_TEMP;
                                    sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + MAX_TEMP + " βαθμούς Κελσίου, καθώς είναι το ανώτατο όριο.";
                                    break;
                                }

                            } else if (command.get(j).toLowerCase().contains("μείωσ") || command.get(j).toLowerCase().contains("κατέβα") || command.get(j).toLowerCase().contains("κάτω")) {
                                ArrayList<String> splitCommand = new ArrayList<>();
                                temperatureShow = -1;
                                for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                    splitCommand.add(command.get(j).split(" ")[i]);
                                }
                                for (int i = 0; i < splitCommand.size(); i++) {
                                    for (Map.Entry<Integer, String> entry : grades.entrySet()) {
                                        if (entry.getValue().equalsIgnoreCase(splitCommand.get(i))) {
                                            temperatureShow = entry.getKey();
                                            break;
                                        }
                                    }
                                    if (temperatureShow == -1) {
                                        try {
                                            temperatureShow = Integer.parseInt(splitCommand.get(i));
                                            break;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (temperatureShow >= 0 && (temperatureReal - temperatureShow >= MIN_TEMP)) {
                                    temperatureReal = temperatureReal - temperatureShow;
                                    if (temperatureShow == 0) {
                                        sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                    } else {
                                        if (grades.containsKey(temperatureReal)) {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + grades.get(temperatureReal) + " βαθμούς Κελσίου.";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + temperatureReal + " βαθμούς Κελσίου.";
                                        }
                                    }
                                    break;
                                } else if (temperatureShow >= 0 && (temperatureReal - temperatureShow < MIN_TEMP)) {
                                    temperatureReal = MIN_TEMP;
                                    sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + MIN_TEMP + " βαθμούς Κελσίου, καθώς είναι το κατώτατο όριο.";
                                    break;
                                }

                            } else if (command.get(j).toLowerCase().contains("λειτουργία")) {
                                if (command.get(j).toLowerCase().contains("αυτόματ")) {
                                    if (modeCount != 1) {
                                        modeStr = "αυτόματη";
                                        sentenceToSay = "Αυτόματη λειτουργία ενεργή.";
                                        modeCount = 1;
                                    } else {
                                        sentenceToSay = "Η αυτόματη λειτουργία είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("ψυχρ")) {
                                    if (modeCount != 2) {
                                        modeStr = "ψυχρή";
                                        sentenceToSay = "Ψυχρή λειτουργία ενεργή.";
                                        modeCount = 2;
                                    } else {
                                        sentenceToSay = "Η ψυχρή λειτουργία είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("αφύγρανση")) {
                                    if (modeCount != 3) {
                                        modeStr = "αφύγρανσης";
                                        sentenceToSay = "Λειτουργία αφύγρανσης ενεργή.";
                                        modeCount = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία αφύγρανσης είναι ηδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("ανεμιστήρα")) {
                                    if (modeCount != 4) {
                                        modeStr = "ανεμιστήρα";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα ενεργή.";
                                        modeCount = 4;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα είναι ηδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("θερμή")) {
                                    if (modeCount != 5) {
                                        modeStr = "θερμή";
                                        sentenceToSay = "Θερμή λειτουργία ενεργή.";
                                        modeCount = 5;
                                    } else {
                                        sentenceToSay = "Η θερμή λειτουργία είναι ηδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ανάκλιση")) {
                                if (command.get(j).toLowerCase().contains("ολικ")) {
                                    if (swingCount != 1) {
                                        swingStr = "ολική";
                                        sentenceToSay = "Λειτουργία ολικής ανάκλισης ενεργή.";
                                        swingCount = 1;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ολικής ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("κάτω")) {
                                    if (swingCount != 2) {
                                        swingStr = "κάτω";
                                        sentenceToSay = "Λειτουργία κάτω ανάκλισης ενεργή.";
                                        swingCount = 2;
                                    } else {
                                        sentenceToSay = "Η λειτουργία κάτω ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("μέση")) {
                                    if (swingCount != 3) {
                                        swingStr = "μέση";
                                        sentenceToSay = "Λειτουργία μέσης ανάκλισης ενεργή.";
                                        swingCount = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία μέσης ανάκλισης είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("πάνω")) {
                                    if (swingCount != 4) {
                                        swingStr = "πάνω";
                                        sentenceToSay = "Λειτουργία πάνω ανάκλισης ενεργή.";
                                        swingCount = 4;
                                    } else {
                                        sentenceToSay = "Η λειτουργία πάνω ανάκλισης είναι ήδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ταχύτητα")) {
                                if (command.get(j).toLowerCase().contains("αυτόματ")) {
                                    if (fanCount != 1) {
                                        fanStr = "αυτόματη";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα ενεργή.";
                                        fanCount = 1;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("χαμηλ")) {
                                    if (fanCount != 2) {
                                        fanStr = "χαμηλή";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα ενεργή.";
                                        fanCount = 2;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("μεσαί")) {
                                    if (fanCount != 3) {
                                        fanStr = "μεσαία";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε μεσαία ταχύτητα ενεργή.";
                                        fanCount = 3;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε μεσαία ταχύτητα είναι ήδη ενεργή.";
                                    }
                                } else if (command.get(j).toLowerCase().contains("ψηλ")) {
                                    if (fanCount != 4) {
                                        fanStr = "υψηλή";
                                        sentenceToSay = "Λειτουργία ανεμιστήρα σε υψηλή ταχύτητα ενεργή.";
                                        fanCount = 4;
                                    } else {
                                        sentenceToSay = "Η λειτουργία ανεμιστήρα σε υψηλή ταχύτητα είναι ήδη ενεργή.";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("ενημέρωση")) {
                                if (grades.containsKey(temperatureReal)) {
                                    if (modeStr.equalsIgnoreCase("ανεμιστήρα") || modeStr.equalsIgnoreCase("αφύγρανσης")) {
                                        sentenceToSay = "Η θερμοκρασία είναι στους " + grades.get(temperatureReal) + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε λειτουργία " + modeStr + ", με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                    } else {
                                        sentenceToSay = "Η θερμοκρασία είναι στους " + grades.get(temperatureReal) + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                    }
                                } else {
                                    if (modeStr.equalsIgnoreCase("ανεμιστήρα") || modeStr.equalsIgnoreCase("αφύγρανσης")) {
                                        sentenceToSay = "Η θερμοκρασία είναι στους " + temperatureReal + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε λειτουργία " + modeStr + ", με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                    } else {
                                        sentenceToSay = "Η θερμοκρασία είναι στους " + temperatureReal + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                    }
                                }
                                if (sleepOn) {
                                    sentenceToSay += "Η λειτουργία ύπνου είναι ενεργή..";
                                }
                                if (cleanOn) {
                                    if (sleepOn) {
                                        sentenceToSay += "Οι λειτουργίες ύπνου και ιονισμού είναι ενεργές..";
                                    } else {
                                        sentenceToSay += "Η λειτουργία ιονισμού είναι ενεργή..";
                                    }
                                }
                                if (timerOn) {
                                    if (!sleepOn && !cleanOn) {
                                        sentenceToSay += "Ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα..";
                                    } else {
                                        sentenceToSay += " και ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα..";
                                    }
                                }
                                break;
                            } else if (command.get(j).toLowerCase().contains("θερμοκρασία")) {
                                ArrayList<String> splitCommand = new ArrayList<>();
                                temperatureShow = -1;
                                for (int i = 0; i < command.get(j).split(" ").length; i++) {
                                    splitCommand.add(command.get(j).split(" ")[i]);
                                }
                                for (int i = 0; i < splitCommand.size(); i++) {
                                    for (Map.Entry<Integer, String> entry : grades.entrySet()) {
                                        if (entry.getValue().equalsIgnoreCase(splitCommand.get(i))) {
                                            temperatureShow = entry.getKey();
                                            break;
                                        }
                                    }
                                    if (temperatureShow == -1) {
                                        try {
                                            temperatureShow = Integer.parseInt(splitCommand.get(i));
                                            break;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (temperatureShow >= MIN_TEMP && temperatureShow <= MAX_TEMP) {
                                    if (temperatureReal == temperatureShow) {
                                        if (grades.containsKey(temperatureReal)) {
                                            sentenceToSay = "Η θερμοκρασία βρίσκεται ήδη στους " + grades.get(temperatureReal) + " βαθμούς.";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία βρίσκεται ήδη στους " + temperatureReal + " βαθμούς.";
                                        }
                                    } else {
                                        temperatureReal = temperatureShow;
                                        if (grades.containsKey(temperatureReal)) {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + grades.get(temperatureReal) + " βαθμούς Κελσίου.";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία ρυθμίστηκε στους " + temperatureReal + " βαθμούς Κελσίου.";
                                        }
                                    }
                                    break;
                                } else {
                                    sentenceToSay = "Το κλιματιστικό δέχεται από " + MIN_TEMP + " , έως και " + MAX_TEMP + " βαθμούς Κελσίου.";
                                    break;
                                }
                            }
                        }
                    }
                    if (!sentenceToSay.equals("")) {
                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        sentenceToSay = "";
                        command.removeAll(command);
                    } else {
                        if (on) {
                            sentenceToSay = "Η εντολή που δώσατε δεν αναγνωρίστηκε!";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
        myIntent.putExtra("onOff", on);
        myIntent.putExtra("mode", modeCount);
        myIntent.putExtra("swing", swingCount);
        myIntent.putExtra("fan", fanCount);
        myIntent.putExtra("sleep", sleepOn);
        if (timerOn) {
            myIntent.putExtra("timerFull", minutesToCount);
            myIntent.putExtra("timerCount", countDown);
            leaveNow = true;
            timerOn = true;
        }
        myIntent.putExtra("timer", timerOn);
        myIntent.putExtra("clean", cleanOn);
        myIntent.putExtra("hide", hideMoreOptions);
        myIntent.putExtra("temperature", temperatureReal);
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
