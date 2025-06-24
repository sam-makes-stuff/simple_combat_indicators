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
import net.sam.sams_combat_indicators.networking.ModPackets;
import net.sam.sams_combat_indicators.networking.packets.S2CAttackedPacket;


@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class DamageTakenIndicator {

    public static final int lifetime = 60;
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

    public Entity attacker;
    public int attackerId;
    public Vec3 attackedFromPos;
    public float damage;

    // to be changed every tick()
    public int centerDistPx = 220;
    public float age = 0;
    public float currentPartialTick = 0f;
    public float lastPartialTick = 0f;
    public float rotation = 0;
    public float maxHealthProportion = 0f;
    public float scale = 0.0f;
    public float distScale = maxDistScale;
    public float scaleRatioSquared = 1.0f;
    public float opacity = 1.0f;

    public static final float baseBigHitColorR = 1.0f;
    public static final float baseBigHitColorG = 0.0f;
    public static final float baseBigHitColorB = 0.0f;

    public static final float baseSmallHitColorR = 1.0f;
    public static final float baseSmallHitColorG = 0.95f;
    public static final float baseSmallHitColorB = 0.3f;

    public float baseR = 0.0f;
    public float baseG = 0.0f;
    public float baseB = 0.0f;

    public float r = 0.0f;
    public float g = 0.0f;
    public float b = 0.0f;

    public DamageTakenIndicator(int attackerId, Entity attacker, float damage) {
        this.attacker = attacker;
        this.attackerId = attackerId;
        this.attackedFromPos = attacker.getEyePosition();
        this.damage = damage;
        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null){
            this.maxHealthProportion = damage / (Minecraft.getInstance().player.getMaxHealth());
            if (this.maxHealthProportion > 1.0f){
                this.maxHealthProportion = 1.0f;
            }
            this.baseR = ((baseBigHitColorR - baseSmallHitColorR) * maxHealthProportion) + baseSmallHitColorR;
            this.baseG = ((baseBigHitColorG - baseSmallHitColorG) * maxHealthProportion) + baseSmallHitColorG;
            this.baseB = ((baseBigHitColorB - baseSmallHitColorB) * maxHealthProportion) + baseSmallHitColorB;

            this.r = this.baseR;
            this.g = this.baseG;
            this.b = this.baseB;
        }
    }

    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent event){
        if(!(event.getEntity().level() instanceof ServerLevel)){return;}
        Entity attacker = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();

        if(receiver instanceof Player p){
            if(p.isInvulnerableTo(event.getSource())){return;}
            if(attacker != null){
                ModPackets.sendToTracking(event.getEntity(), new S2CAttackedPacket(
                        attacker.getId(), receiver.getId(), event.getAmount()
                ));
            }
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
        rotateToAttackerXZ(currentPartialTick);
        //rotateToAttacker(currentPartialTick);
        calcScaleRatioSquared();
        tickScale();
        tickDistScale();
        tickOpacityScale();
        lastPartialTick = currentPartialTick;
    }

//    public void rotateToAttacker(float partialTick){
//        Player player = Minecraft.getInstance().player;
//        Vec3 eyePos = player.getEyePosition(partialTick);
//        Vec3 to = (attackedFromPos.subtract(eyePos).normalize());
//
//        Vec3 up = player.getUpVector(partialTick);
//        Vec3 right = player.getLookAngle().cross(up);
//
//        Vec3 proj = (up.scale(up.dot(to)).add(right.scale(right.dot(to)))).normalize();
//        double dotProd = proj.dot(up);
//        double angle = Math.acos(dotProd);
//        if(to.dot(right) < 0){
//            angle *= -1;
//        }
//        rotation = (float) angle;
//    }

        public void rotateToAttackerXZ(float partialTick){
        Player player = Minecraft.getInstance().player;
        Vec3 eyePos = player.getEyePosition(partialTick);
        Vec3 to = (attackedFromPos.subtract(eyePos));
        Vec2 toXZ = new Vec2((float)to.x, (float)to.z).normalized();

        Vec3 up = player.getUpVector(partialTick);
        Vec3 right = player.getLookAngle().cross(up);
        Vec2 rightXZ = new Vec2((float)right.x, (float)right.z).normalized();

        Vec3 look = player.getLookAngle();
        Vec2 lookXZ = new Vec2((float) look.x, (float) look.z).normalized();



        double dotProd = toXZ.dot(lookXZ);
        double angle = Math.acos(dotProd);
        if(toXZ.dot(rightXZ) < 0){
            angle *= -1;
        }
        rotation = (float)( angle * (180/Math.PI));
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
