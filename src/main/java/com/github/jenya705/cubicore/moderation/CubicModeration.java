package com.github.jenya705.cubicore.moderation;

import lombok.experimental.Delegate;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CubicModeration extends JavaPlugin {

    @Delegate
    private CubicConfig config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new CubicConfig(this);
        register(new BlockNotifier(this));
        ModerationCommand moderationCommand = new ModerationCommand(this);
        getCommand("moderation").setExecutor(moderationCommand);
        register(moderationCommand);
    }

    @Override
    public void onDisable() {

    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

}
