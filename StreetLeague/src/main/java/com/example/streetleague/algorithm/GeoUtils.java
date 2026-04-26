package com.example.streetleague.algorithm;

import org.springframework.stereotype.Component;

@Component
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    public  static final double MAX_DISTANCE_KM = 50.0;

    public double haversineKm(String coordsA, String coordsB) {
        double[] a = parse(coordsA);
        double[] b = parse(coordsB);
        double dLat = Math.toRadians(b[0] - a[0]);
        double dLon = Math.toRadians(b[1] - a[1]);
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(a[0]))
                * Math.cos(Math.toRadians(b[0]))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }

    public double scoreDistance(String coordsA, String coordsB) {
        if (!isGpsCoords(coordsA) || !isGpsCoords(coordsB)) return 0.5; // neutre si absent ou non-GPS
        double km = haversineKm(coordsA, coordsB);
        return Math.max(0.0, 1.0 - km / MAX_DISTANCE_KM);
    }

    public boolean isGpsCoords(String coords) {
        if (coords == null || coords.isBlank()) return false;
        try {
            String[] parts = coords.split(",");
            if (parts.length != 2) return false;
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());
            return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double[] parse(String coords) {
        String[] parts = coords.split(",");
        return new double[]{
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim())
        };
    }
}