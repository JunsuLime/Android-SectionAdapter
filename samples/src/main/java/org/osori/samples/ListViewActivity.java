package org.osori.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by junsu on 2017-06-10.
 */

public class ListViewActivity extends AppCompatActivity {

    ListView listView;
    TestAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        listView = (ListView) findViewById(R.id.list_view);

        adapter = new TestAdapter(this);
        listView.setAdapter(adapter);
    }
}
