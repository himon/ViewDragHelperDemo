package com.example.tencent;

import java.util.Random;

import com.example.tencent.drag.DragLayout;
import com.example.tencent.drag.MyLinearLayout;
import com.example.tencent.drag.DragLayout.OnDragStatusChangeListener;
import com.example.tencent.utils.Cheeses;
import com.example.tencent.utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private DragLayout mDragLayout;
	private ListView mLeftList;
	private ListView mMainList;
	private ImageView mHeaderImage;
	private MyLinearLayout mll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// 查找DragLayout，设置监听
		mDragLayout = (DragLayout) findViewById(R.id.dl);
		mDragLayout.setDragStatusListener(new OnDragStatusChangeListener() {

			@Override
			public void onOpen() {
				Utils.showToast(MainActivity.this, "onOpen");

				// 随机设置一个条目
				Random random = new Random();
				int nextInt = random.nextInt(50);
				mLeftList.smoothScrollToPosition(nextInt);
			}

			@Override
			public void onDraging(float percent) {
				// 更新图标的透明度
				ViewHelper.setAlpha(mHeaderImage, 1 - percent);
			}

			@Override
			public void onClose() {
				Utils.showToast(MainActivity.this, "onClose");
				// 让图标晃动
				// mHeaderImage.setTranslationX(translationX)
				ObjectAnimator mAnim = ObjectAnimator.ofFloat(mHeaderImage,
						"translationX", 15.0f);
				mAnim.setInterpolator(new CycleInterpolator(4));
				mAnim.setDuration(900);
				mAnim.start();
			}
		});

		mHeaderImage = (ImageView) findViewById(R.id.iv_header);
		mll = (MyLinearLayout) findViewById(R.id.mll);
		mll.setDraglayout(mDragLayout);

		mLeftList = (ListView) findViewById(R.id.lv_left);
		mLeftList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				((TextView) view).setTextColor(Color.WHITE);
				return view;
			}
		});

		mMainList = (ListView) findViewById(R.id.lv_main);
		mMainList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.NAMES));

	}

}
