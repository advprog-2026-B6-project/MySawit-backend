package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import java.util.List;

public final class OverlapValidator {

    private OverlapValidator() {}

    public static boolean isOverlapping(List<Coordinate> firstPolygon,
                                        List<Coordinate> secondPolygon) {
        Bounds firstBounds = Bounds.from(firstPolygon);
        Bounds secondBounds = Bounds.from(secondPolygon);

        return firstBounds.minX() < secondBounds.maxX()
                && firstBounds.maxX() > secondBounds.minX()
                && firstBounds.minY() < secondBounds.maxY()
                && firstBounds.maxY() > secondBounds.minY();
    }

    private record Bounds(double minX, double maxX, double minY, double maxY) {
        private static Bounds from(List<Coordinate> polygon) {
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;

            for (Coordinate coordinate : polygon) {
                minX = Math.min(minX, coordinate.getX());
                maxX = Math.max(maxX, coordinate.getX());
                minY = Math.min(minY, coordinate.getY());
                maxY = Math.max(maxY, coordinate.getY());
            }

            return new Bounds(minX, maxX, minY, maxY);
        }
    }
}
