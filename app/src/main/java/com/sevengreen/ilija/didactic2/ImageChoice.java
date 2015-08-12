package com.sevengreen.ilija.didactic2;

import android.graphics.drawable.LayerDrawable;

public class ImageChoice {

    public int choicePosition = 0;
    public boolean isCorrectAnswer = true;
    public String imageResource = "i1_1";

    //constructor
    public ImageChoice(int position, boolean correct, String resource) {
        choicePosition = position;
        isCorrectAnswer = correct;
        imageResource = resource;
    }

    public int getChoicePosition()
    {
        //include validation, logic, logging or whatever you like here
        return this.choicePosition;
    }
    public void setChoicePosition(int value)
    {
        //include more logic
        this.choicePosition = value;
    }
    public boolean getIsCorrectAnswer()
    {
        //include validation, logic, logging or whatever you like here
        return this.isCorrectAnswer;
    }
    public void setIsCorrectAnswer(boolean value)
    {
        //include more logic
        this.isCorrectAnswer= value;
    }
    public String getImageResource()
    {
        //include validation, logic, logging or whatever you like here
        return this.imageResource;
    }
    public void setImageResource(String value)
    {
        //include more logic
        this.imageResource = value;
    }


    public String getChoicePositionString() {
        // TODO Auto-generated method stub
        return Integer.toString(this.choicePosition);
    }
}
