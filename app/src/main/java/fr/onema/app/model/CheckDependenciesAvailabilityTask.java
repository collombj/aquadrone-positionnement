package fr.onema.app.model;

import fr.onema.app.view.RootLayoutController;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.TimerTask;

/**
 * Created by you on 14/02/2017.
 */

/***
 * Classe permettant la déclaration du scheduler chargé de requêter les dépendances
 */
public class CheckDependenciesAvailabilityTask extends TimerTask {
    private final RootLayoutController rlc;

    public CheckDependenciesAvailabilityTask(RootLayoutController rlc) {
        this.rlc = Objects.requireNonNull(rlc);
    }

    /***
     * Permet de mettre à jour la couleur des dépendances en fonction de leur état
     */
    @Override
    public void run() {
        if (Utils.checkPostgresAvailability(rlc.getConfiguration())) {
            rlc.updateDatabaseColor(Color.GREEN);
        } else {
            rlc.updateDatabaseColor(Color.RED);
        }
        if (Utils.checkMavlinkAvailability()) {
            rlc.updateMavlinkColor(Color.GREEN);
        } else {
            rlc.updateMavlinkColor(Color.RED);
        }
    }
}
