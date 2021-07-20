package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import com.jamieswhiteshirt.clothesline.internal.NetworkProvider;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.stream.Collectors;

public class NetworkProviderPersistentState extends PersistentState {
    private final NetworkProvider provider;

    public NetworkProviderPersistentState(String key, NetworkProvider provider) {
        super(key);
        this.provider = provider;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void fromTag(NbtCompound tag) {
        int version;
        if (!tag.contains("Version", NbtType.INT)) {
            Clothesline.LOGGER.warn("Invalid save data. Expected a Version, found no Version. Assuming Version 0.");
            version = 0;
        } else {
            version = tag.getInt("Version");
        }

        if (version != 0) {
            Clothesline.LOGGER.error("Invalid save data. Expected Version <= 0, found " + version + ". Discarding save data.");
            return;
        }

        if (!tag.contains("Networks", NbtType.LIST)) {
            Clothesline.LOGGER.error("Invalid save data. Expected list of Networks, found none. Discarding save data.");
            return;
        }

        provider.reset(
            NBTSerialization.readPersistentNetworks(tag.getList("Networks", NbtType.COMPOUND)).stream()
                .map(BasicPersistentNetwork::toAbsolute)
                .collect(Collectors.toList())
        );
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("Version", 0);
        tag.put("Networks", NBTSerialization.writePersistentNetworks(
            provider.getNetworks().stream()
                .map(BasicPersistentNetwork::fromAbsolute)
                .collect(Collectors.toList())
        ));
        return tag;
    }
}
