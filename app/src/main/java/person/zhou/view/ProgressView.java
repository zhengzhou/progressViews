package person.zhou.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.mgyun.shua.R;

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

    private boolean withText = false;

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
        mProgress = a.getInt(R.styleable.CircleProgress_progress, 0);
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
        //mBacPain.setShadowLayer(5, 2, 2, Color.GRAY);

        mTextPain.setAntiAlias(true);
        mTextPain.setColor(Color.BLACK);
        //mTextPain.setShadowLayer(5, 2, 2, Color.GRAY);
        mTextPain.setStrokeCap(Cap.ROUND);
        mTextPain.setTextSize(getResources().getDimension(R.dimen.fontSize));
        mTextPain.setTypeface(Typeface.DEFAULT_BOLD);
        textWidth = mTextPain.measureText("00%");
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        setProgress(mProgress,false);
    }

    public void setProgress(int progress,boolean withAnim){
        if(withAnim){
            ProgressAnimation animation = new ProgressAnimation(progress);
            this.startAnimation(animation);
        }else if (progress >= 0) {
            this.mProgress = progress;
            invalidate();
        }
    }

    /**
     * 是否显示中间文字
     *
     * @param withText
     */
    public void setWithText(boolean withText){
        this.withText = withText;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval, mStartAngle, mSweepAngle, false, mBacPain);
        canvas.drawArc(oval, mStartAngle, mSweepAngle * mProgress / 100, false, mForPain);
        if(withText)
            canvas.drawText(mProgress + "%", (getPaddingLeft() + getWidth() - textWidth) / 2, getPaddingTop() + getWidth() / 2, mTextPain);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int mode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(mode == MeasureSpec.AT_MOST){
            int width = getMeasuredWidth();
            final int measureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, measureSpec);
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
        }
    }

    public class ProgressAnimation extends Animation {

        int start,end;

        public ProgressAnimation(int end) {
            start = getProgress();
            this.end = end;
            setDuration(2000 * (end - start) / 100);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            setProgress((int) (start + (end - start) * interpolatedTime));
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }
    }
}
