package com.github.taasonei.camerasensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Activity activity = this;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private int lastX = 0;
    private Viewport viewport;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.addSeries(series);

        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        // activate horizontal scrolling  enables horizontal scrolling
       viewport.setScrollable(true);
        // activate horizontal zooming and scrolling
       //viewport.setScalable(true);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.iso:
                                graph.setVisibility(View.VISIBLE);

                                break;
                            case R.id.focus:
                                graph.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.accelerometer:
                                graph.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.coordinates:
                                graph.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.approximation:
                                graph.setVisibility(View.INVISIBLE);
                                break;
                        }
                        return true;
                    }
                }
        );
        addRandomData();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 15; i++) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//
//                    }
//                }
//            }
//        }).start();
//    }

    private void addEntry() {
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), false, 22);
       //viewport.scrollToEnd();
    }

    private void addRandomData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                series.appendData(new DataPoint(lastX++, RANDOM.nextInt(10)), false, 100);
                addRandomData();
            }
        }, 1000);
    }
}