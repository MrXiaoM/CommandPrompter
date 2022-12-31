package com.cyr1en.commandprompter.prompt;

import com.cyr1en.commandprompter.CommandPrompter;
import com.cyr1en.commandprompter.PluginLogger;
import com.cyr1en.commandprompter.hook.hooks.PuerkasChatHook;
import com.cyr1en.commandprompter.unsafe.PvtFieldMutator;
import es.capitanpuerka.puerkaschat.manager.PuerkasFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;
import org.fusesource.jansi.Ansi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("unused")
public class PromptResponseListener implements Listener {

    private final PromptManager manager;
    private final CommandPrompter plugin;
    private final ResponseHandler handler;

    public PromptResponseListener(PromptManager manager, CommandPrompter plugin) {
        this.manager = manager;
        this.plugin = plugin;
        this.handler = new ResponseHandler(plugin);
    }

    public PromptManager getManager() {
        return this.manager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        boolean isPuerkasChatHooked = plugin.getHookContainer().getHook(PuerkasChatHook.class).isHooked();
        if (!isPuerkasChatHooked) {
            handler.onResponse(event.getPlayer(), event.getMessage(), event);
        } else if ((PuerkasFormat.getFormats() != null && !PuerkasFormat.getFormats().isEmpty()))
            handler.onResponse(event.getPlayer(), event.getMessage(), event);
    }

    public static class ResponseHandler {
        private final CommandPrompter plugin;
        private final PromptManager manager;

        public ResponseHandler(CommandPrompter plugin) {
            this.plugin = plugin;
            this.manager = plugin.getPromptManager();
        }

        public void onResponse(Player player, String msg, Cancellable event) {
            plugin.getPluginLogger().debug("Cancellable event: " + event.getClass().getSimpleName());
            if (!manager.getPromptRegistry().inCommandProcess(player))
                return;
            event.setCancelled(true);
            String message = ChatColor.stripColor(
                    ChatColor.translateAlternateColorCodes('&', msg));
            String cancelKeyword = plugin.getConfiguration().cancelKeyword;

            if (cancelKeyword.equalsIgnoreCase(message))
                manager.cancel(player);
            PromptContext ctx = new PromptContext(event, player, message);
            Bukkit.getScheduler().runTask(plugin, () -> manager.processPrompt(ctx));
        }
    }

    public static void setPriority(CommandPrompter plugin) {
        String configPriority = plugin.getPromptConfig().responseListenerPriority.toUpperCase(Locale.ROOT);
        if (configPriority.equals("DEFAULT")) return;

        listAllRegisteredListeners(plugin);
        PluginLogger logger = plugin.getPluginLogger();
        EventPriority currentPriority = getCurrentEventPriority(plugin);
        if (Objects.isNull(currentPriority)) return;

        EventPriority priority = EventPriority.LOWEST;
        try {
            priority = EventPriority.valueOf(configPriority);
        } catch (IllegalArgumentException ignore) {
            logger.err("Could not set '%s' as priority for PromptResponseListener. Defaulted to '%s'",
                    configPriority, priority.name());
        }
        // Do nothing if current priority = config priority
        if (currentPriority.name().equals(priority.name())) return;

        setPriority(plugin, priority);
    }

    private static synchronized void setPriority(CommandPrompter plugin, EventPriority newPriority) {
        PluginLogger logger = plugin.getPluginLogger();
        EventPriority oldPriority = getCurrentEventPriority(plugin);
        logger.debug("Setting PromptResponseListener priority from '%s' to '%s'",
                oldPriority == null ? "null" : oldPriority.name(), newPriority.name());
        HandlerList handlerList = AsyncPlayerChatEvent.getHandlerList();
        try {
            Field handlerSlotsF = handlerList.getClass().getDeclaredField("handlerslots");
            handlerSlotsF.setAccessible(true);
            @SuppressWarnings("unchecked")
            EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerSlots = (EnumMap<EventPriority, ArrayList<RegisteredListener>>) handlerSlotsF.get(handlerList);
            EventPriority currentPriority = getCurrentEventPriority(plugin);

            RegisteredListener registeredListener = handlerSlots.get(currentPriority).stream()
                    .filter(rL -> rL.getListener().getClass().equals(PromptResponseListener.class))
                    .findFirst().orElse(null);
            if (registeredListener == null) throw new NullPointerException();

            handlerList.unregister(registeredListener);

            PvtFieldMutator.forField("priority").in(registeredListener).replaceWith(newPriority);

            handlerList.register(registeredListener);
            handlerList.bake();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        EventPriority priority = getCurrentEventPriority(plugin);
        logger.info("PromptResponsePriority is now '%s'",
                new Ansi().fgRgb(153, 214, 90).a(priority == null ? "null" : priority.name()));
        listAllRegisteredListeners(plugin);
    }

    private static EventPriority getCurrentEventPriority(CommandPrompter plugin) {
        for (RegisteredListener registeredListener : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(plugin.getName()))
                return registeredListener.getPriority();
        }
        return null;
    }

    private static void listAllRegisteredListeners(CommandPrompter plugin) {
        PluginLogger logger = plugin.getPluginLogger();
        logger.debug("Registered Listeners: ");
        for (RegisteredListener registeredListener : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) {
            logger.debug("  - '%s'", registeredListener.getListener().getClass().getSimpleName());
            logger.debug("      Priority: " + registeredListener.getPriority());
            logger.debug("      Plugin: " + registeredListener.getPlugin().getName());
        }
    }

}
