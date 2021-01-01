package com.example.volleytriviaapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

//import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {
    private SharedPreferences preferences;     // SharedPreferences is key-value pair.

    public SharedPrefs(Activity activity) {                     // When we want to use sharedPreferences or any class that connects to an Activity , we have to make sure that we pass along with that the Context. We could've also mentioned Context within Prefs().


        this.preferences = activity.getPreferences(activity . MODE_PRIVATE);      // We want to make the data in app , private ... Hence : MODE_PRIVATE.
    }
    public void setHighScore(int score){
        int currentScore = score;

        int lastScore = preferences.getInt("high_score" , 0);
        if(currentScore > lastScore){
            preferences.edit().putInt("high_score" , currentScore).apply();
        }
    }

    public int getHighScore(){
        return preferences.getInt("high_score", 0);
    }


    public void setState(int currentIndexOfQuestion){
        preferences.edit().putInt("current_index" , currentIndexOfQuestion).apply();
    }

    public int getState(){
      return  preferences.getInt("current_index" , 0);
    }
    public void setCurrentScore(int currentScore){
        preferences.edit().putInt("current_score" , currentScore).apply();
    }

    public int getCurrentScore(){
       return preferences.getInt("current_score" , 0);
    }
}
