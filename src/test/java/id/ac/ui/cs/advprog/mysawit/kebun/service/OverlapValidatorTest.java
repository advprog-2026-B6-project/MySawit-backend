package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverlapValidatorTest {

    private List<Coordinate> square(double x, double y, double size) {
        return List.of(
                new Coordinate(x, y),
                new Coordinate(x + size, y),
                new Coordinate(x + size, y + size),
                new Coordinate(x, y + size)
        );
    }

    @Test
    void testIdenticalSquares_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 200);
        assertTrue(OverlapValidator.isOverlapping(a, a));
    }

    @Test
    void testPartialOverlap_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(50, 50, 100);
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testTouchingEdge_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(100, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testFarApart_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(500, 500, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testContained_shouldOverlap() {
        List<Coordinate> big = square(0, 0, 200);
        List<Coordinate> small = square(50, 50, 50);
        assertTrue(OverlapValidator.isOverlapping(big, small));
    }

    @Test
    void testContainedReversed_shouldOverlap() {
        List<Coordinate> big = square(0, 0, 200);
        List<Coordinate> small = square(50, 50, 50);
        assertTrue(OverlapValidator.isOverlapping(small, big));
    }

    @Test
    void testKebun1RightOfKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(200, 0, 100);
        List<Coordinate> b = square(0, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testKebun1LeftOfKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(200, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testKebun1AboveKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 200, 100);
        List<Coordinate> b = square(0, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testKebun1BelowKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(0, 200, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testTouchingCorner_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(100, 100, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testTinyOverlap_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(99, 0, 100);
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testTouchingEdgeVertical_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(0, 100, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testDifferentSizes_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 300);
        List<Coordinate> b = square(100, 100, 50);
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testNegativeCoordinatesOverlap_shouldOverlap() {
        List<Coordinate> a = square(-200, -200, 150);
        List<Coordinate> b = square(-100, -100, 150);

        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testNegativeCoordinatesNoOverlap_shouldNotOverlap() {
        List<Coordinate> a = square(-300, -300, 100);
        List<Coordinate> b = square(-100, -100, 100);

        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    @Test
    void testZeroWidthPolygon_shouldNotOverlapOrBeRejectedByHigherValidator() {
        List<Coordinate> zeroWidth = List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 100),
                new Coordinate(0, 100),
                new Coordinate(0, 0)
        );
        List<Coordinate> square = square(0, 0, 100);

        assertFalse(OverlapValidator.isOverlapping(zeroWidth, square));
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        var constructor = OverlapValidator.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
