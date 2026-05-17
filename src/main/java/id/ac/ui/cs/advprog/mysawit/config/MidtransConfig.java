package id.ac.ui.cs.advprog.mysawit.config;

import com.midtrans.Midtrans;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidtransConfig {

    @Value("${midtrans.server.key}")
    private String serverKey;

    @Value("${midtrans.client.key}")
    private String clientKey;

    @Value("${midtrans.is.production}")
    private boolean isProduction;

    @PostConstruct
    public void init() {
        setGlobalMidtransConfig(serverKey, clientKey, isProduction);
    }

    private static void setGlobalMidtransConfig(String server, String client, boolean isProd) {
        Midtrans.serverKey = server;
        Midtrans.clientKey = client;
        Midtrans.isProduction = isProd;
    }

}