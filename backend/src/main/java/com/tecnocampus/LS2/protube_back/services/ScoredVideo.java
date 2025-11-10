package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;

public record ScoredVideo(Video video, double score, boolean exactMatch) {
}
