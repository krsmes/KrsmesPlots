package net.krsmes.bukkit.plugins.plots;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.*;
import java.util.logging.Logger;

/**
 * Singleton class for Plots suppport
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class Plots implements Listener {

    private static final Logger LOG = Logger.getLogger("Minecraft");

    public static String ATTR_PLOT = "plot";
    public static String ATTR_DATA = "plotsData";
    public static String ATTR_PUBLIC = "plotsPublic";
    public static String ATTR_PLOT_PROTECTION = "plotProtection";

    private Map<String, Plot> plots;
    private Plot publicPlot;
    private boolean plotProtection;
    private boolean debug;


    public Plots() {
        LOG.info("Plots: creating instance");
    }


//
// properties
//

    public Plot getPublicPlot() {
        return publicPlot;
    }

    public void setPublicPlot(Plot publicPlot) {
        this.publicPlot = publicPlot;
    }

    public boolean isPlotProtection() {
        return plotProtection;
    }

    public void setPlotProtection(boolean plotProtection) {
        this.plotProtection = plotProtection;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


//
// public api
//

    @SuppressWarnings("unchecked")
    public synchronized void load(ConfigurationSection config) {
        debug = config.getBoolean("debug");
        if (debug) {
            LOG.info("Plots: debug enabled");
        }

        plotProtection = config.getBoolean(ATTR_PLOT_PROTECTION, false);
        if (plotProtection) {
            LOG.info("Plots: protection enabled");
        }

        publicPlot = (Plot) config.get(ATTR_PUBLIC, new PublicPlot());

        plots = new HashMap<String, Plot>();
        ConfigurationSection plotsConfig = config.getConfigurationSection(ATTR_DATA);
        if (plotsConfig != null) {
            Map<String, Object> plotsData = plotsConfig.getValues(true);
            for (Map.Entry<String, Object> entry : plotsData.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Plot) {
                    plots.put(entry.getKey(), (Plot) value);
                }
            }
        }

        if (plots.isEmpty()) {
            Plot defaultPlot = createPlot("default", new Area(1,1,-1,-1));
            for (PlotOption option : PlotOption.values()) {
                defaultPlot.addOption(option);
            }
        }
        LOG.info("Plots: load " + plots.size() + " plots");
    }


    public synchronized void save(ConfigurationSection config) {
        config.set(ATTR_PLOT_PROTECTION, plotProtection);
        config.set(ATTR_PUBLIC, publicPlot);
        if (!plots.isEmpty()) {
            config.set(ATTR_DATA, plots);
        }
        config.set("debug", debug);
    }


    public Collection<Plot> allPlots() {
        return Collections.unmodifiableCollection(plots.values());
    }


    public Plot addPlot(Plot plot) {
        LOG.info("Plots: adding " + plot.getName());
        return plots.put(plot.getName(), plot);
    }


    public Plot createPlot(String name, Area area) {
        Plot result = null;
        if (!plots.containsKey(name) && !name.equalsIgnoreCase(PublicPlot.PUBLIC_PLOT_NAME)) {
            result = new Plot(name, area);
            addPlot(result);
        }
        return result;
    }

    public Plot createPlot(String name, Area area, World world) {
        Plot result = createPlot(name, area);
        if (result != null && world != null) {
            int x = area.getCenterX();
            int z = area.getCenterZ();
            int y = world.getHighestBlockYAt(x, z);
            result.setHome(new Location(world, x + 0.5, y + 1.0, z + 0.5));
        }
        return result;
    }


    public void removePlot(String name) {
        plots.remove(name);
    }


    public Plot findPlot(int x, int z) {
        Plot result = findPlot(plots.values(), x, z);
        if (result == null) {
            result = publicPlot;
        }
        return result;
    }

    /*
    Does a normal x/z plot find but first checks "firstCheck" plot for optimization
     */
    public Plot findPlot(Plot firstCheck, int x, int z) {
        Plot result = firstCheck;
        if (firstCheck == null || !firstCheck.contains(x, z)) {
            result = findPlot(x, z);
        }
        return result;
    }


    public Plot findPlot(Location loc) {
        return (loc == null) ? publicPlot : findPlot(loc.getBlockX(), loc.getBlockZ());
    }


    public Plot findPlot(Entity ent) {
        return (ent == null) ? publicPlot : findPlot(ent.getLocation());
    }


    public Plot findPlot(String name) {
        return plots.get(name);
    }


    public List<Plot> findOwnedPlots(String owner) {
        List<Plot> result = new ArrayList<Plot>();
        if (owner != null) {
            for (Plot plot : plots.values()) {
                if (owner.equals(plot.getOwner())) {
                    result.add(plot);
                }
            }
        }
        return result;
    }




