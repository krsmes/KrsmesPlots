package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Area;
import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdDelete implements Cmd {

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        String name = args.remove(0);
        plots.removePlot(name);
        return Arrays.asList("Plot '" + name + "' deleted.");
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "PLOTNAME - permanently removes plot";
    }

}
