package xyz.emirdev.echogen.managers;

import java.util.List;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
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
}
