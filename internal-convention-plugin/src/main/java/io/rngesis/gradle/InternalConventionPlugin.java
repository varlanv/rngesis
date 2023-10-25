package io.rngesis.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JvmVendorSpec;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InternalConventionPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PluginContainer plugins = project.getPlugins();
        plugins.apply(JavaPlugin.class);

        ExtensionContainer extensions = project.getExtensions();
        JavaPluginExtension javaPluginExtension = (JavaPluginExtension) extensions.getByName("java");
        javaPluginExtension.toolchain(javaToolchainSpec -> {
            javaToolchainSpec.getLanguageVersion().set(JavaLanguageVersion.of(8));
            javaToolchainSpec.getVendor().set(JvmVendorSpec.AZUL);
        });
        TaskContainer tasks = project.getTasks();
        tasks.withType(Test.class).configureEach(test -> {
            test.useJUnitPlatform();
            test.setJvmArgs(
                    Stream.of(
                                    test.getJvmArgs(),
                                    Arrays.asList(
                                            "-XX:TieredStopAtLevel=1",
                                            "-noverify",
                                            "-Xmx2048m",
                                            "-XX:+UseParallelGC",
                                            "-XX:ParallelGCThreads=2"
                                    )
                            )
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );
            Map<String, String> systemProperties = new HashMap<>();
            systemProperties.put("junit.jupiter.execution.parallel.enabled", "true");
            systemProperties.put("junit.jupiter.execution.parallel.config.strategy", "dynamic");
            test.systemProperties(systemProperties);
        });

        RepositoryHandler repositories = project.getRepositories();
        repositories.mavenCentral();

        DependencyHandler dependencies = project.getDependencies();
        ProviderFactory providers = project.getProviders();
        String lombokVersion = providers.gradleProperty("lombokVersion").get();
        dependencies.add("compileOnly", "org.projectlombok:lombok:" + lombokVersion);
        dependencies.add("annotationProcessor", "org.projectlombok:lombok:" + lombokVersion);
        dependencies.add("testCompileOnly", "org.projectlombok:lombok:" + lombokVersion);
        dependencies.add("testAnnotationProcessor", "org.projectlombok:lombok:" + lombokVersion);

        plugins.withId("maven-publish", plugin -> {
            project.getExtensions().configure("publishing", publishing -> {
                PublishingExtension publishingExtension = (PublishingExtension) publishing;
                publishingExtension.publications(publications -> {
                    publications.create("mavenJava", MavenPublication.class, mavenPublication -> {
                        mavenPublication.from(project.getComponents().getByName("java"));
                    });
                });
            });
        });
    }
}
