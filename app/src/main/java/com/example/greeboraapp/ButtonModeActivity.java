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

public class ButtonModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech TTS;
    String sentenceToSay;
    String modeStr = "Ψυχρή";
    String fanStr = "Αυτόματη";
    String swingStr = "Ολική";
    int timeStr = 0;
    int countDown = 0;
    boolean hideMoreOptions;
    boolean on = false;
    boolean stopped = false;
    int checkIn = 0;
    int fanCount = 1;
    int swingCount = 1;
    int modeCount = 2;
    int temperatureDif = 0;
    int timeToSet = 0;
    long firstClickUp = 0;
    long firstClickDown = 0;
    long firstClickDownTimer = 0;
    boolean sleepOn = false;
    boolean timerOn = false;
    boolean cleanOn = false;
    boolean tempSaid = false;
    boolean canSpeak = true;
    boolean fakeTimer = false;
    boolean closeNow = false;
    long delayThat = 0;
    final int MAX_TEMP = 30;
    final int MIN_TEMP = 16;
    ObjectAnimator textColorAnim;
    HashMap<Integer, String> grades = new HashMap<Integer, String>() {{
        put(1, "έναν");
        put(3, "τρεις");
        put(4, "τέσσερις");
        put(13, "δεκατρείς");
        put(14, "δεκατέσσερις");
        put(23, "εικοσιτρείς");
        put(24, "εικοσιτέσσερις");
    }};
    private GestureDetector gesture;
    int temperature = 21;
    int tempWarn = 21;
    int temperatureShowReal = 21;
    private int pStatus = 0;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_mode);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            on = intent.getExtras().getBoolean("onOff");
            modeCount = intent.getExtras().getInt("mode");
            swingCount = intent.getExtras().getInt("swing");
            fanCount = intent.getExtras().getInt("fan");
            sleepOn = intent.getExtras().getBoolean("sleep");
            timerOn = intent.getExtras().getBoolean("timer");
            if (timerOn) {
                fakeTimer = true;
                timeStr = intent.getExtras().getInt("timerFull");
                countDown = intent.getExtras().getInt("timerCount");
            }
            cleanOn = intent.getExtras().getBoolean("clean");
            hideMoreOptions = intent.getExtras().getBoolean("hide");
            temperatureShowReal = intent.getExtras().getInt("temperature");
        }

        final Handler mHandler = new Handler();

        gesture = new GestureDetector(new ButtonModeActivity.SwipeGestureDetector());

        final ImageButton onOff = findViewById(R.id.onoff);
        final Button fan = findViewById(R.id.fan);
        final Button swing = findViewById(R.id.swing);
        final Button sleep = findViewById(R.id.sleep);
        final Button timer = findViewById(R.id.timer);
        final Button temp = findViewById(R.id.temp);
        final Button clean = findViewById(R.id.clean);
        final TextView tempShow = findViewById(R.id.tempShow);
        final TextView mode = findViewById(R.id.modeShow);
        final ImageView fanDisp = findViewById(R.id.fanShow);
        final ImageView swingDisp = findViewById(R.id.swingShow);
        final ImageView cleanDisp = findViewById(R.id.cleanShow);
        final ProgressBar progressBar = findViewById(R.id.timerShow);
        final TextView txtProgress = findViewById(R.id.txtProgress);
        final ImageView sleepDisp = findViewById(R.id.sleepShow);
        final TextView gradeDisp = findViewById(R.id.gradeShow);
        final TextView hideShow = findViewById(R.id.options);
        tempShow.setVisibility(View.VISIBLE);
        mode.setVisibility(View.INVISIBLE);
        fanDisp.setVisibility(View.INVISIBLE);
        fanDisp.setColorFilter(Color.parseColor("#808080"));
        swingDisp.setVisibility(View.INVISIBLE);
        cleanDisp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        sleepDisp.setVisibility(View.INVISIBLE);
        gradeDisp.setVisibility(View.INVISIBLE);
        txtProgress.setVisibility(View.INVISIBLE);
        txtProgress.setVisibility(View.INVISIBLE);


        if (hideMoreOptions) {
            hideMoreOptions = false;
        }
        hideShow.setText("︾");

        if (!on) {
            onOff.setImageResource(R.drawable.ic_off);
            fan.setVisibility(View.GONE);
            swing.setVisibility(View.GONE);
            sleep.setVisibility(View.GONE);
            timer.setVisibility(View.GONE);
            temp.setVisibility(View.GONE);
            clean.setVisibility(View.GONE);

            tempShow.setTextSize(80);
            tempShow.setText("OFF");
            gradeDisp.setText("");
        } else {
            onOff.setImageResource(R.drawable.ic_on);
            mode.setVisibility(View.VISIBLE);
            gradeDisp.setVisibility(View.VISIBLE);
            fanDisp.setVisibility(View.VISIBLE);
            fanDisp.setColorFilter(Color.parseColor("#808080"));
            swingDisp.setVisibility(View.VISIBLE);

            if (!hideMoreOptions) {
                fan.setVisibility(View.GONE);
                swing.setVisibility(View.GONE);
                sleep.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                temp.setVisibility(View.GONE);
                clean.setVisibility(View.GONE);
            }

            if (sleepOn) {
                sleepDisp.setVisibility(View.VISIBLE);
            }
            if (cleanOn) {
                cleanDisp.setVisibility(View.VISIBLE);
            }

            tempShow.setText(temperatureShowReal + "");
            temperature = temperatureShowReal;
        }

        textColorAnim = ObjectAnimator.ofInt(hideShow, "textColor", Color.BLACK, Color.TRANSPARENT);
        textColorAnim.setDuration(1000);
        textColorAnim.setEvaluator(new ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim.start();

        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("el_GR");
                    TTS.setPitch((float) 0.9);
                    TTS.setLanguage(localeToUse);
                    TTS.speak("Λειτουργία πλήκτρων.", TextToSpeech.QUEUE_ADD, null);
                }
            }
        });

        findViewById(R.id.onoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale localeToUse = new Locale("el_GR");
                            TTS.setPitch((float) 0.9);
                            TTS.setLanguage(localeToUse);
                            firstClickDown = System.currentTimeMillis();
                            if (canSpeak) {
                                if (!on) {
                                    sentenceToSay = "Ενεργοποίηση";
                                } else {
                                    sentenceToSay = "Απενεργοποίηση";
                                }
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                canSpeak = false;
                            }
                            if (!on) {
                                on = true;
                                onOff.setImageResource(R.drawable.ic_on);
                                tempShow.setTextSize(120);
                                tempShow.setText(temperatureShowReal + "");
                                gradeDisp.setText("℃");
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
                                if (timerOn) {
                                    onCloseThisShit();
                                } else {
                                    on = false;
                                }
                                onOff.setImageResource(R.drawable.ic_off);
                                tempShow.setTextSize(80);
                                tempShow.setText("OFF");
                                gradeDisp.setText("");
                                txtProgress.setVisibility(View.INVISIBLE);
                                gradeDisp.setVisibility(View.INVISIBLE);
                                mode.setVisibility(View.INVISIBLE);
                                fanDisp.setVisibility(View.INVISIBLE);
                                swingDisp.setVisibility(View.INVISIBLE);
                                cleanDisp.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                sleepDisp.setVisibility(View.INVISIBLE);
                            }

                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                public void run() {
                                    if (System.currentTimeMillis() - firstClickDown >= 950) {
                                        canSpeak = true;
                                        if (sentenceToSay.equals("Ενεργοποίηση") && !on) {
                                            TTS.speak("Απενεργοποίηση", TextToSpeech.QUEUE_ADD, null);
                                        } else if (sentenceToSay.equals("Απενεργοποίηση") && on) {
                                            TTS.speak("Ενεργοποίηση", TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                }
                            }, 1000);
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
                                tempWarn--;
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
                                                    if (tempWarn < 16) {
                                                        sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι " + MIN_TEMP + " βαθμούς.";
                                                        tempWarn = MIN_TEMP;
                                                    } else {
                                                        temperatureDif = temperatureShowReal - temperature;
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
                                                    }
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
                                tempWarn++;
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
                                            if (tempWarn - temperature != 0) {
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
                                                    if (tempWarn > 30) {
                                                        sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι " + MAX_TEMP + " βαθμούς.";
                                                        tempWarn = MAX_TEMP;
                                                    } else {
                                                        temperatureDif = temperatureShowReal - temperature;
                                                        if (grades.containsKey(temperatureDif)) {
                                                            if (temperatureDif == 1) {
                                                                sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμό";
                                                            } else {
                                                                sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(temperatureDif) + " βαθμούς";
                                                            }
                                                        } else {
                                                            sentenceToSay = "Αύξηση θερμοκρασίας κατά " + temperatureDif + " βαθμούς";
                                                        }
                                                    }
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
                                final ObjectAnimator timeAnim;
                                timeAnim = ObjectAnimator.ofInt(tempShow, "textColor", Color.TRANSPARENT, Color.BLACK);
                                firstClickDownTimer = System.currentTimeMillis();
                                if (!timerOn) {
                                    if (timeStr != 0) {
                                        progressBar.setMax(timeStr);
                                        progressBar.setProgress(timeStr - countDown);
                                        txtProgress.setText(countDown + "'");
                                        progressBar.setVisibility(View.VISIBLE);
                                        txtProgress.setVisibility(View.VISIBLE);
                                        delayThat = 0;

                                    } else {
                                        delayThat = 2100;

                                        if ((timeToSet + 15) <= 180) {
                                            timeToSet = timeToSet + 15;
                                            if (timeToSet > 99) {
                                                tempShow.setTextSize(90);
                                            } else {
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
                                    }
                                } else {
                                    delayThat = 0;
                                    checkIn = 1;
                                }

                                if (timerOn) {
                                    timeAnim.setDuration(0);
                                    timeAnim.setRepeatCount(0);
                                    timeAnim.setEvaluator(new ArgbEvaluator());
                                    timeAnim.setRepeatMode(ValueAnimator.REVERSE);
                                    timeAnim.start();
                                } else {
                                    timeAnim.setDuration(600);
                                    timeAnim.setRepeatCount(ValueAnimator.INFINITE);
                                    timeAnim.setEvaluator(new ArgbEvaluator());
                                    timeAnim.setRepeatMode(ValueAnimator.REVERSE);
                                    timeAnim.start();
                                }


                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        timeAnim.setDuration(0);
                                        timeAnim.setRepeatCount(0);
                                        if (System.currentTimeMillis() - firstClickDownTimer >= (delayThat - 50)) {

                                            if (timeToSet == 0 && timeStr == 0)
                                                timerOn = true;

                                            if (timerOn) {
                                                checkIn = 0;
                                                if (!closeNow) {
                                                    sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                    tempShow.setTextSize(110);
                                                    tempShow.setText(temperatureShowReal + "");
                                                    gradeDisp.setText("℃");
                                                } else if (closeNow) {
                                                    closeNow = false;
                                                    on = false;
                                                }
                                                txtProgress.setVisibility(View.INVISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                countDown = 0;
                                                timeStr = 0;
                                                stopped = true;
                                                timerOn = false;
                                            } else {
                                                if (timeToSet != 0 && timeStr == 0) {
                                                    sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε για " + timeToSet + " λεπτά.";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                    tempShow.setTextSize(110);
                                                    tempShow.setText(temperatureShowReal + "");
                                                    gradeDisp.setText("℃");
                                                    timeStr = timeToSet;
                                                    pStatus = 0;
                                                } else {
                                                    pStatus = timeStr - countDown;
                                                }
                                                timerOn = true;
                                                timeToSet = 0;

                                                //Loader Start
                                                txtProgress.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.VISIBLE);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (countDown <= 0) {
                                                            countDown = timeStr;
                                                        }
                                                        progressBar.setMax(timeStr);
                                                        while (pStatus <= timeStr) {
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progressBar.setProgress(pStatus);
                                                                    if (countDown >= 0) {
                                                                        txtProgress.setText(countDown + "'");
                                                                    }
                                                                    if (checkIn == 1) {
                                                                        stopped = true;
                                                                        pStatus = timeStr;
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

                                                                    final AlertDialog alertDialog = new AlertDialog.Builder(ButtonModeActivity.this).create();
                                                                    alertDialog.setTitle("Time's Up!");
                                                                    alertDialog.setMessage("Το κλιματιστικό κλείνει.");
                                                                    alertDialog.show();

                                                                    TTS.speak("Το κλιματιστικό κλείνει.", TextToSpeech.QUEUE_ADD, null);
                                                                    countDown = 0;
                                                                    timeStr = 0;
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
                                                                timerOn = false;
                                                            }
                                                        });
                                                    }
                                                }).start();
                                                //Loader End
                                            }
                                        }
                                    }
                                }, delayThat);
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

                                if (modeCount == 2) {
                                    mode.setText("❆");
                                    modeStr = "Ψυχρή";
                                } else if (modeCount == 3) {
                                    mode.setText("⛆");
                                    modeStr = "Αφύγρανση";
                                } else if (modeCount == 4) {
                                    mode.setText("✤");
                                    modeStr = "Ανεμιστήρα";
                                } else if (modeCount == 5) {
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
                                            if (modeCount == 2) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ψυχρή.";
                                            } else if (modeCount == 3) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Αφύγρανση.";
                                            } else if (modeCount == 4) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ανεμιστήρα.";
                                            } else if (modeCount == 5) {
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
                                if (fanCount == 2) {
                                    fanDisp.setImageResource(R.drawable.volume_low);
                                    fanStr = "χαμηλή";
                                } else if (fanCount == 3) {
                                    fanDisp.setImageResource(R.drawable.volume_mid);
                                    fanStr = "μεσαία";
                                } else if (fanCount == 4) {
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
                                            if (fanCount == 2) {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε χαμηλή.";
                                            } else if (fanCount == 3) {
                                                sentenceToSay = "Η ένταση του ανεμιστήρα, ρυθμίστηκε σε μεσαία.";
                                            } else if (fanCount == 4) {
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
                                if (swingCount == 2) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_down);
                                    swingStr = "χαμηλή";
                                } else if (swingCount == 3) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_mid);
                                    swingStr = "μεσαία";
                                } else if (swingCount == 4) {
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

                                            if (swingCount == 2) {
                                                sentenceToSay = "Η ανάκλιση ρυθμίστηκε σε χαμηλή";
                                            } else if (swingCount == 3) {
                                                sentenceToSay = "Η ανάκλιση ρυθμίστηκε σε μεσαία";
                                            } else if (swingCount == 4) {
                                                sentenceToSay = "Η ανάκλιση ρυθμίστηκε σε υψηλή";
                                            } else {
                                                sentenceToSay = "Η ανάκλιση ρυθμίστηκε σε ολική";
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
                                        extras = "Η λειτουργία ύπνου είναι ενεργή.";
                                    }
                                    if (cleanOn) {
                                        if (sleepOn) {
                                            extras = "Οι λειτουργίες ύπνου και καθαρισμού, είναι ενεργές.";
                                        } else {
                                            extras = "Η λειτουργία καθαρισμού είναι ενεργή.";
                                        }
                                    }
                                    if (timerOn) {
                                        if (sleepOn || cleanOn) {
                                            extras = extras + "και ο χρονοδιακόπτης έχει ρυθμιστεί για " + timeStr + " λεπτά";
                                        } else {
                                            extras = "ο χρονοδιακόπτης έχει ρυθμιστεί για " + timeStr + " λεπτά";
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

        if (fakeTimer) {
            fakeTimer = false;
            timerOn = false;
            timer.callOnClick();
        }


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
        Intent myIntent = new Intent(ButtonModeActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        myIntent.putExtra("onOff", on);
        myIntent.putExtra("mode", modeCount);
        myIntent.putExtra("swing", swingCount);
        myIntent.putExtra("fan", fanCount);
        myIntent.putExtra("sleep", sleepOn);
        if (timerOn) {
            checkIn = 1;
            myIntent.putExtra("timerFull", timeStr);
            myIntent.putExtra("timerCount", countDown);
            timerOn = true;
        }
        myIntent.putExtra("timer", timerOn);
        myIntent.putExtra("clean", cleanOn);
        myIntent.putExtra("hide", hideMoreOptions);
        myIntent.putExtra("welcome", false);
        myIntent.putExtra("temperature", temperatureShowReal);
        startActivity(myIntent);
    }

    private void onCloseThisShit() {
        closeNow = true;
        on = true;
        findViewById(R.id.timer).callOnClick();

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
                    ButtonModeActivity.this.onRight();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures");
            }
            return false;
        }
    }

}
