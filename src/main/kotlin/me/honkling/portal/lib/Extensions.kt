package me.honkling.portal.lib

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

fun BlockFace.toItemFrameFacing(): Int {
    return when (this) {
        BlockFace.DOWN -> 0
        BlockFace.UP -> 1
        BlockFace.SOUTH -> 2
        BlockFace.NORTH -> 3
        BlockFace.EAST -> 4
        BlockFace.WEST -> 5
        else -> -1
    }
}

fun Double.toRadian(): Double {
    return this * Math.PI / 180
}

fun BlockFace.toOffset(): Vector {
    return oppositeFace.direction
}

fun Block.canPassthrough(): Boolean {
    return type == Material.AIR || type == Material.LIGHT
}