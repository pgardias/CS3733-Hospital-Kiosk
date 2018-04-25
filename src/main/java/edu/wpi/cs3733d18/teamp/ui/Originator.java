package edu.wpi.cs3733d18.teamp.ui;

public class Originator {
    private String state;

    public Originator(){
    }
    public void setState(String state){
        this.state = state;
    }

    public String getState(){
        return state;
    }

    public Memento saveStateToMemento(){
        return new Memento(state);
    }

    public void getStateFromMemento(Memento memento){
        state = memento.getState();
    }
}
