package me.honkling.portal.listeners

import me.honkling.portal.lib.instance
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.metadata.FixedMetadataValue

object HoldToggleListener : Listener {
    @EventHandler
    fun onHoldToggle(e: PlayerSwapHandItemsEvent) {
        val player = e.player
        e.isCancelled = true

        if (player.hasMetadata("holdingObject"))
            return player.removeMetadata("holdingObject", instance)

        val target = player.getTargetEntity(2) ?: return

        if (target.type != EntityType.ARMOR_STAND || target.hasMetadata("cantPickup")) return

        player.setMetadata("holdingObject", FixedMetadataValue(instance, target))
    }
}