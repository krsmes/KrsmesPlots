package net.krsmes.bukkit.plugins.plots.command;

import net.krsmes.bukkit.plugins.plots.Plot;
import net.krsmes.bukkit.plugins.plots.Plots;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author krsmes
 */
public class CmdTypeId implements Cmd {

    private String name;
    private Method getter;
    private Method setter;

    public CmdTypeId(String name) {
        this.name = name;
        try {
            getter = Plot.class.getMethod("get" + name);
            setter = Plot.class.getMethod("set" + name, Set.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static enum Command {
        SHOW,
        ADD,
        REMOVE
    }

    @Override
    public List<String> execute(Plots plots, List<String> args, Plot currentPlot) {
        Command command = Command.valueOf(args.remove(0).toUpperCase());

        Set<Integer> ids = new HashSet<Integer>();
        for (String value : args) {
            ids.add(Integer.valueOf(value));
        }
        Set<Integer> currentIds = getTypeIds(currentPlot);

        switch (command) {
            case SHOW: break;
            case ADD: currentIds.addAll(ids); break;
            case REMOVE: currentIds.removeAll(ids); break;
        }
        setTypeIds(currentPlot, currentIds);

        return Arrays.asList(currentIds.toString());
    }


    @Override
    public boolean allowed(CommandSender sender, Plots plots, Plot currentPlot) {
        return sender.getName().equals(currentPlot.getOwner()) || sender instanceof ConsoleCommandSender;
    }


    @Override
    public String getDescription(Plots plots, Plot currentPlot) {
        return "ADD|REMOVE|SHOW ID1 IDn - change plot " + name + " blocks/items";
    }



    protected Set<Integer> getTypeIds(Plot plot) {
        try {
            //noinspection unchecked
            return (Set<Integer>) getter.invoke(plot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void setTypeIds(Plot plot, Set<Integer> typeIds) {
        try {
            setter.invoke(plot, typeIds);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


}
