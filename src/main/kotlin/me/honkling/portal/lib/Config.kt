package me.honkling.portal.lib

import me.honkling.portal.portals.PortalType
import me.honkling.portal.world
import org.bukkit.Location
import org.bukkit.block.BlockFace

data class Config(
    val debug: Boolean = false,
    val chambers: List<Chamber> = emptyList()
) {
    data class Chamber(
        val radios: List<TOMLLocation> = emptyList(),
        val cameras: List<TOMLLocation> = emptyList(),
        val portals: List<Portal> = emptyList()
    )

    data class TOMLLocation(
        val x: Double,
        val y: Double,
        val z: Double,
        val xRotation: Double = 0.0,
        val yRotation: Double = 0.0,
        val zRotation: Double = 0.0
    ) {
        fun toLocation(): Location {
            return Location(world, x, y, z)
        }
    }

    data class Portal(
        val type: PortalType,
        val x: Double,
        val y: Double,
        val z: Double,
        val direction: BlockFace,
        val link: Int
    ) {
        fun toLocation(): Location {
            return Location(world, x, y, z)
        }
    }
}