package cn.koolcloud.ipos.appstore.push.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.koolcloud.ipos.appstore.entity.News;


public class PushNewsDBOperator {

    private final static String TAG = "PushNewsDBOperator";
    private static PushNewsDBOperator instance = null;
    private static PushNewsDBHelper dbHelper;
    private static String PUSHNEWS_TYPE = "type";
    private static String PUSHNEWS_ID = "id";
    private static String PUSHNEWS_TITLE = "title";
    private static String PUSHNEWS_DATE = "date";
    private static String PUSHNEWS_IMAGE = "image";
    private static String PUSHNEWS_DESRIPTION = "description";
    public static PushNewsDBOperator getInstance(Context context){
    	if(instance == null)
    		instance = new PushNewsDBOperator(context);
    	return instance;
    }
    
    private PushNewsDBOperator(Context context){
    	dbHelper = new PushNewsDBHelper(context);
    }
    
    /**
     * close database
     */
    public static void closeDb() {
        dbHelper.close();
    }
    
    /**
     * 
     *insert data
     */
    public static void insertPushMessage(News info) {
        try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "insert into push_news(type, id," +
					" title, date, image, description" +
					") values (?,?,?,?,?,?)";
			Object[] bindArgs = { info.getType(), info.getId(), info.getTitle(),
			        info.getDate(), info.getImgId(), info.getDescription(),
			        };
			database.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @param info 传入的News 包含Id
     * @return
     */
    public static boolean deletePushMessge(News info){
    	boolean isSuccess = false;
    	SQLiteDatabase database = dbHelper.getWritableDatabase();
    	isSuccess = database.delete("push_news", "id=?", new String[] { info.getId()}) > 0;;
    	return isSuccess;
    }

    /**
     * 
     * @return 得到所有的推送数据
     */
    public static List<News> getAllPushMessage(){
    	List<News> list = new ArrayList<News>();
    	SQLiteDatabase database = dbHelper.getReadableDatabase();
    	String sql = "select * from push_news";
    	Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                News news = new News(cursor.getString(cursor.getColumnIndex(PUSHNEWS_TYPE)),
                		cursor.getString(cursor.getColumnIndex(PUSHNEWS_ID)),
                		cursor.getString(cursor.getColumnIndex(PUSHNEWS_TITLE)),
                		cursor.getString(cursor.getColumnIndex(PUSHNEWS_DATE)),
                		cursor.getString(cursor.getColumnIndex(PUSHNEWS_IMAGE)),
                		cursor.getString(cursor.getColumnIndex(PUSHNEWS_DESRIPTION)));
                list.add(news);
             }
         } catch (Exception e) {
             Log.e(TAG, "PushNews Error:" + e);
             e.printStackTrace();
         } finally {
             if (cursor != null) {
                 cursor.close();
             }
         }
    	return list;
    }
}
