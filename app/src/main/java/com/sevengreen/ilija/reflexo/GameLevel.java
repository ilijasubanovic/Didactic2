package com.sevengreen.ilija.reflexo;

public class GameLevel {

    private int levelNumber;
    boolean levelBonus;
    boolean levelPaused;

    public GameLevel(int i, boolean b, boolean p) {
        levelNumber = i;
        levelBonus = b;
        levelPaused = p;
    }

    public int getLevelNumber (){
        return this.levelNumber;
    }
    public void setLevelNumber (int i){
        this.levelNumber = i;
    }
    public boolean getLevelBonus (){
        return this.levelBonus;
    }
    public void setLevelBonus (boolean b){
        this.levelBonus = b;
    }
    public boolean getlevelPaused (){
        return this.levelPaused;
    }
    public void setlevelPaused (boolean p){
        this.levelPaused = p;
    }
}
