package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdOwner implements Cmd {

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        if (!args.isEmpty()) {
            String name = args.remove(0);
            if ("DELETE".equalsIgnoreCase(name)) {
                name = null;
            }
            currentPlot.setOwner(name);
        }
        return Arrays.asList("Plot '" + currentPlot.getName() + "' now owned by " + currentPlot.getOwner());
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "USERNAME|delete - assign plot owner or delete plot owner";
    }

}
