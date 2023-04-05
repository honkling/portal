package me.honkling.portal.portals

import me.honkling.portal.lib.*
import me.honkling.portal.world
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector

fun linkPortalPair(orange: PortalDisplay, blue: PortalDisplay) {
    orange.forEach { entity ->
        entity.setMetadata("link", FixedMetadataValue(instance, blue[0]))
    }

    blue.forEach { entity ->
        entity.setMetadata("link", FixedMetadataValue(instance, orange[0]))
    }
}

fun createPlayerPortal(type: PortalType, owner: Player): PortalDisplay? {
    world.entities.forEach { entity ->
        if (!entity.hasMetadata("owner") || !entity.hasMetadata("type"))
            return@forEach

        val frameOwner = entity.getMetadata("owner")[0].value() as Player
        val frameType = entity.getMetadata("type")[0].value() as PortalType

        if (frameOwner == owner && frameType == type)
            entity.remove()
    }

    val facing = owner.getTargetBlockFace(30)!!
    val block = owner.getTargetBlockExact(30)!!
    val frames = displayPortal(type, block, facing) ?: return null

    frames.forEach { it.setMetadata("owner", FixedMetadataValue(instance, owner)) }

    return frames
}

/**
 * Creates the item frames for the portal.
 * @param type The type of portal (orange, blue)
 * @param targetBlock The block the player was facing
 * @param targetFace The face of the block
 * @return A list of item frames
 */
fun displayPortal(type: PortalType, targetBlock: Block, targetFace: BlockFace): PortalDisplay? {
    val sister = getSisterBlock(targetBlock, targetFace) ?: return null
    val frames = mutableListOf<ItemFrame>()

    frames.add(createItemFrame(
        getFrameLocation(targetBlock, targetFace).location,
        targetFace))

    frames.add(createItemFrame(
        getFrameLocation(sister, targetFace).location,
        targetFace))

    frames[0].setItem(type.item)
    frames.forEach { it.setMetadata("type", FixedMetadataValue(instance, type)) }

    return frames
}

/**
 * Gets an adjacent block adequate for a portal, or null if none are found.
 * @param targetBlock The block the portal will be placed on
 * @param targetFace The face the portal will be placed on
 * @return A sibling block for the second item frame.
 */
fun getSisterBlock(targetBlock: Block, targetFace: BlockFace): Block? {
    return targetBlock
        .location
        .clone()
        .add(listOf(
            Vector(0, 1, 0),
            Vector(0, -1, 0),
            Vector(1, 0, 0),
            Vector(-1, 0, 0),
            Vector(0, 0, 1),
            Vector(0, 0, -1)
        ).find {
            val offsetBlock = targetBlock
                .location
                .clone()
                .add(it)
                .block

            return@find offsetBlock.type == targetBlock.type &&
                    getFrameLocation(offsetBlock, targetFace).canPassthrough() &&
                    getFrameLocation(targetBlock, targetFace).canPassthrough()
        } ?: return null)
        .block
}

/**
 * Gets the block that the item frame should be spawned at.
 * @param targetBlock The block to be placed on
 * @param targetFace The face to be placed on
 * @return The block to spawn the item frame at
 */
fun getFrameLocation(targetBlock: Block, targetFace: BlockFace): Block {
    return targetBlock
        .location
        .clone()
        .add(targetFace.direction)
        .block
}