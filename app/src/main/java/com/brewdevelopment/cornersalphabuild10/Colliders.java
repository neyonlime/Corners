package com.brewdevelopment.cornersalphabuild10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by neyon on 2016-08-03.
 */
public class Colliders {
    private float left;
    private float right;
    private float top;
    private float bottom;
    private Paint shapPaint;
    public static int BEIGE = Color.argb(255, 234, 222, 218);
    public static int BLACK = Color.argb(255, 52, 52, 74);
    private String tag;

    public Colliders(float left, float right , float bottom , float top, String tag,int color){
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.tag = tag;
        shapPaint = new Paint();
        shapPaint.setColor(color);
    }

    public Colliders(float length, float width, float x, float y, int color){
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.tag = tag;
        shapPaint = new Paint();
        shapPaint.setColor(color);
    }

    //Getters/Setters
    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public Paint getShapPaint() {
        return shapPaint;
    }

    public void setShapPaint(Paint shapPaint) {
        this.shapPaint = shapPaint;
    }

    public float getBottom() {
        return bottom;
    }

    public void setX(float bottom) {
        this.bottom = bottom;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    //Attributes
    public void draw (Canvas canvas){
        canvas.drawRect(left,top,right,bottom,shapPaint);
    }
}
