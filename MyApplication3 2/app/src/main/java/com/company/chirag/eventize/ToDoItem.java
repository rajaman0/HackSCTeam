package com.company.chirag.eventize;

/**
 * Created by vijaysachet on 11/15/15.
 */
public class ToDoItem {
    private String to;
    private String from;
    private String location;
    private String event;
    private int counter;
    private boolean isComplete;
    private String Id;

    public ToDoItem()
    {

    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int c){
        counter = c;

    }

    public ToDoItem(String event, String location, String to, String from )
    {
        this.setEvent(event);
        this.setLocation(location);
        this.setTo(to);
        this.setFrom(from);
        counter = 0;
        //Id = event + to + from + location;
    }
    public String getEvent()
    {
        return event;
    }
    public String getId()
    {
        return Id;
    }
    public void setId(String id)
    {
        Id = id;

    }
    public Boolean isComplete()
    {
        return isComplete;
    }
    public void setComplete(Boolean bool)
    {
        isComplete=bool;
    }
    public String getLocation()
    {
        return location;
    }
    public String getTo()
    {
        return to;
    }
    public String getFrom()
    {
        return from;
    }
    public void setEvent(String e)
    {
        this.event = e;
    }
    public void setLocation(String e)
    {
        this.location = e;
    }
    public void setTo(String e)
    {
        this.to = e;
    }
    public void setFrom(String e)
    {
        this.from = e;
    }



}
