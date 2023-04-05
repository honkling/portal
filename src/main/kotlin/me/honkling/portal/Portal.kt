package me.honkling.portal

import cc.ekblad.toml.decodeWithDefaults
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.tomlMapper
import me.honkling.commando.CommandManager
import me.honkling.portal.lib.*
import me.honkling.portal.listeners.HoldToggleListener
import me.honkling.portal.listeners.PlayerLifecycleListener
import me.honkling.portal.listeners.PortalGunListener
import me.honkling.portal.portals.displayPortal
import org.bukkit.Bukkit
import org.bukkit.PortalType
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.EulerAngle
import java.nio.file.Path

val world = Bukkit.getWorld("world")!!

class Portal : JavaPlugin() {
	lateinit var config: Config

	override fun onEnable() {
		registerListeners()

		CommandManager(this).registerCommands("me.honkling.portal.commands")
		config = setupConfig()
		setupChambers(config)

		logger.info("Welcome, gentlemen, to Aperture Science.")
	}

	override fun onDisable() {
		world.entities.forEach { if (it !is Player) it.remove() }

		logger.info("Cave Johnson. We're done here.")
	}

	fun setupChambers(config: Config) {
		config.chambers.forEach { chamber ->
			val portals = mutableListOf<PortalDisplay>()

			chamber.radios.forEach { createRadio(it.toLocation(), it.yRotation.toRadian()) }

			chamber.cameras.forEach { createCamera(it.toLocation(), EulerAngle(
				it.xRotation.toRadian(),
				it.yRotation.toRadian(),
				it.zRotation.toRadian())) }

			chamber.portals.forEach { portal ->
				displayPortal(
					portal.type,
					portal.toLocation().block,
					//portal.toLocation().subtract(it.direction.direction.clone().multiply(2)).block,
					portal.direction
				)?.let { portals.add(it) }
			}

			chamber.portals.forEachIndexed { index, portalInfo ->
				if (index >= portals.size || portalInfo.link >= portals.size)
					return@forEachIndexed

				val portal = portals[index]
				val link = portals[portalInfo.link]

				portal.forEach { it.setMetadata("link", FixedMetadataValue(this, link)) }
			}
		}
	}

	fun setupConfig(): Config {
		val mapper = tomlMapper {
			default(Config.TOMLLocation(0.0, 0.0, 0.0))

			mapping<Config.Chamber>(
				"radio" to "radios",
				"camera" to "cameras",
				"portal" to "portals"
			)

			decoder { it: TomlValue.String ->
				val value = it.value

				if (value in PortalType.values().map { it.name })
					PortalType.valueOf(value)

				if (value in BlockFace.values().map { it.name })
					BlockFace.valueOf(value)

				value
			}
		}

		saveResource("config.toml", false)
		val file = Path.of(dataFolder.absolutePath, "config.toml")

		return mapper.decodeWithDefaults(Config(), file)
	}

	private fun registerListeners() {
		val pluginManager = Bukkit.getPluginManager()

		pluginManager.registerEvents(PortalGunListener, this)
		pluginManager.registerEvents(PlayerLifecycleListener, this)
		pluginManager.registerEvents(HoldToggleListener, this)
	}
}
