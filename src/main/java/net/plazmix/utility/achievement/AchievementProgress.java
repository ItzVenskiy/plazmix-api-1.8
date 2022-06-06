package net.plazmix.utility.achievement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AchievementProgress {

    int taskId;
    int maxValue;

    private int initProgressValue(Achievement achievement, int playerId) {
        return achievement.task(taskId).getCachedPlayerData(achievement, playerId).getProgress();
    }

    public boolean isCompleted(Achievement achievement, int playerId) {
        return getPlayerProgress(achievement, playerId) >= maxValue;
    }

    public int getPlayerProgress(Achievement achievement, int playerId) {
        int value = initProgressValue(achievement, playerId);
        return Math.min(value, maxValue);
    }

    public void incrementProgress(Achievement achievement, int playerId) {
        int currentValue = initProgressValue(achievement, playerId);

        if (currentValue >= maxValue) {
            return;
        }

        Achievement.AchievementTaskData data = achievement.task(taskId).getCachedPlayerData(achievement, playerId);
        data.setProgress(data.getProgress() + 1);
    }
}