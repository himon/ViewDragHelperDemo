package com.example.tencent.drag;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
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

	protected static final String TAG = "TAG";
	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private OnDragStatusChangeListener mListener;
	private Status mStatus = Status.Close;

	/**
	 * ״̬ö��
	 * 
	 * @author Administrator
	 * 
	 */
	public static enum Status {
		Close, Open, Draging;
	}

	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public interface OnDragStatusChangeListener {

		void onClose();

		void onOpen();

		void onDraging(float percent);
	}

	public void setDragStatusListener(OnDragStatusChangeListener listener) {
		this.mListener = listener;

	}

	/**
	 * ��Ļ�Ŀ��
	 */
	private int mWidth;
	/**
	 * ��Ļ�ĸ߶�
	 */
	private int mHeight;
	/**
	 * ������ק�ķ�Χ
	 */
	private int mRange;

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

		// c����дcallback�¼�
		/**
		 * 1�����ݷ��ؽ��������ǰchild�Ƿ������ק(������ȥ����view��ʱ�����)
		 */
		// child ��ǰ����ק��View
		// pointerId ���ֶ�㴥����id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			Log.d(TAG, "tryCaptureView��" + child);
			return true;
		}

		/**
		 * ��view�������ʱ����ã������񣺾��ǵ�ǰ������view�ܷ���ק���ͽв���
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			Log.d(TAG, "onViewCaptured��" + capturedChild);
			super.onViewCaptured(capturedChild, activePointerId);
		}

		/**
		 * ��ȡview������ק�ķ�Χ
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			// ������ק�ķ�Χ��������ק�������������ƣ����������˶���ִ�е��ٶ�
			return mRange;
		}

		/**
		 * 2�����ݽ���ֵ������Ҫ�ƶ����ģ�����λ��
		 */
		// ��ʱû�з����������ƶ�
		// child����ǰ��ק��view
		// left���µ�λ�õĽ���ֵ
		// dx���仯�������ոյ�λ�õĲ�ֵ
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			Log.d(TAG,
					"clampViewPositionHorizontal��" + "oldleft:"
							+ child.getLeft() + " dx��" + dx + " left��" + left);
			if (child == mMainContent) {

				left = fixLeft(left);
			}
			return left;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return super.clampViewPositionVertical(child, top, dy);
		};

		/**
		 * 3����Viewλ�øı��ʱ�򣬴���Ҫ�������飨����״̬���ػ���棬���涯����
		 */
		// ��ʱ��View�Ѿ�������λ�õĸı�
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			Log.d(TAG, "onViewPositionChanged�� " + " left��" + left + " dx��"
					+ dx);

			int newLeft = left;

			if (changedView == mLeftContent) {
				// ��������ƶ�֮����ǿ�ƷŻ�ȥ��
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);

				newLeft = mMainContent.getLeft() + dx;
				newLeft = fixLeft(newLeft);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}

			// ����״̬��ִ�ж���
			dispatchDragEvent(newLeft);

			// Ϊ�˼��ݵͰ汾��ÿ���޸�ֵ�󣬽����ػ棨�Ͱ汾��View��offsetLeftAndRight����û���ػ棩
			invalidate();
		}

		/**
		 * 4����View���ͷŵ�ʱ�򣬴�������飨ִ�ж�����
		 */
		// releasedChild ���ͷŵ�View
		// xvel ˮƽ������ٶȣ�����Ϊ+
		// yvel ��ֱ������ٶȣ�����Ϊ+
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {

			Log.d(TAG, "onViewReleased��" + " xvel��" + xvel + " yvel��" + yvel);
			super.onViewReleased(releasedChild, xvel, yvel);

			// �жϹر�/�����
			if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
				open();
			} else if (xvel > 0) {
				open();
			} else {
				close();
			}

		}

		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}

	};

	private void dispatchDragEvent(int newLeft) {
		float percent = newLeft * 1.0f / mRange;

		if (mListener != null) {
			mListener.onDraging(percent);
		}

		// ����״̬��ִ�лص�
		Status lastStatus = mStatus;
		mStatus = updateStatus(percent);
		if (mStatus != lastStatus) {
			if (mStatus == Status.Close) {
				if (mListener != null) {
					mListener.onClose();
				}
			} else if (mStatus == Status.Open) {
				if (mListener != null) {
					mListener.onOpen();
				}
			}
		}

		// ���涯��
		animViews(percent);
	}

	private Status updateStatus(float percent) {
		if (percent == 0) {
			return Status.Close;
		} else if (percent == 1) {
			return Status.Open;
		}
		return Status.Draging;
	}

	/**
	 * ���涯��
	 * 
	 * @param percent
	 */
	private void animViews(float percent) {
		// 1������壺���Ŷ�����ƽ�ƶ�����͸���ȶ���
		// ���Ŷ���
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f * percent + 0.5f);
		// ƽ�ƶ���
		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0f, 0));
		// ͸����
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));

		// 2������壺���Ŷ���
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));

		// 3���������������ȱ仯����ɫ�仯��
		getBackground()
				.setColorFilter(
						(Integer) evaluateColor(percent, Color.BLACK,
								Color.TRANSPARENT), Mode.SRC_OVER);
	}

	/**
	 * ��ֵ��
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	/**
	 * ��ɫ�仯����
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Object evaluateColor(float fraction, Object startValue,
			Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		// 2������ƽ����������Ƶ�ʵ��ã�
		// computeScroll����������Ϊtrue
		if (mDragHelper.continueSettling(true)) {
			// �������true����������Ҫ����ִ��
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public void close() {
		close(true);
	}

	public void open() {
		open(true);
	}

	/**
	 * ��
	 */
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			// 1������һ��ƽ������
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// �������true��˵��û���ƶ���ָ����λ�ã���Ҫˢ��ҳ��
				// ר��Ϊ����׼����Invalidate��������this��child���ڵ�ViewGroup��
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	/**
	 * �ر�
	 */
	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 1������һ��ƽ������
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// �������true��˵��û���ƶ���ָ����λ�ã���Ҫˢ��ҳ��
				// ר��Ϊ����׼����Invalidate��������this        ��child���ڵ�ViewGroup��
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}

	}

	/**
	 * ���ݷ�Χ�������ֵ
	 * 
	 * @param left
	 * @return
	 */
	private int fixLeft(int left) {
		if (left < 0) {
			return 0;
		} else if (left > mRange) {
			return mRange;
		}
		return left;
	}

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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// �����ڲ�������ȡ��Ļ���
		getMeasuredWidth();
	}

	/**
	 * ���ߴ��б仯��ʱ�����
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();

		mRange = (int) (mWidth * 0.6f);
	}
}
