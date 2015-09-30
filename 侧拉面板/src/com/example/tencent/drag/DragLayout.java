package com.example.tencent.drag;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧滑面板
 * 
 * @author Administrator
 * 
 */
public class DragLayout extends FrameLayout {

	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// a、 初始化操作（通过静态方法）
		/**
		 * 第一个参数：所要拖拽的孩子的父View， 第二个参数：敏感度，值越高，越容易滑动 ，第三个参数：回调函数，当触摸到子View的时候就会响应
		 */
		mDragHelper = ViewDragHelper.create(this, mCallback);

	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		/**
		 * 根据返回结果决定当前child是否可以拖拽
		 */
		// child 当前被拖拽的View
		// pointerId 区分多点触摸的id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return false;
		}
	};

	// b、传递事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 传递给mDragHelper
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			// 对多点触摸可能会有异常
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果不return true，触摸事件就不会持续传递给当前控件。当onTouchDown后，就再也收不到onTouchMove事件
		// 返回true， 持续接收事件
		return true;
	}

	/**
	 * Finalize inflating a view from XML.
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		if (getChildCount() < 2) {
			throw new IllegalStateException("布局至少有2个子View！");
		}

		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			// 非法参数异常
			throw new IllegalArgumentException("子View必须是ViewGroup的子类");
		}

		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
}
