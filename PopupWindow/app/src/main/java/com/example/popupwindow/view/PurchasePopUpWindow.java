package com.example.popupwindow.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.popupwindow.R;

public class PurchasePopUpWindow extends PopupWindow {
	TextView tv_change_items;
	Button btn_submit;
	View detail_customization;
	TextView tv_customization_tag;
	LinearLayout ll_above_detail;
	MyGridView grid_selected;
	CustomViewPager vp_details;

	public PurchasePopUpWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		detail_customization = inflater.inflate(R.layout.purchase_popup_window,
				null);
		tv_change_items = (TextView) detail_customization
				.findViewById(R.id.tv_change_items);
		ll_above_detail = (LinearLayout) detail_customization
				.findViewById(R.id.ll_above_detail);

		btn_submit = (Button) detail_customization
				.findViewById(R.id.btn_submit);

		grid_selected = (MyGridView) detail_customization
				.findViewById(R.id.grid_selected);
		vp_details = (CustomViewPager) detail_customization
				.findViewById(R.id.vp_details);

		tv_customization_tag = (TextView) detail_customization
				.findViewById(R.id.tv_customization_tag);

		// 设置SelectPicPopupWindow的View
		this.setContentView(detail_customization);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点�?
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背�?
		this.setBackgroundDrawable(dw);

		tv_change_items.setOnClickListener(itemsOnClick);
		ll_above_detail.setOnClickListener(itemsOnClick);
		btn_submit.setOnClickListener(itemsOnClick);

	}

	/*
	 * 设置TextView的显示属性，
	 */
	public void setTextViewVisibility(int visibility) {
		tv_customization_tag = (TextView) detail_customization
				.findViewById(R.id.tv_customization_tag);

		tv_customization_tag.setVisibility(visibility);

	}

	public View getCustomView() {
		return detail_customization;
	}

}
