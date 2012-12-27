package net.krsmes.bukkit.plugins.plots;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * PublicPlot ...
 *
 * @author krsmes
 * @since 2012-12-23
 */
public class PublicPlot extends Plot {
    private static final long serialVersionUID = 1L;

    public static String PUBLIC_PLOT_NAME = "PUBLIC";
    public static int PUBLIC_PLOT_START_DEPTH = 48;

    public static int[] BREAKABLE = new int[] {17,18,37,38,39,40,59,81,83,86};
    public static int[] PLACEABLE = new int[] {6,18,37,38,39,40,59,81,83,86,295,325,328,333,342,343,338,354};
    public static int[] INTERACTABLE = new int[] {26,54,58,61,64,69,71,77,93,94,95};


    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public PublicPlot(Map<String, Object> values) {
        super(values);
    }

    public PublicPlot() {
        super(PUBLIC_PLOT_NAME);
        setStartDepth(PUBLIC_PLOT_START_DEPTH);
        placeableArr = PLACEABLE;
        breakableArr = BREAKABLE;
        interactableArr = INTERACTABLE;
    }


    @Override
    public void setName(String name) {
    }

    @Override
    public void setOwner(String owner) {
    }

    @Override
    public void setOwner(Player owner) {
    }

    @Override
    public void setAreas(List<Area> areas) {
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public void addArea(Area area) {
    }

}
