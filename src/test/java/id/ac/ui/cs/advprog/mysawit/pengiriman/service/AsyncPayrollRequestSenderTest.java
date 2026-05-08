package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class AsyncPayrollRequestSenderTest {

    @Test
    void testSendPayrollRequestAsync() throws InterruptedException {
        PayrollRequestClient client = mock(PayrollRequestClient.class);
        CountDownLatch latch = new CountDownLatch(1);
        ArgumentCaptor<id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest> captor =
                ArgumentCaptor.forClass(id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest.class);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(client).sendPayrollRequest(captor.capture());

        AsyncPayrollRequestSender sender = new AsyncPayrollRequestSender(client);
        Pengiriman pengiriman = Pengiriman.builder()
                .id(UUID.randomUUID())
                .supirTrukId(UUID.randomUUID())
                .mandorId(1L)
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .status(StatusPengiriman.DISETUJUI)
                .build();

        sender.sendPayrollRequest(pengiriman);

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(pengiriman.getId(), captor.getValue().getPengirimanId());
        assertEquals(pengiriman.getSupirTrukId(), captor.getValue().getSupirTrukId());
    }
}