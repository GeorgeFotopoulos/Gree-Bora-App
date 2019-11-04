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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class GiantModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech TTS;
    String sentenceToSay;
    String modeStr = "Ψυχρή";
    String fanStr = "Αυτόματη";
    String swingStr = "Ολική";
    double timeStr = 0;
    double countDown = 0;
    boolean hideMoreOptions = false;
    boolean on = false;
    boolean stopped = false;
    int checkIn = 0;
    int fanCount = 0;
    int swingCount = 0;
    int modeCount = 1;
    int temperatureDif = 0;
    int timeToSet = 0;
    long firstClickUp = 0;
    long firstClickDown = 0;
    boolean sleepOn = false;
    boolean timerOn = false;
    boolean cleanOn = false;
    boolean tempSaid = false;
    final int MAX_TEMP = 30;
    final int MIN_TEMP = 16;
    ObjectAnimator textColorAnim;
    HashMap<Integer, String> grades = new HashMap<>();
    private GestureDetector gesture;
    int temperature = 21;
    int temperatureShowReal = 21;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giant_mode);
        grades.put(1, "έναν");
        grades.put(3, "τρεις");
        grades.put(4, "τέσσερις");
        grades.put(13, "δεκατρείς");
        grades.put(14, "δεκατέσσερις");
        grades.put(23, "εικοσιτρείς");
        grades.put(24, "εικοσιτέσσερις");
        final Handler mHandler = new Handler();

        gesture = new GestureDetector(new GiantModeActivity.SwipeGestureDetector());

        final Button fan = findViewById(R.id.fan);
        fan.setVisibility(View.GONE);
        final Button swing = findViewById(R.id.swing);
        swing.setVisibility(View.GONE);
        final Button sleep = findViewById(R.id.sleep);
        sleep.setVisibility(View.GONE);
        final Button timer = findViewById(R.id.timer);
        timer.setVisibility(View.GONE);
        final Button temp = findViewById(R.id.temp);
        temp.setVisibility(View.GONE);
        final Button clean = findViewById(R.id.clean);
        clean.setVisibility(View.GONE);

        final TextView tempShow = findViewById(R.id.tempShow);
        tempShow.setVisibility(View.VISIBLE);
        final TextView mode = findViewById(R.id.modeShow);
        mode.setVisibility(View.INVISIBLE);
        final ImageView fanDisp = findViewById(R.id.fanShow);
        fanDisp.setVisibility(View.INVISIBLE);
        fanDisp.setColorFilter(Color.parseColor("#808080"));
        final ImageView swingDisp = findViewById(R.id.swingShow);
        swingDisp.setVisibility(View.INVISIBLE);
        final ImageView cleanDisp = findViewById(R.id.cleanShow);
        cleanDisp.setVisibility(View.INVISIBLE);
        final ProgressBar timerDisp = findViewById(R.id.timerShow);
        timerDisp.setVisibility(View.INVISIBLE);
        final ImageView sleepDisp = findViewById(R.id.sleepShow);
        sleepDisp.setVisibility(View.INVISIBLE);
        final TextView gradeDisp = findViewById(R.id.gradeShow);
        gradeDisp.setVisibility(View.INVISIBLE);
        final TextView progressDisp = findViewById(R.id.txtProgress);
        progressDisp.setVisibility(View.INVISIBLE);

        tempShow.setTextSize(50);
        tempShow.setText("OFF");
        gradeDisp.setText("");

        final TextView hideShow = findViewById(R.id.options);
        textColorAnim = ObjectAnimator.ofInt(hideShow, "textColor", Color.BLACK, Color.TRANSPARENT);
        textColorAnim.setDuration(1000);
        textColorAnim.setEvaluator(new ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim.start();

        findViewById(R.id.onoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setPitch((float) 0.8);
                            TTS.setLanguage(localeToUse);
                            if (!on) {
                                ImageButton onOff = findViewById(R.id.onoff);
                                onOff.setImageResource(R.drawable.ic_on);
                                sentenceToSay = "Ενεργοποίηση";
                                tempShow.setTextSize(120);
                                tempShow.setText(temperatureShowReal+"");
                                gradeDisp.setText("℃");
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                on = true;
                                gradeDisp.setVisibility(View.VISIBLE);
                                mode.setVisibility(View.VISIBLE);
                                fanDisp.setVisibility(View.VISIBLE);
                                swingDisp.setVisibility(View.VISIBLE);
                                if (sleepOn) {
                                    sleepDisp.setVisibility(View.VISIBLE);
                                }
                                if (cleanOn) {
                                    cleanDisp.setVisibility(View.VISIBLE);
                                }
                                if (timerOn) {
                                    timerOn = false;
                                }
                            } else {
                                ImageButton onOff = findViewById(R.id.onoff);
                                onOff.setImageResource(R.drawable.ic_off);
                                sentenceToSay = "Απενεργοποίηση";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                on = false;

                                tempShow.setTextSize(50);
                                tempShow.setText("OFF");
                                gradeDisp.setText("");
                                progressDisp.setVisibility(View.INVISIBLE);
                                gradeDisp.setVisibility(View.INVISIBLE);
                                mode.setVisibility(View.INVISIBLE);
                                fanDisp.setVisibility(View.INVISIBLE);
                                swingDisp.setVisibility(View.INVISIBLE);
                                cleanDisp.setVisibility(View.INVISIBLE);
                                timerDisp.setVisibility(View.INVISIBLE);
                                sleepDisp.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setPitch((float) 0.9);
                            TTS.setLanguage(localeToUse);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                temperatureShowReal--;
                                if (temperatureShowReal >= MIN_TEMP) {
                                    tempShow.setText(temperatureShowReal + "");
                                } else {
                                    temperatureShowReal = MIN_TEMP;
                                    tempShow.setText(temperatureShowReal + "");
                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (temperature - temperatureShowReal != 0) {
                                                if (temperatureShowReal > MIN_TEMP) {
                                                    temperatureDif = temperature - temperatureShowReal;
                                                    if (grades.containsKey(temperatureDif)) {
                                                        if (temperatureDif == 1) {
                                                            sentenceToSay = "Μείωση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμό.";
                                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                        } else {
                                                            sentenceToSay = "Μείωση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμούς.";
                                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                        }
                                                    } else {
                                                        sentenceToSay = "Μείωση θερμοκρασίας κατά " + temperatureDif + " βαθμούς.";
                                                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);

                                                    }
                                                    temperature = temperatureShowReal;
                                                } else {
                                                    sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι " + MIN_TEMP + " βαθμούς.";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                    temperature = MIN_TEMP;
                                                }
                                            }
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                if (!timerOn) {
                                    if ((timeToSet + 15) <= 180) {
                                        timeToSet = timeToSet + 15;
                                        if(timeToSet >99 ){
                                            tempShow.setTextSize(90);
                                        }else{
                                            tempShow.setTextSize(120);
                                        }
                                        tempShow.setText(timeToSet + "");
                                        gradeDisp.setText("'");
                                    } else {
                                        timeToSet = 0;
                                        tempShow.setTextSize(50);
                                        tempShow.setText("Cancel");
                                        gradeDisp.setText("");
                                    }
                                } else if (timerOn) {
                                    checkIn = 1;
                                }
                                final ObjectAnimator timeAnim;
                                timeAnim = ObjectAnimator.ofInt(tempShow, "textColor", Color.TRANSPARENT, Color.BLACK);
                                if (timerOn) {
                                    timeAnim.setDuration(0);
                                    timeAnim.setRepeatCount(0);
                                } else {
                                    timeAnim.setDuration(600);
                                    timeAnim.setRepeatCount(ValueAnimator.INFINITE);
                                }
                                timeAnim.setEvaluator(new ArgbEvaluator());
                                timeAnim.setRepeatMode(ValueAnimator.REVERSE);
                                timeAnim.start();

                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        final ProgressBar timer = findViewById(R.id.timerShow);
                                        timeAnim.setDuration(0);
                                        timeAnim.setRepeatCount(0);
                                        if (System.currentTimeMillis() - firstClickDown >= 2050) {

                                            if (timeToSet == 0)
                                                timerOn = true;

                                            if (timerOn) {
                                                checkIn = 0;
                                                timer.setVisibility(View.INVISIBLE);
                                                progressDisp.setVisibility(View.INVISIBLE);
                                                tempShow.setTextSize(120);
                                                tempShow.setText(temperatureShowReal + "");
                                                gradeDisp.setText("℃");
                                                timerOn = false;
                                            } else {
                                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε για " + timeToSet + " λεπτά.";
                                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                timer.setVisibility(View.VISIBLE);
                                                timerOn = true;
                                                tempShow.setText(temperatureShowReal + "");
                                                gradeDisp.setText("℃");
                                                timeStr = timeToSet;
                                                timeToSet = 0;
                                                countDown = timeStr;
                                                pStatus = 0;

                                                //Loader Start
                                                txtProgress = findViewById(R.id.txtProgress);
                                                progressBar = findViewById(R.id.timerShow);
                                                progressDisp.setVisibility(View.VISIBLE);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        countDown = timeStr;
                                                        progressBar.setMax((int) timeStr);
                                                        while (pStatus <= (int) timeStr) {
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progressBar.setProgress(pStatus);
                                                                    if (countDown >= 0) {
                                                                        txtProgress.setText((int) countDown + "'");
                                                                    }
                                                                    if (checkIn == 1) {
                                                                        stopped = true;
                                                                        pStatus += timeStr;
                                                                        sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                                                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        progressDisp.setVisibility(View.INVISIBLE);
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
                                                        mHandler.post(new Runnable() {
                                                            public void run() {
                                                                if (!stopped) {
                                                                    final AlertDialog alertDialog = new AlertDialog.Builder(GiantModeActivity.this).create();
                                                                    alertDialog.setTitle("Time's Up!");
                                                                    alertDialog.setMessage("Το κλιματιστικό κλείνει.");
                                                                    alertDialog.show();

                                                                    TTS.speak("Το κλιματιστικό κλείνει.", TextToSpeech.QUEUE_ADD, null);

                                                                    //wait
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        public void run() {

                                                                            if (hideMoreOptions) {
                                                                                findViewById(R.id.options).performClick();
                                                                                hideMoreOptions = false;
                                                                            }
                                                                            findViewById(R.id.onoff).performClick();
                                                                            alertDialog.hide();
                                                                        }
                                                                    }, 3000);

                                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                                                    } else {
                                                                        v.vibrate(500);
                                                                    }
                                                                } else {
                                                                    stopped = false;
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).start();
                                                //Loader End
                                            }
                                        }
                                    }
                                }, 2100);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setPitch((float) 0.9);
                            TTS.setLanguage(localeToUse);

                            if (on) {
                                firstClickUp = System.currentTimeMillis();
                                temperatureShowReal++;
                                if (temperatureShowReal <= MAX_TEMP) {
                                    tempShow.setText(temperatureShowReal + "");
                                } else {
                                    temperatureShowReal = MAX_TEMP;
                                    tempShow.setText(temperatureShowReal + "");
                                }

                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickUp >= 950) {
                                            if (temperatureShowReal - temperature != 0) {
                                                if (temperatureShowReal < MAX_TEMP) {
                                                    temperatureDif = temperatureShowReal - temperature;
                                                    if (grades.containsKey(temperatureDif)) {
                                                        if (temperatureDif == 1) {
                                                            sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμό";
                                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                        } else {
                                                            sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμούς";
                                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                        }
                                                    } else {
                                                        sentenceToSay = "Αύξηση θερμοκρασίας κατά " + temperatureDif + " βαθμούς";
                                                        TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                    }
                                                    temperature = temperatureShowReal;
                                                } else {
                                                    sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι " + MAX_TEMP + " βαθμούς.";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                    temperature = MAX_TEMP;
                                                }
                                            }
                                        }

                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                modeCount++;
                                if (modeCount == 6) {
                                    modeCount = 1;
                                }

                                if (modeCount == 1) {
                                    mode.setText("❆");
                                    modeStr = "Ψυχρή";
                                } else if (modeCount == 2) {
                                    mode.setText("⛆");
                                    modeStr = "Αφύγρανση";
                                } else if (modeCount == 3) {
                                    mode.setText("✤");
                                    modeStr = "Ανεμιστήρα";
                                } else if (modeCount == 4) {
                                    mode.setText("☼");
                                    modeStr = "Θερμή";
                                } else {
                                    mode.setText("A");
                                    modeStr = "Αυτόματη";
                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (modeCount == 1) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ψυχρή.";
                                            } else if (modeCount == 2) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Αφύγρανση.";
                                            } else if (modeCount == 3) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ανεμιστήρα.";
                                            } else if (modeCount == 4) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Θερμή.";
                                            } else {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Αυτόματη.";
                                            }
                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hideMoreOptions) {
                    hideShow.setText("︽");
                    fan.setVisibility(View.VISIBLE);
                    swing.setVisibility(View.VISIBLE);
                    sleep.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    temp.setVisibility(View.VISIBLE);
                    clean.setVisibility(View.VISIBLE);

                    final ScrollView sv = findViewById(R.id.scroll);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                    hideMoreOptions = true;
                } else {
                    hideShow.setText("︾");
                    fan.setVisibility(View.GONE);
                    swing.setVisibility(View.GONE);
                    sleep.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    temp.setVisibility(View.GONE);
                    clean.setVisibility(View.GONE);

                    hideMoreOptions = false;
                    final ScrollView sv = findViewById(R.id.scroll);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(View.FOCUS_UP);
                        }
                    });
                }
            }
        });

        findViewById(R.id.fan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                fanCount++;
                                if (fanCount == 5) {
                                    fanCount = 1;
                                }
                                if (fanCount == 1) {
                                    fanDisp.setImageResource(R.drawable.volume_low);
                                    fanStr = "χαμηλή";
                                } else if (fanCount == 2) {
                                    fanDisp.setImageResource(R.drawable.volume_mid);
                                    fanStr = "μεσαία";
                                } else if (fanCount == 3) {
                                    fanDisp.setImageResource(R.drawable.volume_full);
                                    fanStr = "υψηλή";
                                } else {
                                    fanDisp.setImageResource(R.drawable.volume_auto);
                                    fanStr = "αυτόματη";

                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (fanCount == 1) {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε χαμηλή.";
                                            } else if (fanCount == 2) {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε μεσαία.";
                                            } else if (fanCount == 3) {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε υψηλή.";
                                            } else {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε αυτόματη.";
                                            }
                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.swing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {

                                firstClickDown = System.currentTimeMillis();
                                swingCount++;
                                if (swingCount == 5) {
                                    swingCount = 1;
                                }
                                if (swingCount == 1) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_down);
                                    swingStr = "χαμηλή";
                                } else if (swingCount == 2) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_mid);
                                    swingStr = "μεσαία";
                                } else if (swingCount == 3) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_up);
                                    swingStr = "υψηλή";
                                } else {
                                    swingDisp.setImageResource(R.drawable.fan_swing_auto);
                                    swingStr = "ολική";
                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {

                                            if (swingCount == 1) {
                                                sentenceToSay = "Η κατεύθυνση ρυθμίστηκε σε χαμηλή";
                                            } else if (swingCount == 2) {
                                                sentenceToSay = "Η κατεύθυνση ρυθμίστηκε σε μεσαία";
                                            } else if (swingCount == 3) {
                                                sentenceToSay = "Η κατεύθυνση ρυθμίστηκε σε υψηλή";
                                            } else {
                                                sentenceToSay = "Η κατεύθυνση ρυθμίστηκε σε ολική";
                                            }
                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.sleep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (sleepOn) {
                                                sentenceToSay = "Η λειτουργία ύπνου απενεργοποιήθηκε.";
                                                sleepDisp.setVisibility(View.INVISIBLE);
                                                sleepOn = false;
                                            } else {
                                                sentenceToSay = "Η λειτουργία ύπνου ενεργοποιήθηκε.";
                                                sleepDisp.setVisibility(View.VISIBLE);
                                                sleepDisp.setImageResource(R.drawable.moon);
                                                sleepOn = true;
                                            }
                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });


        findViewById(R.id.temp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {

                                if (!tempSaid) {
                                    if (modeStr != "Ανεμιστήρα" && modeStr != "Αφύγρανση") {
                                        if (grades.containsKey(temperatureShowReal)) {
                                            sentenceToSay = "Η θερμοκρασία είναι στους " + grades.get(temperatureShowReal) + "βαθμούς κελσίου, σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                        } else {
                                            sentenceToSay = "Η θερμοκρασία είναι στους " + temperatureShowReal + "βαθμούς κελσίου, σε " + modeStr + " λειτουργία, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                        }
                                    } else {
                                        if (grades.containsKey(temperatureShowReal)) {
                                            if (modeStr != "Ανεμιστήρα") {
                                                sentenceToSay = "Η θερμοκρασία είναι στους " + grades.get(temperatureShowReal) + "βαθμούς κελσίου, σε λειτουργία " + modeStr + "ς, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                            } else {
                                                sentenceToSay = "Η θερμοκρασία είναι στους " + grades.get(temperatureShowReal) + "βαθμούς κελσίου, σε λειτουργία " + modeStr + ", με την έντασή του σε  " + fanStr + " και " + swingStr + " ανάκλιση..";
                                            }
                                        } else {
                                            if (modeStr != "Ανεμιστήρα") {
                                                sentenceToSay = "Η θερμοκρασία είναι στους " + temperatureShowReal + "βαθμούς κελσίου, σε λειτουργία " + modeStr + "ς, με " + fanStr + " ένταση ανεμιστήρα και " + swingStr + " ανάκλιση..";
                                            } else {
                                                sentenceToSay = "Η θερμοκρασία είναι στους " + temperatureShowReal + "βαθμούς κελσίου, σε λειτουργία " + modeStr + ", με την έντασή του σε  " + fanStr + " και " + swingStr + " ανάκλιση..";
                                            }
                                        }
                                    }
                                    String extras = "";
                                    if (sleepOn) {
                                        sleepDisp.setVisibility(View.VISIBLE);
                                        extras = "Η λειτουργία ύπνου είναι ενεργή.";
                                    }
                                    if (cleanOn) {
                                        cleanDisp.setVisibility(View.VISIBLE);
                                        if (sleepOn) {
                                            extras = "Οι λειτουργίες ύπνου και καθαρισμού, είναι ενεργές.";
                                        } else {
                                            extras = "Η λειτουργία καθαρισμού είναι ενεργή.";
                                        }
                                    }
                                    if (timerOn) {
                                        timerDisp.setVisibility(View.VISIBLE);
                                        if (sleepOn || cleanOn) {
                                            extras = extras + "και ο χρονοδιακόπτης έχει ρυθμιστεί για " + (int) countDown + " λεπτά ακόμα";
                                        } else {
                                            extras = "ο χρονοδιακόπτης έχει ρυθμιστεί για " + (int) countDown + " λεπτά ακόμα";
                                        }
                                    }
                                    if (sleepOn || cleanOn || timerOn) {
                                        sentenceToSay = sentenceToSay + "Επιπλέον, " + extras;
                                    }
                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                    tempSaid = true;

                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        public void run() {
                                            tempSaid = false;
                                        }
                                    }, 6000);
                                }
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setLanguage(localeToUse);
                            TTS.setPitch((float) 0.9);
                            if (on) {
                                firstClickDown = System.currentTimeMillis();
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {

                                            if (cleanOn) {
                                                sentenceToSay = "Η λειτουργία καθαρισμού απενεργοποιήθηκε.";
                                                cleanDisp.setVisibility(View.INVISIBLE);
                                                cleanOn = false;
                                            } else {
                                                sentenceToSay = "Η λειτουργία καθαρισμού ενεργοποιήθηκε.";
                                                cleanDisp.setVisibility(View.VISIBLE);
                                                cleanOn = true;
                                            }
                                            TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
            }
        });
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
    public boolean onTouchEvent(MotionEvent event) {
        if (gesture.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onRight() {
        finish();
        Intent myIntent = new Intent(GiantModeActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(myIntent);
        myIntent.putExtra("onOff", on);
        myIntent.putExtra("mode", modeStr);
        myIntent.putExtra("swing", swingStr);
        myIntent.putExtra("fan", fanStr);
        myIntent.putExtra("sleep", sleepOn);
        myIntent.putExtra("timer", timerOn);
        myIntent.putExtra("clean", cleanOn);
        myIntent.putExtra("temperature", temperatureShowReal);
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

                if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    GiantModeActivity.this.onRight();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures");
            }
            return false;
        }
    }

}
