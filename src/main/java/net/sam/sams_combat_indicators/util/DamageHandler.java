package net.sam.sams_combat_indicators.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sam.sams_combat_indicators.networking.ModPackets;
import net.sam.sams_combat_indicators.networking.packets.S2CAttackedPacket;

public class DamageHandler {
    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player)) return;
        if (event.getEntity().level().isClientSide) return;

        // Send packet to client
        ModPackets.sendToTracking(event.getEntity(), new S2CAttackedPacket(
                event.getSource().getEntity().getId(), event.getEntity().getId(), event.getAmount()
        ));
    }
}
