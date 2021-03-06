package com.jackharrhy.storefront

import org.bukkit.block.*
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.WallSign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

class SignListener(private val plugin: Storefront, private val storage: Storage) : Listener {
	init {
		plugin.server.pluginManager.registerEvents(this, plugin)
	}

	private fun isWallSign(sign: Sign): Boolean {
		val blockData = sign.blockData
		return blockData is WallSign
	}

	private fun getBlockBehindWallSign(sign: Sign): Block {
		val signInverseFace = (sign.blockData as Directional).facing.oppositeFace
		return sign.block.getRelative(signInverseFace)
	}

	private fun getChestFromSign(sign: Sign): Chest? {
		if (!isWallSign(sign)) return null

		val blockBehindSignState = getBlockBehindWallSign(sign).state
		return if (blockBehindSignState is Chest) {
			blockBehindSignState
		} else null
	}

	private fun getStorefrontSign(block: Block): Sign? {
		val blockState = block.state as? Sign ?: return null

		return if (blockState.getLine(0).equals("[storefront]", ignoreCase = true)) {
			blockState
		} else null
	}

	@EventHandler
	fun onSignChange(event: SignChangeEvent) {
		if (event.getLine(0)!!.toLowerCase() == "[storefront]") {
			val block = event.block
			val sign = block.state as? Sign ?: return

			val chest = getChestFromSign(sign)

			if (chest != null) plugin.newStorefront(event.player, chest, sign)
		}
	}

	@EventHandler
	fun onSignBreak(event: BlockBreakEvent) {
		val player = event.player

		val sign = getStorefrontSign(event.block) ?: return

		val chest = getChestFromSign(sign) ?: return

		event.isCancelled = !this.plugin.removeStorefront(player, chest)
	}

	@EventHandler
	fun onSignInteract(event: PlayerInteractEvent) {
		val player = event.player

		if (event.action != Action.RIGHT_CLICK_BLOCK) return

		val sign = getStorefrontSign(event.clickedBlock!!) ?: return

		val chest = getChestFromSign(sign) ?: return

		if (storage.storefrontExists(chest.location)) {
			this.plugin.updateStorefront(player, chest, sign)
		} else {
			this.plugin.newStorefront(player, chest, sign)
		}
	}
}
