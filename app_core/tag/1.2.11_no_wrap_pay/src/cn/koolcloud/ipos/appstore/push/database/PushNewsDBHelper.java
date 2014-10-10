package cn.koolcloud.ipos.appstore.push.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PushNewsDBHelper extends SQLiteOpenHelper {


	private final static String DATABASE_NAME = "pushnews.db";
    private final static int DATABASE_VERSION = 1;
	
    public PushNewsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
    private void createTables(SQLiteDatabase db) {
    	  db.execSQL("create table if not exists push_news(" +
          		"_id integer PRIMARY KEY AUTOINCREMENT, " +
          		"type varchar, " +
          		"id varchar, " +
          		"title varchar, " +
          		"date varchar, "+
          		"image varchar, "+
          		"description varchar"+
                  ")");
          
   }

}
