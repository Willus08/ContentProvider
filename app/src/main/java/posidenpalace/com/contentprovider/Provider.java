package posidenpalace.com.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import static android.content.ContentValues.TAG;


public class Provider extends ContentProvider {

    public static final String PROVIDER_NAME= "posidenpalace.com.contentprovider.Provider"; // the name of the provider
    public static final String URL="content://"+ PROVIDER_NAME +"/cppeople"; // the url need for the provider
    public static final Uri CONTENT_URL = Uri.parse(URL);// converts the string URL to a uri

    public static final String id ="id"; // the pk of the table
    public static final String name = "name"; // fields of the table
    public static final String age = "age";
    public static final String phone = "phone";
    public static final String email = "email";
    public static final int uriCode = 1;// used to identify the uri

    private static HashMap<String,String> values;

    public static final UriMatcher matcher;
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PROVIDER_NAME,"cppeople",uriCode);
    }


    //set up stuff for the DataBase
    private  SQLiteDatabase providedBase;
    public static final String DTABASE_NAME = "myPeople";
    public static final String TABLE_NAME ="people";
    public static final int VERSION = 1;
    // sets up to create the table with the input values
    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL);";


    @Override
    public boolean onCreate() {
        //Initialize your provider. The Android system calls this method immediately after it creates your provider.
        // Notice that your provider is not created until a ContentResolver object tries to access it.

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        providedBase = dbHelper.getWritableDatabase();

        return providedBase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
       // Retrieve data from your provider. Use the arguments to select the table to query, the rows and columns to return,
        // and the sort order of the result. Return the data as a Cursor object.

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //sets up for the query
        queryBuilder.setTables(TABLE_NAME);

        // ised to make sure you have the right table
        switch (matcher.match(uri)){
            case uriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI" + uri);// shows an error if it get the wrong uri

        }// end switch

        Cursor cursor = queryBuilder.query(providedBase,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //Return the MIME type corresponding to a content URI. This method is described in more detail in the section
        // Implementing Content Provider MIME Types.

        // ised to make sure you have the right table
        switch (matcher.match(uri)){
            case uriCode:
                return "vnd.android.curssor.dir/cppeople";

            default:
                throw new IllegalArgumentException("Unsuported URI" + uri);// shows an error if it get the wrong uri

        }// end switch

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
       // Insert a new row into your provider. Use the arguments to select the destination table
        // and to get the column values to use. Return a content URI for the newly-inserted row.
        Log.d(TAG, "insert: " + values);
        long rowID = providedBase.insert(TABLE_NAME, null, values);

        // makes sure the row exists
        if (rowID > 0){
            Uri contentUri = ContentUris.withAppendedId(CONTENT_URL,rowID);

            getContext().getContentResolver().notifyChange(contentUri,null);
            return contentUri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Delete rows from your provider. Use the arguments to select the table and the rows to delete. Return the number of rows deleted.
        int rowsDeleted = 0;
        // ised to make sure you have the right table
        switch (matcher.match(uri)){
            case uriCode:
                rowsDeleted = providedBase.delete(TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI" + uri);// shows an error if it get the wrong uri

        }// end switch

        getContext().getContentResolver().notifyChange(uri,null);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
       // Update existing rows in your provider. Use the arguments to select the table and rows to update and to get the updated column values.
        // Return the number of rows updated.

        int rowsUpdated;
        // ised to make sure you have the right table
        switch (matcher.match(uri)){
            case uriCode:
                rowsUpdated = providedBase.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI" + uri);// shows an error if it get the wrong uri

        }// end switch

        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context, DTABASE_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

        }
    }
}
