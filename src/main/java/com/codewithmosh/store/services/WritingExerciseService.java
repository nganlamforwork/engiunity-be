package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.writing.CreateExerciseManuallyRequest;
import com.codewithmosh.store.dtos.writing.WritingExerciseDto;
import com.codewithmosh.store.exceptions.DirectoryCreationException;
import com.codewithmosh.store.exceptions.FileStorageException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.mapppers.WritingExerciseMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.repositories.WritingExerciseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class WritingExerciseService {

    private final WritingExerciseRepository writingExerciseRepository;
    private final UserRepository userRepository;
    private final WritingExerciseMapper writingExerciseMapper;

    @Value("${exercise.upload-dir:writing/exercises}")
    private String uploadDir;

    public WritingExerciseService(WritingExerciseRepository writingExerciseRepository, UserRepository userRepository, WritingExerciseMapper writingExerciseMapper) {
        this.writingExerciseRepository = writingExerciseRepository;
        this.userRepository = userRepository;
        this.writingExerciseMapper = writingExerciseMapper;
    }

    public WritingExerciseDto createExercise(CreateExerciseManuallyRequest request, Long userId, MultipartFile image) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new UserNotFoundException(userId);

        var exercise = writingExerciseMapper.toEntity(request, userId);
        exercise.setUser(user);
        exercise = writingExerciseRepository.save(exercise);

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImageFile(image, exercise.getId());
            exercise.setImage(imagePath);
            writingExerciseRepository.save(exercise);
        }

        return writingExerciseMapper.toDto(exercise);
    }


    private String saveImageFile(MultipartFile image, Long exerciseId) {
        String currentDir = System.getProperty("user.dir");

        // Target: src/main/resources/static/writing/exercises
        Path folderPath = Paths.get(currentDir, "src", "main", "resources", "static", "writing", "exercises");


        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
        } catch (IOException e) {
            throw new DirectoryCreationException("Failed to create directories: " + folderPath, e);
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) throw new FileStorageException("Uploaded file has no name.");


        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = exerciseId + extension;
        Path filePath = folderPath.resolve(fileName);

        if (Files.exists(filePath)) throw new FileStorageException("File already exists: " + filePath);

        try {
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new FileStorageException("Error saving file: " + e.getMessage(), e);
        }

        return "/writing/exercises/" + fileName;


    }
}

