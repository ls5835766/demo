package com.example.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popupwindow.adapter.BaseListAdapter;
import com.example.popupwindow.view.CustomViewPager;
import com.example.popupwindow.view.MyGridView;
import com.example.popupwindow.view.PurchasePopUpWindow;

public class MainWindowActivity extends Activity {
	LayoutInflater inflater;
	private TextView tv_intention_type, tv_intention_des;
	private Button btn_one, btn_two, btn_three, btn_submit;
	private String type = "";
	private PurchasePopUpWindow mPopUpWindow;
	private TextView tv_customization_tag, tv_change_items;
	private ViewPager vp_details;
	private LinearLayout ll_above_detail;
	private MyGridView grid_selected;
	private ParentIconAdapter parentIconAdapter = new ParentIconAdapter();
	private ChildrenIconAdapter adapter_one, adapter_two, adapter_three;
	private List<String> parentList = new ArrayList<String>();
	final int length = 3;
	View[] mView = new View[length];
	private MyGridView[] mGridView = new MyGridView[length];
	private MyPagerAdapter mMyadapter;
	private List<View> listViews;
	private ArrayList<String> data_one, data_two, data_three;
	private int mSumOne = 1;
	private int mSumTwo = 1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 删除
				adapter_one.notifyDataSetChanged();
				adapter_two.notifyDataSetChanged();
				adapter_three.notifyDataSetChanged();
				parentIconAdapter.notifyDataSetChanged();
				break;
			case 2:// 添加
				parentIconAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_window);
		inflater = LayoutInflater.from(getApplicationContext());
		InitViews();
		RegisterListener();
	}

	public void InitViews() {
		tv_intention_type = (TextView) findViewById(R.id.tv_intention_type);
		tv_intention_des = (TextView) findViewById(R.id.tv_intention_des);
		btn_one = (Button) findViewById(R.id.btn_one);
		btn_two = (Button) findViewById(R.id.btn_two);
		btn_three = (Button) findViewById(R.id.btn_three);
		listViews = new ArrayList<View>();
		listViews.add(inflater.inflate(R.layout.purchase_gridview, null));
		listViews.add(inflater.inflate(R.layout.purchase_gridview, null));
		listViews.add(inflater.inflate(R.layout.purchase_gridview, null));
	}

	public void RegisterListener() {
		btn_one.setOnClickListener(listener);
		btn_two.setOnClickListener(listener);
		btn_three.setOnClickListener(listener);
	}

	OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.ll_above_detail:
				mPopUpWindow.dismiss();
				break;
			case R.id.btn_submit:
				if (parentList.size() == 0) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"请至少选择一项item", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				String mString = "";
				for (int i = 0; i < parentList.size(); i++) {
					mString += parentList.get(i).toString().trim() + " ";
				}
				tv_intention_des.setText(mString);
				mPopUpWindow.dismiss();
				break;
			case R.id.btn_one:
				type = "One";
				tv_intention_type.setText("订阅项目One");
				parentList.clear();
				PopUpWindows();
				tv_customization_tag.setText("订阅项目One");
				break;
			case R.id.btn_two:
				type = "Two";
				parentList.clear();
				tv_intention_type.setText("订阅项目Two");
				PopUpWindows();
				tv_customization_tag.setText("订阅项目Two");
				break;
			case R.id.btn_three:
				break;
			case R.id.tv_change_items:
				if (type.equals("One")) {
					if (getDataOne().size() == 0) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"无资讯标签数据", Toast.LENGTH_LONG);
						toast.show();
						return;
					} else {
						if (getDataOne().size() <= 12) {
							vp_details.setCurrentItem(0);
							tv_change_items.setVisibility(View.GONE);
						} else if (getDataOne().size() > 12
								&& getDataOne().size() <= 24) {
							if (mSumOne < 2) {
								vp_details.setCurrentItem(mSumOne);
								mSumOne++;
							} else {
								mSumOne = 0;
								vp_details.setCurrentItem(0);
							}
						} else {
							if (mSumOne < 3) {
								vp_details.setCurrentItem(mSumOne);
								mSumOne++;
							} else {
								mSumOne = 0;
								vp_details.setCurrentItem(0);
							}
						}
					}
				}
				if (type.equals("Two")) {
					if (getDataTwo().size() == 0) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"无资讯标签数据", Toast.LENGTH_LONG);
						toast.show();
						return;
					} else {
						if (getDataTwo().size() <= 12) {
							vp_details.setCurrentItem(0);
							tv_change_items.setVisibility(View.GONE);
						} else if (getDataTwo().size() > 12
								&& getDataTwo().size() <= 24) {
							if (mSumTwo < 2) {
								vp_details.setCurrentItem(mSumTwo);
								mSumTwo++;
							} else {
								mSumTwo = 0;
								vp_details.setCurrentItem(0);
							}
						} else {
							if (mSumTwo < 3) {
								vp_details.setCurrentItem(mSumTwo);
								mSumTwo++;
							} else {
								mSumTwo = 0;
								vp_details.setCurrentItem(0);
							}
						}
					}
				}
				break;
			}

		}
	};

	OnClickListener onClicker = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	public void PopUpWindows() {

		mPopUpWindow = new PurchasePopUpWindow(MainWindowActivity.this,
				onClicker);
		mPopUpWindow.showAtLocation(
				MainWindowActivity.this.findViewById(R.id.ll_main_window),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		View view = mPopUpWindow.getContentView();
		tv_customization_tag = (TextView) view
				.findViewById(R.id.tv_customization_tag);

		ll_above_detail = (LinearLayout) view
				.findViewById(R.id.ll_above_detail);
		tv_change_items = (TextView) view.findViewById(R.id.tv_change_items);
		grid_selected = (MyGridView) view.findViewById(R.id.grid_selected);
		btn_submit = (Button) view.findViewById(R.id.btn_submit);
		vp_details = (CustomViewPager) view.findViewById(R.id.vp_details);

		mMyadapter = new MyPagerAdapter(listViews);
		vp_details.setAdapter(mMyadapter);
		vp_details.setCurrentItem(0);
		data_one = new ArrayList<String>();
		data_two = new ArrayList<String>();
		data_three = new ArrayList<String>();
		adapter_one = new ChildrenIconAdapter(MainWindowActivity.this, data_one);
		adapter_two = new ChildrenIconAdapter(MainWindowActivity.this, data_two);
		adapter_three = new ChildrenIconAdapter(MainWindowActivity.this,
				data_three);
		for (int i = 0; i < 3; i++) {
			mGridView[i] = (MyGridView) listViews.get(i).findViewById(
					R.id.gridview);
		}

		tv_change_items.setOnClickListener(listener);
		ll_above_detail.setOnClickListener(listener);
		btn_submit.setOnClickListener(listener);
		// mAdapterInit();
		if (type.equals("One")) {
			if (getDataOne().size() <= 12) {
				tv_change_items.setVisibility(View.GONE);
				for (int i = 0; i < getDataOne().size(); i++) {
					data_one.add(getDataOne().get(i).toString().trim());
				}
			} else if (getDataOne().size() > 12 && getDataOne().size() <= 24) {
				for (int i = 0; i < 12; i++) {
					data_one.add(getDataOne().get(i).toString().trim());
				}
				for (int i = 12; i < getDataOne().size(); i++) {
					data_two.add(getDataOne().get(i).toString().trim());
				}
			} else if (getDataOne().size() > 24) {
				for (int i = 0; i < 12; i++) {
					data_one.add(getDataOne().get(i).toString().trim());
				}
				for (int i = 12; i < 24; i++) {
					data_two.add(getDataOne().get(i).toString().trim());
				}
				for (int i = 24; i < 36 && i < getDataOne().size(); i++) {
					data_three.add(getDataOne().get(i).toString().trim());
				}
			}

			for (int i = 0; i < length; i++) {
				if (i == 0) {
					mGridView[i].setAdapter(adapter_one);
				}
				if (i == 1) {
					mGridView[i].setAdapter(adapter_two);
				}
				if (i == 2) {
					mGridView[i].setAdapter(adapter_three);
				}
			}
			grid_selected.setAdapter(parentIconAdapter);
		}

		if (type.equals("Two")) {
			tv_change_items.setVisibility(View.GONE);
			if (getDataTwo().size() <= 12) {
				for (int i = 0; i < getDataTwo().size(); i++) {
					data_one.add(getDataTwo().get(i).toString().trim());
				}
			} else if (getDataTwo().size() > 12 && getDataTwo().size() <= 24) {
				for (int i = 0; i < 12; i++) {
					data_one.add(getDataTwo().get(i).toString().trim());
				}
				for (int i = 12; i < getDataTwo().size(); i++) {
					data_two.add(getDataTwo().get(i).toString().trim());
				}
			} else if (getDataTwo().size() > 24) {
				for (int i = 0; i < 12; i++) {
					data_one.add(getDataTwo().get(i).toString().trim());
				}
				for (int i = 12; i < 24; i++) {
					data_two.add(getDataTwo().get(i).toString().trim());
				}
				for (int i = 24; i < 36 && i < getDataTwo().size(); i++) {
					data_three.add(getDataTwo().get(i).toString().trim());
				}
			}

			for (int i = 0; i < length; i++) {
				if (i == 0) {
					mGridView[i].setAdapter(adapter_one);
				}
				if (i == 1) {
					mGridView[i].setAdapter(adapter_two);
				}
				if (i == 2) {
					mGridView[i].setAdapter(adapter_three);
				}
			}
			grid_selected.setAdapter(parentIconAdapter);
		}

	}

	private ArrayList<String> getDataOne() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			result.add("One item " + i);
		}
		return result;
	}

	private ArrayList<String> getDataTwo() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			result.add("Two item " + i);
		}
		return result;
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = mListViews.get(position);
			ViewGroup parent = (ViewGroup) v.getParent();
			;
			// ((ViewPager) collection).addView(v, 0);
			if (parent != null) {
				parent.removeAllViews();
			}
			container.addView(v);
			return v;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

	}

	/**
	 * childgridview图片适配
	 */
	private class ChildrenIconAdapter extends BaseListAdapter<String> {
		private List<String> mData;

		public ChildrenIconAdapter(Context context, List<String> mData) {
			super(context, mData);
			// TODO Auto-generated constructor stub
			this.mData = mData;
		}

		@Override
		public String getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		private class ViewHolder {
			ImageView iv_delete;
			TextView tv_iconname;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.home_custom_grid_item, null);
				holder.tv_iconname = (TextView) convertView
						.findViewById(R.id.tv_intent_iconname);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iv_delete.setVisibility(View.GONE);
			if (parentList.contains(mData.get(position))) {
				holder.tv_iconname.setEnabled(false);

			} else {
				holder.tv_iconname.setEnabled(true);

			}
			holder.tv_iconname.setText(mData.get(position));

			holder.tv_iconname.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (parentList.size() >= 3) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"最多可选择3个特色标签", Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					v.setEnabled(false);
					parentList.add(mData.get(position));
					mHandler.sendEmptyMessage(2);
				}
			});
			return convertView;
		}

		@Override
		protected View getItemView(View convertView, int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public void update(List<String> values) {
			mData = values;
			notifyDataSetInvalidated();
			notifyDataSetChanged();
		}
	}

	/**
	 * parentgridview图片适配
	 */
	private class ParentIconAdapter extends BaseAdapter {
		@Override
		public String getItem(int position) {
			return parentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return parentList.size();
		}

		private class ViewHolder {
			ImageView iv_delete;
			TextView tv_iconname;
			RelativeLayout rl_item;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.home_custom_grid_item, null);
				holder.tv_iconname = (TextView) convertView
						.findViewById(R.id.tv_intent_iconname);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete_item);
				holder.rl_item = (RelativeLayout) convertView
						.findViewById(R.id.rl_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_iconname.setBackgroundResource(R.drawable.zhun_n);
			holder.iv_delete.setVisibility(View.VISIBLE);
			holder.tv_iconname.setTextColor(getResources().getColor(
					R.color.black));

			holder.tv_iconname.setText(parentList.get(position));
			holder.rl_item.setClickable(true);
			holder.rl_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					parentList.remove(position);
					mHandler.sendEmptyMessage(1);
				}
			});
			return convertView;
		}
	}

}
