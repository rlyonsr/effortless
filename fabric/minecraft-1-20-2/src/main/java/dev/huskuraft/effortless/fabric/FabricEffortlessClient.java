package dev.huskuraft.effortless.fabric;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.core.InteractionType;
import dev.huskuraft.effortless.api.events.lifecycle.ClientTick;
import dev.huskuraft.effortless.api.input.InputKey;
import dev.huskuraft.effortless.api.platform.ClientPlatform;
import dev.huskuraft.effortless.fabric.events.ClientRenderEvents;
import dev.huskuraft.effortless.fabric.events.ClientShadersEvents;
import dev.huskuraft.effortless.fabric.events.InteractionInputEvents;
import dev.huskuraft.effortless.fabric.events.KeyboardInputEvents;
import dev.huskuraft.effortless.vanilla.adapters.*;
import dev.huskuraft.effortless.vanilla.platform.MinecraftClientPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public class FabricEffortlessClient extends EffortlessClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        getEventRegistry().getClientStartEvent().invoker().onClientStart(MinecraftConvertor.fromPlatformClient(Minecraft.getInstance()));
        getEventRegistry().getRegisterNetworkEvent().invoker().onRegisterNetwork(receiver -> {
            var channelId = getChannel().getChannelId().<ResourceLocation>reference();
            ClientPlayNetworking.registerGlobalReceiver(channelId, (client, handler, buf, responseSender) -> {
                receiver.receiveBuffer(MinecraftConvertor.fromPlatformBuffer(buf), MinecraftConvertor.fromPlatformPlayer(client.player));
            });
            return (buffer, player1) -> ClientPlayNetworking.send(channelId, MinecraftConvertor.toPlatformBuffer(buffer));
        });

        getEventRegistry().getRegisterKeysEvent().invoker().onRegisterKeys(key1 -> {
            KeyBindingHelper.registerKeyBinding(MinecraftConvertor.toPlatformKeyBinding(key1.getBinding()));
        });

        ClientShadersEvents.REGISTER.register((provider, sink) -> {
            getEventRegistry().getRegisterShaderEvent().invoker().onRegisterShader((resource, format, consumer) -> sink.register(new ShaderInstance(provider, resource.getPath(), format.reference()), shaderInstance -> consumer.accept((MinecraftShader) () -> shaderInstance)));
        });

        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            getEventRegistry().getClientTickEvent().invoker().onClientTick(MinecraftConvertor.fromPlatformClient(minecraft), ClientTick.Phase.START);
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            getEventRegistry().getClientTickEvent().invoker().onClientTick(MinecraftConvertor.fromPlatformClient(minecraft), ClientTick.Phase.END);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            getEventRegistry().getRenderWorldEvent().invoker().onRenderWorld(MinecraftConvertor.fromPlatformRenderer(context.matrixStack()), context.tickDelta());
        });

        ClientRenderEvents.GUI.register((guiGraphics, f) -> {
            getEventRegistry().getRenderGuiEvent().invoker().onRenderGui(MinecraftConvertor.fromPlatformRenderer(guiGraphics.pose()), f);
        });

        KeyboardInputEvents.KEY_INPUT.register((key, scanCode, action, modifiers) -> {
            getEventRegistry().getKeyInputEvent().invoker().onKeyInput(new InputKey(key, scanCode, action, modifiers));
        });

        InteractionInputEvents.ATTACK.register((player, hand) -> {
            return getEventRegistry().getInteractionInputEvent().invoker().onInteractionInput(InteractionType.ATTACK, MinecraftConvertor.fromPlatformInteractionHand(hand)).interruptsFurtherEvaluation();
        });

        InteractionInputEvents.USE_ITEM.register((player, hand) -> {
            return getEventRegistry().getInteractionInputEvent().invoker().onInteractionInput(InteractionType.USE_ITEM, MinecraftConvertor.fromPlatformInteractionHand(hand)).interruptsFurtherEvaluation();
        });

    }

    @Override
    public String getLoaderName() {
        return "Fabric-Official";
    }

    @Override
    public String getLoaderVersion() {
        return FabricLoaderImpl.VERSION;
    }

    @Override
    public String getGameVersion() {
        return FabricLoaderImpl.INSTANCE.getGameProvider().getRawGameVersion();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public ClientPlatform getPlatform() {
        return new MinecraftClientPlatform();
    }

    @Override
    public Environment getEnvironment() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> Environment.CLIENT;
            case SERVER -> Environment.SERVER;
        };
    }

    @Override
    public boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

}
