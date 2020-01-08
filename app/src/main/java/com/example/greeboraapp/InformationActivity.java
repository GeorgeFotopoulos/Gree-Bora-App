package com.example.greeboraapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class InformationActivity extends AppCompatActivity {
    TextToSpeech TTS;
    ArrayList<TextToSpeech> TTSs = new ArrayList<>();
    String sentenceToSay = "";
    boolean firstTime = true;
    boolean soundOn = false;
    int animateSound = 0;
    int modeCount = 2, swingCount = 1, fanCount = 1, temperatureReal = 21, minutesToCount = 0, pStatus = 0, countDown = 0;
    boolean timerOn = false, sleepOn = false, cleanOn = false, on = false, stopped = false, hideMoreOptions, welcome = true, leaveNow = false;
    private GestureDetector gesture;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        gesture = new GestureDetector(new InformationActivity.SwipeGestureDetector());
        final ImageButton sound = findViewById(R.id.note);
        final ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        TreeMap<String, List<String>> item = new TreeMap<>();
        ArrayList<String> blindMode = new ArrayList<>();

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
                timerOn = false;
                onContinueTimer();
            }
            cleanOn = intent.getExtras().getBoolean("clean");
            hideMoreOptions = intent.getExtras().getBoolean("hide");
            welcome = intent.getExtras().getBoolean("welcome");
            temperatureReal = intent.getExtras().getInt("temperature");
        }

        String temp = "1. Για την ενεργοποίηση του κλιματιστικού: Ενεργοποίηση ή Άνοιξε."
                + "\n\n2. Για την απενεργοποίηση του κλιματιστικού: Απενεργοποίηση ή Κλείσε."
                + "\n\n3. Για την αύξηση θερμοκρασίας: Αύξησε ή Πάνω ή Ανέβα ή Ανέβασε και τον αριθμό των βαθμών."
                + "\n\n4. Για την μείωση θερμοκρασίας: Μείωσε ή Κάτω ή Κατέβα ή Κατέβασε και τον αριθμό των βαθμών."
                + "\n\n5. Για αλλαγή θερμοκρασίας σε συγκεκριμένους βαθμούς: Θερμοκρασία και τον αριθμό των βαθμών."
                + "\n\n6. Για αλλαγή της λειτουργίας: Λειτουργία ακολουθούμενη από μια εκ των παρακάτω λέξεων, Αυτόματη ή Ψυχρή ή Αφύγρανση ή Ανεμιστήρας ή Θερμή."
                + "\n\n7. Για αλλαγή της ανάκλισης: Ανάκλιση ακολουθούμενη από μια εκ των παρακάτω λέξεων, Πάνω ή Μέση ή Κάτω ή Ολική."
                + "\n\n8. Για αλλαγή της ταχύτητας: Ταχύτητα ακολουθούμενη από μια εκ των παρακάτω λέξεων, Αυτόματη ή Χαμηλή ή Μεσαία ή Υψηλή."
                + "\n\n9. Για ενεργοποίηση χρονοδιακόπτη: Ενεργοποίηση Χρονοδιακόπτη ή Άνοιξε Χρονοδιακόπτη ακολουθούμενη από τα λεπτά που επιθυμεί ο χρήστης να είναι σε λειτουργία το κλιματιστικό."
                + "\n\n10. Για την απενεργοποίηση του χρονοδιακόπτη: Απενεργοποίηση Χρονοδιακόπτη ή Κλείσε Χρονοδιακόπτη."
                + "\n\n11. Για ενεργοποίηση ή απενεργοποίηση αδρανοποίησης: Αδρανοποίηση."
                + "\n\n12. Για ενεργοποίηση ή απενεργοποίηση ιονισμού: Ιονισμός ή Καθαρισμός."
                + "\n\n13. Για ενημέρωση σχετικά με την κατάσταση του κλιματιστικού: Ενημέρωση."
                + "\n\n14. Για μετακίνηση στη λειτουγρία με κουμπιά: Λειτουργία ακολουθούμενη από μια εκ των παρακάτω λέξεων, Πλήκτρων, ή Κουμπιών."
                + "\n\n15. Για μετακίνηση στις οδηγίες του κλιματιστικού: Οδηγίες ή Βοήθεια.";
        blindMode.add(temp);
        item.put("Blind Mode", blindMode);
        ArrayList<String> buttonMode = new ArrayList<>();
        buttonMode.add("1. Για την ενεργοποίηση και την απενεργοποίηση του κλιματιστικού πατήστε το κουμπί με την ένδειξη Power."
                + "\n\n2. Για την αύξηση της θερμοκρασίας, πατήστε το κουμπί με το πάνω βελάκι."
                + "\n\n3. Για την μείωση της θερμοκρασίας, πατήστε το κουμπί με το κάτω βελάκι."
                + "\n\n4. Για την επέκταση του μενού και την εμφάνιση των επιπλέων λειτουργιών του κλιματιστικού, πατήστε το κουμπί που αναβοσβήνει, στο κάτω μέρος της οθόνης."
                + "\n\n5. Για αλλαγή λειτουργίας μεταξύ αυτόματης, ψυχρής, αφύγρανσης, ανεμιστήρα ή θερμής, πατήστε το κουμπί MODE."
                + "\n\n6. Για αλλαγή της ανάκλισης μεταξύ ολικής, κάτω, μεσαίας και υψηλής, πατήστε το κουμπί SWING."
                + "\n\n7. Για αλλαγή της ταχύτητας του ανεμιστήρα μεταξύ αυτόματης, χαμηλής, μεσαίας και υψηλής, πατήστε το κουμπί FAN."
                + "\n\n8. Για την ενεργοποίηση του χρονοδιακόπτη, πατήστε το κουμπί TIMER. Με διαδοχικά πατήματα του κουμπιού, προστίθενται στον χρονοδιακόπτη 15 λεπτά, έως και τα 180.. Εάν ξαναπατηθεί το κουμπί TIMER, ο Χρονοδιακόπτης απενεργοποιείται."
                + "\n\n9. Για την ενεργοποίηση της αδρανοποίησης, πατήστε το κουμπί SLEEP"
                + "\n\n10. Για την ενεργοποίηση του ιονισμού, πατήστε το κουμπί CLEAN"
                + "\n\n11. Για την ενημέρωση σχετικά με την κατάσταση του κλιματιστικού, πατήστε το κουμπί TEMP");
        item.put("Button Mode", buttonMode);

        final MyExpandableListAdapter adapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = adapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        expandableListView.collapseGroup(i);
                    }
                }
                if (expandableListView.isGroupExpanded(0)) {
                    sentenceToSay = "Καλωσορίσατε στη λειτουργία φωνητικών εντολών..."
                            + "Για την ενεργοποίηση του κλιματιστικού πείτε: 'Ενεργοποίηση', ή 'Άνοιξε'..."
                            + "Για την απενεργοποίηση του κλιματιστικού πείτε: 'Απενεργοποίηση', ή 'Κλείσε'..."
                            + "Για την αύξηση θερμοκρασίας πείτε: 'Αύξησε', ή 'Πάνω', ή 'Ανέβα', ή 'Ανέβασε', και τον αριθμό των βαθμών..."
                            + "Για την μείωση θερμοκρασίας πείτε: 'Μείωσε', ή 'Κάτω', ή 'Κατέβα', ή 'Κατέβασε',  και τον αριθμό των βαθμών..."
                            + "Για αλλαγή θερμοκρασίας σε συγκεκριμένο αριθμό πείτε: 'Θερμοκρασία', και τον αριθμό των βαθμών..."
                            + "Για αλλαγή της λειτουργίας πείτε: 'Λειτουργία', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Ψυχρή', 'Αφύγρανση', 'Ανεμιστήρας', ή 'Θερμή'..."
                            + "Για αλλαγή της ανάκλισης πείτε: 'Ανάκλιση', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Πάνω', 'Μέση', 'Κάτω', ή 'Ολική'..."
                            + "Για αλλαγή της ταχύτητας του ανεμιστήρα πείτε: 'Ταχύτητα', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Αυτόματη', 'Χαμηλή', 'Μεσαία', ή 'Υψηλή'..."
                            + "Για ενεργοποίηση χρονοδιακόπτη πείτε: 'Ενεργοποίηση Χρονοδιακόπτη', ή 'Άνοιξε Χρονοδιακόπτη', ακολουθούμενη από τα λεπτά που επιθυμείτε να είναι σε λειτουργία το κλιματιστικό..."
                            + "Για την απενεργοποίηση του χρονοδιακόπτη πείτε: 'Απενεργοποίηση Χρονοδιακόπτη', ή 'Κλείσε Χρονοδιακόπτη'..."
                            + "Για ενεργοποίηση ή απενεργοποίηση αδρανοποίησης πείτε: 'Αδρανοποίηση'..."
                            + "Για ενεργοποίηση ή απενεργοποίηση ιονισμού πείτε: 'Ιονισμός', ή 'Καθαρισμός'..."
                            + "Για ενημέρωση σχετικά με την κατάσταση του κλιματιστικού πείτε: 'Ενημέρωση'..."
                            + "Για μετακίνηση στη λειτουγρία με κουμπιά πείτε: 'Λειτουργία', ακολουθούμενη από μια εκ των παρακάτω λέξεων.. 'Πλήκτρων', ή 'Κουμπιών'..."
                            + "Για μετακίνηση στις οδηγίες του κλιματιστικού πείτε: 'Οδηγίες', ή 'Βοήθεια'...";
                } else if (expandableListView.isGroupExpanded(1)) {
                    sentenceToSay = "Καλωσορίσατε στη λειτουργία με πλήκτρα..."
                            + "Για την ενεργοποίηση και την απενεργοποίηση του κλιματιστικού πατήστε το κουμπί με την ένδειξη Power..."
                            + "Για την αύξηση της θερμοκρασίας, πατήστε το κουμπί με το πάνω βελάκι..."
                            + "Για την μείωση της θερμοκρασίας, πατήστε το κουμπί με το κάτω βελάκι..."
                            + "Για την επέκταση του μενού και την εμφάνιση των επιπλέων λειτουργιών του κλιματιστικού, πατήστε το κουμπί που αναβοσβήνει, στο κάτω μέρος της οθόνης..."
                            + "Για αλλαγή λειτουργίας μεταξύ αυτόματης, ψυχρής, αφύγρανσης, ανεμιστήρα ή θερμής, πατήστε το κουμπί 'MODE'..."
                            + "Για αλλαγή της ανάκλισης μεταξύ ολικής, κάτω, μεσαίας και υψηλής, πατήστε το κουμπί SWING..."
                            + "Για αλλαγή της ταχύτητας του ανεμιστήρα μεταξύ αυτόματης, χαμηλής, μεσαίας και υψηλής, πατήστε το κουμπί 'FAN'..."
                            + "Για την ενεργοποίηση του χρονοδιακόπτη, πατήστε το κουμπί TIMER. Με διαδοχικά πατήματα του κουμπιού, προστίθενται στον χρονοδιακόπτη 15 λεπτά, έως και τα 180.. Εάν ξαναπατηθεί το κουμπί TIMER, ο Χρονοδιακόπτης απενεργοποιείται..."
                            + "Για την ενεργοποίηση της αδρανοποίησης, πατήστε το κουμπί SLEEP..."
                            + "Για την ενεργοποίηση του ιονισμού, πατήστε το κουμπί CLEAN..."
                            + "Για την ενημέρωση σχετικά με την κατάσταση του κλιματιστικού, πατήστε το κουμπί TEMP...";
                }
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (!expandableListView.isGroupExpanded(0) && !expandableListView.isGroupExpanded(0)) {
                    sentenceToSay = "";
                    onStopTalking();
                    sound.setImageResource(R.drawable.ic_sound_off);
                }
            }
        });

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
                            if (!sentenceToSay.equals("")) {
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
                            }
                            if (firstTime) {
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                TTSs.add(TTS);
                                firstTime = false;
                            } else {
                                if (!TTS.isSpeaking() && (expandableListView.isGroupExpanded(0) || expandableListView.isGroupExpanded(1))) {
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    TTSs.add(TTS);
                                } else {
                                    onStopTalking();
                                    sound.setImageResource(R.drawable.ic_sound_off);
                                }
                            }
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
        onStopTalking();
        finish();
        Intent myIntent = new Intent(InformationActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        myIntent.putExtra("welcome", false);
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

    private void onContinueTimer() {
        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("el_GR");
                    TTS.setLanguage(localeToUse);
                    TTS.setPitch((float) 0.9);
                    pStatus = minutesToCount - countDown;
                    timerOn = true;

                    if (minutesToCount != -1) {

                        new Thread(new Runnable() {
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
                                        Thread.sleep(1000);
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
                        }).start();
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        this.finish();
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
