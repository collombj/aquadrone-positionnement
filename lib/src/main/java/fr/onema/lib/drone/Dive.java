package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.CartesianVelocity;
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
    private CartesianVelocity lastVitesse;
    private List<MeasureEntity> measures = new ArrayList<>();

    /**
     * Crée une nouvelle Dive
     *
     * @param timestamp l'heure de début de la plongée
     */
    public Dive(long timestamp) {
        diveEntity = new DiveEntity();
        dbWorker.newDive(diveEntity);
        numberOfmovement = 0;
        state = ON;
    }

    /**
     * Ajoute une position à la plongée
     *
     * @param position une position
     */
    public void add(Position position) {
        // si c'est le premier point
        if (positions.isEmpty()) {
            position.calculate(null, lastVitesse);
        } else {//si ce n'est pas le premier point
            position.calculate(positions.get(positions.size() - 1), lastVitesse);
        }
        for (MeasureEntity measure : position.getMeasureEntities()) {
            dbWorker.insertMeasure(measure, diveEntity.getId(), 1);//FIXME changer l id de la mesure
            measures.add(measure);
        }
        positions.add(position);
    }

    /**
     * Termine la plongée
     */
    public void endDive() {
        if (state == RECORD)
            dbWorker.stopRecording(System.currentTimeMillis(), diveEntity.getId());
        measures = GeoMaths.recalculatePosition(measures);
        for (MeasureEntity measure : measures) {
            dbWorker.updatePosition(measure.getId(), measure.getLocationCorrected(), measure.getPrecisionCm());
        }
    }

    /**
     * Termine l enregistrement de la plongée
     * * @param timestamp le debut de l enregistrement
     */
    public void startRecording(long timestamp) {
        state = RECORD;
        dbWorker.startRecording(timestamp, diveEntity.getId());
    }

    /**
     * Comment l'enregistrement de la plongée
     * @param timestamp la fin de l'enregistrement
     */
    public void stopRecording(long timestamp) {
        state = ON;
        dbWorker.stopRecording(timestamp, diveEntity.getId());
    }

    /**
     * Incremente le nombre de mouvements effectués
     */
    public void newMovement() {
        numberOfmovement++;
    }


    /**
     * @return le nombre de mouvements
     */
    public int getNumberOfmovement() {
        return numberOfmovement;
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
