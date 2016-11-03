package com.github.xuguang0417.scrollviewwithlong.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 *
 */
public class MonitorScrollView extends ScrollView {
	private OnScrollListener mListener;

	public MonitorScrollView(Context context) {
		this(context,null);
	}

	public MonitorScrollView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public MonitorScrollView(Context context, AttributeSet attrs,
							 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null)
			mListener.onScroll(t);
	}

	public void setOnScrollListener(OnScrollListener listener) {
		this.mListener = listener;
	}
	
	public interface OnScrollListener {
		public void onScroll(int y);
	}
}
