package cx.ath.armox.brewclock;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class TeaData extends SQLiteOpenHelper {

	public static final String DB_NAME = "TeasDB";
	public static final int DB_VER = 1;
	
	public static final String TBL_NAME = "teas";
	public static final String ID = BaseColumns._ID;
	public static final String NAME = "name";
	public static final String B_TIME = "brew_time";
		
	public TeaData(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public TeaData(Context context) {
		super(context, DB_NAME, null, DB_VER);
		
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sql = "CREATE TABLE " + TBL_NAME + " (" +
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			NAME + " TEXT NOT NULL, " +
			B_TIME + " INTEGER" +
			");";

		arg0.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME + ";");
		onCreate(db);
	}

	public void addTea (String name, int brew_time) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues contval = new ContentValues();
		
		contval.put(NAME, name);
		contval.put(B_TIME, brew_time);
		
		db.insert(TBL_NAME, null, contval);
		
	}
	
	public Cursor all (Activity acti) {
		
		SQLiteDatabase db = getReadableDatabase();
		String[] fields = {ID, NAME, B_TIME};
		String order = NAME;
		Cursor crs = db.query(TBL_NAME, fields, null, null, null, null, order);

//		Deprecated:
		acti.startManagingCursor(crs);
		
		return crs;
	}
	
	public long count(){
		SQLiteDatabase db = getReadableDatabase();
		return DatabaseUtils.queryNumEntries(db, TBL_NAME);
	}
	
}
