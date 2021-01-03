package com.spring.boot.rocks.service;

import com.spring.boot.rocks.configuration.FileStorageProperties;
import com.spring.boot.rocks.exception.FileStorageException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.*;

/**
 * Class to handle file upload
 *
 */
@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws Exception {
        this.fileStorageLocation
            = Paths
                .get(fileStorageProperties.getUploadLocation())
                .toAbsolutePath()
                .normalize();
    }

    public Mono<String> storeFile(FilePart filePart) throws FileStorageException {

        log.debug("===> service storeFile :: Begin");
        Mono<String> fileName = null;
        try {
            Path uploadedFilePath = Files.createTempFile(fileStorageLocation, null, filePart.filename());

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(uploadedFilePath, StandardOpenOption.WRITE);
            DataBufferUtils.write(filePart.content(), channel, 0)
            .doOnComplete(() -> {
                log.debug("Finished saving file to {}", uploadedFilePath.toAbsolutePath());
            })
            .subscribe();

            fileName = Mono.just(uploadedFilePath.toAbsolutePath().toString());
        }
        catch (Exception e) {
            log.error("===> Error occurred while saving file {} ", filePart.filename(), e);
            throw new FileStorageException(String.format("Error occurred while saving file %s ", filePart.filename()));
        }
        log.debug("===> service storeFile :: End");
        return fileName;
    }
}
