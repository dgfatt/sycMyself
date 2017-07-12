package cn.syc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cn.syc.listener.OnSelectedListener;


public class MyClockView extends View {

	private static final String TAG = "clock";
	private float currentX;
	private float currentY;// 当前鼠标坐标值
	private boolean isEventMove = false;// 刻度是否开始移动
	private boolean isLoading = false;// 是否loading
	private double loadingPercent = 0; // loading模式下百分比
	private Paint paint = null;
	private Paint outArcPaint = null; // 外弧线画笔
	private Paint inArcPaint = null; // 内弧线画笔
	private Paint textPaint = null;// 文字pain
	private int mWidth;
	private int mHeight;
	private RectF rect = null;// 外弧矩形
	private int outArcStrokeWidth = 5; // 外弧线粗细
	private int inArcStrokeWidth = 120; // 内弧线粗细
	private int mPointRedius = 400;// 指针半径
	private int mPointStrokeWidth = 8; // 指针粗细
	private int mTickeStrokeWidth = 2; // 刻度粗细
	private int mTikeWidth = 60;// 刻度的长度
	private int mTikeCount = 120;// 刻度
	private int selectDoor = 0;// 用户选择view的值如后门，前门
	private OnSelectedListener mListener; // 声明接口对象

	public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub

	}

	public MyClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// setWillNotDraw(false);
		//callBackListener = (OnSelectedCallBackListener) context;
		paint = new Paint();
		paint.setARGB(255, 0, 191, 255);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		outArcPaint = new Paint();
		outArcPaint.setARGB(180, 30, 144, 255);
		outArcPaint.setStyle(Paint.Style.STROKE);
		outArcPaint.setAntiAlias(true);

		inArcPaint = new Paint();
		inArcPaint.setARGB(120, 0, 255, 0);
		inArcPaint.setStyle(Paint.Style.STROKE);
		inArcPaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setARGB(200, 0, 0, 0);
		textPaint.setTextSize(50);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setAntiAlias(true);
	}

	public MyClockView(Context context) {
		super(context);
	}
	
	public void setOnSelectedListener(OnSelectedListener l){
		this.mListener=l;
	}
		
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = measureDimsension(1080, widthMeasureSpec);
		mHeight = measureDimsension(mWidth, heightMeasureSpec);
		// Log.i(TAG, "View width:" + mWidth + " height:" + mHeight);
		setMeasuredDimension(mWidth, mHeight);
	}

	public int measureDimsension(int defaultSize, int measureSpec) {

		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int mOutArcRedius = (mWidth - outArcStrokeWidth) / 2; // 外圆弧半径
		// 定义外弧线矩形区域大小
		rect = new RectF(outArcStrokeWidth, outArcStrokeWidth, mWidth - outArcStrokeWidth, mHeight - outArcStrokeWidth);
		// 画内弧线
		inArcPaint.setStrokeWidth(inArcStrokeWidth);
		RectF secondRectF = new RectF(inArcStrokeWidth + 30, inArcStrokeWidth + 30, mWidth - inArcStrokeWidth - 30,
				mHeight - inArcStrokeWidth - 30);
		canvas.drawArc(secondRectF, 145, 250, false, inArcPaint);

		// 画成120度的三条半径,同时计算内弧线左面和右面与指针交点坐标，同时该坐标也可以用后面初始刻度
		paint.setARGB(255, 192, 192, 192);
		paint.setStrokeWidth(20);
		float startX = (float) Math.cos(Math.PI * 35.0 / 180.0) * 360;
		float startY = (float) Math.sin(Math.PI * 35.0 / 180.0) * 360;
		// Log.d(TAG, startX + "-" + startY + "-" + mOutArcRedius);
		canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2, outArcStrokeWidth + 90, paint);
		canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 - startX, startY + mHeight / 2 - outArcStrokeWidth, paint);
		canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 + startX, startY + mHeight / 2 - outArcStrokeWidth, paint);

		// 画外弧线外面的文字
		canvas.drawText("正门", mWidth / 2 - outArcStrokeWidth + 10, outArcStrokeWidth + 135, textPaint);
		canvas.drawText("后门", mWidth / 2 - outArcStrokeWidth + 10, mHeight - outArcStrokeWidth - 80, textPaint);
		canvas.drawText("值班室", outArcStrokeWidth + 150, mHeight / 2, textPaint);
		canvas.drawText("机房", mWidth - outArcStrokeWidth - 130, mHeight / 2, textPaint);

		// 绘制小圆外圈
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawCircle(mWidth / 2, mHeight / 2, 10, paint);
		// 绘制小圆内圈
		paint.setStrokeWidth(5);
		canvas.drawCircle(mWidth / 2, mHeight / 2, 30, paint);

		// 画刻度
		paint.setARGB(200, 0, 191, 255);
		paint.setStrokeWidth(mTickeStrokeWidth);
		float rAngle = 360f / mTikeCount;
		for (int i = 0; i < mTikeCount / 2; i++) {
			canvas.rotate(rAngle, mWidth / 2, mHeight / 2);
			canvas.drawLine(mWidth / 2, outArcStrokeWidth, mWidth / 2, mTikeWidth, paint);
		}
		canvas.rotate(-rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);
		// 通过旋转画布 绘制左面的刻度
		for (int i = 0; i < mTikeCount / 2; i++) {
			canvas.rotate(-rAngle, mWidth / 2, mHeight / 2);
			canvas.drawLine(mWidth / 2, outArcStrokeWidth, mWidth / 2, mTikeWidth, paint);
		}

		// 现在需要将将画布旋转回来
		canvas.rotate(rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);

		Log.i(TAG, "onDraw is called isloading:" + isEventMove + " isEventMove:" + isEventMove);
		
		if (isLoading) {
			
			if (loadingPercent >= 0 && loadingPercent <= 100) {
				// 画刻度
				paint.setARGB(255, 0, 191, 255);
				paint.setStrokeWidth(mTickeStrokeWidth+6);				
				loadingPercent=loadingPercent*3.6;				
				for (int i = 0; i < loadingPercent/rAngle; i++) {
					//Log.w(TAG, "loadingPercent:" +loadingPercent);
					canvas.rotate(rAngle, mWidth / 2, mHeight / 2);
					canvas.drawLine(mWidth / 2, outArcStrokeWidth, mWidth / 2, mTikeWidth, paint);
				}
				canvas.rotate(-rAngle * (float)loadingPercent, mWidth / 2, mHeight / 2);				
			}		
					
			return;
		}

		

		if (isEventMove) {
			paint.setStrokeWidth(mPointStrokeWidth);
			float pointAngle = -(float) (Math.atan2(mHeight / 2 - currentY, mWidth / 2 - currentX) * 180.0 / Math.PI);
			startX = (float) Math.cos(Math.PI * pointAngle / 180.0) * mPointRedius;
			startY = (float) Math.sin(Math.PI * pointAngle / 180.0) * mPointRedius;
			canvas.drawLine(mWidth / 2 - startX, startY + mHeight / 2, mWidth / 2, mHeight / 2, paint);
			// Log.d(TAG, "指针 currentX:" + currentX + " currentY:" + currentY +
			// "角度:" + pointAngle);
			int intPointAngle = Math.abs((int) pointAngle);
			// Log.d(TAG, "指针转动的角度:" + intPointAngle);
			paint.setStyle(Paint.Style.FILL);
			if (intPointAngle < 10) {
				paint.setARGB(200, 255, 0, 0);
				canvas.drawCircle(mWidth / 2 - startX, startY + mHeight / 2, 50, paint);
			} else if (intPointAngle > 80 && intPointAngle < 100) {
				if (pointAngle > 0) {
					paint.setARGB(200, 127, 255, 0);
					canvas.drawCircle(mWidth / 2 - startX, startY + mHeight / 2, 50, paint);
				} else {
					paint.setARGB(200, 153, 50, 204);
					canvas.drawCircle(mWidth / 2 - startX, startY + mHeight / 2, 50, paint);
				}
			} else if (intPointAngle > 170) {
				paint.setARGB(200, 255, 140, 0);
				canvas.drawCircle(mWidth / 2 - startX, startY + mHeight / 2, 50, paint);
			} else {
				paint.setStyle(Paint.Style.STROKE);
				paint.setARGB(200, 0, 191, 255);
				canvas.drawCircle(mWidth / 2 - startX, startY + mHeight / 2, 25, paint);
			}

			return;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获取手指在屏幕上的坐标
		float x = event.getX();
		float y = event.getY();

		// 获取手指的操作--》按下、移动、松开
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:// 按下
			Log.i(TAG, "ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:// 移动
			isEventMove = true;			
			currentX = x;
			currentY = y;
			mListener.OnfingerDown(x, y);
			break;
		case MotionEvent.ACTION_UP:// 松开
			Log.i(TAG, "ACTION_UP");
			int doorNum = 0;
			String door ="";
			float pointAngle = -(float) (Math.atan2(mHeight / 2 - currentY, mWidth / 2 - currentX) * 180.0 / Math.PI);
			int intPointAngle = Math.abs((int) pointAngle);
			Log.w(TAG, pointAngle + "--" + intPointAngle);

			if (intPointAngle < 10) {
				Log.d(TAG, "你选择了值班室");
				doorNum = 3;
				door="值班室";
			} else if (intPointAngle > 80 && intPointAngle < 100) {
				if (pointAngle > 0) {
					Log.d(TAG, "你选择了后门");
					doorNum = 4;
					door="后门";
				} else {
					Log.d(TAG, "你选择了正门");
					doorNum = 1;
					door="正门";
				}
			} else if (intPointAngle > 170) {
				Log.d(TAG, "你选择了机房");
				doorNum = 2;
				door="机房";
			} else {
				Log.d(TAG, "你没有选择");
				doorNum = 0;
			}
			if (doorNum != 0) {
				this.selectDoor = doorNum;
				mListener.OnSelected(this.selectDoor,door);
			}
			break;
		}
		invalidate();
		return true;
	}

	public int getSelectDoor() {
		return this.selectDoor;

	}

	public synchronized void setPoint(int v) {
		Log.i(TAG, "setpoint:" + v);
		this.isLoading = true;
		this.isEventMove=false;
		this.loadingPercent = v;
		if(v==100){
			this.isLoading=false;
		}
		invalidate();
	}

}
