package xyz.emirdev.echogen.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.Getter;
import org.spongepowered.configurate.serialize.SerializationException;

import io.leangen.geantyref.TypeToken;
import xyz.emirdev.echogen.Echogen;

public class FilterManager {
    @Getter
    private final List<FilterElement> filters = new ArrayList<>();

    public FilterManager() {
        this.load();
    }

    public void load() {
        filters.clear();

        List<Map<String, Map<String, String>>> filterList;
        try {
            filterList = Echogen.get().getPluginConfig().getRootNode().node("chat", "filter").getList(new TypeToken<>() {});
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        for (Map<String, Map<String, String>> filterElement : filterList) {
            Map<String, String> values = filterElement.values().stream().findFirst().get();
            String regex = values.get("regex");
            FilterType type = FilterType.valueOf(values.get("type").toUpperCase());

            if (type == FilterType.COMMAND) {
                String command = values.get("command");
                filters.add(new FilterElement(type, regex, command));
                Echogen.get().getLogger().info(
                        "Loaded a filter with regex: " + regex + " type: " + type + " command: " + command);
            } else {
                filters.add(new FilterElement(type, regex));
                Echogen.get().getLogger().info("Loaded a filter with regex: " + regex + " type: " + type);
            }
        }
    }

    @Getter
    public static class FilterElement {
        private final Pattern pattern;
        private final FilterType type;
        private final String command;

        public FilterElement(FilterType type, String regex) {
            this(type, regex, null);
        }

        public FilterElement(FilterType type, String regex, String command) {
            this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.type = type;
            this.command = command;
        }

    }

    public enum FilterType {
        CENSOR,
        BLOCK_MESSAGE,
        COMMAND
    }
}
