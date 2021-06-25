package com.uploaddownloadfiles.storage;

import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class StorageService {

    // define upload location
    private static final String DEFAULT_LOCATION = System.getProperty("user.home") + "/Downloads/uploads";

    // define upload logic
    public List<String> upload(List<MultipartFile> multipartFiles) throws IOException {
        List<String> filenames = new ArrayList<>();
        for(MultipartFile file: multipartFiles) {
            if(file == null || file.getOriginalFilename() == null) continue;
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileStorage = get(DEFAULT_LOCATION, filename).toAbsolutePath().normalize();
            copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
            filenames.add(filename);
        }
        return filenames;
    }

    // define Download logic
    public ResponseEntity<Resource> download(String filename) throws IOException {
        Path path = get(DEFAULT_LOCATION).resolve(filename).toAbsolutePath().normalize();
        if(!Files.exists(path)) {
            throw new FileNotFoundException("File not found");
        }
        Resource resource = new UrlResource(path.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name="+ resource.getFilename());
        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .contentType(MediaType.valueOf(new Tika().detect(path)))
                .headers(httpHeaders)
                .body(resource);
    }

    public List<String> loadFiles() throws FileNotFoundException {
        List<String> filenames = new ArrayList<>();
        Path fileStorage = get(DEFAULT_LOCATION).toAbsolutePath().normalize();
        File[] dir = fileStorage.toFile().listFiles();
        if(dir == null) return filenames;
        for(File file: dir) {
            filenames.add(file.getName());
        }
        return filenames;
    }
}
