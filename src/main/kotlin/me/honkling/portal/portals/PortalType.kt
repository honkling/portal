package me.honkling.portal.portals

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class PortalType(material: Material) {
    ORANGE(Material.ORANGE_WOOL),
    BLUE(Material.BLUE_WOOL);

    val item = ItemStack(material)
}