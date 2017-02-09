package fr.onema.simulator;

import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.network.NetworkSender;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jérôme on 08/02/2017.
 */

public class Virtualizer {
    private FileManager fileManager;
    private int speed;
    private String simulationName;
    private int port;
    private String host;
    private long start;
    private long stop;

    /**
     * Constructeur qui initialise un FileManager, un entier de vitesse et un nom de simulation
     * @param filePathInput FileManager
     * @param speed valeur de la vitesse
     * @param simulationName Nom de la simulation
     * @param host Adresse de l'hôte
     * @param port Port sur lequel on se connecte à l'hôte
     */
    public Virtualizer(FileManager filePathInput, int speed, String simulationName, String host, int port){
        Objects.requireNonNull(filePathInput);
        Objects.requireNonNull(speed);
        Objects.requireNonNull(simulationName);
        Objects.requireNonNull(host);
        Objects.requireNonNull(port);

        this.fileManager = filePathInput;
        this.speed = speed;
        this.simulationName = simulationName;
        this.host = host;
        this.port = port;
    }

    /**
     * Lance une simulation
     */
    public void start(){
        Objects.requireNonNull(fileManager);
        start = Time.from(Instant.now()).getTime()/1000; //Pour avoir un start en secondes
        List<VirtualizerEntry> entries=fileManager.readVirtualizedEntries();
        NetworkSender sender = new NetworkSender(port, host);
        entries.forEach(x-> {
            sender.add(x);
            try{
                wait(speed);
            }catch(InterruptedException e){
                //Do nothing
            }
        });
        stop = Time.from(Instant.now()).getTime()/1000; //Pour avoir un stop en secondes
    }

    /**
     * Récupère la durée de la simulation
     * @return la durée
     */
    public long getDuration(){
        Objects.requireNonNull(getStart());
        Objects.requireNonNull(getStop());
        return getStop()-getStart();
    }

    /**
     * Attend 1 seconde divisée par la vitesse d'obtention de données
     * @param speed Vitesse d'obtention de données
     * @throws InterruptedException La fonction sleep a été interrompue
     */
    private void wait(int speed) throws InterruptedException{
        int sleepTime = 1000/speed;
        Thread.sleep(sleepTime);
    }

    public void compare(FileManager sourceFile, FileManager errorFile, Configuration config, int errorAllowed)
            throws SQLException, ClassNotFoundException {
        Objects.requireNonNull(sourceFile);
        Objects.requireNonNull(errorFile);
        Objects.requireNonNull(config);
        Objects.requireNonNull(errorAllowed);

        DatabaseDriver driver = DatabaseDriver.build(config);
        driver.initAsReadable();
        List<ReferenceEntry> listRefEntry = sourceFile.readReferenceEntries();
        MeasureRepository repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryReadable(config);
        DiveEntity dive = repository.getLastDive();
        List<MeasureEntity> listMeasures = driver.getMeasureFrom(dive);
        listMeasures.forEach(x ->
            listRefEntry.forEach(y -> {
                if (x.getTimestamp() == y.getTimestamp()) {
                    int realLat = y.getLat();
                    int realLon = y.getLon();
                    int realAlt = y.getAlt();
                    GPSCoordinate realPoint = new GPSCoordinate(realLat, realLon, realAlt);
                    GPSCoordinate calculatedPoint = x.getLocationCorrected();
                    double distance = GeoMaths.gpsDistance(realPoint, calculatedPoint);
                    /*if (distance > errorAllowed) {
                        //TODO Sauvegarder dans errorFile
                    }*/
                }
            })
        );
    }


    /**
     * Récupère le FileManager
     *
     * @return fileManager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Récupère le vitesse d'obtention de données
     *
     * @return speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Récupère le nom de la simulation
     *
     * @return simulationName
     */
    public String getSimulationName() {
        return simulationName;
    }

    /**
     * Récupère le temps de départ
     *
     * @return start
     */
    private long getStart() {
        return start;
    }

    /**
     * Récupère le temps de fin
     *
     * @return stop
     */
    private long getStop() {
        return stop;
    }
}
