package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverlapValidatorTest {

    // Helper: buat persegi dari titik kiri-bawah (x, y) dengan sisi 'size'
    private List<Coordinate> square(double x, double y, double size) {
        return List.of(
                new Coordinate(x, y),
                new Coordinate(x + size, y),
                new Coordinate(x + size, y + size),
                new Coordinate(x, y + size)
        );
    }

    // === 1. Dua persegi identik (persis overlap) ===
    @Test
    void testIdenticalSquares_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 200);
        assertTrue(OverlapValidator.isOverlapping(a, a));
    }

    // === 2. Dua persegi sebagian overlap ===
    @Test
    void testPartialOverlap_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(50, 50, 100);
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    // === 3. Bersebelahan (sharing satu edge, menyentuh) → tidak overlap ===
    @Test
    void testTouchingEdge_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(100, 0, 100); // tepat di kanan
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 4. Berjauhan (tidak overlap sama sekali) ===
    @Test
    void testFarApart_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(500, 500, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 5. Satu persegi di dalam persegi lain (contained) ===
    @Test
    void testContained_shouldOverlap() {
        List<Coordinate> big = square(0, 0, 200);
        List<Coordinate> small = square(50, 50, 50);
        assertTrue(OverlapValidator.isOverlapping(big, small));
    }

    // === 6. Contained terbalik (small dulu, big kedua) ===
    @Test
    void testContainedReversed_shouldOverlap() {
        List<Coordinate> big = square(0, 0, 200);
        List<Coordinate> small = square(50, 50, 50);
        assertTrue(OverlapValidator.isOverlapping(small, big));
    }

    // === 7. Kebun1 di kanan kebun2 (tidak overlap) ===
    @Test
    void testKebun1RightOfKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(200, 0, 100);
        List<Coordinate> b = square(0, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 8. Kebun1 di kiri kebun2 (tidak overlap) ===
    @Test
    void testKebun1LeftOfKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(200, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 9. Kebun1 di atas kebun2 (tidak overlap) ===
    @Test
    void testKebun1AboveKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 200, 100);
        List<Coordinate> b = square(0, 0, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 10. Kebun1 di bawah kebun2 (tidak overlap) ===
    @Test
    void testKebun1BelowKebun2_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(0, 200, 100);
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 11. Menyentuh di satu titik (corner touching) → tidak overlap ===
    @Test
    void testTouchingCorner_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(100, 100, 100); // menyentuh di titik (100,100)
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 12. Overlap sangat kecil (1 meter) ===
    @Test
    void testTinyOverlap_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(99, 0, 100); // overlap 1 meter di X
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    // === 13. Menyentuh edge di sumbu Y → tidak overlap ===
    @Test
    void testTouchingEdgeVertical_shouldNotOverlap() {
        List<Coordinate> a = square(0, 0, 100);
        List<Coordinate> b = square(0, 100, 100); // tepat di atas
        assertFalse(OverlapValidator.isOverlapping(a, b));
    }

    // === 14. Kebun dengan ukuran berbeda, overlap ===
    @Test
    void testDifferentSizes_shouldOverlap() {
        List<Coordinate> a = square(0, 0, 300);
        List<Coordinate> b = square(100, 100, 50);
        assertTrue(OverlapValidator.isOverlapping(a, b));
    }

    // === 15. Private constructor coverage ===
    @Test
    void testConstructorIsPrivate() throws Exception {
        var constructor = OverlapValidator.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
