package cat.emir.echogen.guis

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.util.Slot

import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.Echogen
import cat.emir.echogen.events.PlayerChatListener
import cat.emir.echogen.toComponent
import cat.emir.echogen.toComponentList
import org.bukkit.entity.Player

class PrefixGUI(val plugin: Echogen, val player: Player) {
    fun openGUI() {
        val gui = ChestGui(6, ComponentHolder.of("<aqua>Prefix Selection".toComponent()))
        val outerPane = StaticPane(0, 0, 9, 6)

        val borderItem = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE).apply {
            editMeta {
                it.isHideTooltip = true
            }
        }

        listOf(
            1, 2, 3, 4, 5, 6, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
        ).forEach {
            outerPane.addItem(GuiItem(borderItem), Slot.fromIndex(it))
        }

        val resetItem = ItemStack(Material.RED_STAINED_GLASS).apply {
            editMeta {
                it.customName("<!i><red>Reset prefix to default.</red>".toComponent())
            }
        }

        val resetGuiItem = GuiItem(resetItem).apply {
            setAction {
                if (plugin.prefixDatabase.getPrefix(player) == null) return@setAction
                plugin.prefixDatabase.deletePrefix(player)
                openGUI()
            }
        }

        outerPane.addItem(resetGuiItem, 0, 0)
        gui.addPane(outerPane)

        val innerPane = PaginatedPane(1, 1, 7, 4)

        val prefixGuiItems = plugin.prefixManager.prefixes.map { prefix ->
            val selectedPrefix = plugin.prefixDatabase.getPrefix(player)
            var status = "locked"

            if (prefix.id == selectedPrefix) {
                status = "selected"

            } else if (prefix.id.startsWith("group-")) {
                val groupName = prefix.id.substring(6)
                if (plugin.luckPermsUtils.getUser(player)!!.cachedData.metaData.primaryGroup == groupName)
                    status = "default"

                else if (plugin.luckPermsUtils.getPlayerGroups(player).any { it.name == groupName })
                    status = "unlocked"

            } else if (player.hasPermission("echogen.chat.prefix." + prefix.id))
                status = "unlocked"

            val statusComponent = when (status) {
                "selected" -> "<gray>[<aqua>⏺</aqua>]</gray> <aqua>Selected</aqua>"
                "unlocked" -> "<gray>[<green>✔</green>]</gray> <green>Unlocked</green>"
                "default" ->  "<gray>[<yellow>⚠</yellow>]</gray> <yellow>Default Prefix</yellow>"
                else -> "<gray>[<red>❌</red>]</gray> <red>Locked</red>"
            }.toComponent()

            val item = ItemStack(Material.WRITABLE_BOOK).apply {
                editMeta { meta ->
                    meta.setEnchantmentGlintOverride(status == "selected")
                    meta.customName(prefix.name.toComponent()
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))

                    val description = prefix.description.joinToString("\n") { "<gray><i>$it</i></gray>" }

                    val lore = """
                        <description>

                        <gray>Message Preview:</gray>
                         <preview>

                        <status>"""
                        .trimIndent()
                        .replace("<description>", description)
                        .toComponentList(
                            Placeholder.component("preview",  PlayerChatListener(plugin)
                                .getChatFormat(player, "Preview message".toComponent(), prefix.prefix)),
                            Placeholder.component("status", statusComponent)
                        )

                    meta.lore(lore
                        .map { it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) })
                }
            }

            GuiItem(item).apply {
                setAction {
                    if (status != "unlocked") return@setAction
                    plugin.prefixDatabase.setPrefix(player, prefix.id)
                    openGUI()
                }
            }
        }

        innerPane.populateWithGuiItems(prefixGuiItems)
        gui.addPane(innerPane)

        gui.setOnGlobalClick {
            it.isCancelled = true
        }

        gui.show(player)
    }
}
