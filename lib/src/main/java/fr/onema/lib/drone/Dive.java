package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;

/**
 * Created by loics on 10/02/2017.
 *
 * @author loics
 * @since 10-02-2017
 */
public class Dive {

    private final DiveEntity diveEntity;
    private int localStartTime;
    private int subStartTime;
    private int subEndTime;
    private int actionNumber;
    private State state;
    private boolean isDiveOver;
    private Position lastPosition;

    private Dive(DiveEntity diveEntity, int localStartTime, int subStartTime, int subEndTime, int actionNumber, State state, boolean isDiveOver, Position lastPosition) {
        this.diveEntity = diveEntity;
        this.localStartTime = localStartTime;
        this.subStartTime = subStartTime;
        this.subEndTime = subEndTime;
        this.actionNumber = actionNumber;
        this.state = state;
        this.isDiveOver = isDiveOver;
        this.lastPosition = lastPosition;
    }

    public Dive construct(long startTime) {
        DiveEntity diveEntity = new DiveEntity(startTime, startTime);
        Position lastPosition = new Position();
        return new Dive(
                diveEntity,
                (int) startTime,
                0,
                0,
                0,
                State.OFF,
                false,
                lastPosition
        );
    }

    public void add(Position currentPos, BlockingQueue<Measure> measuresWaiting) {
        this.lastPosition.calculate(currentPos);
    }

    public void endDive() {
        this.isDiveOver = true;
        this.state = State.OFF;
    }

    public void startRecording(Timestamp timestamp) {
        this.state = State.RECORD;
    }

    public void stopRecording(Timestamp timestamp) {
        this.state = State.ON;
    }

    public void newMovement() {

    }

    public boolean isInitiated() {
        return
    }

    private enum State {
        OFF,
        ON,
        RECORD
    }
}
