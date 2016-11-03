package com.github.xuguang0417.scrollviewwithlong.scrollview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.xuguang0417.scrollviewwithlong.R;
import com.github.xuguang0417.scrollviewwithlong.scrollview.MonitorScrollView.OnScrollListener;

/**
 * description : 滚动控件头部悬浮的ScrollView
 */
public class TopFloatScrollView extends FrameLayout implements OnScrollListener {

    // 新增的放置悬浮的容器
    private LinearLayout mAddFloatContainer;
    private Context mContext;
    // 滚动的内容View
    public MonitorScrollView mContainerSv;
    // ScrollView 的根内容
    private ViewGroup mRootView;
    // 悬浮的View
    private View mFloatView;
    // 悬浮View 在父View中的位置
    private int mFloatViewPotion;
    // 悬浮View距父View头部的距离
    private int mFloatToTopHeight;
    // 一直在头部的容器
    private RelativeLayout mFloatTopContainer;

    public TopFloatScrollView(Context context) {
        this(context, null);
    }

    public TopFloatScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopFloatScrollView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mAddFloatContainer = new LinearLayout(mContext);

        // 添加滚动的ScrollView
        mContainerSv = new MonitorScrollView(mContext);
        mContainerSv.setOnScrollListener(this);
        FrameLayout.LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainerSv.setLayoutParams(params);
        super.addView(mContainerSv);

        // 添加头部容器
        mFloatTopContainer = new RelativeLayout(mContext);
        super.addView(mFloatTopContainer);
        mFloatTopContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 3) {
            // 只容许在xml中配置一个ChildView
            throw new IllegalStateException(
                    "TopFloatScrollView can host only one direct child");
        }
        initInnerView();
    }

    /**
     * 初始化内部的View
     */
    private void initInnerView() {
        // 判断是不是只有一个ChildView
        int childCount = this.getChildCount();

        if (childCount == 2) {
            // 刚开始可能没有在layout中设置子View，需要在代码中动态添加
            return;
        }

        // 获取ScrollView的内容
        mRootView = (ViewGroup) this.getChildAt(2);

        if (mRootView == null) {
            return;
        }

        // 先从this里面移除
        this.removeView(mRootView);
        // 添加到ScrollView里面
        mContainerSv.addView(mRootView);

        // 获取悬浮的View
        mFloatView = mRootView.findViewById(R.id.line_two);

        if (mFloatView == null) {
            throw new RuntimeException(
                    "Can't find the float View for id is top_float");
        }

        // 找到FloatView在这个RootView中的位置
        findFloatViewPosition();
        // 把悬浮View放到新增容器中然后把新增容器放到主布局中
        initAddFloatContainerView();
    }

    /**
     * 把悬浮View放到新增容器中然后把新增容器放到主布局中
     */
    private void initAddFloatContainerView() {
        if (mFloatView != null) {
            measureView(mFloatView);
            mRootView.removeView(mFloatView);
            mAddFloatContainer.addView(mFloatView);
            mRootView.addView(mAddFloatContainer, mFloatViewPotion);

            // 设置临时高度
            mAddFloatContainer.getLayoutParams().height = mFloatView
                    .getMeasuredHeight();
        }
    }

    public void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(-1, -2);
        }

        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int tempHeight = p.height;
        int height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(width, height);
    }

    /**
     * 找到FloatView在这个RootView中的位置
     */
    private void findFloatViewPosition() {
        int count = mRootView.getChildCount();
        for (int position = 0; position < count; position++) {
            View childView = mRootView.getChildAt(position);
            if (childView == mFloatView) {
                mFloatViewPotion = position;
                break;
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mAddFloatContainer != null && mFloatView != null) {
            // 获取需要悬浮的View到这个顶部的高度
            mFloatToTopHeight = mAddFloatContainer.getTop();

            mFloatTopContainer.getLayoutParams().height = mFloatView
                    .getMeasuredHeight();

            // 之所以要设置背景是因为快速向上滑动的时候会出现闪现的情况
            mFloatTopContainer.setBackground(new BitmapDrawable(getBitmapFromView(mFloatView)));
        }
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onScroll(int y) {
        if (mFloatView != null) {
            // TODO: 2016/9/23  自己加的方法
            if (onTopScrollListener != null) {
                onTopScrollListener.onScrollY(y);
            }

            if (y > mFloatToTopHeight) {
                addFloatToContainer();
            } else {
                addFloatToRootView();
            }
        }
    }

    /**
     * 添加到原来的RooView中
     */
    private void addFloatToRootView() {
        ViewParent currentParent = mFloatView.getParent();
        if (currentParent != mAddFloatContainer) {
            // 添加到原来的RooView中 我们之前有记录位置
            mFloatTopContainer.removeView(mFloatView);
            mAddFloatContainer.addView(mFloatView);
            mFloatTopContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 添加悬浮到这个准备好的容器中
     */
    private void addFloatToContainer() {
        ViewParent currentParent = mFloatView.getParent();
        if (currentParent != mFloatTopContainer) {
            // 添加到容器中
            mFloatTopContainer.setVisibility(View.VISIBLE);
            mAddFloatContainer.removeAllViews();
            mFloatTopContainer.addView(mFloatView);
        }
    }

    /************************
     * 重载所有的addView()方法
     ************************/

    @Override
    public void addView(View child) {
        judgeChildCount();
        super.addView(child);
        initInnerView();
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        judgeChildCount();
        super.addView(child, params);
        initInnerView();
    }

    @Override
    public void addView(View child, int index) {
        judgeChildCount();
        super.addView(child, index);
        initInnerView();
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        judgeChildCount();
        super.addView(child, index, params);
        initInnerView();
    }

    @Override
    public void addView(View child, int width, int height) {
        judgeChildCount();
        super.addView(child, width, height);
        initInnerView();
    }

    /**
     * 判断子View的个数
     */
    private void judgeChildCount() {
        if (getChildCount() > 3 || mContainerSv.getChildCount() == 1) {
            // 只容许在xml中配置一个ChildView
            throw new IllegalStateException(
                    "TopFloatScrollView can host only one direct child");
        }
    }

    //滑动跳转
    public void onScroll(int scrollX, int scrollY) {
        mContainerSv.scrollTo(scrollX, scrollY);
    }

    private OnTopScrollListener onTopScrollListener;

    /**
     * 设置滚动接口
     *
     * @param onScrollListener
     */
    public void setOnTopScrollListener(OnTopScrollListener onScrollListener) {
        this.onTopScrollListener = onScrollListener;
    }


    /**
     * 滚动的回调接口
     */
    public interface OnTopScrollListener {
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         */
        public void onScrollY(int scrollY);
    }
}
