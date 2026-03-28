package service.client.Capteurs;

import service.client.generated.axis2.ServiceFeuxStub;

public class CapteurFeux implements Runnable {
    private static final String[] ROUTE_IDS = {
        CapteurFlux.NORD,
        CapteurFlux.SUD,
        CapteurFlux.EST,
        CapteurFlux.OUEST
    };
    private static final String[]  NAMES    = {"Av Ibn Rochd", "Av Ibn Rochd", "Av Ma El Aynayne", "Av Ma El Aynayne"};
    private static final boolean[] SEGMENTS = {true, true, false, false};

    private ServiceFeuxStub stub;

    public CapteurFeux() {
        try {
            stub = new ServiceFeuxStub(); // connects to http://localhost:8081/ServiceFeux
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGreen(boolean segment) {
        return CapteurFlux.getIntersectionGreen() == segment;
    }

    @Override
    public void run() {
        try {
            int iteration = 0;
            while (true) {
                Thread.sleep(1000);

                int remaining = CapteurFlux.getTimeRemaining();
 
                ServiceFeuxStub.Feux[] feuxArray = new ServiceFeuxStub.Feux[4];
                for (int i = 0; i < 4; i++) {
                    ServiceFeuxStub.Feux f = new ServiceFeuxStub.Feux();
                    f.setName(NAMES[i]);
                    f.setSegment(SEGMENTS[i]);
                    f.setRemaining(remaining);
                    feuxArray[i] = f;

                    System.out.println("Feux " + NAMES[i] + " : "
                        + "[" + ROUTE_IDS[i] + "] "
                        + (isGreen(SEGMENTS[i]) ? "vert" : "rouge")
                        + " | temps restant: " + remaining + "s");
                }
 
                ServiceFeuxStub.SetFeux body = new ServiceFeuxStub.SetFeux();
                body.setList(feuxArray);

                // 3. Wrap in SetFeuxE envelope
                ServiceFeuxStub.SetFeuxE request = new ServiceFeuxStub.SetFeuxE();
                request.setSetFeux(body);

                // 4. Call the stub
                stub.setFeux(request);

                iteration++;
                System.out.println("iteration : " + iteration + "\n-------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
