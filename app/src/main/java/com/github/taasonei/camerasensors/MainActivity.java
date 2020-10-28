package com.github.taasonei.camerasensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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
    private boolean isRunning = false;
    private String label = "";
    private int currentItem = R.id.iso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // присваиваем chart LineChart из activity+main.xml по id
        chart = findViewById(R.id.chart);

        // разрешает сенсорные взаимодействия с графиком
        chart.setTouchEnabled(true);
        // позволяет передвигать график
        chart.setDragEnabled(true);
        // позволяет менять масштаб графика
        chart.setScaleEnabled(true);

        // описание в правом нижнем углу
        chart.setDescription("ISO");
        chart.setNoDataTextDescription("No data for the moment");
        // цвет фона для графика
        chart.setGridBackgroundColor(Color.WHITE);
        // цвет фона вокруг графика
        chart.setBackgroundColor(Color.LTGRAY);

        // создаем объект класса Legend извлекая объект Legend из диаграммы с помощью getLegend()
        Legend legend = chart.getLegend();
        // задаем форму, которая будут отображать цвет графика напротив его названия
        legend.setForm(Legend.LegendForm.CIRCLE);

        // создаем объект класса XAxis,
        // в котором сохраняются данные для того, что связано с горизонтальной осью
        XAxis x = chart.getXAxis();
        // отображаем значение x под графиком
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        // задаем цвет значений х белым
        x.setTextColor(Color.WHITE);
        // не рисуем осевую линию
        x.setDrawAxisLine(false);
        x.setAvoidFirstLastClipping(true);

        YAxis yl2 = chart.getAxisRight();
        yl2.setEnabled(false);

     

        showISO();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.iso:
                                if (currentItem != R.id.iso) {
                                    currentItem = R.id.iso;
                                    showISO();
                                }
                                break;
                            case R.id.focus:
                                if (currentItem != R.id.focus) {
                                    currentItem = R.id.focus;
                                    showFocus();
                                }
                                break;
                            case R.id.accelerometer:
                                if (currentItem != R.id.accelerometer) {
                                    currentItem = R.id.accelerometer;
                                    showAccelerometer();
                                }
                                break;
                            case R.id.coordinates:
                                if (currentItem != R.id.coordinates) {
                                    currentItem = R.id.coordinates;
                                    showCoordinates();
                                }
                                break;
                            case R.id.approximation:
                                if (currentItem != R.id.approximation) {
                                    currentItem = R.id.approximation;
                                    showApproximation();
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
            //chart.moveViewToX(chart.getXChartMax());
        }
    }

    private LineDataSet createSet(String label) {
        // создаем объект класса LineDataSet
        // список значение - null
        // подпись графика - label
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

    private void add2Entry() {
        LineData data = chart.getData();

        if (data != null) {
            LineDataSet set1 = data.getDataSetByIndex(0);
            LineDataSet set2 = data.getDataSetByIndex(1);
            if (set1 == null) {
                set1 = createSet(label);
                data.addDataSet(set1);
            }
            if (set2 == null) {
                set2 = createSet2("Longitude");
                data.addDataSet(set2);
            }
            data.addXValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).
                    format(new Date())
            );
            // добавляем значение y
            data.addEntry(
                    new Entry(randomGenerator.getLatitude(),
                            set1.getEntryCount()),
                    0);
            data.addEntry(
                    new Entry(randomGenerator.getLongitude(),
                            set2.getEntryCount()),
                    1);
            // сообщаем, что данные обновились, чтобы обновить график
            chart.notifyDataSetChanged();
            // перемещаем отображение графика на последнее добавленное значение
            chart.moveViewToX(chart.getXChartMax());
        }
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

    synchronized private void showISO() {
        clearChart();
        createLineData();
        label = "ISO";
        chart.setDescription(label);
        setYAxis(200f, 3250f);
        isRunning = true;
        notifyAll();
    }

    synchronized private void showFocus() {
        clearChart();
        createLineData();
        label = "Focus";
        chart.setDescription(label);
        setYAxis(0f, 1.2f);
        isRunning = true;
        notifyAll();
    }

    synchronized private void showAccelerometer() {
        clearChart();
        createLineData();
        label = "Accelerometer";
        chart.setDescription(label);
        setYAxis(0f, 110f);
        isRunning = true;
        notifyAll();
    }

    synchronized private void showApproximation() {
        clearChart();
        createLineData();
        label = "Approximation";
        chart.setDescription(label);
        setYAxis(0f, 1.2f);
        isRunning = true;
        notifyAll();
    }

    synchronized private void showCoordinates() {
        clearChart();
        createLineData();
        label = "Latitude";
        chart.setDescription("Coordinates");
        // широта 47.220431f, 47.223050f);
        // долгота 39.707823f, 39.712154f);
        setYAxis(39.707823f, 47.3f);
        isRunning = true;
        notifyAll();
    }

    synchronized private void clearChart() {
        isRunning = false;
        notifyAll();
        // очищаем диаграмму data object = null
        chart.clear();
        chart.invalidate();
    }

    private void createLineData() {
        // создаем объект класса LineData, который устанавливает данные для линейной диаграммы
        LineData data = new LineData();
        // добавляем значения на график
        chart.setData(data);
    }
}