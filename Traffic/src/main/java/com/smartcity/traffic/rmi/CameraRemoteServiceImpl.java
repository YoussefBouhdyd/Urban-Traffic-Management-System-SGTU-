package com.smartcity.traffic.rmi;

import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.service.CameraSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CameraRemoteServiceImpl extends UnicastRemoteObject implements CameraRemoteService {
    private static final Logger logger = LoggerFactory.getLogger(CameraRemoteServiceImpl.class);
    private final CameraSimulationService simulationService;

    public CameraRemoteServiceImpl(CameraSimulationService simulationService) throws RemoteException {
        super();
        this.simulationService = simulationService;
        logger.info("CameraRemoteServiceImpl created");
    }

    @Override
    public void startSimulation() throws RemoteException {
        logger.info("RMI: Start simulation requested");
        simulationService.start();
    }

    @Override
    public void stopSimulation() throws RemoteException {
        logger.info("RMI: Stop simulation requested");
        simulationService.stop();
    }

    @Override
    public String getStatus() throws RemoteException {
        String status = simulationService.getStatus();
        logger.debug("RMI: Status requested - {}", status);
        return status;
    }

    @Override
    public CameraEvent getLastGeneratedEvent() throws RemoteException {
        CameraEvent event = simulationService.getLastGeneratedEvent();
        logger.debug("RMI: Last event requested - {}", event);
        return event;
    }
}
