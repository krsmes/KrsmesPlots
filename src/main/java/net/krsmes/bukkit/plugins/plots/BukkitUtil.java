package net.krsmes.bukkit.plugins.plots;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global utility for interacting with Bukkit functionality that requires a plugin reference.
 *
 * @author krsmes
 * @since 12/24/2012
 */
public class BukkitUtil {

    public static BukkitUtil instance;
    public static ChatColor chatColor = ChatColor.DARK_AQUA;

    public static void initialize(Plugin plugin) {
        instance = new BukkitUtil(plugin);
    }

    private Plugin plugin;

    private BukkitUtil(Plugin plugin) {
        this.plugin = plugin;
    }



//
// public api
//

    public Configuration getConfig() {
        plugin.reloadConfig();
        return plugin.getConfig();
    }

    public void sendMessage(final Player player, final String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                new Runnable() {
                    public void run() {
                        player.sendMessage(message);
                    }
                }
        );
    }


    public void teleport(final Player player, final Location dest) {
        plugin.getServer().getScheduler().runTask(plugin,
                new Runnable() {
                    public void run() {
                        player.teleport(dest);
                    }
                }
        );
    }

    public void callEvent(Event event) {
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public Map<String, Object> getPlayerData(final Player player) {
        return getMetadataValue(player, plugin.getName(), new HashMap<String, Object>(5));
    }


    public <T> T getMetadataValue(Metadatable metadatable, String metadataKey, T defaultFixedValue) {
        List<MetadataValue> metadata = metadatable.getMetadata(metadataKey);
        for (MetadataValue value : metadata) {
            if (value.getOwningPlugin() == plugin) {
                //noinspection unchecked
                return (T) value.value();
            }
        }
        metadatable.setMetadata(metadataKey, new FixedMetadataValue(plugin, defaultFixedValue));
        return defaultFixedValue;
    }


}
