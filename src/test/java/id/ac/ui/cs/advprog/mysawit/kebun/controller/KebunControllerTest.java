package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.MandorInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.SupirInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunAssignmentService;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunSawitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class KebunControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KebunSawitService kebunService;

    @Mock
    private KebunAssignmentService assignmentService;

    @InjectMocks
    private KebunSawitController kebunSawitController;

    @InjectMocks
    private KebunAssignmentController kebunAssignmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(kebunSawitController, kebunAssignmentController).build();
    }

    private KebunSawit createValidKebun() {
        KebunSawit kebun = new KebunSawit();
        kebun.setId("test-id");
        kebun.setNamaKebun("Kebun Test");
        kebun.setKodeUnik("KB-0001");
        kebun.setLuasHektare(4.0);
        kebun.setKiriAtas(new Coordinate(0, 200));
        kebun.setKiriBawah(new Coordinate(0, 0));
        kebun.setKananAtas(new Coordinate(200, 200));
        kebun.setKananBawah(new Coordinate(200, 0));
        return kebun;
    }

    // =====================================================================
    // CRUD CONTROLLER TESTS
    // =====================================================================
    @Nested
    class CrudTests {
        @Test
        void createKebun_valid_returns201() throws Exception {
            KebunSawit kebun = createValidKebun();
            when(kebunService.create(any())).thenReturn(kebun);

            mockMvc.perform(post("/kebun")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kebun)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.kodeUnik").value("KB-0001"));
        }

        @Test
        void createKebun_invalid_returns400() throws Exception {
            when(kebunService.create(any())).thenThrow(new IllegalArgumentException("Format kode unik tidak valid"));

            mockMvc.perform(post("/kebun")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void getAll_returns200() throws Exception {
            KebunSawit kebun = createValidKebun();
            when(kebunService.findAll("", "")).thenReturn(List.of(kebun));

            mockMvc.perform(get("/kebun"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].kodeUnik").value("KB-0001"));
        }

        @Test
        void getByKodeUnik_found_returns200() throws Exception {
            KebunSawit kebun = createValidKebun();
            when(kebunService.findByKodeUnik("KB-0001")).thenReturn(Optional.of(kebun));

            mockMvc.perform(get("/kebun/KB-0001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.namaKebun").value("Kebun Test"));
        }

        @Test
        void getByKodeUnik_notFound_returns404() throws Exception {
            when(kebunService.findByKodeUnik("KB-9999")).thenReturn(Optional.empty());

            mockMvc.perform(get("/kebun/KB-9999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void update_valid_returns200() throws Exception {
            KebunSawit updated = createValidKebun();
            updated.setNamaKebun("Nama Baru");
            when(kebunService.update(eq("test-id"), any())).thenReturn(updated);

            mockMvc.perform(put("/kebun/test-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.namaKebun").value("Nama Baru"));
        }

        @Test
        void update_notFound_returns404() throws Exception {
            when(kebunService.update(eq("nonexistent"), any()))
                    .thenThrow(new IllegalArgumentException("Kebun tidak ditemukan dengan id: nonexistent"));

            mockMvc.perform(put("/kebun/nonexistent")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void update_overlap_returns400() throws Exception {
            when(kebunService.update(eq("test-id"), any()))
                    .thenThrow(new IllegalArgumentException("Kebun overlap dengan kebun: X"));

            mockMvc.perform(put("/kebun/test-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void delete_success_returns204() throws Exception {
            doNothing().when(kebunService).delete("test-id");

            mockMvc.perform(delete("/kebun/test-id"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void delete_notFound_returns404() throws Exception {
            doThrow(new IllegalArgumentException("Kebun tidak ditemukan dengan id: test-id"))
                    .when(kebunService).delete("test-id");

            mockMvc.perform(delete("/kebun/test-id"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void delete_mandorBound_returns409() throws Exception {
            doThrow(new IllegalArgumentException("Tidak dapat menghapus kebun yang masih memiliki Mandor"))
                    .when(kebunService).delete("test-id");

            mockMvc.perform(delete("/kebun/test-id"))
                    .andExpect(status().isConflict());
        }
    }

    // =====================================================================
    // DETAIL VIEW TESTS
    // =====================================================================
    @Nested
    class DetailTests {
        @Test
        void getDetail_success_returns200() throws Exception {
            KebunDetailResponse detail = new KebunDetailResponse(
                    "test-id", "Kebun Test", "KB-0001", 4.0,
                    new Coordinate(0, 200), new Coordinate(0, 0),
                    new Coordinate(200, 200), new Coordinate(200, 0),
                    new MandorInfo(10L, "Pak Mandor", "CERT-001"),
                    List.of(new SupirInfo(20L, "Supir Andi")));

            when(kebunService.getDetail(eq("test-id"), eq(""))).thenReturn(detail);

            mockMvc.perform(get("/kebun/detail/test-id"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mandor.fullname").value("Pak Mandor"))
                    .andExpect(jsonPath("$.supirList[0].fullname").value("Supir Andi"));
        }

        @Test
        void getDetail_notFound_returns404() throws Exception {
            when(kebunService.getDetail(eq("nonexistent"), any()))
                    .thenThrow(new IllegalArgumentException("Kebun tidak ditemukan"));

            mockMvc.perform(get("/kebun/detail/nonexistent"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getDetail_withSupirSearch_returns200() throws Exception {
            KebunDetailResponse detail = new KebunDetailResponse(
                    "test-id", "Kebun Test", "KB-0001", 4.0,
                    new Coordinate(0, 200), new Coordinate(0, 0),
                    new Coordinate(200, 200), new Coordinate(200, 0),
                    null,
                    List.of(new SupirInfo(20L, "Supir Andi")));

            when(kebunService.getDetail("test-id", "Andi")).thenReturn(detail);

            mockMvc.perform(get("/kebun/detail/test-id").param("searchSupir", "Andi"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.supirList").isArray());
        }
    }

    // =====================================================================
    // ASSIGNMENT CONTROLLER TESTS
    // =====================================================================
    @Nested
    class AssignmentTests {
        @Test
        void assignMandor_success_returns201() throws Exception {
            doNothing().when(assignmentService).assignMandor("kebun-1", 10L);

            mockMvc.perform(post("/kebun/kebun-1/mandor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("mandorId", 10))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void assignMandor_kebunNotFound_returns404() throws Exception {
            doThrow(new IllegalArgumentException("Kebun tidak ditemukan"))
                    .when(assignmentService).assignMandor("nonexistent", 10L);

            mockMvc.perform(post("/kebun/nonexistent/mandor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("mandorId", 10))))
                    .andExpect(status().isNotFound());
        }

        @Test
        void assignMandor_alreadyAssigned_returns409() throws Exception {
            doThrow(new IllegalArgumentException("Kebun sudah memiliki Mandor yang ditugaskan"))
                    .when(assignmentService).assignMandor("kebun-1", 10L);

            mockMvc.perform(post("/kebun/kebun-1/mandor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("mandorId", 10))))
                    .andExpect(status().isConflict());
        }

        @Test
        void assignMandor_missingMandorId_returns400() throws Exception {
            mockMvc.perform(post("/kebun/kebun-1/mandor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void reassignMandor_success_returns200() throws Exception {
            doNothing().when(assignmentService).reassignMandor(10L, "kebun-1", "kebun-2");

            Map<String, Object> body = Map.of(
                    "mandorId", 10,
                    "fromKebunId", "kebun-1",
                    "toKebunId", "kebun-2");

            mockMvc.perform(put("/kebun/mandor/reassign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void assignSupir_success_returns201() throws Exception {
            doNothing().when(assignmentService).assignSupir("kebun-1", 20L);

            mockMvc.perform(post("/kebun/kebun-1/supir")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("supirId", 20))))
                    .andExpect(status().isCreated());
        }

        @Test
        void reassignSupir_success_returns200() throws Exception {
            doNothing().when(assignmentService).reassignSupir(20L, "kebun-1", "kebun-2");

            Map<String, Object> body = Map.of(
                    "supirId", 20,
                    "fromKebunId", "kebun-1",
                    "toKebunId", "kebun-2");

            mockMvc.perform(put("/kebun/supir/reassign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk());
        }
    }
}
