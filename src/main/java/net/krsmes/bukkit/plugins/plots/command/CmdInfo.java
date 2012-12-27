package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdInfo implements Cmd {

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        List<String> result = new ArrayList<String>();
        result.add("Plot: " + currentPlot.getName());
        if (!currentPlot.isPublic()) {
            result.add("  Owner: " + currentPlot.getOwner());
            result.add("  Size: " + currentPlot.getSize());
            result.add("  Area: " + currentPlot.getAreas());
        }
        result.add("  Options: " + currentPlot.getOptions());
        result.add("  Visitors: " + currentPlot.getVisitors());
        return result;
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return true;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "- shows details of plot";
    }
}
