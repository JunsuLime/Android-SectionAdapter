package org.osori.samples;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import org.osori.sectionadapter.SectionAdapter;


public class MainActivity extends Activity {

    RecyclerView sampleRecyclerView;
    SampleAdapter sampleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        sampleAdapter = new SampleAdapter(this);
    }
}
