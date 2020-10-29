package com.github.taasonei.camerasensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Activity activity = this;
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private LineChart chart;
    private LineChart chart2;
    private int currentItem = R.id.iso;

    private boolean isRunning = false;
    private String label = "";

    private final String isoLabel = "ISO";
    private final float isoMin = 200f;
    private final float isoMax = 3250f;

    private final String focusLabel = "Focus";
    private final String approximationLabel = "Approximation";
    private final float minSensorValue = 0f;
    private final float maxSensorValue = 1.05f;

    private final String accelerometerLabel = "Accelerometer";
    private float accelerometerMin = 0f;
    private float accelerometerMax = 105;

    private final String longitudeLabel = "Longitude";
    private final float longitudeMin = 39.707823f;
    private final float longitudeMax = 39.713154f;

    private final String latitudeLabel = "Latitude";
    private final float latitudeMin = 47.220431f;
    private final float latitudeMax = 47.224050f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // присваиваем chart LineChart из activity+main.xml по id
        chart = findViewById(R.id.chart);
        chart2 = findViewById(R.id.chart2);

        // разрешает сенсорные взаимодействия с графиком
        chart.setTouchEnabled(true);
        chart2.setTouchEnabled(true);
        // позволяет передвигать график
        chart.setDragEnabled(true);
        chart2.setDragEnabled(true);
        // позволяет менять масштаб графика
        chart.setScaleEnabled(true);
        chart2.setScaleEnabled(true);

        // описание в правом нижнем углу
        chart.setNoDataTextDescription("No data for the moment");
        chart2.setNoDataTextDescription("No data for the moment");
        // цвет фона для графика
        chart.setGridBackgroundColor(Color.WHITE);
        chart2.setGridBackgroundColor(Color.WHITE);
        // цвет фона вокруг графика
        chart.setBackgroundColor(Color.LTGRAY);
        chart2.setBackgroundColor(Color.LTGRAY);

        // создаем объект класса Legend извлекая объект Legend из диаграммы с помощью getLegend()
        Legend legend = chart.getLegend();
        Legend legend2 = chart2.getLegend();
        // задаем форму, которая будут отображать цвет графика напротив его названия
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend2.setForm(Legend.LegendForm.CIRCLE);

        // создаем объект класса XAxis,
        // в котором сохраняются данные для того, что связано с горизонтальной осью
        XAxis x = chart.getXAxis();
        XAxis x2 = chart2.getXAxis();
        // отображаем значение x под графиком
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x2.setPosition(XAxis.XAxisPosition.BOTTOM);
        // задаем цвет значений х белым
        x.setTextColor(Color.WHITE);
        x2.setTextColor(Color.WHITE);
        // не рисуем осевую линию
        x.setDrawAxisLine(false);
        x.setAvoidFirstLastClipping(true);
        x2.setDrawAxisLine(false);
        x2.setAvoidFirstLastClipping(true);

        YAxis y = chart.getAxisRight();
        y.setEnabled(false);
        YAxis y2 = chart2.getAxisRight();
        y2.setEnabled(false);

        showChart(isoLabel, isoMin, isoMax);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.iso:
                                if (currentItem != R.id.iso) {
                                    currentItem = R.id.iso;
                                    chart2.setVisibility(View.GONE);
                                    showChart(isoLabel, isoMin, isoMax);
                                }
                                break;
                            case R.id.focus:
                                if (currentItem != R.id.focus) {
                                    currentItem = R.id.focus;
                                    chart2.setVisibility(View.GONE);
                                    showChart(focusLabel, minSensorValue, maxSensorValue);
                                }
                                break;
                            case R.id.accelerometer:
                                if (currentItem != R.id.accelerometer) {
                                    currentItem = R.id.accelerometer;
                                    chart2.setVisibility(View.GONE);
                                    showChart(accelerometerLabel, accelerometerMin, accelerometerMax);
                                }
                                break;
                            case R.id.coordinates:
                                if (currentItem != R.id.coordinates) {
                                    currentItem = R.id.coordinates;
                                    chart2.setVisibility(View.VISIBLE);
                                    showChart(latitudeLabel, latitudeMin, latitudeMax, longitudeMin, longitudeMax);
                                }
                                break;
                            case R.id.approximation:
                                if (currentItem != R.id.approximation) {
                                    currentItem = R.id.approximation;
                                    chart2.setVisibility(View.GONE);
                                    showChart(approximationLabel, minSensorValue, maxSensorValue);
                                }
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentItem != R.id.coordinates) {
                                addEntry();
                            } else {
                                add2Entry();
                            }
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).start();
    }

    private void addEntry() {
        LineData data = chart.getData();
        if (data != null) {
            // создаем объект класса LineDataSet,
            // который представляет группу записей внутри 1й диаграммы
            // присваемым ему значение по индексу 0 в списке объектов данных DataSet
            LineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet(label);
                data.addDataSet(set);
            }
            // добавляем значение х текущие время и дату
            data.addXValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).
                    format(new Date())
            );
            // добавляем значение y
            switch (label) {
                case "ISO":
                    data.addEntry(
                            new Entry(randomGenerator.getIso(),
                                    set.getEntryCount()), 0);
                    break;
                case "Accelerometer":
                    data.addEntry(
                            new Entry(randomGenerator.getAccelerometer(),
                                    set.getEntryCount()), 0);
                    break;
                case "Focus":
                case "Approximation":
                    data.addEntry(
                            new Entry(randomGenerator.getZeroOneValue(),
                                    set.getEntryCount()), 0);
                    break;
            }
            // сообщаем, что данные обновились, чтобы обновить график
            chart.notifyDataSetChanged();
            // перемещаем отображение графика на последнее добавленное значение
            chart.moveViewToX(chart.getXChartMax());
        }
    }

    private void add2Entry() {
        LineData data = chart.getData();
        LineData data2 = chart2.getData();
        String x;

        if (data != null && data2 != null) {
            LineDataSet set1 = data.getDataSetByIndex(0);
            LineDataSet set2 = data2.getDataSetByIndex(0);
            if (set1 == null) {
                set1 = createSet(label);
                data.addDataSet(set1);
            }
            if (set2 == null) {
                set2 = createSet2(longitudeLabel);
                data2.addDataSet(set2);
            }
            x = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).
                    format(new Date());
            data.addXValue(x);
            data2.addXValue(x);
            // добавляем значение y
            data.addEntry(
                    new Entry(randomGenerator.getLatitude(),
                            set1.getEntryCount()),
                    0);
            data2.addEntry(
                    new Entry(randomGenerator.getLongitude(),
                            set2.getEntryCount()),
                    0);
            // сообщаем, что данные обновились, чтобы обновить график
            chart.notifyDataSetChanged();
            chart2.notifyDataSetChanged();
            // перемещаем отображение графика на последнее добавленное значение
            chart.moveViewToX(chart.getXChartMax());
            chart2.moveViewToX(chart2.getXChartMax());
        }
    }

    private LineDataSet createSet(String label) {
        // создаем объект класса LineDataSet, список значение - null, подпись графика - label
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        // цвет линии графика
        set.setColor(R.color.colorPrimary);
        // цвет точек на графике
        set.setCircleColor(R.color.colorPrimary);
        // толщина линии графика
        set.setLineWidth(3f);
        // размер точек на графике
        set.setCircleSize(5f);
        // размер текста для полученных значений в dp
        set.setValueTextSize(10f);
        return set;
    }

    private LineDataSet createSet2(String label) {
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setCircleSize(5f);
        set.setValueTextSize(10f);
        return set;
    }

    private void setYAxis(float minValue, float maxValue) {
        // создаем объект класса YAxis,
        // в котором сохраняются данные для того, что связано с вертикальной осью
        // отображаем шкалу слева
        YAxis y = chart.getAxisLeft();
        // задаем цвет значений y белым
        y.setTextColor(Color.WHITE);
        // минимальное значение y
        y.setAxisMinValue(minValue);
        // максимальное значение y
        y.setAxisMaxValue(maxValue);
        // значение y начинается с минимального, а не с 0 по умолчанию
        y.setStartAtZero(false);
    }

    private void setYAxis2(float minValue, float maxValue) {
        YAxis y = chart2.getAxisLeft();
        y.setTextColor(Color.WHITE);
        y.setAxisMinValue(minValue);
        y.setAxisMaxValue(maxValue);
        y.setStartAtZero(false);
    }

    synchronized private void showChart(String chartLabel, float min, float max) {
        clearChart();
        createLineData();
        label = chartLabel;
        chart.setDescription(label);
        setYAxis(min, max);
        isRunning = true;
        notifyAll();
    }

    synchronized private void showChart(String label1, float min1, float max1, float min2, float max2) {
        clearChart();
        createLineData();
        createLineData2();
        label = label1;
        chart.setDescription("Coordinates");
        setYAxis(min1, max1);
        setYAxis2(min2, max2);
        isRunning = true;
        notifyAll();
    }

    synchronized private void clearChart() {
        isRunning = false;
        notifyAll();
        // очищаем диаграмму data object = null
        chart.clear();
        chart.invalidate();
        if(chart2 != null) {
            chart2.clear();
            chart2.invalidate();
        }
    }

    private void createLineData() {
        // создаем объект класса LineData, который устанавливает данные для линейной диаграммы
        LineData data = new LineData();
        // добавляем значения на график
        chart.setData(data);
    }

    private void createLineData2() {
        LineData data = new LineData();
        chart2.setData(data);
    }
}