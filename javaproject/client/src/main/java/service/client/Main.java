package service.client;

import java.util.Scanner;

import service.client.Capteurs.CapteurFeux;
import service.client.Capteurs.CapteurFlux;

public class Main {
    public static void main(String[] args) {
        new Thread(new CapteurFlux(10, 20, true,  CapteurFlux.NORD, "Av Ibn Rochd")).start();
        new Thread(new CapteurFlux(10, 15, true,  CapteurFlux.SUD, "Av Ibn Rochd")).start();
        new Thread(new CapteurFlux(10, 30, false, CapteurFlux.EST, "Av Ma El Aynayne")).start();
        new Thread(new CapteurFlux(10, 10, false, CapteurFlux.OUEST, "Av Ma El Aynayne")).start();
        new Thread(new CapteurFeux()).start();
       
        while(true)
        {
            Scanner scanner=new Scanner(System.in);
            int value=scanner.nextInt();
            CapteurFlux.setFeux(value);
        }
    }
}
