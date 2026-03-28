package service.client.Capteurs;

import service.client.generated.IServiceFlux;
import service.client.generated.ServiceFlux;
import service.client.jaxws.ServiceFeux_Service;

public class CapteurFlux implements Runnable {
    public static final String NORD = "nord";
    public static final String SUD = "sud";
    public static final String EST = "est";
    public static final String OUEST = "ouest";

    private static int feux = 10;
    private static boolean intersectionGreen = false;
    private static int stop = 0;
    private static int printCount = 0;
    private int flux;
    private boolean segment;
    private String name;
    private String displayName;
    private final int maxCapacity = 150;
    private IServiceFlux serviceStub;
    private service.client.jaxws.ServiceFeux feuxControlStub;

    public CapteurFlux(int feux, int flux, boolean segment, String name, String displayName) {
        this.flux = flux;
        CapteurFlux.feux = feux;
        this.segment = segment;
        this.name = name;
        this.displayName = displayName;
    }

    public CapteurFlux(int feux, int flux, boolean segment, String name) {
        this(feux, flux, segment, name, name);
    }

    public CapteurFlux(int flux, boolean segment, String name, String displayName) {
        this.flux = flux;
        this.segment = segment;
        this.name = name;
        this.displayName = displayName;
    }

    public CapteurFlux(int flux, boolean segment, String name) {
        this(flux, segment, name, name);
    }

    public CapteurFlux() {
        this.flux = 10;
        this.segment = true;
        this.name = "route";
        this.displayName = "route";
    }
    public static synchronized int getTimeRemaining() {
        return (feux * 2) - stop;
    }
    private boolean isGreen() {
        return intersectionGreen == segment;
    }

    @Override
    public void run() {
        try {
            int publishCounter = 0;
            serviceStub = new ServiceFlux().getServiceFluxPort();
            feuxControlStub = new ServiceFeux_Service().getServiceFeuxPort();
            while (true) {
                Thread.sleep(1000);
                int remoteFeux = readRemoteFeux();
                synchronized (CapteurFlux.class) {
                    applyRemoteControl(remoteFeux);
                    if (drivesClock()) {
                        stop += 1;
                    }
                    if (stop >= feux*2) {
                        stop = 0;
                        intersectionGreen = !intersectionGreen;
                    }
                    if (isGreen()) {
                        alterflux(true, false, true);
                    } else {
                        alterflux(false, true, true);
                    }

                    publishCounter++;
                    if (publishCounter >= 5) {
                        serviceStub.sendFlux(flux, name);
                        publishCounter = 0;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public static synchronized boolean getIntersectionGreen() { return intersectionGreen; }
    public static synchronized int getFeux() { return feux; }

    public void alterflux(boolean sortie, boolean arrivee, boolean neutre) {
        if (sortie) {
            flux -= (int)(Math.random() * 5) + 1;
        }
        if (arrivee) {
            flux += (int)(Math.random() * 3) + 1;
        }
        if (neutre) {
            int random = (int)(Math.random() * 1000) % 2;
            if (random == 0) flux += 1;
            else flux -= 1;
        }

        if (flux < 0) flux = 0;
        if (flux > maxCapacity) flux = maxCapacity;

        System.out.println(this.toString());
        printCount++;
    }
    public static synchronized void setFeux(int i)
    {
        feux=i;
        if (stop > feux * 2) {
            stop = 0;
        }
    }

    private int readRemoteFeux() {
        try {
            return feuxControlStub.getFeuxTemp(name);
        } catch (Exception e) {
            System.out.println("[CapteurFlux] Control sync error for " + name + ": " + e.getMessage());
            return 0;
        }
    }

    private void applyRemoteControl(int remoteFeux) {
        if (remoteFeux == 0) {
            return;
        }

        int remoteDuration = Math.max(1, Math.abs(remoteFeux));
        boolean routeGreen = remoteFeux > 0;
        boolean nextIntersectionGreen = routeGreen ? segment : !segment;

        if (feux != remoteDuration || intersectionGreen != nextIntersectionGreen) {
            feux = remoteDuration;
            intersectionGreen = nextIntersectionGreen;
            stop = 0;
        }
    }

    private boolean drivesClock() {
        return NORD.equalsIgnoreCase(name);
    }

    public String toString() {
        return "Flux dans route " + this.displayName + " [" + this.name + "] Contient: "
            + this.flux + " avec etat de feux : " + (isGreen() ? "vert" : "rouge");
    }
}
