package org.book.bookshop.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPGetter {
    private static final Logger log = LoggerFactory.getLogger(IPGetter.class);

    private static final String[] services = {
            "https://checkip.amazonaws.com",
            "https://ipv4.icanhazip.com",
            "https://myexternalip.com/raw",
            "https://www.trackip.net/ip",
            "https://ipecho.net/plain"
    };

    public static String getIp() throws IOException {
        String ip = "";
        boolean success = false;

        for (String service : services) {
            URL whatIsMyIP = new URL(service);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()))) {
                ip = in.readLine();
                success = true;
                log.info("Successfully retrieved IP from {}", service);
                break;
            } catch (IOException e) {
                log.warn("Couldn't get public IP address with service {}. Trying with next service...", service);
            }
        }

        if (!success) {
            log.error("Couldn't get public IP address with any of the services...");
            return null;
        }

        return ip;
    }
}
