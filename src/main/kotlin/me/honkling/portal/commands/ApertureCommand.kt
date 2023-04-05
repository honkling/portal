@file:Command(
    "aperture",
    permission = "portal.aperture",
    permissionMessage = "&cGLaDOS> &7Make no further attempt to leave the testing area.",
    usage = "/aperture reload",
    description = "Admin-only command for managing the server"
)

package me.honkling.portal.commands

import me.honkling.commando.lib.Command
import me.honkling.portal.lib.instance
import me.honkling.portal.world
import org.bukkit.entity.Player

fun reload(player: Player) {
    world.entities.forEach { if (it !is Player) it.remove() }
    instance.config = instance.setupConfig()
    instance.setupChambers(instance.config)
    player.sendMessage("&cGLaDOS> &7Congratulations! The test is now over.")
}