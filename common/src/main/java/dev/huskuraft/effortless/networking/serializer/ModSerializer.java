package dev.huskuraft.effortless.networking.serializer;

import dev.huskuraft.effortless.api.networking.NetByteBuf;
import dev.huskuraft.effortless.api.networking.NetByteBufSerializer;
import dev.huskuraft.effortless.api.platform.Mod;

public class ModSerializer implements NetByteBufSerializer<Mod> {

    @Override
    public Mod read(NetByteBuf byteBuf) {
        return Mod.create(
                byteBuf.readNullable(NetByteBuf::readString),
                byteBuf.readNullable(NetByteBuf::readString),
                byteBuf.readNullable(NetByteBuf::readString),
                byteBuf.readNullable(NetByteBuf::readString));
    }

    @Override
    public void write(NetByteBuf byteBuf, Mod mod) {
        byteBuf.writeNullable(mod.getId(), NetByteBuf::writeString);
        byteBuf.writeNullable(mod.getVersionStr(), NetByteBuf::writeString);
        byteBuf.writeNullable(mod.getDescription(), NetByteBuf::writeString);
        byteBuf.writeNullable(mod.getName(), NetByteBuf::writeString);
    }
}
