package com.github.jenya705.cubicore.moderation;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Jenya705
 */
@AllArgsConstructor
public class BlockNotifier implements Listener {

    private final CubicModeration plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (plugin.isPlacingNotify(block.getType())) {
            sendNotifyMessage(buildMessage(player, "placed", block));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (plugin.isBreakingNotify(block.getType())) {
            sendNotifyMessage(buildMessage(player, "broke", block));
        }
    }

    private Component buildMessage(Player who, String operation, Block block) {
        Location location = block.getLocation();
        return Component
                .text("[Cubicore] ")
                .color(NamedTextColor.GRAY)
                .append(who
                        .displayName()
                        .decorate(TextDecoration.ITALIC)
                        .hoverEvent(who)
                )
                .append(Component
                        .text(" " + operation + " ")
                        .color(NamedTextColor.GRAY)
                )
                .append(Component
                        .translatable(block)
                )
                .clickEvent(ClickEvent
                        .runCommand(String.format(
                                "/moderation %s %s %s %s",
                                location.getWorld().getName(),
                                location.getBlockX(),
                                location.getBlockY(),
                                location.getBlockZ()
                        ))
                )
                ;
    }

    private void sendNotifyMessage(Component component) {
        Bukkit.getOnlinePlayers().forEach(it -> {
            if (it.hasPermission("cubicore.moderation.notify")) {
                it.sendMessage(component);
            }
        });
    }
}
