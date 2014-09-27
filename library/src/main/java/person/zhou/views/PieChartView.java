package person.zhou.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 大饼图．
 *
 * @author zhou
 */
public class PieChartView extends View {

    /**
     * 绘制区域
     */
    RectF oval = new RectF();

    RectF ovalPie = new RectF();

    private Paint bgPaint;

    private int strokeWidth = 7;

    /**
     * 颜色是key,百分比是value　考虑到百分比可能相同,而相同颜色没有太大意义
     */
    private SparseIntArray mPipAndColor;

    private Paint piePaint;

    private Paint progressPaint;

    boolean inProgress;

    /**
     * 动画
     */
    Runnable invalidateCall = new Runnable() {

        @Override
        public void run() {
            mProgressStartAngle += 15;
            invalidate();
        }
    };

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, getResources()
                .getDisplayMetrics());
        // complexToDimensionPixelOffset(10, getResources().getDisplayMetrics());
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(context.getResources().getColor(R.color.bacColor));
        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        if (isInEditMode()) {
            forPreview();
        }
    }

    private void forPreview() {
        SparseIntArray pipAndColor = new SparseIntArray(5);
        pipAndColor.put(Color.YELLOW, 22);
        pipAndColor.put(Color.GREEN, 33);
        pipAndColor.put(Color.RED, 21);
        pipAndColor.put(Color.MAGENTA, 21);
        setData(pipAndColor);
    }

    private void startAnimation() {
        PieAnimation animation = new PieAnimation(this);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.startAnimation(animation);
    }

    public void init(int[] color) {
        mPipAndColor = new SparseIntArray(4);
        mPipAndColor.put(color[0], 0);
        mPipAndColor.put(color[1], 0);
        mPipAndColor.put(color[2], 0);
        mPipAndColor.put(color[3], 0);
    }

    /**
     * 显示加载进度
     */
    public void beginLoad() {
        mProgressStartAngle = 0;
        inProgress = true;
        invalidate();
    }

    public void setData(SparseIntArray pipAndColor, boolean anim) {
        inProgress = false;
        removeCallbacks(invalidateCall);
        setData(pipAndColor);
        if (anim) {
            startAnimation();
        }
    }

    public void setData(SparseIntArray pipAndColor) {
        this.mPipAndColor = pipAndColor;
        invalidate();
    }

    public SparseIntArray getData() {
        return this.mPipAndColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        if (mPipAndColor != null) {
            if (inProgress) {
                drawProgressPie(canvas);
            } else {
                drawPie(canvas);
            }
        }
    }

    private void drawBg(Canvas canvas) {
        // canvas.drawOval(oval, bgPaint);
        canvas.drawCircle(oval.centerX(), oval.centerY(), (oval.width() + strokeWidth) / 2, bgPaint);
    }

    private int mProgressStartAngle = 0;
    private int mProgressSweepAngle = 0;

    /**
     * 旋转的进度圈
     *
     * @param canvas
     */
    private void drawProgressPie(Canvas canvas) {
        int padding = 40 / mPipAndColor.size();
        if (mProgressStartAngle < 360) {
            mProgressSweepAngle = (1 + mProgressStartAngle) / mPipAndColor.size();
        } else {
            mProgressSweepAngle = (360) / mPipAndColor.size() - padding;
        }
        int startAngle = mProgressStartAngle;
        for (int i = 0; i < mPipAndColor.size(); i++) {
            int color = mPipAndColor.keyAt(i);
            progressPaint.setColor(color);
            canvas.drawArc(ovalPie, startAngle, mProgressSweepAngle, false, progressPaint);
            startAngle += mProgressSweepAngle + padding;
        }
        postDelayed(invalidateCall, 50);
    }

    private void drawPie(Canvas canvas) {
        int startAngle = 0;
        for (int i = 0; i < mPipAndColor.size(); i++) {
            int color = mPipAndColor.keyAt(i);
            int percent = (int) (mPipAndColor.valueAt(i) * 3.6 + 0.5);
            piePaint.setColor(color);
            canvas.drawArc(oval, startAngle, percent, true, piePaint);
            startAngle += percent;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = getMeasuredWidth();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 依据宽度定直径
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int left, right, top, bottom;
        if ((w > 0) && (h > 0) && ((w != oldw) || (h != oldh))) {
            left = getPaddingLeft() + strokeWidth;
            right = getPaddingRight() + strokeWidth;
            top = getPaddingTop() + strokeWidth;
            bottom = getPaddingBottom() + strokeWidth;
            oval.set(left, top, w - right, w - bottom);
            ovalPie.set(left + strokeWidth, top + strokeWidth, w - right - strokeWidth, w - bottom - strokeWidth);
//            Logger.dd("w:%d,h:%d", w, h); strokeWidth
        }
    }

    public class PieAnimation extends Animation {

        SparseIntArray endPipAndColor;
        SparseIntArray animPipAndColor;
        private PieChartView mPieChartView;

        public PieAnimation(PieChartView pieChartView) {
            super();
            endPipAndColor = pieChartView.getData().clone();
            animPipAndColor = pieChartView.getData().clone();
            mPieChartView = pieChartView;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1) {
                for (int i = 0; i < endPipAndColor.size(); i++) {
                    animPipAndColor.put(endPipAndColor.keyAt(i), (int) (endPipAndColor.valueAt(i) * interpolatedTime));
                }
                mPieChartView.setData(animPipAndColor);
            }
        }

    }

}
