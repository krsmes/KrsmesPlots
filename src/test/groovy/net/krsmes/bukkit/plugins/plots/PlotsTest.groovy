package net.krsmes.bukkit.plugins.plots

import org.bukkit.entity.CreatureType
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTargetEvent.TargetReason
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.weather.LightningStrikeEvent
import org.junit.Before
import org.junit.Test

/**
 * PlotsTest ...
 *
 * @author Kevin R. Smith - ksmith@pillartechnology.com
 * @since 2011-06-20
 */
public class PlotsTest {

    Plots plots
    Plot plot
    LivingEntity inPlotEntity
    Player inPlotOwner
    Player inPlotVisitor
    Player inPlotPlayer

    @Before
    void initialize() {
        BukkitUtil.initialize(BukkitFixtures.plugin);
        plots = new Plots()
        plots.load(BukkitFixtures.makeConfig([(Plots.ATTR_PLOT_PROTECTION): true]))
        plot = new Plot('testPlot', new Area(1, 5, 1, 5))
        plots.addPlot(plot)
        inPlotEntity = BukkitFixtures.makeEntity(LivingEntity, [location: BukkitFixtures.makeLocation(x: 1, y: 64, z: 1)])
        inPlotOwner = BukkitFixtures.makePlayer([location: BukkitFixtures.makeLocation(x: 2, y: 64, z: 2)])
        plot.owner = inPlotOwner.name
        inPlotVisitor = BukkitFixtures.makePlayer([location: BukkitFixtures.makeLocation(x: 2, y: 64, z: 2)])
        plot.addVisitor(inPlotVisitor)
        inPlotPlayer = BukkitFixtures.makePlayer([location: BukkitFixtures.makeLocation(x: 2, y: 64, z: 2)])

        assert plots.plotProtection
    }

    @Test
    void testFindPlot() {
        def plot = new Plot('testPlot', new Area(1, 5, 1, 5));
        plots.addPlot(plot);

        assert plots.findPlot('testPlot') == plot
        assert plots.findPlot('otherPlot') != plot

        assert plots.findPlot(1, 1) == plot
        assert plots.findPlot(5, 5) == plot
        assert plots.findPlot(1, 5) == plot
        assert plots.findPlot(5, 1) == plot
        assert plots.findPlot(0, 0) != plot
        assert plots.findPlot(6, 6) != plot

        assert plots.findPlot(plot, 1, 1) == plot
        assert plots.findPlot(plot, 0, 0) != plot
    }


    @Test
    void testEventConditions() {
        // should still spawn if not natural
        def event = new CreatureSpawnEvent(inPlotEntity, CreatureType.CHICKEN, BukkitFixtures.makeLocation(x: 1, y: 64, z: 1), SpawnReason.SPAWNER)
        plot.addOption(PlotOption.NO_SPAWN)
        expectCancelled(false, event, null, plots.&onCreatureSpawn)

        // should still target non-visitors
        event = new EntityTargetEvent(inPlotEntity, inPlotPlayer, TargetReason.CLOSEST_PLAYER)
        plot.addOption(PlotOption.NO_TARGET)
        expectCancelled(false, event, null, plots.&onEntityTarget)

        // should still damage non-visitors
        event = new EntityDamageEvent(inPlotPlayer, DamageCause.ENTITY_ATTACK, 5)
        plot.addOption(PlotOption.NO_DAMAGE)
        expectCancelled(false, event, null, plots.&onEntityDamage)
    }

    @Test
    void testEntityDamage() {
        testEvent(new EntityDamageEvent(inPlotVisitor, DamageCause.ENTITY_ATTACK, 5), PlotOption.NO_DAMAGE, plots.&onEntityDamage)
    }

    @Test
    void testBlockIgnite() {
        testEvent(new BlockIgniteEvent(BukkitFixtures.makeBlock([x:1,y:63,z:1,typeId:1]), IgniteCause.FLINT_AND_STEEL, inPlotOwner),
                PlotOption.NO_IGNITE, plots.&onBlockIgnite)
    }

    @Test
    void testLightningStrike() {
        testEvent(new LightningStrikeEvent(BukkitFixtures.world,
                BukkitFixtures.makeEntity(LightningStrike, [location:BukkitFixtures.makeLocation(x:1,y:64,z:1)])),
                PlotOption.NO_LIGHTNING, plots.&onLightningStrike)
    }

    @Test
    void testAsyncPlayerChat() {
        testEvent(new AsyncPlayerChatEvent(true, inPlotVisitor, "hey you", [] as Set), PlotOption.NO_CHAT, plots.&onAsyncPlayerChat)
    }

    @Test
    void testEntityTarget() {
        testEvent(new EntityTargetEvent(inPlotEntity, inPlotVisitor, TargetReason.CLOSEST_PLAYER), PlotOption.NO_TARGET, plots.&onEntityTarget)
    }

    @Test
    void testCreatureSpawn() {
        testEvent(new CreatureSpawnEvent(inPlotEntity, SpawnReason.NATURAL), PlotOption.NO_SPAWN, plots.&onCreatureSpawn)
    }

    @Test
    void testExplosionPrime() {
        testEvent(new ExplosionPrimeEvent(inPlotEntity, 3.0, false), PlotOption.NO_EXPLODE, plots.&onExplosionPrime)
    }

    def testEvent = { event, option, method ->
        expectCancelled(true, event, option, method)
        expectCancelled(false, event, option, method)
    }


    def expectCancelled = { expectCancelled, event, option, method ->
        if (option) {
            expectCancelled ? plot.addOption(option) : plot.removeOption(option)
        }
        event.cancelled = false
        method(event)
        assert event.cancelled == expectCancelled
    }


//    def expectCancelled = { cancelled, event, setup ->
//        setup()
//        event.cancelled = false
//        event()
//        plots.execute(plots, event)
//        assert event.cancelled == cancelled
//    }
}
