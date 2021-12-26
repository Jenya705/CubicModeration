package com.github.jenya705.cubicore.moderation;

import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class ModerationCommand implements CommandExecutor, Listener {

    private final CubicModeration plugin;
    private final LuckPerms luckPerms = LuckPermsProvider.get();

    private final Map<UUID, PlayerCopy> copies = new HashMap<>();

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (copies.containsKey(event.getPlayer().getUniqueId())) {
            exitModeratorMode(event.getPlayer());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You are not the player!");
            return true;
        }
        boolean exit = args.length == 0 && copies.containsKey(player.getUniqueId());
        if (exit) {
            exitModeratorMode(player);
        }
        else {
            if (!copies.containsKey(player.getUniqueId())) {
                PlayerCopy copy = new PlayerCopy(
                        Arrays
                                .stream(player.getInventory().getContents())
                                .map(it -> it == null ? new ItemStack(Material.AIR) : it)
                                .toArray(ItemStack[]::new),
                        player.getLocation()
                );
                luckPerms.getUserManager().modifyUser(
                        player.getUniqueId(),
                        user -> user
                                .data()
                                .add(Node
                                        .builder("group." + plugin.getModerationModeGroup())
                                        .value(true)
                                        .build()
                                )
                );
                copies.put(player.getUniqueId(), copy);
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
            }
            if (args.length == 4) {
                player.teleport(new Location(
                        Bukkit.getWorld(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3])
                ));
            }
        }
        return true;
    }

    private void exitModeratorMode(Player player) {
        PlayerCopy copy = copies.get(player.getUniqueId());
        if (copy == null) {
            player.sendMessage("You did not enter in moderation mode");
            return;
        }
        player.teleport(copy.getLocation());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        int index = 0;
        for (ItemStack itemStack: copy.getItems()) {
            player.getInventory().setItem(index++, itemStack);
        }
        luckPerms.getUserManager().modifyUser(
                player.getUniqueId(),
                user -> user
                        .data()
                        .remove(Node
                                .builder("group." + plugin.getModerationModeGroup())
                                .build()
                        )
        );
        copies.remove(player.getUniqueId());
    }

}
