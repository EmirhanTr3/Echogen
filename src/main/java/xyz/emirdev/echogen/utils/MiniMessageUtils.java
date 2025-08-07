package xyz.emirdev.echogen.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echogen.Echogen;

public class MiniMessageUtils {
    private long ticks = 0;
    private Map<String, Double> animatedGradientStep = new HashMap<>();

    public void run() {
        Bukkit.getScheduler().runTaskTimer(Echogen.get(), () -> ticks++, 0, 1);
    }

    // <animatedgradient:id:color1:color...:tick:step>
    public TagResolver animatedGradientTag() {
        return TagResolver.resolver("animatedgradient", (args, ctx) -> {
            String id = args.pop().value();
            List<TextColor> colors = new ArrayList<>();
            Long tick = null;
            Double step = null;

            while (args.hasNext()) {
                Tag.Argument arg = args.pop();
                String value = arg.value();
                TextColor color = resolveColorOrNull(value);
                if (color != null) {
                    colors.add(color);
                } else {
                    tick = Long.valueOf(value);
                    step = Double.valueOf(args.pop().value());
                    break;
                }
            }

            if (tick == null || step == null)
                return Tag.selfClosingInserting(Component.text("invalid_animation_gradient"));

            if (animatedGradientStep.containsKey(id)) {
                if (ticks % tick == 0) {
                    animatedGradientStep.put(id, Double.sum(animatedGradientStep.get(id), step));
                    if (animatedGradientStep.get(id) > 1)
                        animatedGradientStep.put(id, -1d);
                    if (animatedGradientStep.get(id) < -1)
                        animatedGradientStep.put(id, 1d);
                }
            } else {
                animatedGradientStep.put(id, 0d);
            }

            return Tag.preProcessParsed(
                    "<gradient:" +
                            String.join(":", colors.stream().map(c -> c.toString()).toList()) +
                            ":" +
                            animatedGradientStep.get(id) +
                            ">");
        });
    }

    public static TextColor resolveColorOrNull(String colorName) {
        TextColor color;
        if (colorName.charAt(0) == TextColor.HEX_CHARACTER) {
            color = TextColor.fromHexString(colorName);
        } else {
            color = NamedTextColor.NAMES.value(colorName);
        }

        return color;
    }
}
