package com.github.jenya705.cubicore.moderation;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jenya705
 */
public class CubicConfig {

    private final CubicModeration plugin;

    private final Set<Material> placingMaterials;
    private final Set<Material> breakingMaterials;
    private final String moderationModeGroup;

    public CubicConfig(CubicModeration plugin) {
        this.plugin = plugin;
        breakingMaterials = loadEnumerator("notifyBreak", Material.class);
        placingMaterials = loadEnumerator("notifyPlace", Material.class);
        moderationModeGroup = plugin.getConfig().getString("moderationModeGroup");
    }

    public boolean isBreakingNotify(Material material) {
        return breakingMaterials.contains(material);
    }

    public boolean isPlacingNotify(Material material) {
        return placingMaterials.contains(material);
    }

    public String getModerationModeGroup() {
        return moderationModeGroup;
    }

    private <T extends Enum<T>> Set<T> loadEnumerator(String key, Class<T> enumClass) {
        List<String> list = plugin.getConfig().getStringList(key);
        Set<T> result = new HashSet<>();
        list.forEach(it -> result.add(Enum.valueOf(enumClass, it)));
        return result;
    }

}
