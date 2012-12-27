package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.BukkitUtil;
import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;

/**
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class PlotsCommand implements CommandExecutor {

    private final Plots plots;

    static Map<String, Cmd> plotCommands = new LinkedHashMap<String, Cmd>(14);
    static {
        plotCommands.put("reload", new CmdReload());
        plotCommands.put("list", new CmdList());
        plotCommands.put("info", new CmdInfo());

        plotCommands.put("create", new CmdCreate());
        plotCommands.put("delete", new CmdDelete());
        plotCommands.put("owner", new CmdOwner());
        plotCommands.put("area", new CmdArea());

        plotCommands.put("visitor", new CmdVisitor());
        plotCommands.put("option", new CmdOption());

        plotCommands.put("placeable", new CmdTypeId("Placeable"));
        plotCommands.put("unplaceable", new CmdTypeId("Unplaceable"));
        plotCommands.put("breakable", new CmdTypeId("Breakable"));
        plotCommands.put("unbreakable", new CmdTypeId("Unbreakable"));
        plotCommands.put("interactable", new CmdTypeId("Interactable"));
        plotCommands.put("uninteractable", new CmdTypeId("Uninteractable"));
    }

    public PlotsCommand(Plots plots) {
        this.plots = plots;
    }


//
// CommandExecutor
//

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> parameters = new ArrayList<String>(Arrays.asList(args));

        Plot currentPlot = sender instanceof Entity ?  plots.findPlot((Entity) sender) : plots.getPublicPlot();

        if (parameters.isEmpty()) {
            for (Map.Entry<String, Cmd> entry: plotCommands.entrySet()) {
                if (entry.getValue().allowed(sender, plots, currentPlot)) {
                    sender.sendMessage(entry.getKey() + " " + entry.getValue().getDescription(plots, currentPlot));
                }
            }
        }
        else {
            String plotCommand = parameters.remove(0);
            Cmd cmd = plotCommands.get(plotCommand);

            if (cmd == null) {
                // try first command as the plot name instead
                currentPlot = plots.findPlot(plotCommand);
                if (currentPlot != null) {
                    plotCommand = parameters.remove(0);
                    cmd = plotCommands.get(plotCommand);
                }
            }

            if (cmd == null) {
                sender.sendMessage(BukkitUtil.chatColor + "Unknown plot command: " + plotCommand);
                sender.sendMessage(BukkitUtil.chatColor + "  try: " + plotCommands.keySet());
            }
            else if (!cmd.allowed(sender, plots, currentPlot)) {
                sender.sendMessage(BukkitUtil.chatColor + "You are not allowed to perform plot command: " + plotCommand);
            }
            else {
                try {
                    List<String> result = cmd.execute(plots, parameters, currentPlot);
                    for (String msg : result) {
                        sender.sendMessage(BukkitUtil.chatColor + msg);
                    }
                }
                catch (Throwable t) {
                    sender.sendMessage("ERROR: (" + t.getClass().getSimpleName() + ") " + t.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


//
// helper methods
//

}
