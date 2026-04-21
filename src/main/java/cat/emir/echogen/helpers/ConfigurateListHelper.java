package cat.emir.echogen.helpers;

import cat.emir.echogen.managers.PrefixManager;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;

public class ConfigurateListHelper {
    public static List<Map<String, Map<String, String>>> getFilterList(ConfigurationNode node) throws SerializationException {
        return node.getList(new TypeToken<>() {});
    }


    public static List<Map<String, PrefixManager.PrefixValue>> getPrefixList(ConfigurationNode node) throws SerializationException {
        return node.getList(new TypeToken<>() {});
    }
}