//
// Bukkit events
//

    // NO_CHAT
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onAsyncPlayerChat"); }
            event.setCancelled(findPlot(event.getPlayer()).hasOption(PlotOption.NO_CHAT));
        }
    }

    // breakable
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDamage(BlockDamageEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockDamage"); }
            Player player = event.getPlayer();
            Map<String, Object> playerData = getPlayerData(player);
            Plot current = playerData == null ? null : (Plot) playerData.get(ATTR_PLOT);
            Block block = event.getBlock();
            if (block != null) {
                findPlot(current, block.getX(), block.getZ()).processEvent(event);
            }
        }
    }

    // NO_BLOCK_EXP
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockExp(BlockExpEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockFromTo"); }
            if (findPlot(event.getBlock().getLocation()).hasOption(PlotOption.NO_BLOCK_EXP)) {
                event.setExpToDrop(0);
            }
        }
    }

    // NO_MELT
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFade(BlockFadeEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockFade"); }
            Block block = event.getBlock();
            Material type = block.getType();
            if (type == Material.ICE || type == Material.SNOW || type == Material.SNOW_BLOCK) {
                event.setCancelled(findPlot(block.getLocation()).hasOption(PlotOption.NO_MELT));
            }
        }
    }

    // NO_LAVA_FLOW, NO_WATER_FLOW
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockFromTo"); }
            Material type = event.getBlock().getType();
            Plot plot = findPlot(event.getToBlock().getLocation());
            if (type == Material.LAVA || type == Material.STATIONARY_LAVA) {
                event.setCancelled(plot.hasOption(PlotOption.NO_LAVA_FLOW));
            }
            else if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                event.setCancelled(plot.hasOption(PlotOption.NO_WATER_FLOW));
            }
        }
    }

    // NO_GROW                                                 `
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockGrow(BlockGrowEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockGrow"); }
            event.setCancelled(findPlot(event.getBlock().getLocation()).hasOption(PlotOption.NO_GROW));
        }
    }

    // NO_IGNITE
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onBlockIgnite"); }
            event.setCancelled(findPlot(event.getBlock().getLocation()).hasOption(PlotOption.NO_IGNITE));
        }
    }

    // NO_SPAWN
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onCreatureSpawn"); }
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                Plot current = findPlot(event.getLocation());
                event.setCancelled(current.hasOption(PlotOption.NO_SPAWN));
            }
        }
    }

    // NO_DAMAGE
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onEntityDamage"); }
            Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
            if (player != null) {
                Plot current = findPlot(player);
                event.setCancelled(current.hasOption(PlotOption.NO_DAMAGE_ALL) || (current.hasOption(PlotOption.NO_DAMAGE) && current.allowed(player)));
            }
        }
    }

    // NO_TARGET
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onEntityTarget"); }
            Player player = event.getTarget() instanceof Player ? (Player) event.getTarget() : null;
            if (player != null) {
                Plot current = findPlot(player);
                event.setCancelled(current.hasOption(PlotOption.NO_TARGET) && current.allowed(player));
            }
        }
    }

    // NO_EXPLODE
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onExplosionPrime"); }
            Plot current = findPlot(event.getEntity());
            event.setCancelled(current.hasOption(PlotOption.NO_EXPLODE));
            if (current.hasOption(PlotOption.NO_IGNITE)) { event.setFire(false); }
        }
    }

    // NO_HUNGER
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onFoodLevelChange"); }
            event.setCancelled(findPlot(event.getEntity()).hasOption(PlotOption.NO_HUNGER));
        }
    }

    // NO_LIGHTNING
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLightningStrike(LightningStrikeEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onLightningStrike"); }
            event.setCancelled(findPlot(event.getLightning()).hasOption(PlotOption.NO_LIGHTNING));
        }
    }

    // NO_CREATIVE
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onPlayerGameModeChange"); }
            Player player = event.getPlayer();
            Map<String, Object> playerData = getPlayerData(player);
            Plot current = playerData == null ? null : (Plot) playerData.get(ATTR_PLOT);
            if (current != null && current.hasOption(PlotOption.NO_CREATIVE) && event.getNewGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    // interactable, placeable
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onPlayerInteract"); }
            Player player = event.getPlayer();
            Map<String, Object> playerData = getPlayerData(player);
            Plot current = playerData == null ? null : (Plot) playerData.get(ATTR_PLOT);
            Block block = event.getClickedBlock();
            if (block != null) {
                findPlot(current, block.getX(), block.getZ()).processEvent(event);
            }
        }
    }

    // move event
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onPlayerMove"); }
            processMoveEvent(getPlayerData(event.getPlayer()), event);
        }
    }

    // move event
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plotProtection) {
            if (debug) { LOG.info("Plots::onPlayerTeleport"); }
            processMoveEvent(getPlayerData(event.getPlayer()), event);
        }
    }



