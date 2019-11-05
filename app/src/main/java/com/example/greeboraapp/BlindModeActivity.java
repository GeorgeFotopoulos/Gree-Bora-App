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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class BlindModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    ObjectAnimator swipeLeftAnim, swipeRightAnim;
    TextToSpeech TTS;
    String sentenceToSay, modeStr = "ψυχρή", swingStr = "ολική", fanStr = "αυτόματη";
    boolean on = false;
    ArrayList<String> command;
    HashMap<String, Integer> unique = new HashMap<>();
    ArrayList<String> grades = new ArrayList<>();
    List<String> uniques = Arrays.asList("μηδέν", "έναν", "ένα", "δύο", "τρεις", "τέσσερις", "πέντε", "έξι", "εφτά", "οκτώ",
            "οχτώ", "εννέα", "εννιά", "δέκα", "ένδεκα", "έντεκα", "δώδεκα", "δεκατρείς", "δεκατέσσερις", "δεκαπέντε");
    int modeChoice = -1, swingChoice = -1, fanChoice = -1, currentTemp = 21, bonusModes = 0;
    boolean timer = false, sleep = false, ionization = false;
    private GestureDetector gesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blind_mode);

        grades.addAll(uniques);

        unique.put("έναν", 1);
        unique.put("τρεις", 3);
        unique.put("τέσσερις", 4);
        unique.put("δεκατρείς", 13);
        unique.put("δεκατέσσερις", 14);
        unique.put("εικοσιτρείς", 23);
        unique.put("εικοσιτέσσερις", 24);

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
                        TTS.setPitch((float) 0.9);
                        TTS.setLanguage(localeToUse);
                    }
                    // ενεργοποίηση κλιματιστικού
                    if (command.get(0).toLowerCase().contains("ενεργοποίηση") || command.get(0).toLowerCase().contains("άνοιξε")) {
                        if (!on) {
                            sentenceToSay = "Το κλιματιστικό ενεργοποιήθηκε.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            on = true;
                        } else {
                            sentenceToSay = "Το κλιματιστικό βρίσκεται ήδη σε λειτουργία.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // απενεργοποίηση κλιματιστικού
                    else if (command.get(0).toLowerCase().contains("απενεργοποίηση") || command.get(0).toLowerCase().contains("κλείσε")) {
                        if (on) {
                            sentenceToSay = "Το κλιματιστικό απενεργοποιήθηκε.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            on = false;
                        } else {
                            sentenceToSay = "Το κλιματιστικό βρίσκεται ήδη εκτός λειτουργίας.";
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // αύξηση θερμοκρασίας
                    else if (command.get(0).toLowerCase().contains("αύξησε") || command.get(0).toLowerCase().contains("ανέβασε") || command.get(0).toLowerCase().contains("αύξηση") || command.get(0).toLowerCase().contains("πάνω") || command.get(0).toLowerCase().contains("ανέβα")) {
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
                            if (grades.contains(st.get(i))) {
                                tempStr = grades.get(i);
                                break;
                            }
                        }
                        if (on) {
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
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // μείωση θερμοκρασίας
                    else if (command.get(0).toLowerCase().contains("μείωσε") || command.get(0).toLowerCase().contains("κατέβασε") || command.get(0).toLowerCase().contains("μείωση") || command.get(0).toLowerCase().contains("κάτω") || command.get(0).toLowerCase().contains("κατέβα")) {
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
                            if (grades.contains(st.get(i))) {
                                tempStr = grades.get(i);
                                break;
                            }
                        }
                        if (on) {
                            if (!tempStr.equals("")) {
                                if (tempStr.equalsIgnoreCase("μηδέν")) {
                                    sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                } else if (tempStr.equalsIgnoreCase("ένα") || tempStr.equalsIgnoreCase("έναν")) {
                                    sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + tempStr + " βαθμό Κελσίου.";
                                    currentTemp -= 1;
                                } else {
                                    if (unique.containsKey(tempStr)) {
                                        currentTemp -= unique.get(tempStr);
                                    }
                                    sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + tempStr + " βαθμούς Κελσίου.";
                                }
                            } else {
                                if (temp == 0) {
                                    sentenceToSay = "Δεν υπήρξε μεταβολή στην θερμοκρασία.";
                                } else if (temp == 1) {
                                    sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + temp + " βαθμό Κελσίου.";
                                    currentTemp -= temp;
                                } else {
                                    sentenceToSay = "Η θερμοκρασία μειώθηκε κατά " + temp + " βαθμούς Κελσίου.";
                                    currentTemp -= temp;
                                }
                            }
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // mode - λειτουργία
                    else if (command.get(0).toLowerCase().contains("λειτουργία")) {
                        if (on) {
                            if (command.get(0).toLowerCase().contains("αυτόματη")) {
                                if (modeChoice != 0) {
                                    modeStr = "αυτόματη";
                                    sentenceToSay = "Αυτόματη λειτουργία ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    modeChoice = 0;
                                } else {
                                    sentenceToSay = "Η αυτόματη λειτουργία είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("ψυχρή")) {
                                if (modeChoice != 1) {
                                    modeStr = "ψυχρή";
                                    sentenceToSay = "Ψυχρή λειτουργία ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    modeChoice = 1;
                                } else {
                                    sentenceToSay = "Η ψυχρή λειτουργία είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("αφύγρανση")) {
                                if (modeChoice != 2) {
                                    modeStr = "αφύγρανσης";
                                    sentenceToSay = "Λειτουργία αφύγρανσης ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    modeChoice = 2;
                                } else {
                                    sentenceToSay = "Η λειτουργία αφύγρανσης είναι ηδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("ανεμιστήρα")) {
                                if (modeChoice != 3) {
                                    modeStr = "ανεμιστήρα";
                                    sentenceToSay = "Λειτουργία ανεμιστήρα ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    modeChoice = 3;
                                } else {
                                    sentenceToSay = "Η λειτουργία ανεμιστήρα είναι ηδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("θερμή")) {
                                if (modeChoice != 4) {
                                    modeStr = "θερμή";
                                    sentenceToSay = "Θερμή λειτουργία ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    modeChoice = 4;
                                } else {
                                    sentenceToSay = "Η θερμή λειτουργία είναι ηδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            }
                        }
                    }
                    // swing - ανάκλιση
                    else if (command.get(0).toLowerCase().contains("ανάκλιση") || command.get(0).toLowerCase().contains("ανάκληση")) {
                        if (on) {
                            if (command.get(0).toLowerCase().contains("ολική")) {
                                if (swingChoice != 0) {
                                    swingStr = "ολική";
                                    sentenceToSay = "Λειτουργία ολικής ανάκλισης ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    swingChoice = 0;
                                } else {
                                    sentenceToSay = "Η λειτουργία ολικής ανάκλισης είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("κάτω")) {
                                if (swingChoice != 1) {
                                    swingStr = "κάτω";
                                    sentenceToSay = "Λειτουργία κάτω ανάκλισης ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    swingChoice = 1;
                                } else {
                                    sentenceToSay = "Η λειτουργία κάτω ανάκλισης είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("μέση")) {
                                if (swingChoice != 2) {
                                    swingStr = "μέση";
                                    sentenceToSay = "Λειτουργία μέσης ανάκλισης ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    swingChoice = 2;
                                } else {
                                    sentenceToSay = "Η λειτουργία μέσης ανάκλισης είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("πάνω")) {
                                if (swingChoice != 3) {
                                    swingStr = "πάνω";
                                    sentenceToSay = "Λειτουργία πάνω ανάκλισης ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    swingChoice = 3;
                                } else {
                                    sentenceToSay = "Η λειτουργία πάνω ανάκλισης είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            }
                        }
                    }
                    // fan - ταχύτητα
                    else if (command.get(0).toLowerCase().contains("ταχύτητα")) {
                        if (on) {
                            if (command.get(0).toLowerCase().contains("αυτόματη")) {
                                if (fanChoice != 0) {
                                    fanStr = "αυτόματη";
                                    sentenceToSay = "Λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    fanChoice = 0;
                                } else {
                                    sentenceToSay = "Η λειτουργία ανεμιστήρα σε αυτόματη ταχύτητα είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("χαμηλή")) {
                                if (fanChoice != 1) {
                                    fanStr = "χαμηλή";
                                    sentenceToSay = "Λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    fanChoice = 1;
                                } else {
                                    sentenceToSay = "Η λειτουργία ανεμιστήρα σε χαμηλή ταχύτητα είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("μεσαία")) {
                                if (fanChoice != 2) {
                                    fanStr = "μεσαία";
                                    sentenceToSay = "Λειτουργία ανεμιστήρα σε μεσαία ταχύτητα ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    fanChoice = 2;
                                } else {
                                    sentenceToSay = "Η λειτουργία ανεμιστήρα σε μεσαία ταχύτητα είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            } else if (command.get(0).toLowerCase().contains("υψηλή")) {
                                if (fanChoice != 3) {
                                    fanStr = "υψηλή";
                                    sentenceToSay = "Λειτουργία ανεμιστήρα σε υψηλή ταχύτητα ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    fanChoice = 3;
                                } else {
                                    sentenceToSay = "Η λειτουργία ανεμιστήρα σε υψηλή ταχύτητα είναι ήδη ενεργή.";
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                }
                            }
                        }
                    }
                    // timer - ενεργοποίηση χρονοδιακόπτη
                    else if (command.get(0).toLowerCase().contains("χρονοδιακόπτη") || (command.get(0).toLowerCase().contains("χρονοδιακόπτης"))) {
                        if (on) {
                            if (!timer) {
                                bonusModes++;
                                timer = true;
                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη βρίσκεται ήδη σε λειτουργία.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }
                    // timer off - απενεργοποίηση χρονοδιακόπτη
                    else if (command.get(0).toLowerCase().contains("χρονοδιακόπτη") && (command.get(0).toLowerCase().contains("χρονοδιακόπτης"))) {
                        if (on) {
                            if (timer) {
                                bonusModes--;
                                timer = false;
                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη βρίσκεται ήδη εκτός λειτουργίας.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }
                    // sleep - αδρανοποίηση
                    else if (command.get(0).toLowerCase().contains("αδρανοποίηση")) {
                        if (on) {
                            if (!sleep) {
                                bonusModes++;
                                sleep = true;
                                sentenceToSay = "Η λειτουργία αδρανοποίησης ενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                bonusModes--;
                                sleep = false;
                                sentenceToSay = "Η λειτουργία αδρανοποίησης απενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }
                    // temp - ενημέρωση
                    else if (command.get(0).toLowerCase().contains("ενημέρωση")) {
                        if (on) {
                            if (modeStr.equalsIgnoreCase("ανεμιστήρα") || modeStr.equalsIgnoreCase("αφύγρανσης")) {
                                sentenceToSay = "Η θερμοκρασία είναι στους " + currentTemp + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε λειτουργία " + modeStr + ", με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                sentenceToSay = "Η θερμοκρασία είναι στους " + currentTemp + " βαθμούς Κελσίου, το κλιματιστικό βρίσκεται σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            }
                            if (sleep) {
                                sentenceToSay = "Η λειτουργία ύπνου είναι ενεργή.";
                            }
                            if (ionization) {
                                if (sleep){
                                    sentenceToSay = "Οι λειτουργίες ύπνου και ιονισμού είναι ενεργές.";
                                } else {
                                    sentenceToSay = "Η λειτουργία ύπνου είναι ενεργή.";
                                }
                            }
                            if(timer){
                                if(!sleep && !ionization){
                                    sentenceToSay = "Ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα";
                                } else {
                                    sentenceToSay += " και ο χρονοδιακόπτης έχει ρυθμιστεί για περίπου 5 λεπτά ακόμα";
                                }
                            }
                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // clean - ιονισμός
                    else if (command.get(0).toLowerCase().contains("ιονισμός") || (command.get(0).toLowerCase().contains("καθαρισμός"))) {
                        if (on) {
                            if (!ionization) {
                                bonusModes++;
                                ionization = true;
                                sentenceToSay = "Η λειτουργία ιονισμού ενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                bonusModes--;
                                ionization = false;
                                sentenceToSay = "Η λειτουργία ιονισμού απενεργοποιήθηκε.";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                            }
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

                // Right swipe
                if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    BlindModeActivity.this.onRight();
                }

                // Left swipe
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
