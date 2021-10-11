package com.noamzaks.chess.utilities;

public class Point<T> {
    public T first, second;

    public Point(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public Point() {}
}
