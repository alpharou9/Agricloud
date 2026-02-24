package esprit.farouk.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class SMSUtils {

    // ---------------------------------------------------------------
    // Twilio credentials — replace with your own from twilio.com/console
    // Trial accounts can only send to verified numbers.
    // ---------------------------------------------------------------
    private static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String AUTH_TOKEN  = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String FROM_NUMBER = "+12345678901"; // Your Twilio number

    private static final String API_URL =
            "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";

    /**
     * Sends an SMS to the customer's shipping phone when their order status changes.
     *
     * @param toPhone     Customer's phone number (international format e.g. +21655123456)
     * @param orderId     Order ID
     * @param newStatus   New order status
     * @param productName Product name
     * @param total       Order total price
     */
    public static void sendOrderStatusUpdate(String toPhone, long orderId,
                                             String newStatus, String productName,
                                             double total) throws Exception {
        if (toPhone == null || toPhone.isBlank()) throw new Exception("No phone number for this order.");

        // Ensure international format — prepend +216 for Tunisia if needed
        String phone = toPhone.trim();
        if (!phone.startsWith("+")) phone = "+216" + phone;

        String emoji = switch (newStatus) {
            case "confirmed"  -> "✅";
            case "processing" -> "⚙️";
            case "shipped"    -> "🚚";
            case "delivered"  -> "📦";
            case "cancelled"  -> "❌";
            default           -> "🔔";
        };

        String body = emoji + " AgriCloud Order Update\n" +
                "Order #" + orderId + " — " + productName + "\n" +
                "Status: " + newStatus.toUpperCase() + "\n" +
                "Total: $" + String.format("%.2f", total) + "\n" +
                (newStatus.equals("shipped") || newStatus.equals("confirmed")
                        ? "Estimated delivery: within 3 business days." : "");

        String params = "To="   + URLEncoder.encode(phone,       "UTF-8") +
                       "&From=" + URLEncoder.encode(FROM_NUMBER, "UTF-8") +
                       "&Body=" + URLEncoder.encode(body,        "UTF-8");

        String credentials = Base64.getEncoder()
                .encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes("UTF-8"));

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + credentials);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        if (status != 200 && status != 201) {
            BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = err.readLine()) != null) sb.append(line);
            err.close();
            throw new Exception("Twilio returned HTTP " + status + ": " + sb);
        }
    }
}
