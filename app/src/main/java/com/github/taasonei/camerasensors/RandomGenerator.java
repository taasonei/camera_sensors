package com.github.taasonei.camerasensors;

import java.util.Random;

public class RandomGenerator {

    private final Random random = new Random();

    // широта
    public float getLatitude() {
        // диапазон от 47.220431 до 47.223050 (0.002619)
        float latitude = 47.220431f;
        latitude += Math.abs((random.nextGaussian() / 500));
        if (latitude > 47.223050f) {
            latitude = 47.223050f;
        }
        return latitude;
    }

    // долгота
    public float getLongitude() {
        // диапазон от 39.707823 до 39.712154 (0,004331)
        float longitude = 39.707823f;
        longitude += Math.abs((random.nextGaussian() / 500));
        if (longitude > 39.712154f) {
            longitude = 39.712154f;
        }
        return longitude;
    }

    public float getIso() {
        float[] values = {200f, 400f, 800f, 1600f, 3200f};
        float iso = values[random.nextInt(values.length)];
        System.out.println(iso);
        return iso;
    }

    public float getAccelerometer() {
        return (float) Math.round(Math.abs(Math.sin(random.nextInt()) * 100));
    }

    // фокус и датчик приближения
    public float getZeroOneValue() {
        return (float) Math.round(Math.abs(Math.cos(random.nextGaussian())));
    }

}
