package net.plazmix.game.utility;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.plazmix.game.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class GameSchedulers {

    @Getter
    private final Set<BukkitTask> pendingTasks = new HashSet<>();

    public BukkitTask submit(Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(GamePlugin.getInstance(), runnable);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

    public BukkitTask submitAsync(Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getInstance(), runnable);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

    public BukkitTask runTimer(long delay, long period, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(GamePlugin.getInstance(), runnable, delay, period);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

    public BukkitTask runTimerAsync(long delay, long period, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(GamePlugin.getInstance(), runnable, delay, period);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

    public BukkitTask runLater(long delay, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(GamePlugin.getInstance(), runnable, delay);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

    public BukkitTask runLaterAsync(long delay, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(GamePlugin.getInstance(), runnable, delay);
        pendingTasks.add(bukkitTask);

        return bukkitTask;
    }

}
