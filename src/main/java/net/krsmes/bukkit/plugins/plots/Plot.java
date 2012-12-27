package net.krsmes.bukkit.plugins.plots;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;


/**
 * Plot definition.
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class Plot implements ConfigurationSerializable {
    private static final long serialVersionUID = 1L;

    public static int PLOT_START_DEPTH = 32;

    String name;
    String owner;
    Location home;
    int startDepth = PLOT_START_DEPTH;

    List<Area> areas = new ArrayList<Area>();
    List<String> visitors = new ArrayList<String>();
    Set<PlotOption> options = new HashSet<PlotOption>();

    int[] placeableArr = new int[0];
    int[] breakableArr = new int[0];
    int[] interactableArr = new int[0];

    int[] unplaceableArr = new int[0];
    int[] unbreakableArr = new int[0];
    int[] uninteractableArr = new int[0];

    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public Plot(Map<String, Object> values) {
        name = (String) values.get("name");
        owner = (String) values.get("owner");
        visitors = (List<String>) values.get("visitors");
        startDepth = (Integer) values.get("startDepth");
        options = makeOptionsFromString((String) values.get("options"));
        placeableArr = makeIntArrayFromString((String) values.get("placeable"));
        unplaceableArr = makeIntArrayFromString((String) values.get("unplaceable"));
        breakableArr = makeIntArrayFromString((String) values.get("breakable"));
        unbreakableArr = makeIntArrayFromString((String) values.get("unbreakable"));
        interactableArr = makeIntArrayFromString((String) values.get("interactable"));
        uninteractableArr = makeIntArrayFromString((String) values.get("uninteractable"));
        areas = (List<Area>) values.get("areas");
    }

    public Plot() {
    }

    public Plot(String name) {
        this.name = name;
    }

    public Plot(String name, Area area) {
        this(name);
        if (area != null) { areas.add(area); }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<String, Object>(32);
        result.put("name", name);
        result.put("owner", owner);
        result.put("visitors", visitors);
        result.put("startDepth", startDepth);
        result.put("options", options.toString());
        result.put("areas", areas);
        result.put("placeable", Arrays.toString(placeableArr));
        result.put("unplaceable", Arrays.toString(unplaceableArr));
        result.put("breakable", Arrays.toString(breakableArr));
        result.put("unbreakable", Arrays.toString(unbreakableArr));
        result.put("interactable", Arrays.toString(interactableArr));
        result.put("uninteractable", Arrays.toString(uninteractableArr));
        return result;
    }



    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Plot) && (name.equals(((Plot) o).name));
    }

    @Override
    public String toString() {
        return name + (owner==null?"":'('+owner+')');
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwner(Player owner) {
        setOwner(owner.getName());
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public void removeAreas() {
        this.getAreas().clear();
    }

    public List<String> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<String> visitors) {
        this.visitors = visitors;
    }

    public Set<PlotOption> getOptions() {
        return Collections.unmodifiableSet(options);
    }

    public boolean hasOption(PlotOption option) {
        return options.contains(option);
    }

    public void addOption(PlotOption option) {
        options.add(option);
    }

    public void removeOption(PlotOption option) {
        options.remove(option);
    }

    public int getStartDepth() {
        return startDepth;
    }

    public void setStartDepth(int startDepth) {
        this.startDepth = startDepth;
    }


    public Set<Integer> getPlaceable() {
        return intArrToSet(placeableArr);
    }

    public void setPlaceable(Set<Integer> value) {
        placeableArr = setToIntArr(value);
    }

    public Set<Integer> getBreakable() {
        return intArrToSet(breakableArr);
    }

    public void setBreakable(Set<Integer> value) {
        breakableArr = setToIntArr(value);
    }

    public Set<Integer> getInteractable() {
        return intArrToSet(interactableArr);
    }

    public void setInteractable(Set<Integer> value) {
        interactableArr = setToIntArr(value);
    }


    public Set<Integer> getUnplaceable() {
        return intArrToSet(unplaceableArr);
    }

    public void setUnplaceable(Set<Integer> value) {
        unplaceableArr = setToIntArr(value);
    }

    public Set<Integer> getUnbreakable() {
        return intArrToSet(unbreakableArr);
    }

    public void setUnbreakable(Set<Integer> value) {
        unbreakableArr = setToIntArr(value);
    }

    public Set<Integer> getUninteractable() {
        return intArrToSet(uninteractableArr);
    }

    public void setUninteractable(Set<Integer> value) {
        uninteractableArr = setToIntArr(value);
    }


    public boolean isPublic() {
        return false;
    }

    public int getSize() {
        int result = 0;
        for (Area a : areas) {
            result += a.getSize();
        }
        return result;
    }

    public void addArea(Area area) {
        this.areas.add(area);
    }

    public void addVisitor(String visitor) {
        this.visitors.add(visitor);
    }

    public void addVisitor(Player player) {
        addVisitor(player.getName());
    }

    public void removeVisitor(String visitor) {
        this.visitors.remove(visitor);
    }

    public void removeVisitor(Player player) {
        removeVisitor(player.getName());
    }

    public boolean allowArrival(Player player) {
        return !hasOption(PlotOption.NO_ENTRY) || allowed(player);
    }

    public boolean allowDeparture(Player player) {
        return true;
    }

    public void arrive(Player player, Map<String, Object> playerData) {
        if (hasOption(PlotOption.NO_INVENTORY)) {
            exchangeInventory(player, playerData, "inv." + getName(), "inv");
        }
    }

    public void depart(Player player, Map<String, Object> playerData) {
        if (hasOption(PlotOption.NO_INVENTORY)) {
            exchangeInventory(player, playerData, "inv", "inv."+getName());
        }
    }

    public boolean contains(int x, int z) {
        for (Area area : areas) {
            if (area.contains(x, z)) { return true; }
        }
        return false;
    }

    public boolean allowed(String name) {
        return hasOption(PlotOption.OPEN) || name.equals(owner) || visitors.contains(name);
    }

    public boolean allowed(Player player) {
        return hasOption(PlotOption.OPEN) || allowed(player.getName());
    }


    public void processEvent(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getY() < startDepth) {
            return;
        }
        if (!allowInteraction(e.getPlayer(), block, e.getItem())) {
            e.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    public void processEvent(BlockDamageEvent e) {
        Block block = e.getBlock();
        if (block.getY() < startDepth) {
            return;
        }
        if (!allowDamage(e.getPlayer(), block)) {
            e.setCancelled(true);
        }
    }


    protected void exchangeInventory(Player player, Map<String, Object> playerData, String fromName, String toName) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contentsFrom = (ItemStack[]) playerData.get(fromName);
        ItemStack[] contentsTo = inventory.getContents();
        if (contentsFrom == null) {
            contentsFrom = new ItemStack[inventory.getSize()];
        }
        inventory.setContents(contentsFrom);
        playerData.put(toName, contentsTo);
    }

    protected boolean allowTypeId(Player player, int typeId, int[] allowedTypeIds, int[] unallowedTypeIds) {
        return (allowed(player) && Arrays.binarySearch(unallowedTypeIds, typeId) < 0) || Arrays.binarySearch(allowedTypeIds, typeId) >= 0;
    }

    protected boolean allowDamage(Player player, Block block) {
        // assumes plot contains block.x and block.z
        return allowTypeId(player, block.getTypeId(), breakableArr, unbreakableArr);
    }

    protected boolean allowInteraction(Player player, Block block, ItemStack item) {
        // assumes plot contains block.x and block.z
        int typeId = item == null ? 0 : item.getTypeId();
        if (typeId > 0) {
            return allowTypeId(player, typeId, placeableArr, unplaceableArr);
        }
        typeId = block.getTypeId();
        return allowTypeId(player, typeId, interactableArr, uninteractableArr);
    }


    static Set<Integer> intArrToSet(int[] arr) {
        Set<Integer> result = new HashSet<Integer>(arr.length);
        for (int i : arr) {
            result.add(i);
        }
        return result;
    }

    static int[] setToIntArr(Set<Integer> value) {
        int[] result = new int[value.size()];
        int idx = 0;
        for (int i : value) {
            result[idx++] = i;
        }
        Arrays.sort(result);
        return result;
    }

    static Set<PlotOption> makeOptionsFromString(String optionsStr) {
        Set<PlotOption> result = new HashSet<PlotOption>();
        for (String optionName : optionsStr.split("[\\[\\], ]")) {
            if (optionName.length() > 0) {
                try {
                    result.add(PlotOption.valueOf(optionName));
                }
                catch (Exception ignore) {
                }
            }
        }
        return result;
    }

    static int[] makeIntArrayFromString(String intArrStr) {
        List<Integer> result = new ArrayList<Integer>();
        if (intArrStr != null) {
            for (String intStr : intArrStr.split("[\\[\\], ]")) {
                if (intStr.length() > 0) {
                    result.add(Integer.valueOf(intStr));
                }
            }
        }
        int[] resultArr = new int[result.size()];
        for (int count = 0; count < resultArr.length; count++) {
            resultArr[count] = result.get(count);
        }
        return resultArr;
    }

}
