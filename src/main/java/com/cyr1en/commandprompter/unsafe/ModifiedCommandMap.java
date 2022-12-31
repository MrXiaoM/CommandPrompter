package com.cyr1en.commandprompter.unsafe;

import com.cyr1en.commandprompter.CommandPrompter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Map;

public class ModifiedCommandMap extends SimpleCommandMap {

    private final Object mapEncapsulator;
    private final CommandPrompter plugin;

    public ModifiedCommandMap(Object mapEncapsulator, CommandPrompter plugin) {
        super(plugin.getServer());
        this.mapEncapsulator = mapEncapsulator;
        this.plugin = plugin;
        rebuildKnownCommands();
    }

    /**
     * Function to rebuild knownCommands from the old map.
     *
     * <p>
     * To minimize the effects of replacing the old map with this new map.
     * We have to make sure that we retain all commands that have been registered in
     * the old map.
     */
    private void rebuildKnownCommands() {
        try {
            SimpleCommandMap commandMap = grabCommandMap();
            Map<String, Command> originalKnownCommands = grabKnownCommandsFromMap(commandMap);
            this.knownCommands.putAll(originalKnownCommands);
            plugin.getPluginLogger().warn("Finished rebuilding known commands.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Command> grabKnownCommandsFromMap(SimpleCommandMap commandMap)
            throws NoSuchFieldException, IllegalAccessException {
        Field mapField = commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
        mapField.setAccessible(true);
        return (Map<String, Command>) mapField.get(commandMap);
    }

    private SimpleCommandMap grabCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field commandMapField = mapEncapsulator.getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        return (SimpleCommandMap) commandMapField.get(mapEncapsulator);
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        CommandDispatchEvent event = new CommandDispatchEvent(sender, commandLine);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return true;
        return super.dispatch(sender, commandLine);
    }


}
