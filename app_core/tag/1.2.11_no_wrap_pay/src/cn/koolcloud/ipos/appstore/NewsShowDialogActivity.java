package cn.koolcloud.ipos.appstore;

import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import cn.koolcloud.ipos.appstore.adapter.NewsListAdapter;
import cn.koolcloud.ipos.appstore.entity.News;
import cn.koolcloud.ipos.appstore.push.database.PushNewsDBOperator;
import cn.koolcloud.ipos.appstore.utils.MyLog;

public class NewsShowDialogActivity extends BaseActivity {

	private ListView newsListView;
	private List<News> newsList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_dialog_layout);
		PushNewsDBOperator.getInstance(NewsShowDialogActivity.this);
		newsList = PushNewsDBOperator.getAllPushMessage();
		MyLog.i("NewsShowDialogActivity-----size()-------" +newsList.size());
		initView();
	}
	
	private void initView(){
		newsListView = (ListView) findViewById(R.id.newsListView);
		newsListView.setAdapter(new NewsListAdapter(NewsShowDialogActivity.this, newsList));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); 
		PushNewsDBOperator.closeDb();
	}

}