//
// helper methods
//

    private Map<String, Object> getPlayerData(Player player) {
        return BukkitUtil.instance.getPlayerData(player);
    }



    protected void processMoveEvent(Map<String, Object> playerData, PlayerMoveEvent e) {
        if (playerData != null) {
            Location to = e.getTo();
            int toX = to.getBlockX();
            int toZ = to.getBlockZ();
            Location from = e.getFrom();
            int fromX = from.getBlockX();
            int fromZ = from.getBlockZ();
            // see if player moved off of block horizontally
            if (toX != fromX || toZ != fromZ || e instanceof PlayerTeleportEvent) {
                Player player = e.getPlayer();
                Plot currentPlot = playerData.containsKey(ATTR_PLOT) ? (Plot) playerData.get(ATTR_PLOT) : null;
                // see if new location is in the same plot (faster than doing a full plot scan)
                if (currentPlot != null && currentPlot.contains(toX, toZ)) {
                    if (e instanceof PlayerTeleportEvent && currentPlot.hasOption(PlotOption.NO_TELEPORT)) {
                        // no teleporting within current plot
                        e.setCancelled(true);
                    }
                }
                else {
                    // where are we now?
                    Plot destinationPlot = findPlot(toX, toZ);
                    if (destinationPlot != currentPlot) {
                        if (currentPlot != null && e instanceof PlayerTeleportEvent && currentPlot.hasOption(PlotOption.NO_TELEPORT)) {
                            // no teleporting out of current plot
                            e.setCancelled(true);
                        }
                        else if (plotChange(player, playerData, currentPlot, destinationPlot)) {
                            // plot change is allowed...
                            if (destinationPlot.hasOption(PlotOption.NO_CREATIVE) && player.getGameMode() == GameMode.CREATIVE) {
                                player.setGameMode(GameMode.SURVIVAL);
                            }
                            playerData.put(ATTR_PLOT, destinationPlot);
                            BukkitUtil.instance.sendMessage(player, BukkitUtil.chatColor + "Now in plot " + destinationPlot);
                        }
                        else {
                            // plot change is not allowed...
                            e.setCancelled(true);
                            if (!(e instanceof PlayerTeleportEvent)) {
                                BukkitUtil.instance.teleport(player, from);
                            }
                        }
                    }
                }
            }
        }
    }


    protected boolean plotChange(Player p, Map<String, Object> playerData, Plot from, Plot to) {
        if (debug) { LOG.info("Plots::plotChange(" + p.getName() + ")"); }
        // first ask plots if departure and arrival are allowed
        if ((from == null || from.allowDeparture(p)) && (to == null || to.allowArrival(p))) {
            // fire PlotChangeEvent
            PlotChangeEvent pce = new PlotChangeEvent(p, from, to);
            BukkitUtil.instance.callEvent(pce);
            // make sure it isn't cancelled
            if (!pce.isCancelled()) {
                // notify from/to plots of departure/arrival
                if (from != null) { from.depart(p, playerData); }
                if (to != null) { to.arrive(p, playerData); }
                return true;
            }
        }
        return false;
    }


    private static Plot findPlot(Collection<Plot> plots, int x, int z) {
        if (plots != null) {
            for (Plot plot : plots) {
                if (plot.contains(x, z)) {
                    return plot;
                }
            }
        }
        return null;
    }

}
