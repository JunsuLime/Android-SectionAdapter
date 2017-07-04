package org.osori.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by junsu on 2017-06-10.
 */

public class ListViewActivity extends AppCompatActivity {

    Button testButton;
    ListView listView;

    SampleListViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        testButton = (Button) findViewById(R.id.test_button);
        listView = (ListView) findViewById(R.id.list_view);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListViewActivity.this, "child count: " + listView.findViewWithTag("test"), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new SampleListViewAdapter(this);
        listView.setAdapter(adapter);
    }
}
