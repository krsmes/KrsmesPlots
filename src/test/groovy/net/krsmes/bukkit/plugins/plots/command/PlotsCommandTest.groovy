package net.krsmes.bukkit.plugins.plots.command

import net.krsmes.bukkit.plugins.plots.BukkitFixtures
import net.krsmes.bukkit.plugins.plots.BukkitUtil;
import net.krsmes.bukkit.plugins.plots.Plots
import org.junit.Before
import org.junit.Test

class PlotsCommandTest {

    Plots plots = new Plots()
    PlotsCommand command = new PlotsCommand(plots)

    @Before void initialize() {
        BukkitUtil.initialize(BukkitFixtures.plugin)
    }

    @Test void commandShouldNotBeNull() {
        assert command
    }

}
