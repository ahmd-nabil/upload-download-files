package com.uploaddownloadfiles;

import com.uploaddownloadfiles.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/files")
public class MainController {

    private final StorageService storageService;

    public MainController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> multipartFiles) {
        try {
            List<String> fileNames = storageService.upload(multipartFiles);
            return ResponseEntity.ok().body(fileNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(new ArrayList<>());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable String filename) {
        try {
            return storageService.download(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
