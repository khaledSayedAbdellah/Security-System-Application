package com.example.yousef96.ledcontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AppCompatActivity {


    ImageButton button_to_speak;
    SpeechRecognizer mSpeechRecognizer;
    Intent mspeechRecognizerIntent;

    boolean State_button = true;
    int Actual_temp=25;
    StickySwitch stickySwitch1 ;
    StickySwitch stickySwitch2 ;

    private int activated_sound;
    private int deactivated_sound;
    private int alarm_off_sound;
    private int camera_sound;
    private int door_open_sound;
    private int door_locked_sound;
    private int light_off_sound;
    private int light_on_sound;
    private SoundPool mysound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageButton imageButton = (ImageButton)findViewById(R.id.activated);



        cheek_permission();



        mSpeechRecognizer =SpeechRecognizer.createSpeechRecognizer(this);
        mspeechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mspeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mspeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) { }
            @Override
            public void onBufferReceived(byte[] buffer) { }
            @Override
            public void onEndOfSpeech() { }
            @Override
            public void onError(int error) { }
            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches= results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches==null) {
                    Toast.makeText(getApplicationContext(), "Nothing !!!!", Toast.LENGTH_LONG).show();
                }else{
                    if(matches.toString().contains("camera")){
                        Toast.makeText(getApplicationContext(),"camera is lunch",Toast.LENGTH_LONG).show();

                        mysound.play(camera_sound,1,1,0,0,1);
                        Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
                        startActivity(intent);
                    }
                    else if (matches.toString().contains("light")&&matches.toString().contains("off")){

                        stickySwitch1.setDirection(StickySwitch.Direction.RIGHT);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("LED_STATUS");
                        myRef.setValue(0);
                        //mysound.play(light_off_sound,1,1,0,0,1);

                    }
                    else if (matches.toString().contains("light") && matches.toString().contains("on")){

                        stickySwitch1.setDirection(StickySwitch.Direction.LEFT);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("LED_STATUS");
                        myRef.setValue(1);
                        //mysound.play(light_on_sound,1,1,0,0,1);
                    }
                    else if (matches.toString().contains("open") && matches.toString().contains("door")){

                        stickySwitch2.setDirection(StickySwitch.Direction.RIGHT);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("door");
                        myRef.setValue(0);

                    }
                    else if ((matches.toString().contains("lock")||matches.toString().contains("close")) && matches.toString().contains("door")){

                        stickySwitch2.setDirection(StickySwitch.Direction.LEFT);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("door");
                        myRef.setValue(1);

                    }
                    else if ((matches.toString().contains("stop")||matches.toString().contains("turn off")||matches.toString().contains("close")) && matches.toString().contains("alarm")){

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("LED_STATUS");
                        myRef.setValue(2);
                        mysound.play(alarm_off_sound,1,1,0,0,1);

                    }
                    else if ((matches.toString().contains("active")||matches.toString().contains("turn on")||matches.toString().contains("enable")) && (matches.toString().contains("system")||matches.toString().contains("security"))){

                        Resources res = getResources();
                        Drawable drawable = res.getDrawable(R.drawable.activated);
                        imageButton.setImageDrawable(drawable);
                        State_button=true;

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("LED_STATUS");
                        myRef.setValue(1);
                        mysound.play(activated_sound,1,1,0,0,1);
                    }
                    else if ((matches.toString().contains("deactivated")||matches.toString().contains("turn off")||matches.toString().contains("disable")) && (matches.toString().contains("system")||matches.toString().contains("security"))){
                        Resources res = getResources();
                        Drawable drawable = res.getDrawable(R.drawable.unactivated);
                        imageButton.setImageDrawable(drawable);
                        State_button=false;

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("LED_STATUS");
                        myRef.setValue(0);
                        mysound.play(deactivated_sound,1,1,0,0,1);
                    }


                    else
                        Toast.makeText(getApplicationContext(),matches.toString(),Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) { }
            @Override
            public void onEvent(int eventType, Bundle params) { }
        });


        button_to_speak=(ImageButton)findViewById(R.id.speech_rec);

        button_to_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"listining .....",Toast.LENGTH_LONG).show();
                mSpeechRecognizer.startListening(mspeechRecognizerIntent);
            }
        });




        //-----------------------------------------------------------------------------------


       mysound = new SoundPool(8, AudioManager.STREAM_MUSIC,0);



        activated_sound = mysound.load(getApplicationContext(),R.raw.activated,1);
        deactivated_sound = mysound.load(getApplicationContext(),R.raw.deactivated,1);
        alarm_off_sound = mysound.load(getApplicationContext(),R.raw.alarm_off,1);
        camera_sound = mysound.load(getApplicationContext(),R.raw.camera_open,1);
        door_open_sound = mysound.load(getApplicationContext(),R.raw.door_is_open,1);
        door_locked_sound = mysound.load(getApplicationContext(),R.raw.door_is_locked,1);
        light_off_sound = mysound.load(getApplicationContext(),R.raw.light_off,1);
        light_on_sound = mysound.load(getApplicationContext(),R.raw.light_on,1);


        stickySwitch1 =(StickySwitch)findViewById(R.id.stickyBtnTestActivity_light);
        stickySwitch1.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(StickySwitch.Direction direction, String s) {
                 if (direction == StickySwitch.Direction.LEFT)
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("LED_STATUS");
                    myRef.setValue(1);
                   mysound.play(light_on_sound,1,1,0,0,1);
                }
                else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("LED_STATUS");
                    myRef.setValue(0);
                     mysound.play(light_off_sound,1,1,0,0,1);
                }
            }
        });



        stickySwitch2 =(StickySwitch)findViewById(R.id.stickyBtnTestActivity_lock);
        stickySwitch2.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(StickySwitch.Direction direction, String s) {
                if (direction == StickySwitch.Direction.LEFT)
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("door");
                    myRef.setValue(0);
                    mysound.play(door_locked_sound,1,1,0,0,1);
                }
                else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("door");
                    myRef.setValue(1);
                    mysound.play(door_open_sound,1,1,0,0,1);
                }
            }
        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(State_button==false){
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.activated);
                    imageButton.setImageDrawable(drawable);
                    State_button=true;

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("LED_STATUS");
                    myRef.setValue(1);
                    mysound.play(activated_sound,1,1,0,0,1);


                }
                else {
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.unactivated);
                    imageButton.setImageDrawable(drawable);
                    State_button=false;

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("LED_STATUS");
                    myRef.setValue(0);
                    mysound.play(deactivated_sound,1,1,0,0,1);

                }
            }
        });


    }

    public void onClick_down(View view) {
        TextView text_temp= (TextView) findViewById(R.id.txt_maxTemp);
        int temprature= Integer.parseInt(text_temp.getText().toString().substring(0,2));
        temprature-=1;
        temperature_equals(temprature);
        text_temp.setText(String.valueOf(temprature)+"°");
        Toast.makeText(getApplicationContext(),"down .....",Toast.LENGTH_SHORT).show();
    }

    public void onClick_up(View view) {
        TextView text_temp= (TextView) findViewById(R.id.txt_maxTemp);
        int temprature= Integer.parseInt(text_temp.getText().toString().substring(0,2));
        temprature+=1;
        temperature_equals(temprature);
        text_temp.setText(String.valueOf(temprature)+"°");
    }

    public void onClick_Alarmoff(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LED_STATUS");
        myRef.setValue(2);
        mysound.play(alarm_off_sound,1,1,0,0,1);
    }

    public void onClick_camera(View view) {
        mysound.play(camera_sound,1,1,0,0,1);
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);


    }

    public void temperature_equals(int max_temp){
        if (Actual_temp == max_temp){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("LED_STATUS");
            myRef.setValue(3);
        }
    }

    private void cheek_permission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(!(ContextCompat.
                    checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();

            }
        }
    }

}
