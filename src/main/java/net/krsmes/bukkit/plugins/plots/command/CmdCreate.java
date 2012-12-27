package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Area;
import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdCreate implements Cmd {

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        String name = args.remove(0);
        Area area = Area.parse(args.remove(0));
        Plot newPlot = plots.createPlot(name, area);
        return new CmdInfo().execute(plots, null, newPlot);
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "PLOTNAME AREA - create new plot with area X:Z-X:Z";
    }
}
