// ClientPacketHandler.java
package net.sam.sams_combat_indicators.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sam.sams_combat_indicators.render.DamageIndicatorRenderer;
import net.sam.sams_combat_indicators.util.DamageIndicator;
import net.sam.sams_combat_indicators.util.FloatingDamageRenderer;

public class ClientPacketHandler {
    public static void handleDamageNumber(int entityId, int sourceId, float damage) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = Minecraft.getInstance().player;
        if(player.getId() != sourceId){return;}

        Entity entity = level.getEntity(entityId);
        if (entity != null) {
            FloatingDamageRenderer.spawnDamageNumber(damage, entity);
        }
    }

    public static void handleS2CAttackedPacket(int attackerId, int receiverId, float damage) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = Minecraft.getInstance().player;
        if(player.getId() != receiverId){return;}

        Entity entity = level.getEntity(attackerId);
        if (entity != null) {
            int entityId = entity.getId();
            DamageIndicatorRenderer.damageIndicators.add(new DamageIndicator(entityId, entity, damage));
        }
    }
}
