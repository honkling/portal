package me.honkling.portal.listeners

import me.honkling.portal.lib.instance
import me.honkling.portal.lib.toOffset
import me.honkling.portal.world
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

private val taskIds = mutableMapOf<Player, BukkitTask>()

object PlayerLifecycleListener : Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        e.joinMessage(Component.empty())

        taskIds[player] = Bukkit.getScheduler().runTaskTimer(instance, Runnable {
            val portal = getPortalCollision(player.location)

            if (portal != null) {
                val linkedPortal = portal.getMetadata("link")[0]!!.value() as ItemFrame
                player.teleport(linkedPortal.location.clone().add(linkedPortal.facing.oppositeFace.toOffset()))
            }

            if (player.hasMetadata("holdingObject")) {
                val heldEntity = player.getMetadata("holdingObject")[0].value() as ArmorStand
                heldEntity.teleport(getRayOrReach(player.eyeLocation))
            }
        }, 0L, 1L)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player

        e.quitMessage()

        Bukkit.getScheduler().cancelTask(taskIds[player]!!.taskId)
    }

    private fun getPortalCollision(location: Location): ItemFrame? {
        location.world.entities.forEach { entity ->
            if (entity.type != EntityType.ITEM_FRAME ||
                !entity.hasMetadata("link") ||
                entity.location.distance(location) > 0.7
            ) return@forEach

            return@getPortalCollision entity as ItemFrame
        }

        return null
    }

    private fun getRayOrReach(location: Location, max: Double = 2.0): Location {
        val rayTrace = world.rayTrace(
            location,
            location.direction,
            max,
            FluidCollisionMode.NEVER,
            true,
            0.0) { false }

        val freeTeleport = location
            .clone()
            .add(location
                .direction
                .clone()
                .multiply(2))
            .subtract(0.0, 1.6, 0.0)

        if (rayTrace == null)
            return freeTeleport

        val rayPosition = rayTrace.hitPosition.toLocation(world)

        if (location.distance(freeTeleport) > location.distance(rayPosition)) {
            val offsetPosition = rayPosition.clone().subtract(0.0, 1.6, 0.0)
            val portal = getPortalCollision(rayPosition.clone())

            if (portal != null) {
                val linkedPortal = portal.getMetadata("link")[0].value() as ItemFrame
                val extraDistance = 2.0 - location.distance(rayPosition)
                val position = getRayOrReach(linkedPortal.location.clone().add(linkedPortal.facing.oppositeFace.toOffset()), extraDistance).clone()
                position.y = offsetPosition.y
                return position
            }

            return offsetPosition
        }

        return freeTeleport
    }
}