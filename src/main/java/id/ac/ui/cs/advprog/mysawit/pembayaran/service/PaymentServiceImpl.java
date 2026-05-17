package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public CheckoutResponse createSnapToken(CheckoutRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();

            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", request.getOrderId().toString());
            transactionDetails.put("gross_amount", request.getAmount().intValue());
            params.put("transaction_details", transactionDetails);

            Map<String, String> customerDetails = new HashMap<>();
            customerDetails.put("first_name", request.getCustomerName());
            customerDetails.put("email", request.getCustomerEmail());
            params.put("customer_details", customerDetails);

            JSONObject response = SnapApi.createTransaction(params);

            String token = response.getString("token");
            String redirectUrl = response.getString("redirect_url");

            return new CheckoutResponse(token, redirectUrl);

        } catch (MidtransError e) {
            logger.error("Midtrans specific error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Midtrans Snap Token: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during Midtrans API call: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during payment processing.", e);
        }
    }
}