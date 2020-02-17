package com.example.user.l_1_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class Draw extends View {

    private String itemData;
    private Paint zonePaint;
    private Paint defaultPaint;

    public Draw(Context context, String itemData) {
        super(context);
        this.itemData = itemData;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int zone = getZone();
        defaultPaint = new Paint(Color.BLACK);
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeWidth(10);

        switch (zone) {
            case 1:
                zonePaint = new Paint();
                zonePaint.setColor(Color.RED);
                zonePaint.setStyle(Paint.Style.STROKE);
                zonePaint.setStrokeWidth(20);
                drawHouseWalls(canvas, defaultPaint);
                drawWindow(canvas, defaultPaint);
                drawDoor(canvas, zonePaint);
                break;
            case 2:
                zonePaint = new Paint();
                zonePaint.setColor(Color.argb(255, 255, 0, 0));
                zonePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                zonePaint.setStrokeWidth(15);
                drawInterior(canvas, zonePaint);
                drawHouseWalls(canvas, defaultPaint);
                drawDoor(canvas, defaultPaint);
                drawWindow(canvas, defaultPaint);
                break;
            case 3:
                zonePaint = new Paint();
                zonePaint.setColor(Color.RED);
                zonePaint.setStyle(Paint.Style.STROKE);
                zonePaint.setStrokeWidth(20);
                drawHouseWalls(canvas, defaultPaint);
                drawDoor(canvas, defaultPaint);
                drawWindow(canvas, zonePaint);
                break;
            case 4:
                zonePaint = new Paint();
                zonePaint.setColor(Color.rgb(255, 255, 0));
                zonePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                zonePaint.setStrokeWidth(15);
                drawInterior(canvas, zonePaint);
                drawHouseWalls(canvas, defaultPaint);
                drawDoor(canvas, defaultPaint);
                drawWindow(canvas, defaultPaint);
                break;
            default:
                break;
        }
    }

    int getZone() {
        String values[] = this.itemData.split(" ");
        int zone = Integer.parseInt(values[1]);
        return zone;
    }

    void drawDoor(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(20);
        float lineLength = canvas.getHeight() - 158;
        canvas.drawLine(canvas.getWidth() / 2, canvas.getHeight() - 50 + 5, canvas.getWidth() / 2, lineLength, paint);
        RectF rectF = new RectF((canvas.getWidth() / 4) + 175, (canvas.getHeight() / 2) + 620, (canvas.getWidth() / 2) + 100, canvas.getHeight() + 59);
        canvas.drawArc(rectF, 180, 90, false, paint);
    }

    void drawHouseWalls(Canvas canvas, Paint paint) {
        int left = 50;
        int top = 50;
        int right = canvas.getWidth() - 50;
        int bottom = canvas.getHeight() - 50;
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, paint);
    }

    void drawWindow(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(20);
        canvas.drawLine(canvas.getWidth() - 50, canvas.getHeight() / 2, canvas.getWidth() - 50, canvas.getHeight() / 4, paint);
    }

    void drawInterior(Canvas canvas, Paint paint) {
        int left = 50;
        int top = 50;
        int right = canvas.getWidth() - 50;
        int bottom = canvas.getHeight() - 50;
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, paint);
    }
}