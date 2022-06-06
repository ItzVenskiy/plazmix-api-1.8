package net.plazmix.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.command.annotation.CommandArgument;
import net.plazmix.command.manager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ContextCommand<S extends CommandSender>
        extends BaseMegaCommand<S>
        implements TabCompleter {

    @SuppressWarnings("all")
    public ContextCommand(String command, String... aliases) {
        super(command, aliases);

        commandArguments.clear();
        Arrays.stream(getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(CommandArgument.class) != null)
                .filter(method -> method.getParameterCount() == 1 && (Arrays.equals(method.getParameterTypes(), new Class<?>[]{CommandContext.class})))

                .forEach(method -> {
                    method.setAccessible(true);

                    commandArguments.put(method.getName().toLowerCase(), method);

                    for (String alias : method.getDeclaredAnnotation(CommandArgument.class).aliases()) {
                        commandArguments.put(alias.toLowerCase(), method);
                    }
                });

        setNoArgumentMessage(this::onUsage);

        // Костыль, но ворк же ))0
        Bukkit.getScheduler().runTaskLater(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class),
                () -> ((BaseCommand<S>) CommandManager.COMMAND_MAP.getCommand(command)).setTabCompleter(this), 20);
    }

    protected abstract TabCompleteSuggestions onTabComplete(@NonNull S sender);

    @SneakyThrows
    @Override
    public void onExecute(S commandSender, String[] args) {
        if (args.length == minimalArgsCount) {
            onUsage(commandSender);
            return;
        }

        String label = args[minimalArgsCount].toLowerCase();
        Method argumentMethodMethod = commandArguments.get(args[minimalArgsCount].toLowerCase());

        if (argumentMethodMethod != null) {
            argumentMethodMethod.invoke(this, CommandContext.create(getSenderType(), commandSender, label, Arrays.copyOfRange(args, minimalArgsCount + 1, args.length)));

        } else {

            noArgumentMessage.accept(commandSender);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return commandArguments.keySet()
                    .stream()
                    .filter(s -> s.toLowerCase().startsWith(args.length > 0 ? args[0].toLowerCase() : ""))
                    .collect(Collectors.toList());
        }

        // Initialize context.
        TabCompleteSuggestions suggestions = onTabComplete(((S) sender));
        Method currentMethod = commandArguments.get(args[0].toLowerCase());

        if (currentMethod == null) {
            return null;
        }

        Function<String, Collection<String>> suggestionsFunction = suggestions.getSuggestions(currentMethod.getName(), args.length - 2);

        if (suggestionsFunction != null) {
            return new ArrayList<>(suggestionsFunction.apply(args[args.length - 1]));
        }

        return null;
    }


    @Value(staticConstructor = "create")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class CommandContext<S extends CommandSender> {

        Class<S> senderType;

        @Getter
        S sender;

        @Getter
        String executedLabel;

        String[] arguments;


        public boolean isArgumentsEmpty() {
            return arguments.length == 0;
        }
        
        public int getArgumentsLength() {
            return arguments.length;
        }


        public String getFirstArgument() {
            return getArgument(0);
        }

        public String getSecondArgument() {
            return getArgument(1);
        }

        public String getArgument(int index) {
            return arguments[index];
        }
    }

    @Value(staticConstructor = "create")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class TabCompleteSuggestions {

        @NonNull
        Table<String, Integer, Function<String, Collection<String>>> suggestionsByArgumentsTable = HashBasedTable.create();


        public Map<Integer, Function<String, Collection<String>>> getSuggestionsByArgumentIndexMap(@NonNull String argumentMethod) {
            return suggestionsByArgumentsTable.row(argumentMethod.toLowerCase());
        }

        public Function<String, Collection<String>> getSuggestions(@NonNull String argumentMethod, int index) {
            return suggestionsByArgumentsTable.get(argumentMethod.toLowerCase(), index);
        }


        public void clear(@NonNull String argumentMethod, int index) {
            suggestionsByArgumentsTable.remove(argumentMethod.toLowerCase(), index);
        }

        public void clearFull(@NonNull String argumentMethod) {
            suggestionsByArgumentsTable.row(argumentMethod.toLowerCase()).forEach((index, suggestions) -> clear(argumentMethod, index));
        }


        public TabCompleteSuggestions put(@NonNull String argumentMethod, int index, @NonNull Function<String, Collection<String>> suggestions) {
            suggestionsByArgumentsTable.put(argumentMethod.toLowerCase(), index, suggestions);
            return this;
        }
    }

}
