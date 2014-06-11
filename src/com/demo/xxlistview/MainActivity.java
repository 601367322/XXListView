package com.demo.xxlistview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.demo.xxlistview.XXListView.IXListViewListener;

public class MainActivity extends Activity implements IXListViewListener {

	private XXListView listview;
	private ArrayAdapter<String> adapter;
	private List<String> source;
	private int allPage = 2;// 总页数
	private int nowPage = 0;// 现在的页数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listview = (XXListView) findViewById(R.id.listview);

		source = getData();

		listview.setAdapter(adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, source));

		listview.setXListViewListener(this);
	}

	@Override
	public void onRefresh() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					// 刷新从网络获取数据，并且重置原来的数据
					List<String> list = getData();

					/** 不能用 source = list 必须保持数据源的引用不变 **/
					source.clear();
					source.addAll(list);

					nowPage = 0;

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ui();
				}
			}
		}).start();
	}

	public void ui() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				if (nowPage > allPage) {//没有下一页就禁止加载更多按钮 有的话 就开启
					listview.setPullLoadEnable(false);
				} else {
					listview.setPullLoadEnable(true);
				}
				listview.stop();
			}
		});
	}

	@Override
	public void onLoadMore() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					// 刷新从网络获取数据，并且重置原来的数据
					List<String> list = getData();
					source.addAll(list);
					nowPage++;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ui();
						}
					});

				}
			}
		}).start();
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		data.add("操操操");
		for (int i = 0; i < 15; i++) {
			data.add(String.valueOf((int) (Math.random() * 100000000)));
		}
		return data;
	}

}
