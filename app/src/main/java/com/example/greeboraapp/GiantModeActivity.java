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
    int upCount = 0;
    long firstClick = 0;
    boolean sleepOn = false;
    ObjectAnimator textColorAnim;
    HashMap<Integer, String> grades = new HashMap<Integer, String>();
    private TextView txvResult;
    private GestureDetector gesture;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giant_mode);
        grades.put(1, "έναν");
        grades.put(3, "τρείς");
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
        final TextView hideShow = findViewById(R.id.options);

        textColorAnim = ObjectAnimator.ofInt(hideShow, "flicker", Color.BLACK, Color.TRANSPARENT);
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

                            } else {
                                ImageButton onOff = findViewById(R.id.onoff);
                                onOff.setImageResource(R.drawable.ic_off);
                                sentenceToSay = "Απενεργοποίηση";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                on = false;
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
                            TTS.setPitch((float) 0.8);
                            TTS.setLanguage(localeToUse);
                            firstClick = System.currentTimeMillis();
                            if (on) {
                                upCount++;
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClick >= 2000) {
                                            if (grades.containsKey(upCount)) {
                                                if (upCount == 1) {
                                                    sentenceToSay = "Μείωση θερμοκρασίας κατά " + grades.get(upCount) + " βαθμό";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                } else {
                                                    sentenceToSay = "Μείωση θερμοκρασίας κατά " + grades.get(upCount) + " βαθμούς";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                }
                                            } else {
                                                sentenceToSay = "Μείωση θερμοκρασίας κατά " + upCount + " βαθμούς";
                                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                            }
                                            upCount = 0;
                                        }

                                    }
                                }, 2000);
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
                            TTS.setPitch((float) 0.8);
                            TTS.setLanguage(localeToUse);
                            firstClick = System.currentTimeMillis();
                            if (on) {
                                upCount++;
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (System.currentTimeMillis() - firstClick >= 2000) {
                                            if (grades.containsKey(upCount)) {
                                                if (upCount == 1) {
                                                    sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(upCount) + " βαθμό";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                } else {
                                                    sentenceToSay = "Αύξηση θερμοκρασίας κατά " + grades.get(upCount) + " βαθμούς";
                                                    TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                                }
                                            } else {
                                                sentenceToSay = "Αύξηση θερμοκρασίας κατά " + upCount + " βαθμούς";
                                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
                                            }
                                            upCount = 0;
                                        }

                                    }
                                }, 2000);
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
                            if (on) {
                                sentenceToSay = "Η ένταση ρυθμίστηκε σε μεσαία";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
                            if (on) {
                                sentenceToSay = "Η κατεύθυνση ρυθμίστηκε σε χαμηλή";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
                            if (on) {
                                if (!sleepOn) {
                                    sentenceToSay = "Η λειτουργία ύπνου ενεργοποιήθηκε";
                                    sleepOn = true;
                                } else {
                                    sentenceToSay = "Η λειτουργία ύπνου απενεργοποιήθηκε";
                                    sleepOn = false;
                                }
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
                            if (on) {
                                sentenceToSay = "Η ένταση ρυθμίστηκε σε μεσαία";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
                            if (on) {
                                sentenceToSay = "Η ένταση ρυθμίστηκε σε μεσαία";
                                TTS.speak(sentenceToSay, TextToSpeech.QUEUE_ADD, null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                }
                break;
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
    }

    private void onRight() {
        finish();
        Intent myIntent = new Intent(GiantModeActivity.this, BlindModeActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(myIntent);
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
                    GiantModeActivity.this.onLeft();
                }
                // Right swipe

                else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    GiantModeActivity.this.onRight();
                }
            } catch (Exception e) {
                Log.e("", "Error on gestures");
            }
            return false;
        }
    }
}
