package com.iss.iss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class IssScheduler {

    @Autowired
    private RestTemplate restTemplate;

    private static List<Map> points;

    public static final double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    @PostConstruct
    public void init() {
        points = new LinkedList<>();
    }

    @Scheduled(fixedDelay = 10000)
    public void calculate() {
        Map response = restTemplate.getForObject("http://api.open-notify.org/iss-now.json", Map.class);
        Map<String, String> issPosition = (Map<String, String>) response.get("iss_position");
        Double longitudeNow = Double.valueOf(issPosition.get("longitude"));
        Double latitudeNow = Double.valueOf(issPosition.get("latitude"));
        Map<String, Double> point = new HashMap<>();
        point.put("longitude", longitudeNow);
        point.put("latitude", latitudeNow);

        if (points.size() > 0) {
            Map<String, Double> lastPoint = points.get(points.size() - 1);
            Double longitudeLast = lastPoint.get("longitude");
            Double latitudeLast = lastPoint.get("latitude");

            System.out.println("1 Point longitude " + longitudeNow);
            System.out.println("1 Point latitude " + latitudeNow);
            System.out.println("2 Point longitude " + longitudeLast);
            System.out.println("2 Point latitude " + latitudeLast);
            System.out.println("Speed " + this.countDistance(latitudeNow, longitudeNow, latitudeLast, longitudeLast) + "km/10sek");
            System.out.println("------------------------------------------------------------------");

            Map<String, Double> fisrtPoint = points.get(0);
            Double longitudeFirst = fisrtPoint.get("longitude");
            Double latitudeFirst = fisrtPoint.get("latitude");

            //System.out.println("1 Point longitude " + longitudeNow);
            //System.out.println("1 Point latitude " + latitudeNow);
            //System.out.println("2 Point longitude " + longitudeFirst);
            //System.out.println("2 Point latitude " + latitudeFirst);
            //System.out.println("Distance " + this.countDistance(latitudeNow, longitudeNow, latitudeFirst, longitudeFirst));
            //System.out.println("------------------------------------------------------------------");
            System.out.println("Last Point longitude " + longitudeNow);
            System.out.println("Last Point latitude " + latitudeNow);
            System.out.println("First Point longitude " + longitudeFirst);
            System.out.println("First Point latitude " + latitudeFirst);
            System.out.println("Distance " + this.countDistance(latitudeNow, longitudeNow, latitudeFirst, longitudeFirst));
            System.out.println("------------------------------------------------------------------");
        }

        points.add(point);
    }

    private Long countDistance(double latitudeNow, double longitudeNow, double latitudeLast, double longitudeLast) {
        double latDistance = Math.toRadians(latitudeNow - latitudeLast);
        double lngDistance = Math.toRadians(longitudeNow - longitudeLast);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitudeNow)) * Math.cos(Math.toRadians(latitudeLast))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c);
    }


}
