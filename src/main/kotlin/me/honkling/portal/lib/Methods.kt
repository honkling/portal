package me.honkling.portal.lib

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.EulerAngle

fun <T : Entity> spawnEntity(type: EntityType, location: Location, persistent: Boolean = false): T {
    val entity = location.world.spawnEntity(location, type) as T
    if (!persistent) entity.setMetadata("portalRemovable", FixedMetadataValue(instance, true))
    return entity
}
fun createRadio(loc: Location, y: Double) {
    val radio = createArmorStand(loc)
    radio.headPose = EulerAngle(0.0, y, 0.0)
    radio.equipment.helmet = ItemStack(Material.TORCH)
}

fun createCamera(loc: Location, angle: EulerAngle) {
    val camera = createArmorStand(loc)
    camera.setMetadata("cantPickup", FixedMetadataValue(instance, true))
    camera.equipment.helmet = ItemStack(Material.SOUL_TORCH)
    camera.headPose = angle
}

fun createItemFrame(loc: Location, facing: BlockFace): ItemFrame {
    val itemFrame = spawnEntity<ItemFrame>(
        EntityType.ITEM_FRAME,
        loc
    )

    itemFrame.isVisible = instance.config.debug
    itemFrame.setFacingDirection(facing, true)

    return itemFrame
}

fun createArmorStand(loc: Location): ArmorStand {
    val armorStand = spawnEntity<ArmorStand>(
        EntityType.ARMOR_STAND,
        loc
    )

    armorStand.setGravity(false)
    armorStand.isInvulnerable = true
    armorStand.isInvisible = true

    return armorStand
}
