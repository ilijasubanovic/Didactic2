package com.sevengreen.ilija.reflexo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.provider.Settings.Secure;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;

//API Key kpbQSlvAeDHzuFq0VQIinfpINXaPp90N
/*
<script type="text/javascript" src="http://ad.leadbolt.net/show_app_ad.js?section_id=542959196"></script>

*/
public class MainActivity extends Activity {

    Dialog d;
    String rank="0", rankSpeed="0", action="0", user="0", score="0", countAll="0", countAllSpeed="0", userDeviceId="0";
    boolean ntwkState=false;
    Typeface type;
    Typeface highScoreText;
    InterstitialAd mInterstitialAd;
    int screenHeight, screenWidth;


    //start of async task to update score online and to get current score position
    public class BackgroundAsyncTask extends
            AsyncTask<Void, Integer, Void> {

        public BackgroundAsyncTask(String userId, String userTopScore, String actionV, Dialog dialog) {
            d = dialog;
            action = actionV;
            user = userId;
            score = userTopScore;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //open connectivity
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            //connectivity state indicator

//	  Log.e("cm.getNetworkInfo(0).getState()",cm.getNetworkInfo(0).getState().toString());
//	  Log.e("cm.getNetworkInfo(1).getState()",cm.getNetworkInfo(1).getState().toString());
//	  Log.e("NetworkInfo.State.CONNECTED",NetworkInfo.State.CONNECTED.toString());
//	  Log.e("NetworkInfo.State.CONNECTING",NetworkInfo.State.CONNECTING.toString());

            //if connected
            if ( cm.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
                ntwkState = true;

            //String id = null;
            InputStream is=null;
            String result=null;
            String line;
            //ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
          //  ContentValues httpValues=new ContentValues();

            //nameValuePairs.add(new BasicNameValuePair("id",id));
            //httpValues.put("id",id);

            //if"get" only get current score position for user
            if(action.equalsIgnoreCase("get") && ntwkState)
            {
                try
                {
                   /* HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://7green.vacau.com/didacticGame/db_get_position.php?u="+user);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("pass 1", "connection success ");*/
                    //new
                    URL url = new URL("http://7green.vacau.com/didacticGame/db_get_position.php?u="+user);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                // read the response
                    System.out.println("Response Code: " + conn.getResponseCode());
                    is = new BufferedInputStream(conn.getInputStream());
                    //String response1 = org.apache.http.m  in, "UTF-8");
                    System.out.println(is.toString());
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());
                    //		Toast.makeText(getApplicationContext(), "Invalid IP Address",					  Toast.LENGTH_LONG).show();
                }

                try
                {
                    BufferedReader reader = null;
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null)
                        {
                         sb.append(line + "\n");
                        }
                        is.close();
                        result = sb.toString();
                    //    Log.e("pass 2", "connection success ");
                    }
                }
                catch(Exception e)
                {
                    Log.e("Fail 2", e.toString());
                }

                try
                {
                    JSONObject json_data = new JSONObject(result);
                    rank=(json_data.getString("position"));
                    countAll=(json_data.getString("count"));
                    rankSpeed=(json_data.getString("positionSpeed"));
                    countAllSpeed=(json_data.getString("countSpeed"));
              //      Log.i("asdasdadsads", rank);
               //     Log.i("count", countAll);
                }
                catch(Exception e)
                {
                    Log.e("Fail 3", e.toString());
                }
            }
            //insert or update
            if(ntwkState &&
                    (action.equalsIgnoreCase("update") ||
                            action.equalsIgnoreCase("updateSpeed")))
            {
                try
                {
                    URL url=null;
                    if(action.equalsIgnoreCase("update"))
                        url = new URL("http://7green.vacau.com/didacticGame/db_insert.php?userId="+user+"&score="+score);
                    else if (action.equalsIgnoreCase("updateSpeed"))
                        url = new URL("http://7green.vacau.com/didacticGame/db_speed_insert.php?userId="+user+"&score="+score);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

// read the response
                    System.out.println("Response Code: " + conn.getResponseCode());
                    is = new BufferedInputStream(conn.getInputStream());
                    String response = is.toString();
                    System.out.println(response);


       /*             HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = null;
                    if(action.equalsIgnoreCase("update"))
                        httppost = new HttpPost("http://7green.vacau.com/didacticGame/db_insert.php?userId="+user+"&score="+score);
                    else if(action.equalsIgnoreCase("updateSpeed"))
                        httppost = new HttpPost("http://7green.vacau.com/didacticGame/db_speed_insert.php?userId="+user+"&score="+score);

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("pass 1", "connection success ");
                    Log.e("username",user);
                    Log.e("score",score);*/
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());
                    //	Toast.makeText(getApplicationContext(), "Invalid IP Address",						Toast.LENGTH_LONG).show();
                }

