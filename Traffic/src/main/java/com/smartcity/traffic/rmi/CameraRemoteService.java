package com.smartcity.traffic.rmi;

import com.smartcity.traffic.model.CameraEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CameraRemoteService extends Remote {
    /**
     * Start the camera simulation
     */
    void startSimulation() throws RemoteException;

    /**
     * Stop the camera simulation
     */
    void stopSimulation() throws RemoteException;

    /**
     * Get the current status of the camera simulator
     * @return Status string (RUNNING, STOPPED, ERROR)
     */
    String getStatus() throws RemoteException;

    /**
     * Get the last generated camera event
     * @return Last CameraEvent or null if none generated yet
     */
    CameraEvent getLastGeneratedEvent() throws RemoteException;
}
