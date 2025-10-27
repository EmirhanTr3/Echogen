package xyz.emirdev.echogen.guis;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.events.ChatEvent;
import xyz.emirdev.echogen.utils.LuckPermsUtils;
import xyz.emirdev.echogen.utils.Utils;

public class PrefixGUI {

    public static void openGUI(Player player) {
        ChestGui gui = new ChestGui(6, ComponentHolder.of(Utils.formatMessage("<aqua>Prefix Selection")));
        StaticPane outerPane = new StaticPane(0, 0, 9, 6);

        ItemStack borderItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        borderItem.editMeta(meta -> {
            meta.setHideTooltip(true);
        });

        List.of(
                1, 2, 3, 4, 5, 6, 7, 8,
                9, 17,
                18, 26,
                27, 35,
                36, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53)
                .forEach(n -> outerPane.addItem(new GuiItem(borderItem), Slot.fromIndex(n)));

        ItemStack resetItem = new ItemStack(Material.RED_STAINED_GLASS);
        resetItem.editMeta(meta -> {
            meta.customName(Utils.formatMessage("<!i><red>Reset prefix to default.</red>"));
        });

        GuiItem resetGuiItem = new GuiItem(resetItem);
        resetGuiItem.setAction(event -> {
            if (Echogen.get().getPrefixDatabase().getPrefix(player) == null)
                return;
            Echogen.get().getPrefixDatabase().deletePrefix(player);
            openGUI(player);
        });

        outerPane.addItem(resetGuiItem, 0, 0);

        gui.addPane(outerPane);

        PaginatedPane innerPane = new PaginatedPane(1, 1, 7, 4);

        List<GuiItem> prefixGuiItems = Echogen.get().getPrefixManager().getPrefixes().stream().map(prefix -> {
            String selectedPrefix = Echogen.get().getPrefixDatabase().getPrefix(player);
            String status = "locked";

            if (prefix.getId().equals(selectedPrefix)) {
                status = "selected";

            } else if (prefix.getId().startsWith("group-")) {
                String groupName = prefix.getId().substring(6);
                if (LuckPermsUtils.getUser(player).getCachedData().getMetaData().getPrimaryGroup().equals(groupName))
                    status = "default";

                else if (LuckPermsUtils.getPlayerGroups(player).stream()
                        .anyMatch(g -> g.getName().equals(groupName)))
                    status = "unlocked";

            } else if (player.hasPermission("echogen.chat.prefix." + prefix.getId())) {
                status = "unlocked";
            }

            Component statusComponent = switch (status) {
                case "selected" -> Utils.formatMessage("<gray>[<aqua>⏺</aqua>]</gray> <aqua>Selected</aqua>");
                case "unlocked" -> Utils.formatMessage("<gray>[<green>✔</green>]</gray> <green>Unlocked</green>");
                case "default" ->
                    Utils.formatMessage("<gray>[<yellow>⚠</yellow>]</gray> <yellow>Default Prefix</yellow>");
                default -> Utils.formatMessage("<gray>[<red>❌</red>]</gray> <red>Locked</red>");
            };

            final String finalStatus = status;

            ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
            item.editMeta(meta -> {
                meta.setEnchantmentGlintOverride(finalStatus == "selected");
                meta.customName(Utils.formatMessage(prefix.getName())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                String description = String.join("\n",
                        prefix.getDescription().stream().map(desc -> "<gray><i>" + desc + "</i></gray>").toList());

                List<Component> lore = Utils.formatMultiline("""
                        <description>

                        <gray>Message Preview:</gray>
                         <preview>

                        <status>"""
                        .replace("<description>", description),
                        Placeholder.component("preview", ChatEvent.getChatFormat(player,
                                Utils.formatMessage("Preview message"), prefix.getPrefix())),
                        Placeholder.component("status", statusComponent));
                meta.lore(lore.stream()
                        .map(line -> line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .toList());

            });

            GuiItem guiItem = new GuiItem(item);
            guiItem.setAction(event -> {
                if (!finalStatus.equals("unlocked"))
                    return;
                Echogen.get().getPrefixDatabase().setPrefix(player, prefix.getId());
                openGUI(player);
            });

            return guiItem;
        }).toList();

        innerPane.populateWithGuiItems(prefixGuiItems);

        gui.addPane(innerPane);

        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);
        });

        gui.show(player);
    }
}
