package org.osori.samples;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.osori.sectionadapter.SectionAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class MainActivity extends Activity {

    RecyclerView sampleRecyclerView;
    SampleAdapter sampleAdapter;

    String[] stringArray = {
            "This is", "RecyclerView", "Adapter",
            "for", "EveryOne", ",", "Opensource", "made by", "Osori.junsuLime"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        List<String> stringList = Arrays.asList(stringArray);
        sampleAdapter = new SampleAdapter(this, stringList);

        sampleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sampleRecyclerView.setAdapter(sampleAdapter);
    }
}
