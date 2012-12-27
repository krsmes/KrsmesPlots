package net.krsmes.bukkit.plugins.plots;

import net.krsmes.bukkit.plugins.plots.command.PlotsCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


/**
 * Bukkit plugin.
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class KrsmesPlotsPlugin extends JavaPlugin implements Listener {

    private static final Logger LOG = Logger.getLogger("Minecraft");

    private Plots plots;


    public KrsmesPlotsPlugin() {
        BukkitUtil.initialize(this);
    }



//
// JavaPlugin
//

    @Override
    public void onDisable() {
        LOGinfo("onDisable");
        save();
        unregister();
    }


    @Override
    public void onEnable() {
        LOGinfo("onEnable");
        load();
        register();
    }



//
// Bukkit events
//

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        LOGinfo("onPlayerLogin", event.getPlayer().getName());
    }


    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        LOGinfo("onWorldSave");
        // never called ?!
        save();
    }



//
// public methods
//



//
// helper methods
//

    protected void load() {
        LOGinfo("load");
        if (plots == null) {
            plots = new Plots();
        }
        plots.load(getConfig());
    }

    protected void save() {
        LOGinfo("save");
        plots.save(getConfig());
        saveConfig();
    }

    protected void register() {
        LOGinfo("register");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(plots, this);
        getCommand("plots").setExecutor(new PlotsCommand(plots));
    }

    protected void unregister() {
        LOGinfo("unregister");
        HandlerList.unregisterAll((Plugin) this);
        getCommand("plots").setExecutor(null);
    }


    protected void LOGinfo(String method) {
        LOG.info(getName() + "::" + method + "()");
    }

    protected void LOGinfo(String method, String message) {
        LOG.info(getName() + "::" + method + "() " + message);
    }



//
// static helpers
//

    static {
        ConfigurationSerialization.registerClass(Area.class);
        ConfigurationSerialization.registerClass(Plot.class);
        ConfigurationSerialization.registerClass(PublicPlot.class);
    }

}
