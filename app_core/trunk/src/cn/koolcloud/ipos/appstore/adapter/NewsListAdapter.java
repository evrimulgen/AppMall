package cn.koolcloud.ipos.appstore.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.ipos.appstore.R;
import cn.koolcloud.ipos.appstore.cache.ImageDownloader;
import cn.koolcloud.ipos.appstore.entity.News;
import cn.koolcloud.ipos.appstore.push.database.PushNewsDBOperator;

public class NewsListAdapter extends BaseAdapter {

	private Context mContext;
	private List<News> mList;
	private LayoutInflater mInflater;
	HoldView holdView;
	public NewsListAdapter(Context mContext,List<News> mList){
		this.mContext = mContext;
		this.mList = mList; 
		mInflater = LayoutInflater.from(this.mContext);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
	
		if(arg1 == null){
			arg1 = mInflater.inflate(R.layout.news_listview_item_layout, null);
			holdView = new HoldView();
			holdView.titleText = (TextView) arg1.findViewById(R.id.newsListItemTitle);
			holdView.timeText = (TextView) arg1.findViewById(R.id.newsListItemTime);
			holdView.imageView = (ImageView) arg1.findViewById(R.id.newsListItemImg);
			holdView.messageText = (TextView) arg1.findViewById(R.id.newsListItemMessge);
			holdView.deleteBtn = (LinearLayout) arg1.findViewById(R.id.newsListItemDeletBtn);
			holdView.destroyBtn = (ImageButton) arg1.findViewById(R.id.newsListItemDestroyBtn);
			arg1.setTag(holdView); 
		}else{
			holdView = (HoldView) arg1.getTag();
		}
		Options options = new Options();
		options.inSampleSize = 1;
		options.outHeight = 450;
		options.outWidth = 800;
		Bitmap defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.push_default_pic,options);
		ImageDownloader.getInstance(mContext).download(mList.get(arg0).getPushFileIcon(), defaultBitmap, holdView.imageView);
		holdView.titleText.setText(mList.get(arg0).getTitle());
		holdView.timeText.setText(mList.get(arg0).getDate());
		holdView.messageText.setText(mList.get(arg0).getDescription());
		holdView.deleteBtn.setOnClickListener(new MyListener(arg0));
		holdView.destroyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});
		return arg1;
	}
	
	class HoldView{
		TextView  titleText;
		TextView timeText;
		ImageView imageView;
		TextView messageText;
		LinearLayout deleteBtn;
		ImageButton destroyBtn;
	}
	
	 class MyListener implements OnClickListener{  
        int mPosition;  
        public MyListener(int inPosition){  
            mPosition= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
        	 int vid= v. getId ( ) ; 
             if ( vid == holdView.deleteBtn.getId()) {
                 PushNewsDBOperator.deletePushMessge(mList.get(mPosition));
                 removeItem (mPosition) ; 
             }
         } 
     }  
     
	 public void removeItem ( int position ) { 
	        mList.remove (position) ; 
	        if(mList.size() == 0){
	        	Activity activity = (Activity) mContext;
	        	activity.finish();
	        }else
	        	this.notifyDataSetChanged() ; 
	    } 

}
