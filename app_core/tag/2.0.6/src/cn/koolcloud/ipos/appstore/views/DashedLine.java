package cn.koolcloud.ipos.appstore.views;

 

import cn.koolcloud.ipos.appstore.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

 

public class DashedLine extends View {

	private float density;
	private Paint paint;
	private Path path;
	private PathEffect effects;

	public DashedLine(Context context)
	{
	    super(context);
	    init(context);
	}

	public DashedLine(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	    init(context);
	}

	public DashedLine(Context context, AttributeSet attrs, int defStyle)
	{
	    super(context, attrs, defStyle);
	    init(context);
	}

	private void init(Context context)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
	    density = metrics.density;
	    paint = new Paint();
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth(density * 4);
	    //set your own color
	    paint.setColor(context.getResources().getColor(R.color.gray_bg));
	    path = new Path();
	    //array is ON and OFF distances in px (4px line then 2px space)
	    effects = new DashPathEffect(new float[] { 4, 2, 4, 2 }, 0);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
	    // TODO Auto-generated method stub
	    super.onDraw(canvas);
	    paint.setPathEffect(effects);
	    int measuredHeight = getMeasuredHeight();
	    int measuredWidth = getMeasuredWidth();
	    if (measuredHeight <= measuredWidth)
	    {
	        // horizontal
	        path.moveTo(0, 0);
	        path.lineTo(measuredWidth, 0);
	        canvas.drawPath(path, paint);
	    }
	    else
	    {
	        // vertical
	        path.moveTo(0, 0);
	        path.lineTo(0, measuredHeight);
	        canvas.drawPath(path, paint);
	    }

	}

}