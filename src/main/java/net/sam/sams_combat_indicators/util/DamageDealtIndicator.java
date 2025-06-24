package net.sam.sams_combat_indicators.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sam.sams_combat_indicators.SamsCombatIndicators;
import net.sam.sams_combat_indicators.config.ClientConfig;
import net.sam.sams_combat_indicators.networking.ModPackets;
import net.sam.sams_combat_indicators.networking.packets.S2CAttackedPacket;
import net.sam.sams_combat_indicators.networking.packets.S2CDamageDealtPacket;


@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class DamageDealtIndicator {

    public static final int lifetime = 30;
    public static final int scaleTime = 20;
    public static final float maxSizeScale = 2.0f;
    public static final float minSizeScale = 1.0f;
    public static final float maxDistScale = 1.5f;
    public static final float minDistScale = 1.0f;
    public static final float maxOpacityScale = 1.0f;
    public static final float minOpacityScale = 0.0f;
    public static final float maxColourScale = 1.0f;
    public static final float minColourScale = 0.0f;
    public static final float maxMaxHealthProportionScale = 2.0f; //hits that deal 100% of max health will be this much bigger

    public Entity target;
    public int targetId;
    public Vec3 targetPos;
    public float damage;

    // to be changed every tick()
    public int centerDistPx = 220;
    public float age = 0;
    public float currentPartialTick = 0f;
    public float lastPartialTick = 0f;
    public float rotation; //in degrees
    public float maxHealthProportion = 0f;
    public float scale = 0.0f;
    public float distScale = maxDistScale;
    public float scaleRatioSquared = 1.0f;
    public float opacity = 1.0f;

    public static final float baseBigHitColorR = 0.0f;
    public static final float baseBigHitColorG = 1.0f;
    public static final float baseBigHitColorB = 0.0f;

    public static final float baseSmallHitColorR = 0.0f;
    public static final float baseSmallHitColorG = 0.95f;
    public static final float baseSmallHitColorB = 0.0f;

    public float baseR = 0.0f;
    public float baseG = 0.0f;
    public float baseB = 0.0f;

    public float r = 0.0f;
    public float g = 0.0f;
    public float b = 0.0f;

    public DamageDealtIndicator(int targetId, Entity target, float damage) {
        this.target = target;
        this.targetId = targetId;
        this.targetPos = target.position().add(new Vec3(0,target.getBbHeight() * 0.5f,0));
        this.damage = damage;
        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null){
            this.maxHealthProportion = damage / (Minecraft.getInstance().player.getMaxHealth());
            this.baseR = ((baseBigHitColorR - baseSmallHitColorR) * maxHealthProportion) + baseSmallHitColorR;
            this.baseG = ((baseBigHitColorG - baseSmallHitColorG) * maxHealthProportion) + baseSmallHitColorG;
            this.baseB = ((baseBigHitColorB - baseSmallHitColorB) * maxHealthProportion) + baseSmallHitColorB;

            this.r = this.baseR;
            this.g = this.baseG;
            this.b = this.baseB;

            if(ClientConfig.ROTATE_NUMBERS.get()) {
                this.rotation = (target.level().random.nextFloat() * ClientConfig.ROTATE_RANGE.get() * 2) - ClientConfig.ROTATE_RANGE.get();
            }else{
                this.rotation = 0.0f;
            }

        }
    }

    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent event){
        if(!(event.getEntity().level() instanceof ServerLevel)){return;}
        Entity attacker = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        if(attacker instanceof Player p){
            ModPackets.sendToTracking(event.getEntity(), new S2CDamageDealtPacket(
                    attacker.getId(), receiver.getId(), event.getAmount()
            ));
        }
    }

    public void tick(float partialTick){
        currentPartialTick = partialTick;
        double timeDif;
        if(lastPartialTick > currentPartialTick){
            timeDif = (1 - lastPartialTick + currentPartialTick); //ticks
        }else{
            timeDif = (currentPartialTick - lastPartialTick); //ticks
        }
        this.age += timeDif;
        calcScaleRatioSquared();
        tickScale();
        tickDistScale();
        tickOpacityScale();
        lastPartialTick = currentPartialTick;
    }


    public void calcScaleRatioSquared(){
        float scaleRatio = age/scaleTime;
        if(scaleRatio > 1){
            scaleRatio = 1;
        }

        scaleRatioSquared = (1 - scaleRatio) * (1 - scaleRatio);
    }

    public void tickDistScale(){
        this.distScale = (scaleRatioSquared * (maxDistScale - minDistScale)) + minDistScale;
    }

    public void tickScale(){
        this.scale = ((scaleRatioSquared * (maxSizeScale - minSizeScale)) + minSizeScale) * (1 + (maxHealthProportion * (maxMaxHealthProportionScale - 1)));
    }

    public void tickOpacityScale(){
        float scaleRatio = age/lifetime;
        this.opacity = ((1 - scaleRatio) * (maxOpacityScale - minOpacityScale)) + minOpacityScale;
    }
}
