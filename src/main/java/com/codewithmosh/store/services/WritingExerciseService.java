package com.codewithmosh.store.services;

import com.codewithmosh.store.cdn.BunnyCdnUploader;
import com.codewithmosh.store.dtos.scoring.writing.WritingEvaluationDto;
import com.codewithmosh.store.dtos.writing.*;
import com.codewithmosh.store.entities.WritingExercise;
import com.codewithmosh.store.entities.WritingExerciseResponse;
import com.codewithmosh.store.entities.enums.CreationSource;
import com.codewithmosh.store.entities.enums.ScoreStatus;
import com.codewithmosh.store.entities.enums.Status;
import com.codewithmosh.store.exceptions.ExerciseNotFoundException;
import com.codewithmosh.store.exceptions.FileStorageException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.mapppers.WritingExerciseMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.repositories.WritingExerciseRepository;
import com.codewithmosh.store.repositories.WritingExerciseResponseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class WritingExerciseService {
    private final WritingExerciseRepository writingExerciseRepository;
    private final WritingExerciseResponseRepository writingExerciseResponseRepository;
    private final UserRepository userRepository;
    private final WritingExerciseMapper writingExerciseMapper;
    private final BunnyCdnUploader bunnyCdnUploader;
    private final WritingEvaluationService writingEvaluationService;

    private final ObjectMapper objectMapper;

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

    public WritingExerciseDto getExerciseById(Long id, Long userId) {
//        var user = userRepository.findById(userId).orElse(null);
//        if (user == null) throw new UserNotFoundException(userId);

        Optional<WritingExercise> exercise = writingExerciseRepository.findByIdAndUserId(
                id,
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED);
        if (!exercise.isPresent()) throw new ExerciseNotFoundException();

        WritingExerciseDto writingExerciseDto = writingExerciseMapper.toEntity(exercise.get());

        return writingExerciseDto;
    }

    public void createResponse(Long exerciseId, Long userId, WritingExerciseResponseRequest request) {
//        var user = userRepository.findById(userId).orElse(null);
//        if (user == null) throw new UserNotFoundException(userId);

        Optional<WritingExercise> exerciseOptional = writingExerciseRepository.findByIdAndUserId(
                exerciseId,
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED);
        if (!exerciseOptional.isPresent()) throw new ExerciseNotFoundException();

        WritingExercise exercise = exerciseOptional.get();

        Integer maxTakenNumber = writingExerciseResponseRepository.findMaxTakeNumberByExerciseId(exerciseId);

        int nextTakeNumber = (maxTakenNumber != null ? maxTakenNumber + 1 : 1);

        WritingExerciseResponse writingExerciseResponse = writingExerciseMapper.toEntity(request);
        writingExerciseResponse.setExercise(exercise);
        writingExerciseResponse.setTakeNumber(nextTakeNumber);

        writingExerciseResponseRepository.save(writingExerciseResponse);
        if (exercise.getStatus() == null || exercise.getStatus() == Status.NOT_STARTED) {
            exercise.setStatus(Status.IN_PROGRESS);
            writingExerciseRepository.save(exercise);
        }
    }

    public WritingExerciseResponseNotScoredDto getLatestNotScoredResponse(Long exerciseId, Long userId) {
//        var user = userRepository.findById(userId).orElse(null);
//        if (user == null) throw new UserNotFoundException(userId);

        Optional<WritingExercise> exerciseOptional = writingExerciseRepository.findByIdAndUserId(
                exerciseId,
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED);
        if (!exerciseOptional.isPresent()) throw new ExerciseNotFoundException();

        WritingExerciseResponse response = writingExerciseResponseRepository
                .findLatestNotGradedResponseByExerciseId(exerciseId)
                .orElse(null);
        if (response == null) return null;

        WritingExerciseResponseNotScoredDto responseDto = writingExerciseMapper.toNotScoredDto(response);

        return responseDto;
    }

    public WritingEvaluationDto submitResponse(Long exerciseId, Long userId, WritingExerciseResponseRequest request) {
//        var user = userRepository.findById(userId).orElse(null);
//        if (user == null) throw new UserNotFoundException(userId);

        Optional<WritingExercise> exerciseOptional = writingExerciseRepository.findByIdAndUserId(
                exerciseId,
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED);
        WritingExercise exercise = exerciseOptional.get();
        if (!exerciseOptional.isPresent()) throw new ExerciseNotFoundException();

        WritingExerciseResponse response = null;

        // Submit draft response
        if (request.getId() != null) {
            response = writingExerciseResponseRepository.findById(request.getId()).orElse(null);
            if (response != null){
                response.setTakenAt(Instant.now());
                response.setContent(request.getContent());
            }
        }

        // Submit new response or submit draft response
        if (request.getId() == null || response == null) {
            Integer maxTakenNumber = writingExerciseResponseRepository.findMaxTakeNumberByExerciseId(exerciseId);

            int nextTakeNumber = (maxTakenNumber != null ? maxTakenNumber + 1 : 1);

            response = writingExerciseMapper.toEntity(request);
            response.setExercise(exerciseOptional.get());
            response.setTakeNumber(nextTakeNumber);

        }
        WritingEvaluationDto result = writingEvaluationService.evaluateWriting(response.getContent(), exerciseOptional.get().getContent());

        // Set score & taken time
        response.setScore(result.getOverview().getTotalScore());
        Map<String, Object> scoreDetailMap = objectMapper.convertValue(result, new TypeReference<>() {
        });
        response.setScoreDetail(scoreDetailMap);

        response.setScoreStatus(ScoreStatus.SCORED);

        writingExerciseResponseRepository.save(response);

        exercise.setScore(result.getOverview().getTotalScore());
        exercise.setStatus(Status.DONE);
        writingExerciseRepository.save(exercise);

        return result;
    }

    public WritingExerciseSubmissionsDto getAllSubmissions(Long exerciseId, Long userId) {
        Optional<WritingExercise> exerciseOptional = writingExerciseRepository.findByIdAndUserId(
                exerciseId,
                userId,
                CreationSource.USER_CREATED,
                CreationSource.AI_GENERATED,
                CreationSource.SYSTEM_UPLOADED);
        if (!exerciseOptional.isPresent()) throw new ExerciseNotFoundException();
//        System.out.println(exerciseOptional.get());
        List<WritingExerciseResponse> responses = writingExerciseResponseRepository.findAllByExercise_Id(exerciseOptional.get().getId());
//        System.out.println("responses");
//        System.out.println(responses);
        List<WritingExerciseResponseDto> responsesDtos = responses.stream()
                .map(writingExerciseMapper::toDto)
                .toList();
        WritingExerciseSubmissionsDto submissions = new WritingExerciseSubmissionsDto(writingExerciseMapper.toDto(exerciseOptional.get()), responsesDtos);

        return submissions;
    }
}

