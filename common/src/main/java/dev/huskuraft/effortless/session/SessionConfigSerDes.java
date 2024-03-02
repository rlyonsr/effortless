package dev.huskuraft.effortless.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;

import dev.huskuraft.effortless.api.core.ResourceLocation;
import dev.huskuraft.effortless.api.toml.CommentedConfigDeserializer;
import dev.huskuraft.effortless.api.toml.CommentedConfigSerializer;

public class SessionConfigSerDes implements CommentedConfigDeserializer<SessionConfig>, CommentedConfigSerializer<SessionConfig> {

    private static final String KEY_GLOBAL = "global";
    private static final String KEY_PLAYER = "player";

    private static final String KEY_ALLOW_USE_MOD = "allowUseMod";
    private static final String KEY_ALLOW_BREAK_BLOCKS = "allowBreakBlocks";
    private static final String KEY_ALLOW_PLACE_BLOCKS = "allowPlaceBlocks";
    private static final String KEY_MAX_REACH_DISTANCE = "maxReachDistance";
    private static final String KEY_MAX_DISTANCE_PER_AXIS = "maxDistancePerAxis";
    private static final String KEY_MAX_BREAK_BLOCKS = "maxBreakBlocks";
    private static final String KEY_MAX_PLACE_BLOCKS = "maxPlaceBlocks";
    private static final String KEY_WHITELISTED_ITEMS = "whitelistedItems";
    private static final String KEY_BLACKLISTED_ITEMS = "blacklistedItems";
    private static final String SEPARATOR = ".";

    private static <T> void setNullable(Config config, String path, T value) {
        if (value == null) {
            config.remove(path);
        } else {
            config.add(path, value);
        }
    }

