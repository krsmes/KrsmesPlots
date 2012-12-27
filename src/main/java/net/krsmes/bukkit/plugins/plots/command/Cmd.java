package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;

import java.util.List;


/**
 * @author krsmes
 */
public interface Cmd {

    List<String> execute(Plots plots, List<String> paramters, Plot currentPlot);

    boolean allowed(CommandSender sender, Plots plots, Plot currentPlot);

    String getDescription(Plots plots, Plot currentPlot);

}
