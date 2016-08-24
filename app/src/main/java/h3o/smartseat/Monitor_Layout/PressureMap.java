package h3o.smartseat.Monitor_Layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import h3o.smartseat.R;

/**
 * Created by mukga on 4/9/2016.
 */
public class PressureMap extends View {

    private int pTL = 0;
    private int pTR = 0;
    private int pMM = 0;
    private int pBL = 0;
    private int pBR = 0;
    private int temp = 2;

    public PressureMap(Context context) { super(context); }
    public PressureMap(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public PressureMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius;
        radius = 100;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.LTGRAY);
        canvas.drawPaint(paint);

//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.chair);
//        canvas.drawBitmap(b, 0, 0, p);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C")*temp);

        canvas.drawCircle(x / 4, y / 4, radius, paint);
        canvas.drawCircle(3*x / 4, 3*y / 4, radius, paint);
        canvas.drawCircle(3*x / 4, y / 4, radius, paint);
        canvas.drawCircle(x / 4, 3*y / 4, radius, paint);
        canvas.drawCircle(x / 2, y / 2, radius, paint);
    }

    public void setPressure(int nTL, int nTR, int nMM, int nBL, int nBR){
        pTL = nTL;
        pTR = nTR;
        pMM = nMM;
        pBL = nBL;
        pBR = nBR;
        temp = temp*2;
        invalidate();
        requestLayout();
    }
}
