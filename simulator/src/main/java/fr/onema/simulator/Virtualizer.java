package fr.onema.simulator;

import fr.onema.lib.file.FileManager;

import java.sql.Time;
import java.time.Instant;
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

    public Virtualizer(FileManager filePathInput, int speed, String simulationName){
        this.fileManager = filePathInput;
        this.speed = speed;
        this.simulationName = simulationName;
    }

    public void start(){
        Objects.requireNonNull(fileManager);
        start = Time.from(Instant.now()).getTime()/1000; //Pour avoir un start en secondes
        //List<VirtualizerEntry> entries=fileManager.readVirtualizedEntries();
        //TODO Refactor de ce code pour faire ce qui est nécessaire
        /*entries.forEach(x-> {
            MAVLinkMessage gpsMessage = x.getGPSMessage();
            MAVLinkMessage imuMessage = x.getIMUMessage();
            MAVLinkMessage pressureMessage = x.getPressureMessage();
            MAVLinkMessage temperatureMessage = x.getTemperatureMessage();
            //TODO Envoyer les messages et faire le wait
        });*/
        stop = Time.from(Instant.now()).getTime()/1000; //Pour avoir un stop en secondes
    }

    public long getDuration(){
        return getStop()-getStart();
    }

    public void wait(int speed) throws InterruptedException{
        int sleepTime = 1000/speed;
        Thread.sleep(sleepTime);
    }

    //TODO Coder la fonction
    /*public int compare(FileManager sourceFile, FileManager errorFile, int errorAllowed){

    }*/


    public FileManager getFileManager() {
        return fileManager;
    }

    public int getSpeed() {
        return speed;
    }

    public String getSimulationName() {
        return simulationName;
    }

    private long getStart() {
        return start;
    }

    private long getStop() {
        return stop;
    }
}
