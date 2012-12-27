package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdList implements Cmd {

    @Override
    public List<String> execute(Plots plots, java.util.List<String> args, Plot currentPlot) {
        List<String> result = new ArrayList<String>();
        for (Plot plot : plots.allPlots()) {
            result.add(plot.toString() + " " + plot.getAreas());
        }
        return result;
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return true;
    }


    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "- shows list of all plots and areas";
    }

}
