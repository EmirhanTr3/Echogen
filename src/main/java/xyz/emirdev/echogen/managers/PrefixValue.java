package xyz.emirdev.echogen.managers;

import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PrefixValue {
    @Setting
    private String name;
    @Setting
    private String prefix;
    @Setting
    private List<String> description;

    public PrefixValue() {
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
