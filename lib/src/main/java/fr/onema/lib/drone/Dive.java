package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.worker.DatabaseWorker;

import java.util.ArrayList;
import java.util.List;

import static fr.onema.lib.drone.Dive.State.ON;
import static fr.onema.lib.drone.Dive.State.RECORD;

/**
 * Created by strock on 09/02/2017.
 */
public class Dive {

    private final List<Position> positions = new ArrayList<>();
    private final DatabaseWorker dbWorker = DatabaseWorker.getInstance();
    private DiveEntity diveEntity;
    private State state;
    private int numberOfmovement;
    private GPSCoordinate lastVitesse;
    private List<MeasureEntity> measures = new ArrayList<>();

    /**
     * @param timestamp
     */
    public Dive(long timestamp) {
        diveEntity = new DiveEntity();
        dbWorker.newDive(diveEntity);
        numberOfmovement = 0;
        state = ON;
    }

    /**
     * @param position
     */
    public void add(Position position) {
        //faire le calcul de position
        if(positions.isEmpty()) {
            position.calculate(null, lastVitesse);
        } else {
            position.calculate(positions.get(positions.size()-1), lastVitesse);
        }
        for (MeasureEntity measure : position.getMeasureEntities()) {
            dbWorker.insertMeasure(measure, diveEntity.getId(), 1);//FIXME changer l id de la mesure
            measures.add(measure);
        }
        positions.add(position);
    }

    /**
     *
     */
    public void endDive() {
        if (state == RECORD)
            dbWorker.stopRecording(System.currentTimeMillis(), diveEntity.getId());
        measures = GeoMaths.recalculatePosition(measures);
        for (MeasureEntity measure : measures) {
            dbWorker.updatePosition(measure.getId(),measure.getLocationCorrected(),measure.getPrecisionCm());
        }
    }

    /**
     * * @param timestamp
     */
    public void startRecording(long timestamp) {
        state = RECORD;
        dbWorker.startRecording(timestamp, diveEntity.getId());
    }

    /**
     * @param timestamp
     */
    public void stopRecording(long timestamp) {
        state = ON;
        dbWorker.stopRecording(timestamp, diveEntity.getId());
    }

    /**
     *
     */
    public void newMovement() {
        numberOfmovement++;
    }

    /**
     * Les différents états de la plongée
     */
    enum State {
        OFF,
        ON,
        RECORD
    }


}
