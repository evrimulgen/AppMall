package cn.koolcloud.ipos.appstore;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.koolcloud.ipos.appstore.cache.ImageDownloader;
import cn.koolcloud.ipos.appstore.entity.News;
import cn.koolcloud.ipos.appstore.push.database.PushNewsDBOperator;
import cn.koolcloud.ipos.appstore.receiver.MyPushMessageReceiver;
import cn.koolcloud.ipos.appstore.utils.MyLog;

public class NewsPushDialogActivity extends BaseActivity implements OnClickListener{

	private TextView newsPushTitle,newsPushTime,newsPushMessge;
	private ImageButton newsPushDeletBtn;
	private ImageView newsPushImg;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_push_dialog_layout);
		PushNewsDBOperator.getInstance(NewsPushDialogActivity.this);
		MyLog.i("NewsPushDialogActivity------onCreate--------" +"===");
		initView();
	}
 
	private void initView(){
		newsPushTitle = (TextView) findViewById(R.id.newsPushTitle);
		newsPushTime = (TextView) findViewById(R.id.newsPushTime);
		newsPushMessge = (TextView) findViewById(R.id.newsPushMessge);
		newsPushImg = (ImageView) findViewById(R.id.newsPushImg);
		newsPushDeletBtn = (ImageButton) findViewById(R.id.newsPushDeletBtn);
		newsPushDeletBtn.setOnClickListener(this);
		
	}
	
	private void initViewData(News news){
		newsPushTitle.setText(news.getTitle());
		newsPushTime.setText(news.getDate());
		
		Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.push_default_pic);
		ImageDownloader.getInstance(NewsPushDialogActivity.this).download(news.getPushFileIcon(), defaultBitmap,newsPushImg);
		newsPushMessge.setText(news.getDescription());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Bundle mBundle = null;
		mBundle = getIntent().getExtras();
		if(mBundle != null){
			try {
				//String customContentString = (String) mBundle.getString(Constants.SER_KEY);
				News news = new News();
				JSONObject customJson = new JSONObject(MyPushMessageReceiver.pushMessage);
				news.setType(customJson.getString("type"));
				news.setId(customJson.getString("id"));
				news.setTitle(customJson.getString("title"));
				news.setDate(customJson.getString("date"));
				news.setImgId(customJson.getString("image"));
				news.setDescription(customJson.getString("description"));
				initViewData(news);
				PushNewsDBOperator.insertPushMessage(news);
				MyLog.i("NewsPushDialogActivity------onResume--------" +news.getDescription() +"==" +news.getImgId() +"== "+news.getTitle());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		PushNewsDBOperator.closeDb();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newsPushDeletBtn:
			finish();
			break;
		default:
			break;
		}
	}

	
	
}
