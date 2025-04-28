package com.codewithmosh.store.services;

import com.codewithmosh.store.cdn.BunnyCdnUploader;
import com.codewithmosh.store.dtos.writing.CreateExerciseManuallyRequest;
import com.codewithmosh.store.dtos.writing.WritingExerciseDto;
import com.codewithmosh.store.dtos.writing.WritingExerciseSummaryDto;
import com.codewithmosh.store.entities.WritingExercise;
import com.codewithmosh.store.entities.enums.CreationSource;
import com.codewithmosh.store.exceptions.ExerciseNotFoundException;
import com.codewithmosh.store.exceptions.FileStorageException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.mapppers.WritingExerciseMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.repositories.WritingExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@AllArgsConstructor
@Service
public class WritingExerciseService {
    private final WritingExerciseRepository writingExerciseRepository;
    private final UserRepository userRepository;
    private final WritingExerciseMapper writingExerciseMapper;
    private final BunnyCdnUploader bunnyCdnUploader;

    public WritingExerciseSummaryDto createExercise(CreateExerciseManuallyRequest request, Long userId, MultipartFile image) {
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

        return writingExerciseMapper.toSummaryDto(exercise);
    }

    // TODO: một phiên bản tối ưu hơn dùng Spring WebClient (async, nhanh hơn HttpURLConnection)
    private String saveImageFile(MultipartFile image, Long exerciseId) {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileStorageException("Uploaded file has no name.");
        }

        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String targetFileName = "writing/exercises/" + exerciseId + extension;

        return bunnyCdnUploader.uploadFile(image, targetFileName);
    }

    public Iterable<WritingExerciseSummaryDto> getAllExercisesByUser(Long userId) {
        var exercises = writingExerciseRepository.findAllByUserIdAndCreationSource(
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED
        );

//        System.out.println(exercises);
        return exercises.stream()
                .map(writingExerciseMapper::toSummaryDto)
                .toList();
    }

    public WritingExerciseDto getExerciseById(Long id) {
        Optional<WritingExercise> exercise = writingExerciseRepository.findById(id);
        if (!exercise.isPresent()) throw new ExerciseNotFoundException();

        WritingExerciseDto writingExerciseDto = writingExerciseMapper.toDto(exercise.get());

        return writingExerciseDto;
    }
}