                try
                {
                    BufferedReader reader = null;
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

                    StringBuilder sb = new StringBuilder();
                    SharedPreferences prefs = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
             //       Log.e("pass 2", "connection success ");
                    if(action.equalsIgnoreCase("update"))
                        prefs = getSharedPreferences("classicPrefsKey", Context.MODE_PRIVATE);
                    else if(action.equalsIgnoreCase("updateSpeed"))
                        prefs = getSharedPreferences("speedPrefsKey", Context.MODE_PRIVATE);

                    Editor editor = prefs.edit();
                    editor.putString(Integer.toString(1), score+"#"+"TRUE");
                    editor.commit();
                    }

                }
                catch(Exception e)
                {
                    Log.e("Fail 2", e.toString());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(action.equalsIgnoreCase("get"))
            {
                String scoreRankText;

                TextView tx = (TextView) d.findViewById(R.id.textViewOnlineScore);
                float tmpf = Integer.parseInt(rank);
                tmpf /= Integer.parseInt(countAll);
                tmpf *= 100;
                int tmpInt = Math.round(tmpf);
                int tmpInt2 = 100 - tmpInt;
                scoreRankText = "Your CLASSIC score is better than "+Integer.toString(tmpInt2)+" % of all people\n";
                tmpf = Integer.parseInt(rankSpeed);
                tmpf /= Integer.parseInt(countAllSpeed);
                tmpf *= 100;
                tmpInt = Math.round(tmpf);
                tmpInt2 = 100 - tmpInt;
                scoreRankText += "Your SPEED score is better than "+Integer.toString(tmpInt2)+" % of all people";
                if(ntwkState)
                    tx.setText(scoreRankText);
                else
                    tx.setText("No network available!");

                tx.setTypeface(highScoreText);
                tx.setTextSize(14);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }//end of asnyc task


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = p.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
            SharedPreferences prefs = this.getSharedPreferences("classicPrefsKey", Context.MODE_PRIVATE);
            Editor editor = prefs.edit();
            editor.putString(Integer.toString(1), Long.toString(0) + "#" + "FALSE");
            editor.apply();
            prefs = this.getSharedPreferences("speedPrefsKey", Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString(Integer.toString(1), Long.toString(0) + "#" + "FALSE");
            editor.apply();
            prefs = this.getSharedPreferences("gamesPlayed", Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString("gamesPlayed",Integer.toString(0));
            editor.apply();
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

       /* if(savedInstanceState == null) {
            // Initialize Leadbolt SDK with your api key
            AppTracker.startSession(getApplicationContext(),"kpbQSlvAeDHzuFq0VQIinfpINXaPp90N");
        }
        // cache Leadbolt Ad without showing it
        AppTracker.loadModuleToCache(getApplicationContext(),"inapp");
*/

        //adds
        AdView mAdView = (AdView) findViewById(R.id.mainActivityBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        //test
     //   mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //real
        mInterstitialAd.setAdUnitId("ca-app-pub-8731252909086422/7857875598");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();



        final Intent newGame = new Intent("com.sevengreen.ilija.reflexo.theGame");
        //declare preferences used for high scores and user name
        final SharedPreferences prefs = this.getSharedPreferences("classicPrefsKey", Context.MODE_PRIVATE);
        final SharedPreferences prefsSpeed = this.getSharedPreferences("speedPrefsKey", Context.MODE_PRIVATE);

        type = Typeface.createFromAsset(getAssets(),"playtime.otf");
        highScoreText = Typeface.createFromAsset(getAssets(), "play_a.ttf");

        userDeviceId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
int tmpint = screenHeight;
        tmpint*=0.23;
        //button start normal game
        Button game = (Button) findViewById(R.id.button1);
        game.getLayoutParams().height=tmpint;
        game.setTypeface(type);
        game.requestLayout();
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame.putExtra("gameType", "classic");
                startActivity(newGame);
            }
        });
        //button start spped game
        Button gameSpeed = (Button) findViewById(R.id.button4);
        gameSpeed.getLayoutParams().height=tmpint;
        gameSpeed.setTypeface(type);
        gameSpeed.requestLayout();
        gameSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame.putExtra("gameType", "speed");
                startActivity(newGame);
            }
        });

        //button check high score list
        Button highScore = (Button) findViewById(R.id.button2);
        highScore.getLayoutParams().height=tmpint;
        highScore.setTypeface(type);
        highScore.requestLayout();
        highScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create a Dialog component
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //tell the Dialog to use the dialog.xml as it's layout description

                dialog.setContentView(R.layout.dialog);
                String topScoreUpdated[] = prefs.getString(Integer.toString(1), "").split("#");
                String topScoreSpeedUpdated[] = prefsSpeed.getString(Integer.toString(1), "").split("#");

                //update / insert if not already done
              /*  Log.e("topScoreUpdated[0]",topScoreUpdated[0]);
                Log.e("topScoreUpdated[1]",topScoreUpdated[1]);
                Log.e("topScoreSpeedUpdated[0]",topScoreSpeedUpdated[0]);
                Log.e("topScoreSpeedUpdated[1]",topScoreSpeedUpdated[1]);*/
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! add check for index (if speed not played)

                if(topScoreUpdated[1].equalsIgnoreCase("FALSE"))
                {
                    //Log.e("goToUpdate",topScoreUpdated[1]);
                    new BackgroundAsyncTask(userDeviceId,topScoreUpdated[0],"update",dialog).execute();
                }
                if(topScoreSpeedUpdated[1].equalsIgnoreCase("FALSE"))
                {
                    //Log.e("goToUpdate",topScoreSpeedUpdated[1]);
                    new BackgroundAsyncTask(userDeviceId,topScoreSpeedUpdated[0],"updateSpeed",dialog).execute();
                }

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonClose);
                dialogButton.setTypeface(type);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            dialog.dismiss();
                        } else
                        dialog.dismiss();
                    }
                });
                Button checkOnlineButton = (Button) dialog.findViewById(R.id.dialogButtonCheckOnline);
                checkOnlineButton.setTypeface(type);
                checkOnlineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView onlineResult = (TextView) dialog.findViewById(R.id.textViewOnlineScore);
                        onlineResult.setText("test result");
                        new BackgroundAsyncTask(userDeviceId,"1234","get",dialog).execute();
                        onlineResult.setText(rank);
                    }


                });

                dialog.show();
                setScores(dialog);
            }

            private void setScores(Dialog dialog) {

                TextView scoreFieldNormal;
                TextView scoreFieldSpeed;

                for(int index = 1;index<11;index++)
                {
                    //dynamically set TextView string variable in format textViewR[rowNumber][columnNumber]
                    String tmpStr2 = "textViewR";
                    String tmpStr3 = "textViewR";
                    //row
                    tmpStr2 += Integer.toString(index);
                    tmpStr3 += Integer.toString(index);
                    //column
                    tmpStr2 += "2";
                    tmpStr3 += "3";
                    //get high scores from preferences
                    String[] s = (prefs.getString(Integer.toString(index), "")).split("#");
                    String[] sSpeed = (prefsSpeed.getString(Integer.toString(index), "")).split("#");
                    //set TextView to adequate row
                    scoreFieldNormal =(TextView)dialog.findViewById(getBaseContext().getResources().getIdentifier(tmpStr2,"id","com.sevengreen.ilija.reflexo"));
                    scoreFieldSpeed =(TextView)dialog.findViewById(getBaseContext().getResources().getIdentifier(tmpStr3,"id","com.sevengreen.ilija.reflexo"));

                    //set score to each row
                    for(int i=0;i<s.length;i++)
                    {
                        if(i==0)
                            scoreFieldNormal.setText(s[0]);
                    }
                    for(int i=0;i<sSpeed.length;i++)
                    {
                        if(i==0)
                            scoreFieldSpeed.setText(sSpeed[0]);
                    }
                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);






        return true;
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("0FD8485732E4C649D04FE57F12A5845D")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


}
