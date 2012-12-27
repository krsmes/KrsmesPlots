package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.BukkitUtil;
import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdReload implements Cmd {

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        plots.load(BukkitUtil.instance.getConfig());
        return Arrays.asList(plots.allPlots().size() + " plots reloaded.");
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender.isOp();
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "- reloads plot data from config";
    }

}
