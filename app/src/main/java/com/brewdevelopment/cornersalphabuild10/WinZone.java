package com.brewdevelopment.cornersalphabuild10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by neyon on 2016-08-05.
 */
public class WinZone {

    public static int RED = Color.argb(255, 254, 147, 140);
    public static int BLUE = Color.argb(255, 108, 212, 255);
    public static int GREEN = Color.argb(255, 6, 214, 160);
    public static int DARK_BLUE = Color.argb(255, 52, 52, 74);



    public static int TLEFT = 0;
    public static int TRIGHT = 1;
    public static int BLEFT = 2;
    public static int BRIGHT = 3;
    public static int NONE=-1;

    private int maxHeight;
    private int maxWidth;
    private int mColor;

    private float[] range;          //0: x range , 1:y range
    private Paint shapePaint;
    private float x;
    private float y;
    private int location;
    private int tag;
    private int RADIUS;
    private int growthRate;         //pixels per 10mils
    private int shrinkRate;         //Pixels per 10mils
    private boolean[] isFinished = {true,true};
    private int maxSize;
    private int minSize;
    private int lastRADIUS;
    private int extraRadius;
    private int winningRadius;

    //Tags
    public static int RED_TAG =0;
    public static int BLUE_TAG = 1;
    public static int GREEN_TAG = 2;
    public static int DARK_BLUE_TAG= 3;

    public WinZone(int RADIUS, int color, int location, int maxWidth, int maxHeight, int extraRadius) {
        shapePaint = new Paint();
        shapePaint.setColor(color);
        this.RADIUS = RADIUS;
        this.location = location;
        maxSize += RADIUS;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.extraRadius = extraRadius;
        this.mColor=color;
        this.winningRadius = RADIUS+extraRadius;

        //Setting range
        range = new float[4];

       //Set initial range
        setRange(maxHeight, maxWidth);

        //if specific location is defined
        if(location != -1){
            setXYPos(maxWidth,maxHeight);
        }

        //Setting Tag
        if(color == RED){
            tag = RED_TAG;
        }else if(color == BLUE){
            tag = BLUE_TAG;
        }else if(color == GREEN){
            tag = GREEN_TAG;
        }else if(color == DARK_BLUE){
            tag = DARK_BLUE_TAG;
        }
    }

    //With Specified x and y
    public WinZone(float x, float y, int RADIUS, int color, int location, int maxWidth, int maxHeight) {
        shapePaint = new Paint();
        shapePaint.setColor(color);
        this.x = x;
        this.y = y;
        this.RADIUS = RADIUS;
        this.location = location;
        maxSize += RADIUS;
        this.mColor = color;

        //Setting range
        range = new float[4];

        //Set initial range
        setRange(maxHeight, maxWidth);


      //Setting Tag
        if(color == RED){
            tag = RED_TAG;
        }else if(color == BLUE){
            tag = BLUE_TAG;
        }else if(color == GREEN){
            tag = GREEN_TAG;
        }else if(color == DARK_BLUE){
            tag = DARK_BLUE_TAG;
        }


    }

    //Setters & Getters

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        if(maxSize >0) {
            isFinished[0] = false;
        }
        this.maxSize += maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = RADIUS;
        if(minSize > 0 && minSize < RADIUS){
            isFinished[1] =false;
        }
        this.minSize -= minSize;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Paint getShapePaint() {
        return shapePaint;
    }

    public void setShapePaint(Paint shapePaint) {
        this.shapePaint = shapePaint;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(int growthRate) {
        this.growthRate = growthRate;
    }

    public void setIsFinished(int index, boolean flag) {
        isFinished[index] = flag;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getRADIUS() {
        return RADIUS;
    }

    public int getShrinkRate() {
        return shrinkRate;
    }

    public void setShrinkRate(int shrinkRate) {
        this.shrinkRate = shrinkRate;
    }

    public void setRADIUS(int RADIUS) {
        this.RADIUS = RADIUS;
    }

    public int getLastRADIUS() {
        return lastRADIUS;
    }

    public void setLastRADIUS(int lastRADIUS) {
        this.lastRADIUS = lastRADIUS;
    }

    //Functions
    public void draw(Canvas canvas){
        canvas.drawCircle(x,y,RADIUS, shapePaint);
    }

    //Draw Win Line
    public void drawLines(Canvas canvas){
        int tX,tY;
        int xFIX,yFIX;
        tX=tY=0;
        xFIX=yFIX=0;

        Paint linePaint = new Paint();
        linePaint.setColor(mColor);
        linePaint.setStrokeWidth(20);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);


        //Calculate X and Y
        int temp = 350+extraRadius;
        tX=tY=((int)(temp*((Math.sin(Math.PI/4)))))*2;

        //Calculate x and y fix

        if(location == TLEFT){
            canvas.drawLine(-10,tY,tX,-10,linePaint);
            return;
        }else if(location == TRIGHT){
            canvas.drawLine(maxWidth-tX,-10,maxWidth+10,tY,linePaint);
            return;
        }else if(location == BLEFT){
            canvas.drawLine(-10,maxHeight-tY,tX,maxHeight+10,linePaint);
            return;
        }else if(location == BRIGHT){
            canvas.drawLine(maxWidth-tX,maxHeight+10,maxWidth+10,maxHeight-tY,linePaint);
            return;
        }
    }

    //Set Range
    public void setRange(int maxHeight, int maxWidth){

        //Top Left
        if(location == TLEFT){
            range[0] = RADIUS;      //X
            range[1] = RADIUS;      //Y
            range[2] = 0;           //X = <
            range[3] = 0;           //Y = <
        }

        //Top Right
        else if (location == TRIGHT){
            range[0] = maxWidth -RADIUS;     //X
            range[1] = RADIUS;              //Y
            range[2] = 1;                   //X = >
            range[3] = 0;                   //Y = <
        }

        //Bottom Left
        else if(location == BLEFT){
            range[0] = RADIUS;              //X
            range[1] = maxHeight-RADIUS;    //Y
            range[2] = 0;
            range[3] = 2;
        }

        //Bottom Right
        else if(location == BRIGHT){
            range[0] = maxWidth-RADIUS;     //X
            range[1] = maxHeight-RADIUS;    //Y
            range[2] = 1;
            range[3] = 2;
        }
    }

    //Get Range
    public float[] getRange(){
        return range;
    }

    //Setting x and y based on location
    private void setXYPos(int maxWidth, int maxHeight){
        switch (location){
            case 0:
                //Top Left
                x =0;
                y =0;
                break;
            case 1:
                //Top Right
                x=maxWidth;
                y=0;
                break;
            case 2:
                //Bottom Left
                x=0;
                y=maxHeight;
                break;
            case 3:
                //Bottom Right
                x=maxWidth;
                y=maxHeight;
                break;
        }
    }


    //Grow at Growth rate
    public void grow(){
        Log.d("GROW", "RADIUS: " + RADIUS + "MAX SIZE: " + maxSize);
        if(RADIUS < maxSize){
            RADIUS+=growthRate;
        }else if(RADIUS >= maxSize){
            isFinished[0] = true;
        }
    }

    //Is Finished growing
    public boolean[] isFinished(){
        return isFinished;
    }

    //Shrink
    public void shrink(){
        if(RADIUS > minSize){
            RADIUS -=shrinkRate;
        }else if(RADIUS <= minSize){
            isFinished[1] = true;
        }
    }

    public int getWinningRadius() {
        return winningRadius;
    }

    public int getmColor() {
        return mColor;
    }
}

