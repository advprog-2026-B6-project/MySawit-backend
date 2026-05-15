package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KebunGeometryTest {

    private KebunGeometry geometry;

    @BeforeEach
    void setUp() {
        geometry = new KebunGeometry();
    }

    private KebunSawit makeKebun(double kiriAtasX, double kiriAtasY,
                                  double kiriBawahX, double kiriBawahY,
                                  double kananAtasX, double kananAtasY,
                                  double kananBawahX, double kananBawahY) {
        KebunSawit kebun = new KebunSawit();
        kebun.setKiriAtas(new Coordinate(kiriAtasX, kiriAtasY));
        kebun.setKiriBawah(new Coordinate(kiriBawahX, kiriBawahY));
        kebun.setKananAtas(new Coordinate(kananAtasX, kananAtasY));
        kebun.setKananBawah(new Coordinate(kananBawahX, kananBawahY));
        return kebun;
    }

    @Nested
    class IsAxisAlignedSquareTests {

        @Test
        void validSquare_shouldReturnTrue() {
            // 200x200 square at origin
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 200, 200, 200, 0);
            assertTrue(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void validSquare_negativeCoords_shouldReturnTrue() {
            // 100x100 square at (-100, -100)
            KebunSawit kebun = makeKebun(-100, 0, -100, -100, 0, 0, 0, -100);
            assertTrue(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void rectangle_shouldReturnFalse() {
            // 200 wide, 100 tall — not a square
            KebunSawit kebun = makeKebun(0, 100, 0, 0, 200, 100, 200, 0);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void rhombus_shouldReturnFalse() {
            // Sides not axis-aligned
            KebunSawit kebun = makeKebun(0, 80, 60, 0, 100, 80, 160, 0);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void zeroSizeSquare_shouldReturnFalse() {
            // All corners at same point
            KebunSawit kebun = makeKebun(5, 5, 5, 5, 5, 5, 5, 5);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void leftSidesNotAligned_shouldReturnFalse() {
            // kiriAtas.x != kiriBawah.x
            KebunSawit kebun = makeKebun(0, 200, 1, 0, 200, 200, 200, 0);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void rightSidesNotAligned_shouldReturnFalse() {
            // kananAtas.x != kananBawah.x
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 200, 200, 201, 0);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void topSidesNotAligned_shouldReturnFalse() {
            // kiriAtas.y != kananAtas.y
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 200, 201, 200, 0);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }

        @Test
        void bottomSidesNotAligned_shouldReturnFalse() {
            // kiriBawah.y != kananBawah.y
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 200, 200, 200, 1);
            assertFalse(geometry.isAxisAlignedSquare(kebun));
        }
    }

    @Nested
    class CalculateHectaresTests {

        @Test
        void standardSquare_shouldCalculateCorrectly() {
            // 200x200 = 40000 m2 = 4.0 hectares
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 200, 200, 200, 0);
            assertEquals(4.0, geometry.calculateHectares(kebun), 0.001);
        }

        @Test
        void largeSquare_shouldCalculateCorrectly() {
            // 1000x1000 = 1_000_000 m2 = 100 hectares
            KebunSawit kebun = makeKebun(0, 1000, 0, 0, 1000, 1000, 1000, 0);
            assertEquals(100.0, geometry.calculateHectares(kebun), 0.001);
        }

        @Test
        void rectangle_shouldCalculateArea() {
            // 300 wide x 200 tall = 60000 m2 = 6.0 hectares
            KebunSawit kebun = makeKebun(0, 200, 0, 0, 300, 200, 300, 0);
            assertEquals(6.0, geometry.calculateHectares(kebun), 0.001);
        }

        @Test
        void negativeCoords_shouldUseAbsoluteArea() {
            // (-100, 0) to (0, -100) => width=100, height=100 => 10000m2 => 1.0 ha
            KebunSawit kebun = makeKebun(-100, 0, -100, -100, 0, 0, 0, -100);
            assertEquals(1.0, geometry.calculateHectares(kebun), 0.001);
        }
    }
}
