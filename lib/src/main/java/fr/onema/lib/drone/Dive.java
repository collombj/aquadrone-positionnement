package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;

import java.security.Timestamp;
import java.util.Queue;

/**
 * Created by strock on 09/02/2017.
 */
public class Dive {

    private DiveEntity entity;
    private int localStartTime;
    private int subStartTime;
    private int localEndTime;
    private int subEndTime;
    private int actionNumber;
    private enum state {OFF,ON,RECORD};
    private boolean divesOver;
    private Position lastPosition;
    private Queue<Position> positions;

    public Dive(int localStartTime) {
        this.localStartTime = localStartTime;
    }

    public void add(Position position){
        positions.add(position);
    }

    public void endDive(){

    }

    public void startRecording (Timestamp time){

    }

    public void stopRecording (Timestamp time){

    }

    public void newMovement(){

    }

    //todo getter


}
