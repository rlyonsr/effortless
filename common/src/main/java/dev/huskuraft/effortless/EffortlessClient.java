package dev.huskuraft.effortless;

import com.google.auto.service.AutoService;

import dev.huskuraft.effortless.api.events.ClientEventRegistry;
import dev.huskuraft.effortless.api.platform.ClientEntrance;
import dev.huskuraft.effortless.api.platform.PlatformLoader;

@AutoService(ClientEntrance.class)
public class EffortlessClient implements ClientEntrance {

    private final ClientEventRegistry eventRegistry = PlatformLoader.getSingleton();

    private final EffortlessClientNetworkChannel channel;
    private final EffortlessClientStructureBuilder structureBuilder;
    private final EffortlessClientManager clientManager;
    private final EffortlessClientConfigStorage configStorage;

    public EffortlessClient() {
        this.channel = new EffortlessClientNetworkChannel(this);
        this.structureBuilder = new EffortlessClientStructureBuilder(this);
        this.clientManager = new EffortlessClientManager(this);
        this.configStorage = new EffortlessClientConfigStorage(this);
    }

    public static EffortlessClient getInstance() {
        return (EffortlessClient) ClientEntrance.getInstance();
    }

    public ClientEventRegistry getEventRegistry() {
        return eventRegistry;
    }

    public EffortlessClientNetworkChannel getChannel() {
        return channel;
    }

    public EffortlessClientStructureBuilder getStructureBuilder() {
        return structureBuilder;
    }

    @Override
    public EffortlessClientManager getClientManager() {
        return clientManager;
    }

    public EffortlessClientConfigStorage getConfigStorage() {
        return configStorage;
    }

    @Override
    public String getId() {
        return Effortless.MOD_ID;
    }

}
