package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.stereotype.Component;

@Component
public class KebunGeometry {

    private static final double EPSILON = 0.000001;
    private static final double SQUARE_METERS_PER_HECTARE = 10_000.0;

    public boolean isAxisAlignedSquare(KebunSawit kebun) {
        double leftX = kebun.getKiriAtas().getX();
        double rightX = kebun.getKananAtas().getX();
        double topY = kebun.getKiriAtas().getY();
        double bottomY = kebun.getKiriBawah().getY();

        boolean verticalSides = nearlyEqual(leftX, kebun.getKiriBawah().getX())
                && nearlyEqual(rightX, kebun.getKananBawah().getX());
        boolean horizontalSides = nearlyEqual(topY, kebun.getKananAtas().getY())
                && nearlyEqual(bottomY, kebun.getKananBawah().getY());

        double width = rightX - leftX;
        double height = topY - bottomY;

        return verticalSides
                && horizontalSides
                && width > EPSILON
                && height > EPSILON
                && nearlyEqual(width, height);
    }

    public double calculateHectares(KebunSawit kebun) {
        double width = kebun.getKananAtas().getX() - kebun.getKiriAtas().getX();
        double height = kebun.getKiriAtas().getY() - kebun.getKiriBawah().getY();
        return Math.abs(width * height) / SQUARE_METERS_PER_HECTARE;
    }

    private boolean nearlyEqual(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }
}
