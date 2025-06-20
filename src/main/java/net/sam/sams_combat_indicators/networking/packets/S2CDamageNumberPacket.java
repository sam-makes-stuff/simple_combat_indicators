package net.sam.sams_combat_indicators.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.sam.sams_combat_indicators.networking.ClientPacketHandler;

import java.util.function.Supplier;

public class S2CDamageNumberPacket {
    private final int entityId;
    private final int sourceId;
    private final float damage;


    public S2CDamageNumberPacket(int entityId, int sourceId, float damage) {
        this.entityId = entityId;
        this.sourceId = sourceId;
        this.damage = damage;
    }

    public static void encode(S2CDamageNumberPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.entityId);
        buf.writeInt(pkt.sourceId);
        buf.writeFloat(pkt.damage);
    }

    public static S2CDamageNumberPacket decode(FriendlyByteBuf buf) {
        return new S2CDamageNumberPacket(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void handle(S2CDamageNumberPacket pkt, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.handleDamageNumber(pkt.entityId, pkt.sourceId, pkt.damage)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}