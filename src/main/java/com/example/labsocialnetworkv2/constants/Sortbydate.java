package com.example.labsocialnetworkv2.constants;

import com.example.labsocialnetworkv2.domain.Message;

import java.util.Comparator;

public class Sortbydate implements Comparator<Message> {
    // Used for sorting in ascending order of
    // roll number
    public int compare(Message a, Message b)
    {
        return a.getData().compareTo(b.getData());
    }
}