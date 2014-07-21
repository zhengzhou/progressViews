package person.zhou.view;

import com.example.progressdemo.R;
import com.example.progressdemo.R.dimen;
import com.example.progressdemo.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 弧形的进度条．
 *
 * @author zhou
 *
 */
public class ProgressView extends View {

    /** 所代表的进度 */
    private int mProgress;
    private int mAnimaProgress = -1;

    /** 前景画笔 */
    private Paint mForPain;
    /** 背景画笔 */
    private Paint mBacPain;
    private Paint mTextPain;

    /** 前景色 */
    private int mForColor;
    /** 背景色 */
    private int mBacColor;
    /** 开始角度 */
    private float mStartAngle;
    /** 圆弧扫过的角度 */
    private float mSweepAngle;

    /** 绘制区域 */
    RectF oval = new RectF();
    /** 文字的宽度 */
    float textWidth;

    /** 动画 */
    Callback invalidateCall = new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (mAnimaProgress < mProgress) {
                mAnimaProgress += 3;
                invalidate();
            }
            return true;
        }
    };

    Handler animaHandle = new Handler(invalidateCall);

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        obtionAttrs(context, attrs);
        init();
    }

    /** 获取自定义属性，颜色，角度 */
    private void obtionAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mForColor = a.getColor(R.styleable.CircleProgress_forColor, Color.rgb(10, 20, 220));
        mBacColor = a.getColor(R.styleable.CircleProgress_bacColor, Color.rgb(200, 200, 200));
        mStartAngle = a.getFloat(R.styleable.CircleProgress_startAngle, 160);
        mSweepAngle = a.getFloat(R.styleable.CircleProgress_sweepAngle, 540 - 2 * mStartAngle);
        mProgress = a.getInt(R.styleable.CircleProgress_progress, 20);
        a.recycle();
    }

    /** 这里配置字体颜色等界面效果 */
    private void init() {
        mForPain = new Paint();
        mBacPain = new Paint();
        mTextPain = new Paint();
        // BlurMaskFilter filter = new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER);
        float strokeWidth = getResources().getDimension(R.dimen.strokeWidth);
        mForPain.setAntiAlias(true);
        mForPain.setStyle(Paint.Style.STROKE);
        mForPain.setColor(mForColor);
        mForPain.setStrokeCap(Cap.ROUND);
        mForPain.setStrokeWidth(strokeWidth);

        mBacPain.setAntiAlias(true);
        mBacPain.setStyle(Paint.Style.STROKE);
        mBacPain.setColor(mBacColor);
        mBacPain.setStrokeCap(Cap.ROUND);
        mBacPain.setStrokeWidth(strokeWidth);
        // mBacPain.setMaskFilter(filter);
        mBacPain.setShadowLayer(5, 2, 2, Color.GRAY);

        mTextPain.setAntiAlias(true);
        mTextPain.setColor(Color.BLACK);
        mTextPain.setShadowLayer(5, 2, 2, Color.GRAY);
        mTextPain.setStrokeCap(Cap.ROUND);
        mTextPain.setTextSize(getResources().getDimension(R.dimen.fontSize));
        mTextPain.setTypeface(Typeface.DEFAULT_BOLD);
        textWidth = mTextPain.measureText("00%");
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        mAnimaProgress = -1;
        animaHandle.sendEmptyMessage(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval, mStartAngle, mSweepAngle, false, mBacPain);
        canvas.drawArc(oval, mStartAngle, mSweepAngle * mAnimaProgress / 100, false, mForPain);
        canvas.drawText(mProgress + "%", (getPaddingLeft() + getWidth() - textWidth) / 2, getPaddingTop() + getWidth() / 2, mTextPain);
        if (mAnimaProgress < mProgress) {
            animaHandle.sendEmptyMessageDelayed(0, 500 / mProgress);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int left, right, top, bottom;
        if ((w > 0) && (h > 0) && ((w != oldw) || (h != oldh))) {
            left = getPaddingLeft();
            right = getPaddingRight();
            top = getPaddingTop();
            bottom = getPaddingBottom();
            oval.set(left, top, w - right, w - bottom);
            // Logger.dd("w:%d,h:%d", w, h);
        }
    }

}
