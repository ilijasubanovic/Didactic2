package com.sevengreen.ilija.reflexo;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class theGame extends Activity {

    ImageView life1, life2, life3, life4, mainImage, tmpImageView;
    ImageView result;
    float resultImageAlphaF=1f;
    int screenHeight, screenWidth;
    int lives, timerIsReset=0;
   // int resultImageAlpha=255;
    int[] indexOfImagePosition = new int[4];
    private int[] indexOfImageContent = new int[4];
    TextView textLevelValue, score, gameNotification;
    ImageChoice i1 = new ImageChoice(0,false,"1");
    ImageChoice i2 = new ImageChoice(0,false,"2");
    ImageChoice i3 = new ImageChoice(0,false,"3");
    GameLevel level = new GameLevel(0,false,false);
    ProgressBar mProgressBar;
    boolean gameTypeClassic=true;
    InterstitialAd mInterstitialAd;
    SharedPreferences prefsGamesPlayed;

    long TIMEOUT;
    long elapsed, elapsedForLevel=0, scorePoints;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent thisGame = getIntent();
        String gameType = thisGame.getStringExtra("gameType"); // will return "speed" or "classic"
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
        prefsGamesPlayed = this.getSharedPreferences("gamesPlayed", Context.MODE_PRIVATE);


        //declare
        if(gameType.equals("classic"))
            TIMEOUT = 3500;
        else
        {
            TIMEOUT = 4000;
            gameTypeClassic = false;
        }

        setContentView(R.layout.the_game);
        //adds
        AdView mAdView = (AdView) findViewById(R.id.gameActivityBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        //test
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //real
       // mInterstitialAd.setAdUnitId("ca-app-pub-8731252909086422/3355453991");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        lives = 3;
        textLevelValue = (TextView)findViewById(R.id.TextLevelValue);
        gameNotification = (TextView)findViewById(R.id.notificationBar);
        score = (TextView)findViewById(R.id.score);
        setProgressBarVisibility(true);

        int tmpint = screenWidth;
        tmpint*=0.3;
        mainImage = (ImageView) findViewById(R.id.ImageView04);
        mainImage.getLayoutParams().height = tmpint;
        mainImage.getLayoutParams().width = tmpint;
        mainImage.requestLayout();
        result =  (ImageView) findViewById(R.id.ImageResult);
        result.getLayoutParams().height = tmpint;
        result.getLayoutParams().width = tmpint;
        result.requestLayout();
        tmpImageView = (ImageView) findViewById(R.id.ImageView01);
        tmpImageView.getLayoutParams().height = tmpint;
        tmpImageView.getLayoutParams().width = tmpint;
        tmpImageView.requestLayout();
        tmpImageView = (ImageView) findViewById(R.id.ImageView02);
        tmpImageView.getLayoutParams().height = tmpint;
        tmpImageView.getLayoutParams().width = tmpint;
        tmpImageView.requestLayout();
        tmpImageView = (ImageView) findViewById(R.id.ImageView03);
        tmpImageView.getLayoutParams().height = tmpint;
        tmpImageView.getLayoutParams().width = tmpint;
        tmpImageView.requestLayout();
        //start new level
        initializeNewLevel ();
        //initiateTime();


        Drawable draw=ContextCompat.getDrawable(getApplicationContext().getApplicationContext(), R.drawable.custom_progressbar);

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar1);
        mProgressBar.setMax((int) TIMEOUT);
        //mProgressBar.getProgressDrawable().setColorFilter(Color.RED, Mode.SRC_IN);
        mProgressBar.setProgressDrawable(draw);



        life1 = (ImageView) findViewById(R.id.ImageLife1);
        life2 = (ImageView) findViewById(R.id.ImageLife2);
        life3 = (ImageView) findViewById(R.id.ImageLife3);
        life4 = (ImageView) findViewById(R.id.ImageLife4);
        life4.setImageDrawable(null);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
//		Log.v("onResume", "Tick of Progress onResume   "+timerActive);
        if(level.getlevelPaused() && !level.getLevelBonus())
        {
            resumeLevel(level);
            //if(level.getLevelNumber()!=1)
            initiateTime();
        }
        super.onResume();
//    	Log.v("onResume", "Tick of Progress onResume 2  "+timerActive);
    }
    @Override
    protected void onPause() {
//		Log.v("onPause", "Tick of Progress onPause   "+timerActive);
        pauseLevel(level);
        super.onPause();
//		Log.v("onPause", "Tick of Progress onPause32   "+timerActive);

    }

    @Override
    protected void onStop() {
//		Log.v("onStop", "Tick of Progress onStop   "+timerActive);
        pauseLevel(level);
        super.onStop();
//    	Log.v("onStop", "Tick of Progress onStop 2  "+timerActive);
    }

    @Override
    protected void onDestroy() {
//		Log.v("onDestroy", "Tick of Progress onDestroy   "+timerActive);
        pauseLevel(level);
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
//		Log.v("onRestart", "Tick of Progress onRestart   "+timerActive);
        super.onRestart();
//		Log.v("onRestart", "Tick of Progress onRestart 2  "+timerActive);
    }
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Finish game?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, new OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //continue time if not bonus level
                        if(!level.getLevelBonus() && level.getlevelPaused())
                        {
                            //if(level.getLevelNumber()!=1)
                            initiateTime();
                            resumeLevel(level);
                        }
                    }
                })
                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        theGame.super.onBackPressed();
                    }
                }).create().show();
        //pause timer when back button pressed
        level.setlevelPaused(true);
    }
    public void initializeNewLevel () {

        //initiate timer only after first level
        if(level.getLevelNumber()==0)
        {
            gameNotification.setText(R.string.GameNotificationFirstLevel);
            //initiateTime();
        }
        else
            gameNotification.setText("");
        //initiate timer only after first level
        if(level.getLevelNumber()==1)
            elapsed = 0;
        //initiateTime();

        level.setLevelNumber(level.getLevelNumber()+1);
        //check and set bonus level
        if(level.getLevelNumber()%10==0 && lives<4 && level.getLevelNumber()>1 && !gameTypeClassic)
        {
            level.setLevelBonus(true);
            level.setlevelPaused(true);//pause timer if bonus level
            if(level.getLevelNumber()<12)//only shoe notification in first bonus levels
                gameNotification.setText(R.string.GameNotificationBonusLevel);
        }
        else
        {
            level.setLevelBonus(false);
            //resumeLevel(level);
            if(level.getlevelPaused())
            {
                initiateTime();
                level.setlevelPaused(false);
//        		resumeLevel(level);
            }

        }

        //display level value
        textLevelValue.setText(String.valueOf(level.getLevelNumber()));
        //generate indexes of position of each image container
        generateImagePositionIndexes();
        //generate indexes of content of each image container from range of available images
        generateImageContentIndexes();

        //initialize main image

        //main image has same image as i1 image container
        //mainImage main image
        mainImage.setImageResource(getBaseContext().getResources().getIdentifier("i1_" + indexOfImageContent[1], "drawable", "com.sevengreen.ilija.reflexo"));

        //initialize generated properties
        initializeImageProperties(i1, 1);
        initializeImageProperties(i2,2);
        initializeImageProperties(i3,3);

        //display images
        setImageToView(i1);
        setImageToView(i2);
        setImageToView(i3);

        if(level.getLevelNumber()==1)
            initiateTime();


    }
    private void setScore() {
        long dif;

        if(!(level.getLevelBonus()))
        {
            if(level.getLevelNumber()==1)
                dif= elapsedForLevel =  elapsed;
            else if (timerIsReset!=0)
                dif = timerIsReset * 5000 - (elapsedForLevel - 1000) + elapsed;
            else
                dif = elapsed - (elapsedForLevel - 1000);

            elapsedForLevel = elapsed;
            timerIsReset = 0;
            if (dif < 1000 && dif!=0)
                dif = 1000;
            if(dif!=0)
                scorePoints += (10000/dif);
            score.setText(String.valueOf(scorePoints));
        }
    }
    //generate position of each image container (i1,i2,i3) on screen (1,2,3)
    public void generateImagePositionIndexes () {
        Random r = new Random();
        //chose position of first element in list
        indexOfImagePosition[1] = r.nextInt(4 - 1) + 1;
        //chose position of second element in list
        do{
            indexOfImagePosition[2] = r.nextInt(4 - 1) + 1;
        }while(indexOfImagePosition[2]==indexOfImagePosition[1]);
        //chose position of third element in list
        do{
            indexOfImagePosition[3] = r.nextInt(4 - 1) + 1;
        }while(indexOfImagePosition[3]==indexOfImagePosition[1] || indexOfImagePosition[3]==indexOfImagePosition[2]);

    }
    //generate content of each image container (i1,i2,i3) from available list of images (n)
    public void generateImageContentIndexes () {
        //if bonus level all images should be same (every n-th level is bonus; n=3)
        if(level.getLevelBonus())
            indexOfImageContent[1]=indexOfImageContent[2]=indexOfImageContent[3]=2;
        else
        {
            Random r = new Random();
            //content of first image container
            indexOfImageContent[1] = r.nextInt(164 - 1) + 1;
            //content of second and third image from list of possible images n+1 (in first m levels; m=10)
            do{
                indexOfImageContent[2] = r.nextInt(164 - 1) + 1;
                indexOfImageContent[3] = indexOfImageContent[2];
            }while(indexOfImageContent[2]==indexOfImageContent[1]);
            //for levels higher then 5 all three choices are different
            if(level.getLevelNumber()>5)
            {
                do{
                    indexOfImageContent[3] = r.nextInt(164 - 1) + 1;
                }while(indexOfImageContent[3]==indexOfImageContent[1] || indexOfImageContent[3]==indexOfImageContent[2]);
            }
        }
    }
    //put generated values to image containers (i1,i2,i3)
    private void initializeImageProperties(ImageChoice imageToInitialize, int imageIndex){
        //initialize container position
        imageToInitialize.setChoicePosition(indexOfImagePosition[imageIndex]);
        //image container 1 has same image as main one and therefore is always correct answer
        if(imageIndex==1)
            imageToInitialize.setIsCorrectAnswer(true);
        else
            imageToInitialize.setIsCorrectAnswer(false);
        //initialize container content
        imageToInitialize.setImageResource("i1_" + indexOfImageContent[imageIndex]);
    }
    //puts images of imageChoice containers to display layout
    public void setImageToView (final ImageChoice i) {
        ImageView choiceX;
        String imageResource = "ImageView0";
        imageResource += i.getChoicePositionString();
        choiceX = (ImageView) findViewById(getBaseContext().getResources().getIdentifier(imageResource,"id","com.sevengreen.ilija.reflexo"));
        choiceX.setImageResource(getBaseContext().getResources().getIdentifier(i.getImageResource(), "drawable", "com.sevengreen.ilija.reflexo"));

        //add hand pointer on correct answer on first level
        //************
        if(i.getIsCorrectAnswer() && level.getLevelNumber()==1)
        {
            //Resources r = getResources();
            Drawable[] layers = new Drawable[2];
            //layers[0] = r.getDrawable(R.drawable.hand_point_white);
            //layers[0] = r.getDrawable(getBaseContext().getResources().getIdentifier(i.getImageResource(),"drawable","com.sevengreen.ilija.reflexo"));
            //layers[1] = r.getDrawable(R.drawable.hand_point_white);
            layers[0] = ContextCompat.getDrawable(getApplicationContext().getApplicationContext(),getBaseContext().getResources().getIdentifier(i.getImageResource(),"drawable","com.sevengreen.ilija.reflexo"));
            //r.getDrawable(getBaseContext().getResources().getIdentifier(i.getImageResource(),"drawable","com.sevengreen.ilija.reflexo"));
            layers[1] = ContextCompat.getDrawable(getApplicationContext().getApplicationContext(),R.drawable.hand_point_white);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            choiceX.setImageDrawable(layerDrawable);

        }

        choiceX.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //if true answer
                if (i.getIsCorrectAnswer())
                    answerCorrect();
                    //if wrong answer and not bonus level
                else if (!i.getIsCorrectAnswer())
                    answerWrong();
            }
        });
    }


    protected void increaseLife() {
        //Bitmap picture = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.cherry));
      //  Bitmap picture = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.cherry);
        //.openRawResource(R.drawable.cherry));
        //decrease number of lives
        lives++;
        //remove life images
        if(life1.getDrawable()==null)
        {
            life1.setImageResource(R.drawable.cherry);
            displayMessage("New Life !!!");
        }
        else if(life2.getDrawable()==null)
        {
            life2.setImageResource(R.drawable.cherry);
            displayMessage("New Life !!!");
        }
        else if(life3.getDrawable()==null)
        {
            life3.setImageResource(R.drawable.cherry);
            displayMessage("New Life !!!");
        }
        else if(life4.getDrawable()==null)
        {
            life4.setImageResource(R.drawable.cherry);
            displayMessage("New Life !!!");
        }
        else
            lives--;

    }

    public void answerCorrect () {
        //display correct check mark
        result.setImageResource(getBaseContext().getResources().getIdentifier("correct", "drawable", "com.sevengreen.ilija.reflexo"));
        //set opacity of mark
        //resultImageAlpha = 250;
        resultImageAlphaF=1f;
        //increase timer by one second for each correct answer
        if(gameTypeClassic)
            elapsed -= 1000;
        else
            elapsed -= 500; //only increase by 500 for speed game

        //time cannot be greater than initial time, so reset time if necessary
        if (elapsed<0)
            elapsed = 0;
        //increase life if bonus level
        if(level.getLevelBonus())
            increaseLife();
        if(!level.getLevelBonus())
            setScore();

        //start new level
        initializeNewLevel ();
    }

    public void answerWrong () {
        //display wrong check mark
        result.setImageResource(getBaseContext().getResources().getIdentifier("wrong", "drawable", "com.sevengreen.ilija.reflexo"));
        //set opacity of mark
        //resultImageAlpha = 250;
        resultImageAlphaF=1f;
        if(!level.getLevelBonus())
            removeLife();
        initializeNewLevel ();

    }


    public void removeLife () {
        //decrease number of lives
        lives--;
        //remove life images
        if(life4.getDrawable()!=null)
            life4.setImageDrawable(null);
        else if(life3.getDrawable()!=null)
            life3.setImageDrawable(null);
        else if(life2.getDrawable()!=null)
            life2.setImageDrawable(null);
        else if(life1.getDrawable()!=null)
            life1.setImageDrawable(null);
            //if last life game over
        else
        {
            insertHighScore();
            //	SharedPreferences prefs = this.getSharedPreferences("1", Context.MODE_PRIVATE);
            //	int scorea = prefs.getInt("1", 0); //0 is the default value
            displayMessage("GAME OVER !!! \n Your score: " + scorePoints);
            level.setlevelPaused(true);
            int gamesPlayed = Integer.parseInt(prefsGamesPlayed.getString("gamesPlayed", ""));
            if (mInterstitialAd.isLoaded() && gamesPlayed%5==0 ) {
                mInterstitialAd.show();
                Editor editorGamesPlayed = prefsGamesPlayed.edit();
                gamesPlayed+=1;
                editorGamesPlayed.putString("gamesPlayed",Integer.toString(gamesPlayed));
                editorGamesPlayed.apply();
                this.finish();
            } else
            {
                Editor editorGamesPlayed = prefsGamesPlayed.edit();
                gamesPlayed+=1;
                editorGamesPlayed.putString("gamesPlayed",Integer.toString(gamesPlayed));
                editorGamesPlayed.apply();
                this.finish();
            }
        }
    }

    private void insertHighScore() {
        String prefsKey;
        if(gameTypeClassic)
            prefsKey = "classicPrefsKey";
        else
            prefsKey = "speedPrefsKey";
        SharedPreferences prefs = this.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();


        for(int index = 1;index<11;index++)
        {
            int storedScore=0;
            String[] tmpString;

            tmpString = (prefs.getString(Integer.toString(index), "")).split("#");
            for(int i=0;i<tmpString.length;i++)
            {
                if (i==0 && !(tmpString[0].equals("")))
                    storedScore = Integer.parseInt(tmpString[0]);
            }
            //if empty space, fill it
            if(storedScore==0)
            {
                editor.putString(Integer.toString(index), Long.toString(scorePoints) + "#" + "FALSE");
               // editor.commit();
                editor.apply();
                break;
            }
            if (scorePoints>storedScore)
            {
                for(int j=10; j>index;j--)
                {
                    editor.putString(Integer.toString(j), prefs.getString(Integer.toString(j-1), ""));

                }
                editor.putString(Integer.toString(index), Long.toString(scorePoints)+"#"+"FALSE");

                editor.apply();
                break;
            }
        }
    }
    public void initiateTime () {

        final long INTERVAL=10;
//	final long TIMEOUT=5000;


        final ImageView choiceX;
        String imageResource = "ImageView0";
        imageResource += i1.getChoicePositionString();
        choiceX = (ImageView) findViewById(getBaseContext().getResources().getIdentifier(imageResource,"id","com.sevengreen.ilija.reflexo"));


        task = new TimerTask(){
            @SuppressLint("NewApi")
            @Override
            public void run() {
                //increase elapsed time
                elapsed+=INTERVAL;

                //change color of timer bar if time near to finish
                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        if(level.getLevelNumber()==1)
                        {
                            if((elapsed/200)%2==1)
                                choiceX.setImageResource(getBaseContext().getResources().getIdentifier(i1.getImageResource(),"drawable","com.sevengreen.ilija.reflexo"));
                            if((elapsed/200)%2==0)
                                choiceX.setImageResource(R.drawable.blank);

                        }

                        if(!gameTypeClassic)
                        {
                            if(10<level.getLevelNumber() && level.getLevelNumber()<20)
                            {
                                //mProgressBar.setBackgroundColor(Color.GREEN);
                                //mProgressBar.getProgressDrawable().setColorFilter(Color.RED, Mode.SRC_IN);
                                TIMEOUT=3000;
                            }
                            else if(20<level.getLevelNumber() && level.getLevelNumber()<30)
                            {
                                //mProgressBar.setBackgroundColor(Color.YELLOW);
                                TIMEOUT=2500;
                            }
                            else if(30<level.getLevelNumber() && level.getLevelNumber()<40)
                            {
                                //mProgressBar.setBackgroundColor(Color.RED);
                                //mProgressBar.getProgressDrawable().setColorFilter(Color.YELLOW, Mode.SRC_IN);
                                TIMEOUT=2000;
                            }

                            else if(40<level.getLevelNumber() && level.getLevelNumber()<50)
                            {
                                //mProgressBar.setBackgroundColor(Color.MAGENTA);
                                TIMEOUT=1500;
                            }
                            mProgressBar.setMax((int )TIMEOUT);
                        }


                        //fade out correct/wrong mark
                       // if(resultImageAlpha>0)
                        //    resultImageAlpha -= 2;
                        if(resultImageAlphaF>0.01f)
                            resultImageAlphaF -= 0.01f;
                        //fade out correct/wrong mark
                        //f=(float)(resultImageAlpha/250);
                       // Log.e("XXXXXXX",Float.toString(resultImageAlphaF));
                        if(level.getLevelBonus())
                            result.setAlpha(0f);
                        else
                            result.setAlpha(resultImageAlphaF);
                    }
                });
                //if time has expired
                if(elapsed>=TIMEOUT && level.getLevelNumber()>1)
                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //remove life
                            removeLife();
                            // and reset progress bar color
                            ////mProgressBar.setBackgroundColor(Color.GRAY);
                            //mProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, Mode.SRC_IN);

                        }
                    });
                    //	Log.v("Log_tag", "Tick of Progress   "+  lives);
                    //restart timer
                    elapsed=0;
                    timerIsReset += 1;
                    //if it was last life stop timer
                    if(lives<1)
                    {
                        this.cancel();
                        return;
                    }
                }
                //update progress in progress bar
                mProgressBar.setProgress((int) TIMEOUT-(int) elapsed);
                //if it is bonus level stop timer
                if(level.getlevelPaused())
                {
//            	Log.v("getlevelPaused", "getlevelPauseds   "+  timerActive);
                    //          	level.setlevelPaused(false);
                    this.cancel();

                }
            }

        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL);


    }
    void resumeLevel(GameLevel l)
    {
        l.setlevelPaused(false);
    }
    void pauseLevel(GameLevel l)
    {
        l.setlevelPaused(true);
        //initiateTime();
    }
    void displayMessage (String text){
        Toast showtoast=Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        showtoast.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("0FD8485732E4C649D04FE57F12A5845D")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}

