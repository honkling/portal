package me.honkling.portal.listeners

import io.papermc.paper.event.player.PlayerArmSwingEvent
import me.honkling.portal.portals.PortalType
import me.honkling.portal.portals.createPlayerPortal
import me.honkling.portal.portals.linkPortalPair
import me.honkling.portal.world
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object PortalGunListener : Listener {
    @EventHandler
    fun onLeftClick(e: PlayerArmSwingEvent) {
        val player = e.player
        val hand = e.hand
        val tool = player.inventory.getItem(hand)

        if (tool.type != Material.CROSSBOW) return

        e.isCancelled = true

        val orange = createPlayerPortal(PortalType.ORANGE, player) ?: return
        val blue = mutableListOf<ItemFrame>()

        world.entities.forEach { entity ->
            if (!entity.hasMetadata("owner") || !entity.hasMetadata("type"))
                return@forEach

            val frameOwner = entity.getMetadata("owner")[0].value() as Player
            val frameType = entity.getMetadata("type")[0].value() as PortalType

            if (frameOwner == player && frameType == PortalType.BLUE)
                blue.add(entity as ItemFrame)
        }

        if (blue.isNotEmpty())
            linkPortalPair(orange, blue)
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK)
            return

        val player = e.player
        val hand = e.hand!!
        val tool = player.inventory.getItem(hand)

        if (tool.type != Material.CROSSBOW) return

        e.isCancelled = true

        val blue = createPlayerPortal(PortalType.BLUE, player) ?: return
        val orange = mutableListOf<ItemFrame>()

        world.entities.forEach { entity ->
            if (!entity.hasMetadata("owner") || !entity.hasMetadata("type"))
                return@forEach

            val frameOwner = entity.getMetadata("owner")[0].value() as Player
            val frameType = entity.getMetadata("type")[0].value() as PortalType

            if (frameOwner == player && frameType == PortalType.ORANGE)
                orange.add(entity as ItemFrame)
        }

        if (orange.isNotEmpty())
            linkPortalPair(blue, orange)
    }
}