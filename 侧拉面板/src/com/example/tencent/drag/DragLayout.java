package com.example.tencent.drag;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * �໬���
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

		// a�� ��ʼ��������ͨ����̬������
		/**
		 * ��һ����������Ҫ��ק�ĺ��ӵĸ�View�� �ڶ������������жȣ�ֵԽ�ߣ�Խ���׻��� ���������������ص�����������������View��ʱ��ͻ���Ӧ
		 */
		mDragHelper = ViewDragHelper.create(this, mCallback);

	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		/**
		 * ���ݷ��ؽ��������ǰchild�Ƿ������ק
		 */
		// child ��ǰ����ק��View
		// pointerId ���ֶ�㴥����id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return false;
		}
	};

	// b�������¼�
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// ���ݸ�mDragHelper
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			// �Զ�㴥�����ܻ����쳣
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// �����return true�������¼��Ͳ���������ݸ���ǰ�ؼ�����onTouchDown�󣬾���Ҳ�ղ���onTouchMove�¼�
		// ����true�� ���������¼�
		return true;
	}

	/**
	 * Finalize inflating a view from XML.
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		if (getChildCount() < 2) {
			throw new IllegalStateException("����������2����View��");
		}

		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			// �Ƿ������쳣
			throw new IllegalArgumentException("��View������ViewGroup������");
		}

		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
}
