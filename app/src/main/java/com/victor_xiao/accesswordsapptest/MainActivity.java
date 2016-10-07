package com.victor_xiao.accesswordsapptest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "db";
    private ContentResolver resolver;

    private final static String words = "words";
    private final static String means = "means";
    private final static String samples = "samples";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolver = this.getContentResolver();
        final Uri uri = Uri.parse("content://com.victor_xiao.wordsprovider/word");

        ListView list = (ListView) findViewById(R.id.lstWords);
        registerForContextMenu(list);


        //得到按钮
        Button buttonAll = (Button) findViewById(R.id.buttonAll);
        Button buttonInsert = (Button) findViewById(R.id.buttonInsert);
        Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
        Button buttonDeleteAll = (Button) findViewById(R.id.buttonDeleteAll);

        //为每个按钮设置监听器
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAll();
            }
        });
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWord = "Banana";
                String strMeaning = "香蕉";
                String strSample = "This banana is very nice.";
                ContentValues values = new ContentValues();

                values.put(Words.Word.COLUMN_NAME_WORD, strWord);
                values.put(Words.Word.COLUMN_NAME_MEANING, strMeaning);
                values.put(Words.Word.COLUMN_NAME_SAMPLE, strSample);
                getContentResolver().insert(uri, values);
                values.clear();

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = "red";

                Uri uri = Uri.parse(Words.Word.CONTENT_URI_STRING);

                Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID},
                        "word=?", new String[]{word}, null);
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(0);
                    //根据id删除data中的相应数据
                    resolver.delete(uri, "word=?", new String[]{word});
                }
            }
        });

        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolver.delete(uri, null, null);
            }
        });

    }

    private void getAll() {
        final Uri uri = Uri.parse("content://com.victor_xiao.wordsprovider/word");


        ListView list = (ListView) findViewById(R.id.lstWords);
        List<Map<String, Object>> items = new ArrayList<>();

        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Toast.makeText(MainActivity.this, "没有找到记录", Toast.LENGTH_LONG).show();
            return;
        }


        String msg = "";
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(Words.Word._ID));
                String word = cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_WORD));
                String mean = cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_MEANING));
                String sample = cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_SAMPLE));


                Map<String, Object> item = new HashMap<>();
                item.put(words, word);
                item.put(means, mean);
                item.put(samples, sample);
                items.add(item);

                Log.d("db", "word is " + word);
                Log.d("db", "ID is " + id);
                Log.d("db", "meaning " + mean);
                Log.d("db", "here is a sample: " + sample);

            } while (cursor.moveToNext());
        }


        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.word_list,
                new String[]{words, means, samples}, new int[]{R.id.listword,
                R.id.listmeaning, R.id.listsample});

        list.setAdapter(adapter);
    }

}
