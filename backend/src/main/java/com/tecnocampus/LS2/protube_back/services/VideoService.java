package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.persistence.*;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {

    public final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, UserRepository userRepository,
            CategoryRepository categoryRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public List<VideoDTO> getVideos() {
        return videoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<VideoDTO> getVideosBySearch(String search) {
        List<Video> videos = videoRepository.findAll();
        if (search == null || search.isBlank() || videos == null || videos.isEmpty()) {
            return List.of();
        }

        final String normalizedSearch = normalize(search);

        List<ScoredVideo> scored = new ArrayList<>();
        for (Video video : videos) {
            String title = video != null ? video.getTitle() : null;
            if (title == null || title.isBlank())
                continue;

            SimilarityResult result = calculateTitleSimilarity(normalize(title), normalizedSearch);
            scored.add(new ScoredVideo(video, result.score(), result.isMatch()));
        }

        scored.sort(Comparator.comparing(ScoredVideo::score).reversed());

        return scored.stream()
                .filter(ScoredVideo::isMatch)
                .map(sv -> toDTO(sv.video()))
                .collect(Collectors.toList());
    }

    public VideoDTO getVideoById(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }
        return toDTO(video);
    }

    public VideoDTO createVideo(VideoDTO videoDTO) {
        Video video = toDomain(videoDTO);
        Video saved = videoRepository.save(video);
        return toDTO(saved);
    }

    public VideoDTO deleteVideo(Long id) {
        VideoDTO videoDTO = getVideoById(id);
        if (videoDTO != null) {
            videoRepository.deleteById(id);
        }
        return videoDTO;
    }

    public VideoDTO updateVideo(Long id, VideoDTO videoDTO) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
            video.updateVideo(videoDTO);
            User user = userRepository.findById(videoDTO.userId()).orElse(null);
            Category category = categoryRepository.findById(videoDTO.categoryId()).orElse(null);
            Tag tag = tagRepository.findById(videoDTO.tagId()).orElse(null);
            Comment comment = commentRepository.findById(videoDTO.commentId()).orElse(null);

            if (user != null && category != null && tag != null && comment != null) {
                video.getCategories().add(category);
                video.getTags().add(tag);
                video.getComments().add(comment);
                video.setUser(user);

            }

            Video updated = videoRepository.save(video);
            return toDTO(updated);
        }
        return null;
    }

    private VideoDTO toDTO(Video video) {
        return new VideoDTO(
                video.getId(),
                video.getTitle(),
                video.getWidth(),
                video.getHeight(),
                video.getDuration(),
                video.getDescription(),
                video.getFilename(),
                video.getUser() != null ? video.getUser().getId() : null,
                (video.getCategories() != null && !video.getCategories().isEmpty())
                        ? video.getCategories().get(0).getId()
                        : null,
                (video.getTags() != null && !video.getTags().isEmpty())
                        ? video.getTags().get(0).getId()
                        : null,
                (video.getComments() != null && !video.getComments().isEmpty())
                        ? video.getComments().get(0).getId()
                        : null);
    }

    private Video toDomain(VideoDTO videoDTO) {
        Video video = new Video(videoDTO);
        return video;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String lowered = value.trim().toLowerCase();
        String normalized = java.text.Normalizer.normalize(lowered, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private static SimilarityResult calculateTitleSimilarity(String firstTitle, String secondTitle) {
        if (firstTitle == null || firstTitle.isEmpty() || secondTitle == null || secondTitle.isEmpty()) {
            return new SimilarityResult(false, false, 0.0);
        }
        if (Objects.equals(firstTitle, secondTitle)) {
            return new SimilarityResult(true, true, 1.0);
        }

        int editDistance = calculateEditDistance(firstTitle, secondTitle);
        int longestNameLength = Math.max(firstTitle.length(), secondTitle.length());
        double editSimilarity = 1.0 - ((double) editDistance / (double) longestNameLength);

        boolean nameContainsOther = firstTitle.contains(secondTitle) || secondTitle.contains(firstTitle);

        String[] wordsInFirst = firstTitle.split("\\s+");
        String[] wordsInSecond = secondTitle.split("\\s+");

        Set<String> setFirst = new HashSet<>();
        for (String w : wordsInFirst) {
            if (!w.isEmpty())
                setFirst.add(w);
        }
        Set<String> setSecond = new HashSet<>();
        for (String w : wordsInSecond) {
            if (!w.isEmpty())
                setSecond.add(w);
        }

        Set<String> intersection = new HashSet<>(setFirst);
        intersection.retainAll(setSecond);
        int commonWordCount = intersection.size();

        int maxWordCount = Math.max(setFirst.size(), setSecond.size());
        double wordSimilarity = maxWordCount == 0 ? 0.0 : ((double) commonWordCount / (double) maxWordCount);

        double similarityScore = Math.max(editSimilarity, wordSimilarity);
        if (nameContainsOther) {
            similarityScore = Math.max(similarityScore, 0.9);
        }

        final double EXACT_MATCH_THRESHOLD = 0.9;
        final double SIMILARITY_THRESHOLD = 0.7;

        boolean isExact = similarityScore >= EXACT_MATCH_THRESHOLD;
        boolean isMatch = similarityScore >= SIMILARITY_THRESHOLD;
        return new SimilarityResult(isMatch, isExact, similarityScore);
    }

    private static int calculateEditDistance(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();

        int[][] distanceMatrix = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i <= sourceLength; i++) {
            distanceMatrix[i][0] = i;
        }
        for (int j = 0; j <= targetLength; j++) {
            distanceMatrix[0][j] = j;
        }

        for (int i = 1; i <= sourceLength; i++) {
            for (int j = 1; j <= targetLength; j++) {
                int substitutionCost = (source.charAt(i - 1) == target.charAt(j - 1)) ? 0 : 1;
                int deleteCost = distanceMatrix[i - 1][j] + 1;
                int insertCost = distanceMatrix[i][j - 1] + 1;
                int substituteCost = distanceMatrix[i - 1][j - 1] + substitutionCost;
                distanceMatrix[i][j] = Math.min(Math.min(deleteCost, insertCost), substituteCost);
            }
        }
        return distanceMatrix[sourceLength][targetLength];
    }

}
