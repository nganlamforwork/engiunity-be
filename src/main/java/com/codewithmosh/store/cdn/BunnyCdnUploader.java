/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.cdn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class BunnyCdnUploader {

    @Value("${bunny.storage.zoneName}")
    private String zoneName;

    @Value("${bunny.storage.regionUrl}")
    private String regionUrl;

    @Value("${bunny.storage.accessKey}")
    private String accessKey;

    @Value("${bunny.storage.pullZoneUrl}")
    private String pullZoneUrl;

    public String uploadFile(MultipartFile file, String targetFileName) {
        try {
            String uploadUrl = regionUrl + "/" + zoneName + "/" + targetFileName;
            URL url = new URL(uploadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("AccessKey", accessKey);
            connection.setRequestProperty("Content-Type", file.getContentType());
            System.out.println(uploadUrl);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(file.getBytes());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 201) {
                throw new RuntimeException("Failed to upload file to BunnyCDN. Response Code: " + responseCode);
            }

            return pullZoneUrl + '/' + targetFileName;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to BunnyCDN: " + e.getMessage(), e);
        }
    }
}
