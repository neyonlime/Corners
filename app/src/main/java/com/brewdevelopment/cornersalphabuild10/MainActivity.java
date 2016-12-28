package com.brewdevelopment.cornersalphabuild10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public GameView gameView;
    public ArrayList<GameShapes> shapeList;
    public ArrayList<WinZone> zoneList;
    public int canvasWidth;
    public int canvasHeight;
    public boolean first = true;
    public float x,y;
    private float xMargin,yMargin;
    public boolean hasVelocity = false;
    public boolean collision = false;
    public boolean mfirst = true;
    public CollisionListener colListener;
    public WinChecker winChecker;
    public boolean end = false;
    public TimerTask mTask;
    public Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        shapeList = new ArrayList<GameShapes>();
        zoneList = new ArrayList<WinZone>();            //Index: 0=RED, 1=BLUE, 2=GREEN, 4=DARK_BLUE
                colListener = new CollisionListener();
        gameView = new GameView(this);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frame);
        frameLayout.addView(gameView);
        final Random random = new Random();
        final int[] colors = {GameShapes.RED, GameShapes.BLUE, GameShapes.GREEN,GameShapes.DARK_BLUE};
        final int[] positions = {GameShapes.BOTTOM, GameShapes.LEFT, GameShapes.RIGHT, GameShapes.TOP};

        mTask = new TimerTask() {
            @Override
            public void run() {
                if(!end){
                    int color = colors[random.nextInt(4)];
                    int position = positions[random.nextInt(4)];
                    int velocity = random.nextInt((80-30)+1)+30;

                    GameShapes shape = new GameShapes(80,color,position,canvasHeight,canvasWidth);
                    shapeList.add(shape);

                    if(mfirst) {
                        xMargin = canvasWidth - shapeList.get(0).getRadius();
                        yMargin = canvasHeight - shapeList.get(0).getRadius();
                        mfirst = false;
                    }

                    int index = shapeList.size();
                    colListener.fling(velocity,velocity,0.4f,1,index-1);
                    hasVelocity = true;
                }

            }
        };


    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    //Zones
    public void generateZones(){
        //
        WinZone redWinZone = new WinZone(350,WinZone.RED,WinZone.TLEFT, canvasWidth, canvasHeight,160);
        WinZone blueWinZone = new WinZone(350,WinZone.BLUE, WinZone.TRIGHT, canvasWidth, canvasHeight,160);

        zoneList.add(redWinZone);
        zoneList.add(blueWinZone);

        winChecker = new WinChecker();
    }

    //Populate Shape List
    public void populateShapeList(){

         mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTask,0,1400);


    }

    private class GameView extends View {
        private Timer timer;
        private TimerTask task;
        private int canvasColor= Color.argb(255, 255, 248, 240);
        public GestureDetector gestures;
        private int[] extraRadius;

        public GameView(Context context) {
            super(context);

            gestures = new GestureDetector(MainActivity.this,
                    new GestureListener(GameView.this));

            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    if(hasVelocity) {
                        postInvalidate();
                    }
                }
            };
        }

        public void pause() {
            Log.d("PAUSE", "PAUSE");
            timer.cancel();
            timer.purge();
            task.cancel();

        }

        public void resume() {
           timer = new Timer();
            timer.scheduleAtFixedRate(task, 0, 10);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //DRAWING CANVAS
            canvas.drawColor(canvasColor);        //Canvas Background


            //If First Call
            if(first){
                canvasHeight = canvas.getHeight();
                canvasWidth  = canvas.getWidth();

                populateShapeList();
                generateZones();


                first  = false;
            }


            //If item has velocity
            if(hasVelocity){
                Log.d("INITIAL", "CAlLED 1");
                boolean collisionOccured = false;
                for(int i = 0;i<shapeList.size();i++){

                    //get Current shape's current info
                    float currX = shapeList.get(i).getX();
                    float currY = shapeList.get(i).getY();
                    float currXVelocity = shapeList.get(i).getVelocityX();
                    float currYVelocity = shapeList.get(i).getVelocityY();
                    float xAccel = shapeList.get(i).getxAcceleration();
                    float yAccel = shapeList.get(i).getyAcceleration();

                    //X Border Collision
                    if(colListener.checkBorderCollision(colListener.X_AXIS,currX,i)){
                        Log.d("Collision", "called");

                        collisionOccured = true;
                        colListener.bounce(i,colListener.X_AXIS,currX,currXVelocity);

                        //Check if border collision occures
                        collision = false;
                        if(colListener.checkCollision(i)){
                            Log.d("Collision" , "CALLED%#");
                            collision = true;
                        }
                    }

                    //Y Border Collision
                    if (colListener.checkBorderCollision(colListener.Y_AXIS,currY,i)) {
                        collisionOccured = true;

                        colListener.bounce(i,colListener.Y_AXIS,currY,currYVelocity);

                        collision = false;
                        if (colListener.checkCollision(i)) {
                            Log.d("Collision", "CALLED%#");
                            collision = true;
                        }
                    }

                    //If a collision occured with border has occured
                    if(collisionOccured){
                        Log.d("INITIAL" , "CALLED 3");
                        currXVelocity = shapeList.get(i).getVelocityX();
                        currYVelocity = shapeList.get(i).getVelocityY();
                        xAccel = shapeList.get(i).getxAcceleration();
                        yAccel = shapeList.get(i).getyAcceleration();
                        currX = shapeList.get(i).getX();
                        currY = shapeList.get(i).getY();

                        colListener.resetAcceleration(i,currXVelocity,currYVelocity
                                                                    ,xAccel,yAccel,0.4f);
                        //Movement
                        if(xAccel > 0 && currXVelocity < 0){
                            shapeList.get(i).setX(currX + currXVelocity);
                            shapeList.get(i).setVelocityX(currXVelocity + xAccel);
                        }else if(xAccel < 0 && currXVelocity > 0){
                            shapeList.get(i).setX(currX + currXVelocity);
                            shapeList.get(i).setVelocityX(currXVelocity + xAccel);
                        }

                        if(yAccel > 0 && currYVelocity < 0){
                            shapeList.get(i).setY(currY + currYVelocity);
                            shapeList.get(i).setVelocityY(currYVelocity + yAccel);
                        }else if(yAccel < 0 && currYVelocity > 0){
                            shapeList.get(i).setY(currY + currYVelocity);
                            shapeList.get(i).setVelocityY(currYVelocity + yAccel);
                        }
                    }else{
                            //Movement

                            if(xAccel > 0 && currXVelocity < 0){
                                Log.d("INITIAL", "CALLED 55");
                                shapeList.get(i).setX(currX + currXVelocity);
                                shapeList.get(i).setVelocityX(currXVelocity + xAccel);
                            }else if(xAccel < 0 && currXVelocity > 0){
                                Log.d("INITIAL", "CALLED 56");
                                shapeList.get(i).setX(currX + currXVelocity);
                                shapeList.get(i).setVelocityX(currXVelocity + xAccel);
                            }

                            if(yAccel > 0 && currYVelocity < 0){
                                Log.d("INITIAL", "CALLED 57");
                                shapeList.get(i).setY(currY + currYVelocity);
                                shapeList.get(i).setVelocityY(currYVelocity + yAccel);
                            }else if(yAccel < 0 && currYVelocity > 0){
                                Log.d("INITIAL", "CALLED 58");
                                shapeList.get(i).setY(currY + currYVelocity);
                                shapeList.get(i).setVelocityY(currYVelocity + yAccel);
                            }
                    }

                    if(winChecker.score(i) == 0){
                        for(int j=0;j<zoneList.size();j++) {
                            if (winChecker.getScorringTag()== zoneList.get(j).getTag()) {
                                zoneList.get(j).setLastRADIUS(zoneList.get(j).getRADIUS());
                                zoneList.get(j).setMaxSize(shapeList.get(i).getRadius() / 2);
                                zoneList.get(j).setGrowthRate(2);
                            }
                        }
                        //Remove Shape
                        shapeList.remove(i);
                        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        mVibrator.vibrate(20);
                        Log.d("WIN", "WIN");

                        //LOSING
                    }else if(winChecker.score(i) == 2){
                        for(int j=0;j<zoneList.size();j++) {
                            if (winChecker.getLosingTag() == zoneList.get(j).getTag()) {
                                zoneList.get(j).setLastRADIUS(zoneList.get(j).getRADIUS());
                                zoneList.get(j).setMinSize(shapeList.get(i).getRadius() / 2);
                                zoneList.get(j).setShrinkRate(2);
                            }
                        }
                        //Remove Shape
                        shapeList.remove(i);
                        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        mVibrator.vibrate(20);
                    }

                }
            }

            for(int i=0 ; i<shapeList.size();i++){
                shapeList.get(i).draw(canvas);          //All Canvas Shapes
            }


            //Potential Grow/Shrink
            for(int j=0;j<zoneList.size();j++){
                if(winChecker.win(j)){
                    canvasColor = zoneList.get(j).getmColor();
                    zoneList.remove(j);
                    break;
                }
                if(!zoneList.get(j).isFinished()[0]){
                    zoneList.get(j).grow();
                }else if(!zoneList.get(j).isFinished()[1]){
                    zoneList.get(j).shrink();
                }
            }


            //Zone Related Drawing
            for(int i = 0;i<zoneList.size();i++){
                zoneList.get(i).draw(canvas);
                zoneList.get(i).drawLines(canvas);
            }
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            x = event.getX();
            y = event.getY();
            return gestures.onTouchEvent(event);
        }
    }


    //Gesture Listener Class
    private class GestureListener implements GestureDetector.OnGestureListener{

        public GameView view;

        public GestureListener(GameView view){
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("THING", "FLING");
            float x = e1.getX();
            float y = e1.getY();
            int index = getTouchedShape(x, y);

            if(index!=-1) {

                colListener.fling(e1, e2, velocityX, velocityY, 0.4f, 100, index);

                if (colListener.checkCollision(index)) {
                    collision = true;
                }

                hasVelocity = true;
            }
            return true;
        }

        //getTouchedShape
        public int getTouchedShape(float x,float y){
            int index=-1;
            float tempX;
            float tempY;
            int radius;
            for(int i = 0;i<shapeList.size();i++){
                tempX= shapeList.get(i).getX();
                tempY= shapeList.get(i).getY();
                radius = shapeList.get(i).getRadius();
                if(x > ((tempX)-(radius)) && x < ((tempX) + (radius))
                        && y > ((tempY) -(radius)) && y < ((tempY) + (radius))){
                    index = i;
                }
            }
            return index;
        }

    }

    //Collision Listener Class
    private class CollisionListener {

        public int X_AXIS = 0;
        public int Y_AXIS = 1;


        public CollisionListener(){

        }

        //On Fling
        synchronized public void fling(MotionEvent e1,MotionEvent e2,       //e2>e1 Fling right : e2<e1 fling left
                                       float velocityX,float velocityY,float friction, int RANGE, int index){
            int xFlag,yFlag;
            xFlag = yFlag= -1;

            if(e2.getX() < e1.getX()){
                xFlag = 1;
            }
            if(e2.getY() < e1.getY()){
                yFlag = 1;
            }

            shapeList.get(index).setVelocityX(velocityX/RANGE);
            shapeList.get(index).setVelocityY(velocityY / RANGE);

            shapeList.get(index).setTheta(Math.atan2(velocityY, velocityX));

            float xAccel =Math.abs((float)(friction*(Math.cos(shapeList.get(index).getTheta()))));
            float yAccel =Math.abs((float)(friction*Math.sin(shapeList.get(index).getTheta())));

            xAccel = xFlag*xAccel;
            yAccel = yFlag*yAccel;

            shapeList.get(index).setxAcceleration(xAccel);
            shapeList.get(index).setyAcceleration(yAccel);
        }

        //On Fling
        synchronized public void fling(float velocityX,float velocityY,float friction, int RANGE, int index){
            int xFlag,yFlag;
            xFlag = yFlag= -1;

            if(shapeList.get(index).getPosition() == GameShapes.TOP){
                velocityX = 0;

                yFlag = -1;
            }
            else if(shapeList.get(index).getPosition() == GameShapes.RIGHT){
                velocityX = velocityX*(-1);
                velocityY = 0;
                xFlag = 1;
            }else if(shapeList.get(index).getPosition() == GameShapes.BOTTOM){
                velocityX = 0;
                velocityY = velocityY*(-1);
                yFlag = 1;
            }else if(shapeList.get(index).getPosition() == GameShapes.LEFT){
                velocityY =0;
                xFlag =  -1;
            }


            shapeList.get(index).setVelocityX(velocityX/RANGE);
            shapeList.get(index).setVelocityY(velocityY / RANGE);

            shapeList.get(index).setTheta(Math.atan2(velocityY, velocityX));

            float xAccel =Math.abs((float)(friction*(Math.cos(shapeList.get(index).getTheta()))));
            float yAccel =Math.abs((float)(friction*Math.sin(shapeList.get(index).getTheta())));

            xAccel = xFlag*xAccel;
            yAccel = yFlag*yAccel;

            shapeList.get(index).setxAcceleration(xAccel);
            shapeList.get(index).setyAcceleration(yAccel);
        }

        //Check if Collision occures with border
        synchronized public boolean checkCollision(int index){
            //Check if border collision occures
            float finalX = shapeList.get(index).getFinalX();
            float finalY = shapeList.get(index).getFinalY();

            if(finalX > xMargin || finalY > yMargin || finalX < shapeList.get(index).getRadius()
                                                    || finalY < shapeList.get(index).getRadius()){
                Log.d("Collision" , "CALLED%#");
                return true;
            }else{
                return false;
            }
        }

        //Check if Border Collision has occured
        synchronized public boolean checkBorderCollision(int border, float respAxisValue, int index){

            if(border == 0){
                if(respAxisValue >= xMargin ||respAxisValue <= shapeList.get(index).getRadius()){
                    return true;
                }
            }else{
                if(respAxisValue >= yMargin ||respAxisValue <= shapeList.get(index).getRadius()){
                    return true;
                }
            }

            return false;
        }

        //Bounce Function
        synchronized public void bounce(int index, int AXIS, float respAxisValue, float respAxisVelocity){
            float newVelocity = (-1)*(respAxisVelocity/4);

            // X Velocity
            if(AXIS == 0){
                shapeList.get(index).setVelocityX(newVelocity);
                shapeList.get(index).setxAcceleration((-1) * (shapeList.get(index).getxAcceleration()));

                if(respAxisValue >= xMargin) {
                    shapeList.get(index).setX(xMargin - 1);
                }else{
                    shapeList.get(index).setX(80 - 1);
                }
            }

            //Y Velocity
            else if(AXIS == 1){
                shapeList.get(index).setVelocityY(newVelocity);
                shapeList.get(index).setyAcceleration((-1) * (shapeList.get(index).getyAcceleration()));

                if(respAxisValue >= yMargin) {
                    shapeList.get(index).setY(yMargin - 1);
                }else{
                    shapeList.get(index).setY(80 - 1);
                }
            }
        }

        //Acceleration Reset
        synchronized public void resetAcceleration(int index, float xVelocity, float yVelocity,
                                                   float xAccel, float yAccel , float friction){
            int xFlag,yFlag;
            xFlag=yFlag=-1;

            if(xAccel > 0){
                xFlag = 1;
            }
            if(yAccel >0){
                yFlag = 1;
            }

            shapeList.get(index).setTheta(Math.atan2(yVelocity, xVelocity));
            xAccel =Math.abs((float)(friction*(Math.cos(shapeList.get(index).getTheta()))));
            yAccel =Math.abs((float)(friction*Math.sin(shapeList.get(index).getTheta())));

            xAccel = xFlag*xAccel;
            yAccel = yFlag*yAccel;

            shapeList.get(index).setxAcceleration(xAccel);
            shapeList.get(index).setyAcceleration(yAccel);
        }

    }

    //Win Check Class
    private class WinChecker{
        int winningTag = -1;
        int RED = 0;
        int BLUE = 1;
        int GREEN = 2;
        int DARK_BLUE = 3;
        int losingTag;

        public WinChecker(){

        }

        synchronized public int score (int index){
            int shapeTag = shapeList.get(index).getTag();
            //Log.d("WIN","Tag: " + shapeTag);

            //RED ZONE
                float currX = 0;
                float currY = 0;
                int tempIndex = -1;

                //Finding occurance of red zone
                for (int i = 0; i < zoneList.size(); i++) {
                    if (zoneList.get(i).getTag() == RED) {
                        tempIndex = i;
                        break;
                    }
                }

                //Fetch range
                float[] range;

                if (tempIndex != -1) {                              //If red Zone found
                    range = zoneList.get(tempIndex).getRange();

                    switch ((int) (range[2] + range[3])) {

                        //Top Left
                        case 0:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                            if (currX < range[0] && currY < range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.RED_TAG){
                                    winningTag = RED;
                                    return 0;
                                }else{
                                    losingTag = RED;
                                    return 2;       //LOSE STATE
                                }

                            }
                            break;
                        //
                        case 1:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY < range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.RED_TAG){
                                    winningTag = RED;
                                    return 0;
                                }else{
                                    losingTag = RED;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                        case 2:
                            currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX < range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.RED_TAG){
                                    winningTag = RED;
                                    return 0;
                                }else{
                                    losingTag = RED;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                        case 3:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.RED_TAG){
                                    winningTag = RED;
                                    return 0;
                                }else{
                                    losingTag = RED;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                    }
                }


            //BLUE
                currX = 0;
                currY = 0;
                tempIndex = -1;

                //Finding occurance of red zone
                for (int i = 0; i < zoneList.size(); i++) {
                    if (zoneList.get(i).getTag() == BLUE) {
                        tempIndex = i;
                        break;
                    }
                }


                if (tempIndex != -1) {
                    //Set Occurance
                    range = zoneList.get(tempIndex).getRange();

                    switch ((int) (range[2] + range[3])) {

                        //Top Left
                        case 0:
                            Log.d("WIN", "0OUT CALLED");
                            if (currX < range[0] && currY < range[1]) {
                                currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                                currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                                if(shapeList.get(index).getTag() == GameShapes.BLUE_TAG){
                                    winningTag = BLUE;
                                    return 0;
                                }else{
                                    losingTag = BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Top Right
                        case 1:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY < range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.BLUE_TAG){
                                    winningTag = BLUE;
                                    return 0;
                                }else{
                                    losingTag = BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Left
                        case 2:
                            currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX < range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.BLUE_TAG){
                                    winningTag = BLUE;
                                    return 0;
                                }else{
                                    losingTag = BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Right
                        case 3:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.BLUE_TAG){
                                    winningTag = BLUE;
                                    return 0;
                                }else{
                                    losingTag = BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                    }
                }


            //GREEN
                currX = 0;
                currY = 0;
                tempIndex = -1;

                //Finding occurance of red zone
                for (int i = 0; i < zoneList.size(); i++) {
                    if (zoneList.get(i).getTag() == GREEN) {
                        tempIndex = i;
                        break;
                    }
                }

                //Set Occurance


                if (tempIndex != -1) {
                    range = zoneList.get(tempIndex).getRange();
                    switch ((int) (range[2] + range[3])) {

                        //Top Left
                        case 0:
                            Log.d("WIN", "0OUT CALLED");
                            if (currX < range[0] && currY < range[1]) {
                                currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                                currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                                if(shapeList.get(index).getTag() == GameShapes.GREEN_TAG){
                                    winningTag = GREEN;
                                    return 0;
                                }else{
                                    losingTag = GREEN;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Top Right
                        case 1:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY < range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.GREEN_TAG){
                                    winningTag = GREEN;
                                    return 0;
                                }else{
                                    losingTag = GREEN;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Left
                        case 2:
                            currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX < range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.GREEN_TAG){
                                    winningTag = GREEN;
                                    return 0;
                                }else{
                                    losingTag = GREEN;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Right
                        case 3:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.GREEN_TAG){
                                    winningTag = GREEN;
                                    return 0;
                                }else{
                                    losingTag = GREEN;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                    }
                }



            //DARK_BLUE

                currX = 0;
                currY = 0;
                tempIndex = -1;

                //Finding occurance of red zone
                for (int i = 0; i < zoneList.size(); i++) {
                    if (zoneList.get(i).getTag() == DARK_BLUE) {
                        tempIndex = i;
                        break;
                    }
                }

                if (tempIndex != -1) {
                    //Set Occurance
                    range = zoneList.get(tempIndex).getRange();
                    switch ((int) (range[2] + range[3])) {

                        //Top Left
                        case 0:
                            Log.d("WIN", "0OUT CALLED");
                            if (currX < range[0] && currY < range[1]) {
                                currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                                currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                                if(shapeList.get(index).getTag() == GameShapes.DARK_BLUE_TAG){
                                    winningTag = DARK_BLUE;
                                    return 0;
                                }else{
                                    losingTag = DARK_BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Top Right
                        case 1:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() + (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY < range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.DARK_BLUE_TAG){
                                    winningTag = DARK_BLUE;
                                    return 0;
                                }else{
                                    losingTag = DARK_BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Left
                        case 2:
                            currX = (shapeList.get(index).getX()) + (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX < range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.DARK_BLUE_TAG){
                                    winningTag = DARK_BLUE;
                                    return 0;
                                }else{
                                    losingTag = DARK_BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;

                        //Bottom Right
                        case 3:
                            currX = (shapeList.get(index).getX()) - (shapeList.get(index).getRadius());
                            currY = shapeList.get(index).getY() - (shapeList.get(index).getRadius());
                            if (currX > range[0] && currY > range[1]) {
                                if(shapeList.get(index).getTag() == GameShapes.DARK_BLUE_TAG){
                                    winningTag = DARK_BLUE;
                                    return 0;
                                }else{
                                    losingTag = DARK_BLUE;
                                    return 2;       //LOSE STATE
                                }
                            }
                            break;
                    }
                }

            return 1;
        }

        synchronized public boolean win(int index){
            int currRadius = zoneList.get(index).getRADIUS();
            int winningRadius = zoneList.get(index).getWinningRadius();

            if(currRadius >= winningRadius){
                return true;
            }else
                return false;

        }

        public int getScorringTag(){
            return winningTag;
        }

        public int getLosingTag(){
            return losingTag;
        }
    }
}
