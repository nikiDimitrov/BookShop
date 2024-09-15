package org.book.bookshop.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

    public static String getIp() throws MalformedURLException {
        URL whatIsMyIP = new URL(services[0]);
        String ip = "";
        boolean success = false;

        for(int i = 1; i < services.length; i++) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatIsMyIP.openStream()))){
                ip = in.readLine();
                success = true;
            }
            catch(IOException e) {
                log.warn("Couldn't get public IP address with service {}. Trying with {}...", whatIsMyIP, services[i]);
                whatIsMyIP = new URL(services[i]);
            }

            if(success) {
                break;
            }
        }

        if(ip.isEmpty()){
            log.error("Couldn't get public IP address with neither services...");
        }

        return ip;
    }
}
