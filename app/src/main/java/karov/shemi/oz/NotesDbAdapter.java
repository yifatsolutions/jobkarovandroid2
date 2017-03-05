package karov.shemi.oz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_TITLE2 = "title2";
    public static final String KEY_TITLE3 = "title3";
    public static final String KEY_PRIMARY = "_primary";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ADRESS = "adress";
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "xy";
    private static final String DATABASE_AGENTS_TABLE = "agents";
    private static final int DATABASE_VERSION = 4;

    
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table "+DATABASE_TABLE+" ("+KEY_PRIMARY+" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, "+KEY_ROWID+" integer not null, "
        + "title text not null, x double not null, y double not null, name text not null, type integer not null, adress text not null, "+Constants.PHOTO+" text not null"+");";//
    private static final String DATABASE_AGENTS_CREATE =
            "create table "+DATABASE_AGENTS_TABLE+" (_id integer not null, "
            + KEY_X+" double not null, "+KEY_Y+" double not null, "+Constants.ROLE+" text not null, "+Constants.SIZE+" text not null, "+Constants.SPECIALITY+" integer not null, "+Constants.CITIES+" integer not null, "+Constants.AREA+" integer not null, "+KEY_ADRESS+" text not null, "+KEY_TITLE+" text not null, "+KEY_TITLE2+" text not null, "+KEY_TITLE3+" text not null);";//

 
    
   
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_AGENTS_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_AGENTS_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createNote(int id, String name,String comp,double x,double y,int type, String address,String companyid) {//
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_TITLE, name);
        initialValues.put(KEY_X, x);
        initialValues.put(KEY_Y, y);
        initialValues.put(KEY_NAME, comp);//
        initialValues.put(KEY_TYPE,type);
        initialValues.put(KEY_ADRESS,address);
        initialValues.put(Constants.PHOTO,companyid);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
   
    public long createAgent(int job,String role,String size,int area,int city,String address,double x,double y,String title1,String title2,String title3){
        ContentValues initialValues = new ContentValues();      
        initialValues.put(Constants.SPECIALITY, job);
        initialValues.put(Constants.ROLE, role);
        initialValues.put(Constants.SIZE, size);
        initialValues.put(Constants.AREA, area);
        initialValues.put(Constants.CITIES, city);
        initialValues.put(KEY_X, x);
        initialValues.put(KEY_Y, y);
        initialValues.put(KEY_ADRESS,address);
        initialValues.put(KEY_TITLE,title1);
        initialValues.put(KEY_TITLE2,title2);
        initialValues.put(KEY_TITLE3,title3);
        return mDb.insert(DATABASE_AGENTS_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId,int type) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId+" and "+KEY_TYPE+"="+Integer.toString(type), null) > 0;
    }
    public boolean deleteAgent(long rowId) {

        return mDb.delete(DATABASE_AGENTS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes(int target,int orderByName,double x,double y) {//
    	if (orderByName==0){
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, KEY_PRIMARY+" DESC");
    		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, KEY_PRIMARY+" DESC");
    	}
    	else if (orderByName==-1){
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, null);
    		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, null);//
    	}
    	else if (orderByName==4){
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, KEY_ADRESS);
    		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, KEY_ADRESS);//
    	}
    	else if (orderByName==3){
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, KEY_NAME);
    		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, KEY_NAME);//
    	}
    	else if (orderByName==2){
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, KEY_TITLE);
    		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, KEY_TITLE);//
    	}
    	else if (orderByName==1){
    		
    		double c= Math.cos(x*Math.PI/180);//Math.pow(Math.cos(x*Math.PI/180),2);
    		
    		//var d = (c*(KEY_X-x)*(KEY_X-x) + (KEY_X-x)*(KEY_X-x));
    		if (target==Constants.SEARCH+1) return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + "=" + target, null, null, null, c+"*("+KEY_Y+"-"+y+")*("+KEY_Y+"-"+y+") + ("+KEY_X+"-"+x+")*("+KEY_X+"-"+x+")"+" ASC");
		    else return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TYPE,KEY_TITLE,KEY_X,KEY_Y,KEY_NAME,KEY_ADRESS,Constants.PHOTO}, KEY_TYPE + ">=" + target, null, null, null, c+"*("+KEY_Y+"-"+y+")*("+KEY_Y+"-"+y+") + ("+KEY_X+"-"+x+")*("+KEY_X+"-"+x+")"+" ASC");//
	
    	}
    	return null;
    }
    public Cursor fetchAllAgents() {
    	return mDb.query(DATABASE_AGENTS_TABLE, new String[] {KEY_ROWID,KEY_TITLE,KEY_TITLE2,KEY_TITLE3,Constants.SPECIALITY,Constants.ROLE,Constants.SIZE,Constants.AREA,Constants.CITIES,KEY_X,KEY_Y,KEY_ADRESS}, "1>0", null, null, null, KEY_ROWID);	
    }
    public Cursor fetchAgent(long rowId){
    	return mDb.query(DATABASE_AGENTS_TABLE, new String[] {KEY_ROWID,Constants.SPECIALITY,Constants.ROLE,Constants.SIZE,Constants.AREA,Constants.CITIES,KEY_X,KEY_Y,KEY_ADRESS}, KEY_ROWID + "=" + rowId, null, null, null, KEY_ROWID);	
    }
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_X,KEY_Y,KEY_NAME,KEY_TYPE,KEY_ADRESS,Constants.PHOTO}, KEY_ROWID + "=" + rowId, null,
                    null, null, KEY_TITLE+" COLLATE NOCASE ASC", null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public boolean Exists(int id,int type) {
    	Cursor cursor;
    	if(type==Constants.SEARCH+1) cursor= mDb.query(DATABASE_TABLE, new String[] {KEY_TITLE}, KEY_ROWID + "=" + Integer.toString(id)+" and "+KEY_TYPE + "=" + Integer.toString(type), null, null, null, null);
    	else cursor= mDb.query(DATABASE_TABLE, new String[] {KEY_TITLE}, KEY_ROWID + "=" + Integer.toString(id)+" and "+KEY_TYPE + ">" + Integer.toString(type-1), null, null, null, null);
    	   //Cursor cursor =  mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TITLE, KEY_X,KEY_Y,KEY_NAME}, KEY_ROWID + "=" + new String[] {Integer.toString(id)}, null,         null, null, null, null);
    	   //mDb.rawQuery(Q, new String[] { name });
        	   boolean exists = (cursor.getCount() > 0);
    	   cursor.close();
    	   return exists;
    	}
    
    public boolean ExistsType(int type) {
   	
    	Cursor cursor= mDb.query(DATABASE_TABLE, new String[] {KEY_TITLE}, KEY_TYPE + "=" + Integer.toString(type), null, null, null, null);
    	   //Cursor cursor =  mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TITLE, KEY_X,KEY_Y,KEY_NAME}, KEY_ROWID + "=" + new String[] {Integer.toString(id)}, null,         null, null, null, null);
    	   //mDb.rawQuery(Q, new String[] { name });
        	   boolean exists = (cursor.getCount() > 0);
    	   cursor.close();
    	   return exists;
    	}
    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(int id, String name,String comp,double x,double y,int type, String adress,String companyid) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_TITLE, name);
        initialValues.put(KEY_X, x);
        initialValues.put(KEY_Y, y);
        initialValues.put(KEY_NAME, comp);//
        initialValues.put(KEY_TYPE,type);
        initialValues.put(KEY_ADRESS,adress);
        initialValues.put(Constants.PHOTO,companyid);

        return mDb.update(DATABASE_TABLE, initialValues, KEY_TYPE + "=" + type, null) > 0;
    }
    
    public boolean updateAgent(int id,int job,String role,String size,int area,int city,String address,double x,double y,String title1,String title2,String title3){
        ContentValues initialValues = new ContentValues();      
        initialValues.put(Constants.SPECIALITY, job);
        initialValues.put(Constants.ROLE, role);
        initialValues.put(Constants.SIZE, size);
        initialValues.put(Constants.AREA, area);
        initialValues.put(Constants.CITIES, city);
        initialValues.put(KEY_X, x);
        initialValues.put(KEY_Y, y);
        initialValues.put(KEY_ADRESS,address);
        initialValues.put(KEY_TITLE,title1);
        initialValues.put(KEY_TITLE2,title2);
        initialValues.put(KEY_TITLE3,title3);
        return mDb.update(DATABASE_AGENTS_TABLE, initialValues, KEY_ROWID + "=" + id, null) > 0;
    }
}