    private static ConfigSpec getGlobalBuilderConfigSpec() {
        var spec = new ConfigSpec();
        spec.define(KEY_ALLOW_USE_MOD, BuilderConfig.ALLOW_USE_MOD_DEFAULT, Objects::nonNull);
        spec.define(KEY_ALLOW_BREAK_BLOCKS, BuilderConfig.ALLOW_BREAK_BLOCKS_DEFAULT, Objects::nonNull);
        spec.define(KEY_ALLOW_PLACE_BLOCKS, BuilderConfig.ALLOW_PLACE_BLOCKS_DEFAULT, Objects::nonNull);
        spec.defineInRange(KEY_MAX_REACH_DISTANCE, BuilderConfig.MAX_REACH_DISTANCE_DEFAULT, BuilderConfig.MAX_REACH_DISTANCE_RANGE_START, BuilderConfig.MAX_REACH_DISTANCE_RANGE_END);
        spec.defineInRange(KEY_MAX_DISTANCE_PER_AXIS, BuilderConfig.MAX_DISTANCE_PER_AXIS_DEFAULT, BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_START, BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
        spec.defineInRange(KEY_MAX_BREAK_BLOCKS, BuilderConfig.MAX_BREAK_BLOCKS_DEFAULT, BuilderConfig.MAX_BREAK_BLOCKS_RANGE_START, BuilderConfig.MAX_BREAK_BLOCKS_RANGE_END);
        spec.defineList(KEY_WHITELISTED_ITEMS, BuilderConfig.WHITELISTED_ITEMS_DEFAULT, Objects::nonNull);
        spec.defineList(KEY_BLACKLISTED_ITEMS, BuilderConfig.BLACKLISTED_ITEMS_DEFAULT, Objects::nonNull);
        return spec;
    }

    private static ConfigSpec getPlayerBuilderConfigSpec(CommentedConfig config) {
        var spec = new ConfigSpec();
        if (config.contains(KEY_ALLOW_USE_MOD)) {
            spec.define(KEY_ALLOW_USE_MOD, BuilderConfig.ALLOW_USE_MOD_DEFAULT, Objects::nonNull);
        }
        if (config.contains(KEY_ALLOW_BREAK_BLOCKS)) {
            spec.define(KEY_ALLOW_BREAK_BLOCKS, BuilderConfig.ALLOW_BREAK_BLOCKS_DEFAULT, Objects::nonNull);
        }
        if (config.contains(KEY_ALLOW_PLACE_BLOCKS)) {
            spec.define(KEY_ALLOW_PLACE_BLOCKS, BuilderConfig.ALLOW_PLACE_BLOCKS_DEFAULT, Objects::nonNull);
        }
        if (config.contains(KEY_MAX_REACH_DISTANCE)) {
            spec.defineInRange(KEY_MAX_REACH_DISTANCE, BuilderConfig.MAX_REACH_DISTANCE_DEFAULT, BuilderConfig.MAX_REACH_DISTANCE_RANGE_START, BuilderConfig.MAX_REACH_DISTANCE_RANGE_END);
        }
        if (config.contains(KEY_MAX_DISTANCE_PER_AXIS)) {
            spec.defineInRange(KEY_MAX_DISTANCE_PER_AXIS, BuilderConfig.MAX_DISTANCE_PER_AXIS_DEFAULT, BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_START, BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
        }
        if (config.contains(KEY_MAX_BREAK_BLOCKS)) {
            spec.defineInRange(KEY_MAX_BREAK_BLOCKS, BuilderConfig.MAX_BREAK_BLOCKS_DEFAULT, BuilderConfig.MAX_BREAK_BLOCKS_RANGE_START, BuilderConfig.MAX_BREAK_BLOCKS_RANGE_END);
        }
        if (config.contains(KEY_WHITELISTED_ITEMS)) {
            spec.defineList(KEY_WHITELISTED_ITEMS, BuilderConfig.WHITELISTED_ITEMS_DEFAULT, Objects::nonNull);
        }
        if (config.contains(KEY_BLACKLISTED_ITEMS)) {
            spec.defineList(KEY_BLACKLISTED_ITEMS, BuilderConfig.BLACKLISTED_ITEMS_DEFAULT, Objects::nonNull);
        }
        return spec;
    }

    private static CommentedConfig serialize(BuilderConfig builderConfig, boolean global) {
        var config = CommentedConfig.inMemory();
        setNullable(config, KEY_ALLOW_USE_MOD, builderConfig.allowUseMod());
        if (global) {
            config.setComment(KEY_ALLOW_USE_MOD, "Should allow players to use this mod?");
        }
        setNullable(config, KEY_ALLOW_BREAK_BLOCKS, builderConfig.allowBreakBlocks());
        if (global) {
            config.setComment(KEY_ALLOW_BREAK_BLOCKS, "Should allow players to break blocks using this mod?");
        }
        setNullable(config, KEY_ALLOW_PLACE_BLOCKS, builderConfig.allowPlaceBlocks());
        if (global) {
            config.setComment(KEY_ALLOW_PLACE_BLOCKS, "Should allow players to place blocks using this mod?");
        }
        setNullable(config, KEY_MAX_REACH_DISTANCE, builderConfig.maxReachDistance());
        if (global) {
            config.setComment(KEY_MAX_REACH_DISTANCE, "The maximum distance a player can reach when building using this mod. \nRange: " + BuilderConfig.MAX_REACH_DISTANCE_RANGE_START + " ~ " + BuilderConfig.MAX_REACH_DISTANCE_RANGE_END);
        }
        setNullable(config, KEY_MAX_DISTANCE_PER_AXIS, builderConfig.maxDistancePerAxis());
        if (global) {
            config.setComment(KEY_MAX_DISTANCE_PER_AXIS, "The maximum size a player can build per axis when building using this mod. \nRange: " + BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_START + " ~ " + BuilderConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
        }
        setNullable(config, KEY_MAX_BREAK_BLOCKS, builderConfig.maxBreakBlocks());
        if (global) {
            config.setComment(KEY_MAX_BREAK_BLOCKS, "The maximum number of blocks a player can break when building using this mod. \nRange: " + BuilderConfig.MAX_BREAK_BLOCKS_RANGE_START + " ~ " + BuilderConfig.MAX_BREAK_BLOCKS_RANGE_END);
        }
        setNullable(config, KEY_MAX_PLACE_BLOCKS, builderConfig.maxPlaceBlocks());
        if (global) {
            config.setComment(KEY_MAX_PLACE_BLOCKS, "The maximum number of blocks a player can break when building using this mod. \nRange: " + BuilderConfig.MAX_PLACE_BLOCKS_RANGE_START + " ~ " + BuilderConfig.MAX_PLACE_BLOCKS_RANGE_END);
        }
        setNullable(config, KEY_WHITELISTED_ITEMS, builderConfig.whitelistedItems().stream().map(ResourceLocation::getString).toList());
        if (global) {
            config.setComment(KEY_WHITELISTED_ITEMS, "The list of items that players are allowed to use when building using this mod. \nIf the whitelist is empty, all items are allowed. \nIf the whitelist is not empty, only the items in the whitelist are allowed. \nThe value must be a list of item resource locations like [\"minecraft:stone\", \"minecraft:dirt\"].");
        }
        setNullable(config, KEY_BLACKLISTED_ITEMS, builderConfig.blacklistedItems().stream().map(ResourceLocation::getString).toList());
        if (global) {
            config.setComment(KEY_BLACKLISTED_ITEMS, "The list of items that players are not allowed to use when building using this mod. \nIf the blacklist is empty, no items are not allowed. \nIf an item exists both in the blacklist and the whitelist, it will not be allowed. \nThe value must be a list of item resource locations like [\"minecraft:stone\", \"minecraft:dirt\"].");
        }

        if (global) {
            getGlobalBuilderConfigSpec().correct(config);
        } else {
            getPlayerBuilderConfigSpec(config).correct(config);
        }
        return config;
    }

    private static BuilderConfig deserializeBuilderConfig(CommentedConfig config, boolean global) {
        if (config == null) {
            config = CommentedConfig.inMemory();
        }
        if (global) {
            getGlobalBuilderConfigSpec().correct(config);
        } else {
            getPlayerBuilderConfigSpec(config).correct(config);
        }
        return new BuilderConfig(
                config.get(KEY_ALLOW_USE_MOD),
                config.get(KEY_ALLOW_BREAK_BLOCKS),
                config.get(KEY_ALLOW_PLACE_BLOCKS),
                config.get(KEY_MAX_REACH_DISTANCE),
                config.get(KEY_MAX_DISTANCE_PER_AXIS),
                config.get(KEY_MAX_BREAK_BLOCKS),
                config.get(KEY_MAX_PLACE_BLOCKS),
                ((List<String>) config.get(KEY_WHITELISTED_ITEMS)).stream().map(ResourceLocation::decompose).toList(),
                ((List<String>) config.get(KEY_BLACKLISTED_ITEMS)).stream().map(ResourceLocation::decompose).toList()
        );
    }

    @Override
    public SessionConfig deserialize(CommentedConfig config) {
        var globalTomlConfig = (CommentedConfig) config.get(KEY_GLOBAL);
        var globalConfig = deserializeBuilderConfig(globalTomlConfig, true);

        var playerConfigs = new HashMap<UUID, BuilderConfig>();
        var playerTomlConfig = (Config) config.get(KEY_PLAYER);

        if (playerTomlConfig == null) {
            playerTomlConfig = CommentedConfig.inMemory();
        }

        for (var entry : playerTomlConfig.entrySet()) {
            try {
                playerConfigs.put(UUID.fromString(entry.getKey()), deserializeBuilderConfig(entry.getValue(), false));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new SessionConfig(globalConfig, Collections.unmodifiableMap(playerConfigs));
    }

    @Override
    public CommentedConfig serialize(SessionConfig sessionConfig) {
        var config = CommentedConfig.inMemory();
        setNullable(config, KEY_GLOBAL, serialize(sessionConfig.globalConfig(), true));
        for (var entry : sessionConfig.playerConfigs().entrySet()) {
            setNullable(config, KEY_PLAYER + SEPARATOR + entry.getKey().toString(), serialize(entry.getValue(), false));
        }
        return config;
    }

}
