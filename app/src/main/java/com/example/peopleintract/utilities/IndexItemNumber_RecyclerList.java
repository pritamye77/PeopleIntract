package com.example.peopleintract.utilities;

public class IndexItemNumber_RecyclerList {
    private final int count;

    public IndexItemNumber_RecyclerList(int count) {
        this.count = count;

    }

    public int getCursorPosition(){
        return count;
    }
}
