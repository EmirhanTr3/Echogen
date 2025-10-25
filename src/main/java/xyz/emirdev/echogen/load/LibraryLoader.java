package xyz.emirdev.echogen.load;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;

@SuppressWarnings("UnstableApiUsage")
public class LibraryLoader implements PluginLoader {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        ComponentLogger logger = classpathBuilder.getContext().getLogger();
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();

        // No point in loading libraries if there aren't any :shrug:
        if (pluginLibraries.dependencies == null || pluginLibraries.repositories == null) return;

        pluginLibraries.asRepositories().forEach(rep -> {
            logger.info(mm.deserialize("Adding repository with ID <b><id></b> and URL <b><url></b>...",
                    unparsed("id", rep.getId()),
                    unparsed("url", rep.getUrl())));
            resolver.addRepository(rep);
        });

        pluginLibraries.asDependencies().forEach(dep -> {
            Artifact artifact = dep.getArtifact();
            logger.info(mm.deserialize("Adding dependency <b><group>:<artifact>:<version></b>...",
                    unparsed("group", artifact.getGroupId()),
                    unparsed("artifact", artifact.getArtifactId()),
                    unparsed("version", artifact.getVersion())));
            resolver.addDependency(dep);
        });

        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load() {
        try (var in = getClass().getResourceAsStream("/paper-libraries.json")) {
            if (in == null)
                return new Gson().fromJson("{}", PluginLibraries.class);

            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            return repositories.entrySet().stream()
                    .map(e -> {
                        if (e.getKey().equals("MavenRepo")) {
                            e.setValue(MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR);
                        }
                        RemoteRepository.Builder builder = new RemoteRepository.Builder(e.getKey(), "default",
                                e.getValue());
                        return builder.build();
                    });
        }
    }
}