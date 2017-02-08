package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.mavlink.messages.MAVLinkMessage;

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
    private long start;
    private long stop;

    /**
     * Constructeur qui initialise un FileManager, un entier de vitesse et un nom de simulation
     * @param filePathInput FileManager
     * @param speed valeur de la vitesse
     * @param simulationName Nom de la simulation
     */
    public Virtualizer(FileManager filePathInput, int speed, String simulationName){
        this.fileManager = filePathInput;
        this.speed = speed;
        this.simulationName = simulationName;
    }

    /**
     * Lance une simulation
     */
    public void start(){
        Objects.requireNonNull(fileManager);
        start = Time.from(Instant.now()).getTime()/1000; //Pour avoir un start en secondes
        List<VirtualizerEntry> entries=fileManager.readVirtualizedEntries();
        //TODO Intégrer le NetworkSender
        /*NetworkSender sender = new NetworkSender();
        sender.add(entries)
        //.forEach(x-> {
        //    MAVLinkMessage gpsMessage = x.getGPSMessage();
        //    MAVLinkMessage imuMessage = x.getIMUMessage();
        //    MAVLinkMessage pressureMessage = x.getPressureMessage();
        //    MAVLinkMessage temperatureMessage = x.getTemperatureMessage();
            //TODO Envoyer les messages et faire le wait
        });*/
        stop = Time.from(Instant.now()).getTime()/1000; //Pour avoir un stop en secondes
    }

    /**
     * Récupère la durée de la simulation
     * @return la durée
     */
    public long getDuration(){
        return getStop()-getStart();
    }

    /**
     * Attend 1 seconde divisée par la vitesse d'obtention de données
     * @param speed Vitesse d'obtention de données
     * @throws InterruptedException La fonction sleep a été interrompue
     */
    public void wait(int speed) throws InterruptedException{
        int sleepTime = 1000/speed;
        Thread.sleep(sleepTime);
    }

    //TODO Coder la fonction
    /*public int compare(FileManager sourceFile, FileManager errorFile, int errorAllowed){

    }*/


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
