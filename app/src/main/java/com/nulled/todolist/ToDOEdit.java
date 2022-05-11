package com.nulled.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToDOEdit extends Activity implements OnClickListener{

    private Button save;
    private String mode;
    private EditText code;
    private String id, status = "incomplete";
    private CheckBox td_status_cb_edit;
    private TextView td_dates_txtview_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_page);

        // get the values passed to the activity from the calling activity
        // determine the mode - add, update or delete
        if (this.getIntent().getExtras() != null){
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // get references to the buttons and attach listeners
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        td_status_cb_edit = findViewById(R.id.td_status_cb_edit);
        td_dates_txtview_edit = findViewById(R.id.td_dates_txtview_edit);

        code = (EditText) findViewById(R.id.code);

        // if in add mode disable the delete option
        if(mode.trim().equalsIgnoreCase("add")){

        }
        // get the rowId for the specific country
        else{
            Bundle bundle = this.getIntent().getExtras();
            id = bundle.getString("rowId");
            loadCountryInfo();
        }

        td_status_cb_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(td_status_cb_edit.isChecked()){
                    status = "complete";

                }
            }
        });
    }

    public void onClick(View v) {

        // get values from the spinner and the input text fields
        String myCode = code.getText().toString();

        // check for blanks
        if(myCode.trim().equalsIgnoreCase("")){
            Toast.makeText(getBaseContext(), "Please ENTER country code", Toast.LENGTH_LONG).show();
            return;
        }


        switch (v.getId()) {
            case R.id.save:
                ContentValues values = new ContentValues();
                values.put(ToDoDb.KEY_CODE, myCode);
                values.put(ToDoDb.KEY_NAME, status);
                values.put(ToDoDb.KEY_CONTINENT, getDateTime());

                // insert a record
                if(mode.trim().equalsIgnoreCase("add")){
                    getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                }
                // update a record
                else {
                    Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
                    getContentResolver().update(uri, values, null, null);
                }
                finish();
                break;

        }
    }

    // based on the rowId get all information from the Content Provider
    // about that country
    private void loadCountryInfo(){

        String[] projection = {
                ToDoDb.KEY_ROWID,
                ToDoDb.KEY_CODE,
                ToDoDb.KEY_NAME,
                ToDoDb.KEY_CONTINENT};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);

        if (cursor != null) {
            cursor.moveToFirst();
            String myCode = cursor.getString(cursor.getColumnIndexOrThrow(ToDoDb.KEY_CODE));
            String myName = cursor.getString(cursor.getColumnIndexOrThrow(ToDoDb.KEY_NAME));
            String myContinent = cursor.getString(cursor.getColumnIndexOrThrow(ToDoDb.KEY_CONTINENT));
            code.setText(myCode);
            td_dates_txtview_edit.setText(myName);
        }
    }

    // this sets the spinner selection based on the value
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yyyy HH:mm:ss",
                Locale.getDefault());

        Date date = new Date();
        return dateFormat.format(date);
    }
}