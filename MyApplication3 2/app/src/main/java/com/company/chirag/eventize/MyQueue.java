package com.company.chirag.eventize;

import java.util.Queue;

public class MyQueue<T>{
    private int head;
    private int tail;
    private T[] list;
    private boolean full;

    public MyQueue() {
        full = false;
        list = (T[]) new Object[5];
        head = tail = 0;
    }

    public boolean isEmpty() {//returns true if queue is empty
        return size() == 0;
    }

    public void add(T newItem) {//adds an item. If the queue is full, calls resize method to create a new, larger array.
        list[tail++] = newItem;
        if (tail == list.length)
            tail = 0;   
        
        full = tail == head;
        
        if (full) {
            resize();
        }
         
    }

    private void resize() {//resizes the array. Two cases, based on if head >= tail or if tail > head. 
        T[] list_temp = (T[]) new Object[list.length * 2];
        if (head >= tail){
            for (int i = head; i < list.length; i ++)
                list_temp[i-head] = list[i];
            for (int i = 0; i < tail; i ++){
                list_temp[list.length - head + i] = list[i];
            }
        }else
            for (int i = head; i < tail; i++)
                list_temp[i- head] = list[i];
        head = 0; 
        tail = size();
        list = list_temp;
        full = false;
    }

    public T get() {//returns and removes the first object
        full = false;
        if (size() == 0) throw new IllegalStateException();
        T item = list[head++];
        if (head == list.length)
            head = 0;

        return item;
    }

    public int size() {//returns the size of the array
        if (full) return list.length;
        if (head > tail)
            return list.length - head + tail;
        else
            return tail - head;
    }
}
