package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.PlotOption;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author krsmes
 */
public class CmdOption implements Cmd {

    private static enum Command {
        HELP,
        SHOW,
        ADD,
        REMOVE
    }

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        if (args.isEmpty()) {
            return Arrays.asList("try: " + Arrays.asList(Command.values()).toString());
        }
        Command command = Command.valueOf(args.remove(0).toUpperCase());

        if (command == Command.HELP) {
            return Arrays.asList(Arrays.asList(PlotOption.values()).toString());
        }

        for (String optionName : args) {
            PlotOption option = PlotOption.valueOf(optionName.toUpperCase());
            switch (command) {
                case ADD: currentPlot.addOption(option); break;
                case REMOVE: currentPlot.removeOption(option); break;
            }
        }

        return Arrays.asList(currentPlot.getOptions().toString());
    }


    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender.getName().equals(currentPlot.getOwner()) || sender instanceof ConsoleCommandSender;
    }

    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "ADD|REMOVE|SHOW|HELP OPTION1 OPTIONn - change plot options";
    }

}
