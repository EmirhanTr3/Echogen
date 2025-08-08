package xyz.emirdev.echogen.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.configurate.serialize.SerializationException;

import io.leangen.geantyref.TypeToken;
import net.luckperms.api.model.group.Group;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.utils.LuckPermsUtils;

public class PrefixManager {
    List<Prefix> prefixes = new ArrayList<>();

    public PrefixManager() {
        this.load();
    }

    public List<Prefix> getPrefixes() {
        return prefixes;
    }

    public Prefix getPrefix(String id) {
        Optional<Prefix> optionalPrefix = prefixes.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (!optionalPrefix.isPresent())
            return null;
        return optionalPrefix.get();
    }

    public void load() {
        prefixes.clear();

        if (!Echogen.get().getPluginConfig().getRootNode().node("chat", "prefix", "enabled").getBoolean())
            return;

        List<String> blacklistedGroups;
        try {
            blacklistedGroups = Echogen.get().getPluginConfig().getRootNode().node("chat", "prefix", "blacklist")
                    .getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
            blacklistedGroups = new ArrayList<>();
        }

        for (Group group : LuckPermsUtils.getAllGroups()) {
            if (blacklistedGroups.contains(group.getName()))
                continue;

            prefixes.add(new Prefix(
                    "group-" + group.getName(),
                    "<aqua>" + group.getFriendlyName(),
                    group.getCachedData().getMetaData().getPrefix(),
                    List.of(
                            "This prefix requires " + group.getFriendlyName() + " group.")));
            Echogen.get().getLogger().info("Loaded prefix from LuckPerms group " + group.getName());
        }

        List<Map<String, PrefixValue>> prefixList;
        try {
            prefixList = Echogen.get().getPluginConfig().getRootNode().node("chat", "prefix", "extra")
                    .getList(new TypeToken<Map<String, PrefixValue>>() {
                    });
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        for (Map<String, PrefixValue> prefixElement : prefixList) {
            String id = prefixElement.keySet().stream().findFirst().get();
            PrefixValue values = prefixElement.get(id);

            String name = values.getName();
            String prefix = values.getPrefix();
            List<String> description = values.getDescription();

            prefixes.add(new Prefix(id, name, prefix, description));
            Echogen.get().getLogger().info("Loaded extra prefix with id: " + id + " name: " + name + " prefix: "
                    + prefix + " description: " + description);
        }

    }

    public class Prefix {
        private String id;
        private String name;
        private String prefix;
        private List<String> description;

        public Prefix(String id, String name, String prefix, List<String> description) {
            this.id = id;
            this.name = name;
            this.prefix = prefix;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public List<String> getDescription() {
            return description;
        }
    }
}
