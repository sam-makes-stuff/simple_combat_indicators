// ClientPacketHandler.java
package net.sam.sams_combat_indicators.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sam.sams_combat_indicators.config.ClientConfig;
import net.sam.sams_combat_indicators.render.DamageIndicatorRenderer;
import net.sam.sams_combat_indicators.util.DamageDealtIndicator;
import net.sam.sams_combat_indicators.util.DamageTakenIndicator;
public class ClientPacketHandler {
//    public static void handleS2CDamageDealtPacket(int entityId, int sourceId, float damage) {
//
//        Level level = Minecraft.getInstance().level;
//        if (level == null) return;
//
//        Player player = Minecraft.getInstance().player;
//        if(player.getId() != sourceId){return;}
//
//        Entity entity = level.getEntity(entityId);
//        if (entity != null) {
//            FloatingDamageRenderer.spawnDamageNumber(damage, entity);
//        }
//    }

    public static void handleS2CDamageDealtPacket(int attackerId, int receiverId, float damage) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = Minecraft.getInstance().player;
        if(player.getId() != attackerId){return;}

        Entity entity = level.getEntity(receiverId);
        if (entity != null) {
            int entityId = entity.getId();

            boolean stackingDamage = ClientConfig.STACKING_NUMBERS.get();
            if(stackingDamage){
                boolean found = false;
                for (DamageDealtIndicator dmg : DamageIndicatorRenderer.damageDealtIndicators) {
                    if(dmg.target == entity){
                        DamageIndicatorRenderer.damageDealtIndicators.add(new DamageDealtIndicator(entityId,entity, damage + dmg.damage));
                        DamageIndicatorRenderer.damageDealtIndicators.remove(dmg);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    DamageIndicatorRenderer.damageDealtIndicators.add(new DamageDealtIndicator(entityId, entity, damage));
                }
            }else{
                DamageIndicatorRenderer.damageDealtIndicators.add(new DamageDealtIndicator(entityId, entity, damage));
            }

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
            DamageIndicatorRenderer.damageTakenIndicators.add(new DamageTakenIndicator(entityId, entity, damage));
        }
    }
}
