package posidenpalace.com.contentprovider;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "test";
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = (EditText) findViewById(R.id.etName);

    }

    public void saveToDatabase(View view) {

        String name = etName.getText().toString();
        ContentValues values = new ContentValues();
        values.put(Provider.name,name);


        Log.d(TAG, "saveToDatabase: " +values);
        Uri uri = getContentResolver().insert(Provider.CONTENT_URL,values);
        Toast.makeText(this, name+" added to database", Toast.LENGTH_SHORT).show();

    }
}
