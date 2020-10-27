package com.github.taasonei.camerasensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

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
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Activity activity = this;
    private static final Random RANDOM = new Random();
    private LineChart chart;

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
        // запрещает масштабировать оси по отдельности
        chart.setPinchZoom(true);

        // описание в правом нижнем углу
        chart.setDescription("ISO");
        chart.setNoDataTextDescription("No data for the moment");
        // цвет фона для графика
        chart.setGridBackgroundColor(Color.WHITE);
        // цвет фона вокруг графика
        chart.setBackgroundColor(Color.LTGRAY);

        //chart.setMaxVisibleValueCount(10); //only when setDrawValues() is enabled

        // создаем объект класса LineData, который устанавливает данные для линейной диаграммы
        LineData data = new LineData();
        // добавляем значения на график
        chart.setData(data);

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
                                showISO();
                                break;
                            case R.id.focus:
                                showFocus();
                                break;
                            case R.id.accelerometer:
                                showAccelerometer();
                                break;
                            case R.id.coordinates:
                                showCoordinates();
                                break;
                            case R.id.approximation:
                                showApproximation();
                                break;
                        }
                        return true;
                    }
                }
        );
    }


    private void addEntry() {
        LineData data = chart.getData();

        if (data != null) {
            // создаем объект класса LineDataSet,
            // который представляет группу записей внутри 1й диаграммы
            // присваемым ему значение по индексу 0 в списке объектов данных DataSet
            LineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            // добавляем значение х текущие время и дату
            data.addXValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).
                    format(new Date())
            );
            // добавляем значение y
            data.addEntry(new Entry(
                    //
                    (float) (Math.random() * 10), set.getEntryCount()), 0);
            // сообщаем, что данные обновились, чтобы обновить график
            chart.notifyDataSetChanged();
            // устанавливаем максимальное количество видимых точек на графике - 5
            chart.setVisibleXRange(4);
            // перемещаем отображение графика на последнее добавленное значение
            chart.moveViewToX(data.getXValCount() - 5);
        }
    }

    private LineDataSet createSet() {
        // создаем объект класса LineDataSet
        // список значение - null
        // подпись графика - label
        LineDataSet set = new LineDataSet(null, "ISO");
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

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
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

    private void showISO() {
        chart.setDescription("ISO");
        // 200 400 800 1600 3200
        setYAxis(200f, 3200f);
    }

    private void showFocus() {
        chart.setDescription("Focus");
        setYAxis(0f, 1f);
    }

    private void showAccelerometer() {
        chart.setDescription("Accelerometer");
        setYAxis(0f, 100f);
    }

    private void showApproximation() {
        chart.setDescription("Approximation");
        setYAxis(0f, 1f);
    }

    private void showCoordinates() {
        chart.setDescription("Coordinates");
//        // широта
//        setYAxis(47.220431f, 47.223050f);
//        // долгота
//        setYAxis(39.707823f, 39.712154f);



        setYAxis(39.707823f, 47.223050f);
    }

    private void clearChart() {
        // очищаем диаграмму data object = null
        chart.clear();
        chart.invalidate();
    }
}