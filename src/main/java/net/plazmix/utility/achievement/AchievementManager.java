package net.plazmix.utility.achievement;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.plazmix.core.connection.protocol.server.SAchievementRegisterPacket;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.achievement.AchievementTask;
import net.plazmix.utility.JsonUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class AchievementManager {

    public static final AchievementManager INSTANCE = new AchievementManager();

    private final TIntObjectMap<Achievement> questsMap = new TIntObjectHashMap<>();


    public Collection<Achievement> getServerRegisteredAchievements() {
        return questsMap.valueCollection();
    }

    public void registerAchievement(Achievement achievement) {
        questsMap.put(achievement.getId(), achievement);

        // Send achievement register packet.
        List<AchievementTask> achievementTaskList = achievement.getTasks().stream().map(task -> JsonUtil.fromJson(JsonUtil.toJson(this), AchievementTask.class)).collect(Collectors.toList());
        List<String> rewardsTitlesList = achievement.getRewards().stream().map(Achievement.AchievementReward::getTitle).collect(Collectors.toList());

        CoreConnector.getInstance().sendPacket(
                new SAchievementRegisterPacket(achievement.getId(), achievement.getMaterial(), achievementTaskList, rewardsTitlesList)
        );
    }

    public Achievement getServerAchievement(int achievementID) {
        return questsMap.get(achievementID);
    }

}
