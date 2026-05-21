package id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class SupirIdentityMapper {
    public UUID toSupirId(String supirEmail) {
        return UUID.nameUUIDFromBytes(supirEmail.getBytes(StandardCharsets.UTF_8));
    }
}
