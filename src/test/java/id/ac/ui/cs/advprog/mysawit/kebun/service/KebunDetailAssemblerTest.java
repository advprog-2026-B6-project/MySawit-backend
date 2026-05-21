package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.*;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunDetailAssemblerTest {

    @Mock
    private KebunSawitRepository repository;

    @Mock
    private KebunAssignmentRepository assignmentRepository;

    @Mock
    private KebunUserReader userReader;

    private KebunDetailAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new KebunDetailAssembler(
                repository, assignmentRepository, userReader, new KebunResponseMapper());
    }

    private KebunSawit createKebun() {
        KebunSawit kebun = new KebunSawit();
        kebun.setId("id-1");
        kebun.setNamaKebun("Kebun Test");
        kebun.setKodeUnik("KB-0001");
        kebun.setLuasHektare(4.0);
        kebun.setKiriAtas(new Coordinate(0, 200));
        kebun.setKiriBawah(new Coordinate(0, 0));
        kebun.setKananAtas(new Coordinate(200, 200));
        kebun.setKananBawah(new Coordinate(200, 0));
        return kebun;
    }

    @Test
    void getDetail_kebunNotFound_shouldThrow() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());
        assertThrows(KebunNotFoundException.class,
                () -> assembler.getDetail("nonexistent", null));
    }

    @Test
    void getDetail_withMandorAndSupirs_shouldReturnFullDetail() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.of(10L));
        when(userReader.findUserById(10L)).thenReturn(Optional.of(
                new UserSnapshot(10L, "Pak Mandor", "mandor1", Role.MANDOR, "CERT-001")));
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L));
        when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                new UserSnapshot(20L, "Supir Andi", "supir1", Role.SUPIR, null)));

        KebunDetailResponse detail = assembler.getDetail("id-1", null);

        assertEquals("KB-0001", detail.getKodeUnik());
        assertNotNull(detail.getMandor());
        assertEquals("Pak Mandor", detail.getMandor().getFullname());
        assertEquals(1, detail.getSupirList().size());
    }

    @Test
    void getDetail_noMandorNoSupirs_shouldReturnNulls() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of());

        KebunDetailResponse detail = assembler.getDetail("id-1", null);

        assertNull(detail.getMandor());
        assertTrue(detail.getSupirList().isEmpty());
    }

    @Test
    void getDetail_mandorIdFoundButUserNotFound_shouldReturnNullMandor() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.of(10L));
        when(userReader.findUserById(10L)).thenReturn(Optional.empty());
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of());

        KebunDetailResponse detail = assembler.getDetail("id-1", null);
        assertNull(detail.getMandor());
    }

    @Test
    void getDetail_filterSupirByName_shouldFilter() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L, 21L));
        when(userReader.findUsersByIds(List.of(20L, 21L))).thenReturn(List.of(
                new UserSnapshot(20L, "Andi Supir", "supir1", Role.SUPIR, null),
                new UserSnapshot(21L, "Budi Driver", "supir2", Role.SUPIR, null)));

        KebunDetailResponse detail = assembler.getDetail("id-1", "Andi");

        assertEquals(1, detail.getSupirList().size());
        assertEquals("Andi Supir", detail.getSupirList().get(0).getFullname());
    }

    @Test
    void getDetail_filterSupirByEmptyString_shouldReturnAll() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L));
        when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                new UserSnapshot(20L, "Andi", "supir1", Role.SUPIR, null)));

        KebunDetailResponse detail = assembler.getDetail("id-1", "");
        assertEquals(1, detail.getSupirList().size());
    }

    @Test
    void getDetail_supirWithNullFullname_shouldBeFilteredOut() {
        when(repository.findById("id-1")).thenReturn(Optional.of(createKebun()));
        when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
        when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L));
        when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                new UserSnapshot(20L, null, "supir1", Role.SUPIR, null)));

        KebunDetailResponse detail = assembler.getDetail("id-1", "search");
        assertTrue(detail.getSupirList().isEmpty());
    }
}
