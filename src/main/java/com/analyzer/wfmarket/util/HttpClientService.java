package com.analyzer.wfmarket.util;

import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

@Component
public class HttpClientService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(HttpClientService.class);

    private HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    public HttpResponse<String> sendHttpRequest(String url, HttpMethod method, String body, Map<String, String> headers) {
        String[] headersArray = convertHeadersMapToArray(headers);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .headers(headersArray)
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(1))
                    .method(method.name(), (method.equals(HttpMethod.GET) || body == null) ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.info("Response: {}", response);
            }

            return response;
        } catch (Exception e) {
            logger.info("Error sending request: {}", e.getMessage());
            logger.info("Stack trace: {}", Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException(e);
        }
    }

    private String[] convertHeadersMapToArray(Map<String, String> headers) {
        String[] headersArray = new String[headers.size() * 2];

        if(headers.isEmpty()) {
            return headersArray;
        }
        int i = 0;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersArray[i] = entry.getKey();
            i++;
            headersArray[i] = entry.getValue();
            i++;
        }

        logger.debug("Headers: {}", Arrays.toString(headersArray));
        return headersArray;
    }
}
