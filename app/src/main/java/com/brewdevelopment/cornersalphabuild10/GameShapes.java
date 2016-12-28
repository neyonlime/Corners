package com.brewdevelopment.cornersalphabuild10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.WindowManager;

/**
 * Created by neyon on 2016-08-02.
 */
public class GameShapes {
    private int Radius;
    private float x;
    private float y;
    private double theta;
    private Paint shapePaint;
    public static int RED = Color.argb(255, 254, 147, 140);
    public static int BLUE = Color.argb(255, 108, 212, 255);
    public static int GREEN = Color.argb(255, 6, 214, 160);
    public static int DARK_BLUE = Color.argb(255, 52, 52, 74);

    public static int TOP = 0;
    public static int RIGHT =1;
    public static int BOTTOM = 2;
    public static int LEFT = 3;

    private int tag;
    private float velocityX;
    private float velocityY;
    private float xAcceleration;
    private float yAcceleration;
    private int position;
    private int maxHeight;
    private int maxWidth;

    //Tags
    public static int RED_TAG =0;
    public static int BLUE_TAG = 1;
    public static int GREEN_TAG = 2;
    public static int DARK_BLUE_TAG= 3;

    public GameShapes(int RADIUS, float x, float y, int color,int maxHeight, int maxWidth){
        this.Radius = RADIUS;
        shapePaint = new Paint();
        shapePaint.setColor(color);
        this.x = x;
        this.y = y;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;

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

    public GameShapes(int RADIUS, int color, int position,int maxHeight, int maxWidth){
        this.Radius = RADIUS;
        shapePaint = new Paint();
        shapePaint.setColor(color);
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.position = position;


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
        
        //Setting initial position
        if(position == TOP){
            x=maxWidth/2;
            y=RADIUS+5;
        }else if(position == RIGHT){
            x=maxWidth-(RADIUS+5);
            y=maxHeight/2;
        }else if(position == BOTTOM){
            x=maxWidth/2;
            y=maxHeight -(RADIUS+5);
        }else if(position == LEFT){
            x=RADIUS+5;
            y=maxHeight/2;
        }
    }

    //Setters

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }

    public void setxAcceleration(float xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public void setyAcceleration(float yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }



    //Getters


    public int getPosition() {
        return position;
    }

    public int getTag() {
        return tag;
    }

    public int getRadius() {
        return Radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getxAcceleration() {
        return xAcceleration;
    }

    public float getyAcceleration() {
        return yAcceleration;
    }

    public double getTheta() {
        return theta;
    }

    //Work
    public void draw (Canvas canvas){
        canvas.drawCircle(x,y,Radius,shapePaint);
    }

    public float getFinalX(){
        float delta = (0 - (float)(Math.pow(velocityX,2)))/(2*xAcceleration);
        float finalX = x + delta;

        return finalX;
    }

    public float getFinalY(){
        float delta = (0-(float)(Math.pow(velocityY,2)))/(2*yAcceleration);
        float finalY = y + delta;

        return finalY;
    }
}
