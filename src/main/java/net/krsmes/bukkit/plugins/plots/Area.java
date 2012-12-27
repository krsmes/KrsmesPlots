package net.krsmes.bukkit.plugins.plots;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class Area implements ConfigurationSerializable {

    int minX;
    int maxX;
    int minZ;
    int maxZ;

    public Area(Map<String, Object> values) {
        this((Integer) values.get("minX"),
             (Integer) values.get("maxX"),
             (Integer) values.get("minZ"),
             (Integer) values.get("maxZ"));
    }

    public Area(int x1, int x2, int z1, int z2) {
        minX = Math.min(x1, x2);
        maxX = Math.max(x1, x2);
        minZ = Math.min(z1, z2);
        maxZ = Math.max(z1, z2);
    }

    public Area(Location loc1, Location loc2) {
        this(loc1.getBlockX(), loc2.getBlockX(), loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Area(Block blk1, Block blk2) {
        this(blk1.getX(), blk2.getX(), blk1.getZ(), blk2.getZ());
    }


    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }


    public int getWidth() {
        return (maxX - minX) + 1;
    }

    public int getDepth() {
        return (maxZ - minZ) + 1;
    }

    public int getSize() {
        return ((maxX - minX) + 1) * ((maxZ - minZ) + 1);
    }

    public int getCenterX() {
        return minX + (getWidth() / 2);
    }

    public int getCenterZ() {
        return minZ + (getDepth() / 2);
    }

    public boolean intersects(Area a) {
        return a.contains(minX, minZ) || a.contains(maxX, maxZ);
    }

    public boolean contains(int x, int z) {
        return (x >= minX && x <= maxX) && (z >= minZ && z <= maxZ);
    }

    boolean contains(Block block) {
        return contains(block.getX(), block.getZ());
    }

    boolean contains(Location loc) {
        return contains(loc.getBlockX(), loc.getBlockZ());
    }

    // 50:120-70:330
    public String toString() {
        return "" + minX + ':' + minZ + '-' + maxX + ':' + maxZ;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<String, Object>(4);
        result.put("minX", minX);
        result.put("minZ", minZ);
        result.put("maxX", maxX);
        result.put("maxZ", maxZ);
        return result;
    }

    public static Area parse(String str) {
        String[] elements = str.split("[:-]");
        return new Area(Integer.parseInt(elements[0]), Integer.parseInt(elements[2]), Integer.parseInt(elements[1]), Integer.parseInt(elements[3]));
    }
}
