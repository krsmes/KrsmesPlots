package net.krsmes.bukkit.plugins.plots;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Custom event that gets fired when a player moves from one plot to another.
 *
 * If the event gets cancelled then the player is moved back to their 'from' plot.
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class PlotChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    protected boolean cancelled;

    public Player player;
    public Plot from;
    public Plot to;


    public PlotChangeEvent(Player player, Plot from, Plot to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
