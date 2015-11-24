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
 * 侧滑面板
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
	 * 状态枚举
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
	 * 屏幕的宽度
	 */
	private int mWidth;
	/**
	 * 屏幕的高度
	 */
	private int mHeight;
	/**
	 * 横向拖拽的范围
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

		// a、 初始化操作（通过静态方法）
		/**
		 * 第一个参数：所要拖拽的孩子的父View， 第二个参数：敏感度，值越高，越容易滑动 ，第三个参数：回调函数，当触摸到子View的时候就会响应
		 */
		mDragHelper = ViewDragHelper.create(this, mCallback);

	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		// c、重写callback事件
		/**
		 * 1、根据返回结果决定当前child是否可以拖拽(当尝试去捕获view的时候调用)
		 */
		// child 当前被拖拽的View
		// pointerId 区分多点触摸的id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			Log.d(TAG, "tryCaptureView：" + child);
			return true;
		}

		/**
		 * 当view被捕获的时候调用（被捕获：就是当前触摸的view能否被拖拽动就叫捕获）
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			Log.d(TAG, "onViewCaptured：" + capturedChild);
			super.onViewCaptured(capturedChild, activePointerId);
		}

		/**
		 * 获取view横向拖拽的范围
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			// 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定了动画执行的速度
			return mRange;
		}

		/**
		 * 2、根据建议值修正将要移动到的（横向）位置
		 */
		// 此时没有发生真正的移动
		// child：当前拖拽的view
		// left：新的位置的建议值
		// dx：变化量，跟刚刚的位置的差值
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			Log.d(TAG,
					"clampViewPositionHorizontal：" + "oldleft:"
							+ child.getLeft() + " dx：" + dx + " left：" + left);
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
		 * 3、当View位置改变的时候，处理要做的事情（更新状态，重绘界面，伴随动画）
		 */
		// 此时，View已经发生了位置的改变
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			Log.d(TAG, "onViewPositionChanged： " + " left：" + left + " dx："
					+ dx);

			int newLeft = left;

			if (changedView == mLeftContent) {
				// 当左面板移动之后，再强制放回去。
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);

				newLeft = mMainContent.getLeft() + dx;
				newLeft = fixLeft(newLeft);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}

			// 更新状态，执行动画
			dispatchDragEvent(newLeft);

			// 为了兼容低版本，每次修改值后，进行重绘（低版本的View的offsetLeftAndRight方法没有重绘）
			invalidate();
		}

		/**
		 * 4、当View被释放的时候，处理的事情（执行动画）
		 */
		// releasedChild 被释放的View
		// xvel 水平方向的速度，向右为+
		// yvel 垂直方向的速度，向下为+
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {

			Log.d(TAG, "onViewReleased：" + " xvel：" + xvel + " yvel：" + yvel);
			super.onViewReleased(releasedChild, xvel, yvel);

			// 判断关闭/打开面板
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

		// 更新状态，执行回调
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

		// 伴随动画
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
	 * 伴随动画
	 * 
	 * @param percent
	 */
	private void animViews(float percent) {
		// 1、左面板：缩放动画，平移动画，透明度动画
		// 缩放动画
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f * percent + 0.5f);
		// 平移动画
		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0f, 0));
		// 透明度
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));

		// 2、主面板：缩放动画
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));

		// 3、背景动画：亮度变化（颜色变化）
		getBackground()
				.setColorFilter(
						(Integer) evaluateColor(percent, Color.BLACK,
								Color.TRANSPARENT), Mode.SRC_OVER);
	}

	/**
	 * 估值器
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
	 * 颜色变化过度
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

		// 2、持续平滑动画（高频率调用）
		// computeScroll方法下设置为true
		if (mDragHelper.continueSettling(true)) {
			// 如果返回true，动画还需要继续执行
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
	 * 打开
	 */
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			// 1、触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 如果返回true就说明没有移动到指定的位置，需要刷新页面
				// 专门为动画准备的Invalidate，参数传this（child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	/**
	 * 关闭
	 */
	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 1、触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 如果返回true就说明没有移动到指定的位置，需要刷新页面
				// 专门为动画准备的Invalidate，参数传this        （child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}

	}

	/**
	 * 根据范围修正左边值
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 可以在测量完后获取屏幕宽高
		getMeasuredWidth();
	}

	/**
	 * 当尺寸有变化的时候调用
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();

		mRange = (int) (mWidth * 0.6f);
	}
}
