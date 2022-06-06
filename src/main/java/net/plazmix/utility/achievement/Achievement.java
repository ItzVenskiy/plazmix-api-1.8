package net.plazmix.utility.achievement;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.plazmix.utility.JsonUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Achievement {

    public static Achievement create(int id, Material icon) {
        return new Achievement(id, icon);
    }

    @Setter
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AchievementTaskData {

        static AchievementTaskData fromAchievement(Achievement achievement, int taskId, int playerId) {
            AchievementTask achievementTask = achievement.task(taskId);
            return new AchievementTaskData(achievementTask.getId(), achievementTask.getProgress().getPlayerProgress(achievement, playerId));
        }

        static AchievementTaskData fromJson(String json) {
            return JsonUtil.fromJson(json, AchievementTaskData.class);
        }


        final int taskId;
        int progress;

        public String toJson() {
            return JsonUtil.toJson(this);
        }
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class AchievementReward {

        String title;
        Consumer<Player> handler;
    }

    int id;

    @NonNull
    Material material;

    @NonNull
    List<AchievementTask> tasks = new ArrayList<>();

    @NonNull
    List<AchievementReward> rewards = new ArrayList<>();


    public AchievementTask task(int taskId) {
        for (AchievementTask achievementTask : getTasks()) {

            if (achievementTask.getId() == taskId) {
                return achievementTask;
            }
        }

        return null;
    }

    public Achievement addTask(AchievementTask achievementTask) {
        tasks.add(achievementTask);
        return this;
    }

    public Achievement addReward(AchievementReward achievementReward) {
        rewards.add(achievementReward);
        return this;
    }

    public void initPlayerData(int playerId) {

        for (AchievementTask achievementTask : getTasks()) {
            achievementTask.getCachedPlayerData(this, playerId);
        }
    }

    public void updatePlayerData(int playerId) {

        for (AchievementTask achievementTask : getTasks()) {
            achievementTask.updatePlayerData(this, playerId, achievementTask.getCachedPlayerData(this, playerId));
        }
    }

    public void acceptRewards(Player player) {
        for (AchievementReward achievementReward : rewards) {
            achievementReward.handler.accept(player);
        }
    }

}
