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
public class CmdArea implements Cmd {

    private static enum Command {
        SHOW,
        ADD,
        CLEAR
    }

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        Command command = Command.valueOf(args.remove(0).toUpperCase());

        if (!args.isEmpty()) {
            for (String areaStr : args) {
                Area area = Area.parse(areaStr);
                switch (command) {
                    case ADD: currentPlot.addArea(area); break;
                    case CLEAR: currentPlot.removeAreas(); break;
                }
            }
        }

        return Arrays.asList(currentPlot.getAreas().toString());
    }

    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "SHOW|ADD AREA|CLEAR - edit plot areas X:Z-X:Z";
    }
}
