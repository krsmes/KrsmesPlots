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
public class CmdVisitor implements Cmd {

    private static enum Command {
        SHOW,
        ADD,
        REMOVE
    }

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        Command command = Command.valueOf(args.remove(0).toUpperCase());

        if (!args.isEmpty()) {
            for (String name : args) {
                switch (command) {
                    case ADD: currentPlot.addVisitor(name); break;
                    case REMOVE: currentPlot.removeVisitor(name); break;
                }
            }
        }

        return Arrays.asList(currentPlot.getVisitors().toString());
    }


    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender.getName().equals(currentPlot.getOwner()) || sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "ADD|REMOVE|SHOW USERNAME1 USERNAMEn - change plot visitors";
    }


}
