package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import java.util.List;

public class OverlapValidator {

    private OverlapValidator() {}

    public static boolean isOverlapping(List<Coordinate> poly1,
                                        List<Coordinate> poly2) {
        // Cari batas min/max X dan Y untuk kebun pertama
        double minX1 = Double.MAX_VALUE;
        double maxX1 = -Double.MAX_VALUE;
        double minY1 = Double.MAX_VALUE;
        double maxY1 = -Double.MAX_VALUE;

        for (Coordinate c : poly1) {
            if (c.getX() < minX1) minX1 = c.getX();
            if (c.getX() > maxX1) maxX1 = c.getX();
            if (c.getY() < minY1) minY1 = c.getY();
            if (c.getY() > maxY1) maxY1 = c.getY();
        }

        // Cari batas min/max X dan Y untuk kebun kedua
        double minX2 = Double.MAX_VALUE;
        double maxX2 = -Double.MAX_VALUE;
        double minY2 = Double.MAX_VALUE;
        double maxY2 = -Double.MAX_VALUE;

        for (Coordinate c : poly2) {
            if (c.getX() < minX2) minX2 = c.getX();
            if (c.getX() > maxX2) maxX2 = c.getX();
            if (c.getY() < minY2) minY2 = c.getY();
            if (c.getY() > maxY2) maxY2 = c.getY();
        }

        // Cek apakah tidak overlap
        if (minX1 >= maxX2) return false; // kebun1 di kanan kebun2
        if (maxX1 <= minX2) return false; // kebun1 di kiri kebun2
        if (minY1 >= maxY2) return false; // kebun1 di atas kebun2
        if (maxY1 <= minY2) return false; // kebun1 di bawah kebun2

        // Tidak ada celah di sumbu manapun → overlap
        return true;
    }
}

