package com.example.greeboraapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GiantModeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech TTS;
    String sentenceToSay;
    boolean hideMoreOptions = false;
    boolean on = false;
    int fanCount = 0;
    int swingCount = 0;
    int modeCount = 0;
    long firstClickUp = 0;
    long firstClickDown = 0;
    int temperatureDif = 0;
    boolean sleepOn = false;
    boolean timerOn = false;
    boolean cleanOn = false;
    final int MAX_TEMP = 30;
    final int MIN_TEMP = 16;
    ObjectAnimator textColorAnim;
    HashMap<Integer, String> grades = new HashMap<>();
    private GestureDetector gesture;
    int temperature = 21;
    int temperatureShowReal = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giant_mode);
        grades.put(1, "έναν");
        grades.put(3, "τρεις");
        grades.put(4, "τέσσερις");
        grades.put(13, "δεκατρείς");
        grades.put(14, "δεκατέσσερις");

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
        tempShow.setVisibility(View.INVISIBLE);
        final TextView mode = findViewById(R.id.modeShow);
        mode.setVisibility(View.INVISIBLE);
        final ImageView fanDisp = findViewById(R.id.fanShow);
        fanDisp.setVisibility(View.INVISIBLE);
        final ImageView swingDisp = findViewById(R.id.swingShow);
        swingDisp.setVisibility(View.INVISIBLE);
        final ImageView cleanDisp = findViewById(R.id.cleanShow);
        cleanDisp.setVisibility(View.INVISIBLE);
        final TextView timerDisp = findViewById(R.id.timerShow);
        timerDisp.setVisibility(View.INVISIBLE);
        final ImageView sleepDisp = findViewById(R.id.sleepShow);
        timerDisp.setVisibility(View.INVISIBLE);


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
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                on = true;
                                tempShow.setVisibility(View.VISIBLE);
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
                                    timerDisp.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ImageButton onOff = findViewById(R.id.onoff);
                                onOff.setImageResource(R.drawable.ic_off);
                                sentenceToSay = "Απενεργοποίηση";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                on = false;
                                tempShow.setVisibility(View.INVISIBLE);
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
                                    tempShow.setText(temperatureShowReal + "℃");
                                } else {
                                    temperatureShowReal = MIN_TEMP;
                                    tempShow.setText(temperatureShowReal + "℃");
                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {

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
                                                sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι 16 βαθμούς.";
                                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                temperature = MIN_TEMP;
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
                                if (temperatureShowReal <= MAX_TEMP) {
                                    tempShow.setText(temperatureShowReal + "℃");
                                } else {
                                    temperatureShowReal = MAX_TEMP;
                                    tempShow.setText(temperatureShowReal + "℃");
                                }

                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickUp >= 950) {

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
                                                sentenceToSay = "Το κλιματιστικό δέχεται θερμοκρασίες μέχρι 30 βαθμούς.";
                                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                temperature = MAX_TEMP;
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
                                } else if (modeCount == 2) {
                                    mode.setText("⛆");
                                } else if (modeCount == 3) {
                                    mode.setText("✤");
                                } else if (modeCount == 4) {
                                    mode.setText("☼");
                                } else {
                                    mode.setText("A");
                                }
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (modeCount == 1) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ψυχρή.";
                                            } else if (modeCount == 2) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Αφύγρανση .";
                                            } else if (modeCount == 3) {
                                                sentenceToSay = "Η λειτουργία του κλιματιστικού, ρυθμίστηκε σε Ανεμιστήρας .";
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
                                } else if (fanCount == 2) {
                                    fanDisp.setImageResource(R.drawable.volume_mid);
                                } else if (fanCount == 3) {
                                    fanDisp.setImageResource(R.drawable.volume_full);
                                } else {
                                    fanDisp.setImageResource(R.drawable.volume_auto);
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
                                } else if (swingCount == 2) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_mid);
                                } else if (swingCount == 3) {
                                    swingDisp.setImageResource(R.drawable.fan_swing_up);
                                } else {
                                    swingDisp.setImageResource(R.drawable.fan_swing_auto);
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

        findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
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
                                        TextView timer = findViewById(R.id.timerShow);
                                        if (System.currentTimeMillis() - firstClickDown >= 950) {
                                            if (timerOn) {
                                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη απενεργοποιήθηκε.";
                                                timer.setVisibility(View.INVISIBLE);
                                                timerOn = false;
                                            } else {
                                                sentenceToSay = "Η λειτουργία χρονοδιακόπτη ενεργοποιήθηκε.";
                                                timer.setVisibility(View.VISIBLE);
                                                timerOn = true;
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
                                sentenceToSay = "Η ένταση ρυθμίστηκε σε μεσαία";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
        myIntent.putExtra("temperature", temperature);
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
