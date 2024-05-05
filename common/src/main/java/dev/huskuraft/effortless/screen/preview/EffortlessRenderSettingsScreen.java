package dev.huskuraft.effortless.screen.preview;

import java.util.function.Consumer;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.gui.AbstractScreen;
import dev.huskuraft.effortless.api.gui.AbstractWidget;
import dev.huskuraft.effortless.api.gui.Dimens;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.config.ClientConfig;
import dev.huskuraft.effortless.building.config.RenderConfig;
import dev.huskuraft.effortless.building.pattern.Pattern;
import dev.huskuraft.effortless.screen.settings.SettingOptionsList;

public class EffortlessRenderSettingsScreen extends AbstractScreen {

    private final Consumer<RenderConfig> consumer;
    private RenderConfig originalConfig;
    private RenderConfig lastConfig;

    private AbstractWidget saveButton;

    public EffortlessRenderSettingsScreen(Entrance entrance) {
        super(entrance, Text.translate("effortless.render_settings.title"));
        this.consumer = pattern -> {
            getEntrance().getStructureBuilder().setPattern(getEntrance().getClient().getPlayer(), Pattern.DISABLED);
            getEntrance().getConfigStorage().update(config -> new ClientConfig(this.lastConfig, config.patternConfig(), config.transformerPresets(), config.passiveMode()));
        };
        this.lastConfig = getEntrance().getConfigStorage().get().renderConfig();
        this.originalConfig = lastConfig;
    }

    @Override
    public void onCreate() {
        addWidget(new TextWidget(getEntrance(), getWidth() / 2, Dimens.Screen.TITLE_36 - 12, getScreenTitle(), TextWidget.Gravity.CENTER));

        var entries = addWidget(new SettingOptionsList(getEntrance(), 0, Dimens.Screen.TITLE_36, getWidth(), getHeight() - Dimens.Screen.TITLE_36 - Dimens.Screen.BUTTON_ROW_1, false, false));
        entries.addSwitchEntry(Text.translate("effortless.render_settings.show_other_players_build"), null, lastConfig.showOtherPlayersBuild(), (value) -> {
            this.lastConfig = new RenderConfig(value, lastConfig.showOtherPlayersBuildTooltips(), lastConfig.showBlockPreview(), lastConfig.maxRenderVolume(), lastConfig.maxRenderDistance());
        });
        entries.addSwitchEntry(Text.translate("effortless.render_settings.show_other_players_build_tooltips"), null, lastConfig.showOtherPlayersBuildTooltips(), (value) -> {
            this.lastConfig = new RenderConfig(lastConfig.showOtherPlayersBuild(), value, lastConfig.showBlockPreview(), lastConfig.maxRenderVolume(), lastConfig.maxRenderDistance());
        });
        entries.addSwitchEntry(Text.translate("effortless.render_settings.show_block_preview"), null, lastConfig.showBlockPreview(), (value) -> {
            this.lastConfig = new RenderConfig(lastConfig.showOtherPlayersBuild(), lastConfig.showOtherPlayersBuildTooltips(), value, lastConfig.maxRenderVolume(), lastConfig.maxRenderDistance());
        });
        entries.addIntegerEntry(Text.translate("effortless.render_settings.max_render_volume"), null, lastConfig.maxRenderVolume(), RenderConfig.MAX_RENDER_VOLUME_MIN, RenderConfig.MAX_RENDER_VOLUME_MAX, (value) -> {
            this.lastConfig = new RenderConfig(lastConfig.showOtherPlayersBuild(), lastConfig.showOtherPlayersBuildTooltips(), lastConfig.showBlockPreview(), value, lastConfig.maxRenderDistance());
        });
//        entries.addIntegerEntry(Text.translate("effortless.render_settings.max_render_distance"), null, lastConfig.maxRenderDistance(), RenderConfig.MIN_MAX_RENDER_DISTANCE, RenderConfig.MAX_MAX_RENDER_DISTANCE, (value) -> {
//            this.lastConfig = new RenderConfig(lastConfig.showOtherPlayersBuild(), lastConfig.showOtherPlayersBuildTooltips(), lastConfig.showBlockPreview(), lastConfig.maxRenderVolume(), value);
//        });


        addWidget(Button.builder(getEntrance(), Text.translate("effortless.button.cancel"), button -> {
            detach();
        }).setBoundsGrid(getWidth(), getHeight(), 0f, 0f, 0.5f).build());

        this.saveButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.button.save"), button -> {
            consumer.accept(lastConfig);
            detach();
        }).setBoundsGrid(getWidth(), getHeight(), 0f, 0.5f, 0.5f).build());

    }

    @Override
    public void onReload() {
        this.saveButton.setActive(!originalConfig.equals(lastConfig));
    }

    @Override
    protected EffortlessClient getEntrance() {
        return (EffortlessClient) super.getEntrance();
    }
}
