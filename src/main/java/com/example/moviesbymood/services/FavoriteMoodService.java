package com.example.moviesbymood.services;

import com.example.moviesbymood.models.MoodCategory;
import java.util.List;
import java.util.Set;

public interface FavoriteMoodService {
    Set<Long> getFavoriteMoodIds();
    List<MoodCategory> getFavoriteMoods();
    boolean isFavoriteMood(Long moodId);
    void toggleFavoriteMood(Long moodId);
}
