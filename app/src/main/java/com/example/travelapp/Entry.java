package com.example.travelapp;

public class Entry<Key, V> {
    public final Key key;
    public final V value;

    public Entry(Key k, V v) {
        key = k;
        value = v;
    }

    public V getValue() {
        return value;
    }
}