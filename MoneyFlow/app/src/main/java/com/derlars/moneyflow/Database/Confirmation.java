package com.derlars.moneyflow.Database;

public class Confirmation<V> {
    final long time;
    final String k;
    final String k1;
    final String k2;
    final V v;

    final Runnable ru;

    public Confirmation(long time, String k1, String k2, V v, Runnable ru) {
        this.time = time;
        this.k = "";
        this.k1 = k1;
        this.k2 = k2;
        this.v = v;

        this.ru = ru;
    }

    public Confirmation(long time, String k, V v, Runnable ru) {
        this.time = time;
        this.k = k;
        this.k1 = "";
        this.k2 = "";
        this.v = v;

        this.ru = ru;
    }

    public String toString() {
        return "Confirmation: {"
                + " time:" + time
                + " k:" + k
                + " k1:" + k1
                + " k2:" + k2
                + " v:" + v
                + "}";
    }
}
