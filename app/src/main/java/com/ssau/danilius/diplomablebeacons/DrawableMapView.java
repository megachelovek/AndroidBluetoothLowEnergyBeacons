package com.ssau.danilius.diplomablebeacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawableMapView extends View {
    public int width;
    public  int height;
    private boolean isEditable;
    private Path drawPath;
    private Paint beaconPaint,beaconText;
    private Paint beaconPaint2;
    private Paint drawPaint;
    private Paint canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int paintColor = Color.RED;
    public DrawableMapView(Context context) {
        super(context);
    }
    public DrawableMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.canvasPaint = new Paint(Paint.DITHER_FLAG);
        setupDrawing();
    }
    public DrawableMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.height = h;
        this.width = w;
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public void setDrawingEnabled(boolean isEditable){
        this.isEditable = isEditable;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isEditable){
            float touchX = event.getX(); //765
            float touchY = event.getY(); //825
            float beacoTestX = 765;
            float beacoTestY = 825;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:

                    drawPath.lineTo(touchX, touchY);
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath = new Path();
                    break;
                default:
                    return false;
            }
        } else{
            return false;
        }
        invalidate();
        return true;
    }

    private void drawBeacon(float x, float y, String name,String mac){
        drawCanvas.drawCircle(x,y,12,beaconPaint2);
        drawCanvas.drawCircle(x,y,6,beaconPaint);
        drawCanvas.drawText("67:78:89:AB:CD:EF", x-90, y+65, beaconText);
        drawCanvas.drawText("No name beacon", x-90, y+35, beaconText);
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(10);

        beaconPaint = new Paint();
        beaconPaint.setColor(Color.BLUE);
        beaconPaint.setAntiAlias(true);
        beaconPaint.setDither(true);
        beaconPaint.setStyle(Paint.Style.STROKE);
        beaconPaint.setStrokeJoin(Paint.Join.ROUND);
        beaconPaint.setStrokeCap(Paint.Cap.ROUND);
        beaconPaint.setStrokeWidth(10);

        beaconPaint2 = new Paint();
        beaconPaint2.setColor(Color.YELLOW);
        beaconPaint2.setAntiAlias(true);
        beaconPaint2.setDither(true);
        beaconPaint2.setStyle(Paint.Style.STROKE);
        beaconPaint2.setStrokeJoin(Paint.Join.ROUND);
        beaconPaint2.setStrokeCap(Paint.Cap.ROUND);
        beaconPaint2.setStrokeWidth(10);

        beaconText = new Paint();
        beaconText.setColor(Color.rgb(46,97,29));
        beaconText.setTextSize(30);
    }
}