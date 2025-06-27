package net.sam.sams_combat_indicators.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sam.sams_combat_indicators.SamsCombatIndicators;
import net.sam.sams_combat_indicators.config.ClientConfig;
import net.sam.sams_combat_indicators.networking.ModPackets;
import net.sam.sams_combat_indicators.networking.packets.S2CDamageDealtPacket;


@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class DamageDealtIndicator {

    public static int lifetime = ConfigUtils.getOrDefault(ClientConfig.NUMBER_DURATION);
    public static int initialTime = ConfigUtils.getOrDefault(ClientConfig.INITIAL_HIT_TIME);

    public static float maxRotation = ConfigUtils.getOrDefault(ClientConfig.ROTATE_RANGE);

    public LivingEntity target;
    public int targetId;
    public Vec3 targetPos;
    public float totalDamage;
    public float damage;
    public boolean isKill; // damage number for an enemy dying

    public float age = 0;
    public float currentPartialTick = 0f;
    public float lastPartialTick = 0f;
    public float rotation; //in degrees
    public float maxHealthProportion = 0f;

    public static float baseScale = (float) (ConfigUtils.getOrDefault(ClientConfig.NUMBER_BASE_SCALE)/ Minecraft.getInstance().options.guiScale().get());
    public static float incrementScale = (float) (double) (ConfigUtils.getOrDefault(ClientConfig.NUMBER_INCREMENT_SCALE));
    public static float decrementSpeed = (float) (double) (ConfigUtils.getOrDefault(ClientConfig.NUMBER_DECREMENT_SPEED));

    public static int r_kill = ConfigUtils.getOrDefault(ClientConfig.KILL_COLOR_R);
    public static int g_kill = ConfigUtils.getOrDefault(ClientConfig.KILL_COLOR_G);
    public static int b_kill = ConfigUtils.getOrDefault(ClientConfig.KILL_COLOR_B);

    public float scale;
    public int color;

    public static int r_s = ConfigUtils.getOrDefault(ClientConfig.START_NUMBER_COLOR_R);
    public static int g_s = ConfigUtils.getOrDefault(ClientConfig.START_NUMBER_COLOR_G);
    public static int b_s = ConfigUtils.getOrDefault(ClientConfig.START_NUMBER_COLOR_B);

    public static int r_e = ConfigUtils.getOrDefault(ClientConfig.END_NUMBER_COLOR_R);
    public static int g_e = ConfigUtils.getOrDefault(ClientConfig.END_NUMBER_COLOR_G);
    public static int b_e = ConfigUtils.getOrDefault(ClientConfig.END_NUMBER_COLOR_B);

    public int r;
    public int g;
    public int b;
    public int a = 255;

    public static float gravity = 0.0f;


    public static float sprayVel = 10.0f;
    public static float sprayAngleRange = 180.0f;
    public static float sprayAngleOffset = 0.0f;
    public static float friction = 0.1f;

    public int xOffset = 0;
    public int yOffset = 0;

    public float xVel;
    public float yVel;

    public DamageDealtIndicator(int targetId, LivingEntity target, float damage, float totalDamage, float scale) {
        this.target = target;
        this.targetId = targetId;
        this.targetPos = target.position().add(new Vec3(0,target.getBbHeight() * 0.5f,0));
        this.totalDamage = totalDamage;
        this.damage = damage;
        this.scale = scale;

        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null){
            this.maxHealthProportion = totalDamage / (Minecraft.getInstance().player.getMaxHealth());

            if(maxRotation != 0.0) {
                this.rotation = (target.level().random.nextFloat() * maxRotation * 2) - maxRotation;
            }else{
                this.rotation = 0.0f;
            }

            if(this.damage >= this.target.getHealth()){
                this.isKill = true;
            }else{
                this.isKill = false;
            }

            if(ConfigUtils.getOrDefault(ClientConfig.SPRAY_NUMBERS)){
                float randAngle = (float)(((Minecraft.getInstance().level.random.nextFloat() * (sprayAngleRange * (Math.PI/180.0f)) * 2) - (sprayAngleRange * (Math.PI/180.0f))) + (sprayAngleOffset * (Math.PI/180.0f)));
                xVel = (float)(sprayVel * Math.cos(randAngle));
                yVel = (float)(sprayVel * Math.sin(randAngle));
            }else{
                xVel = 0.0f;
                yVel = 0.0f;
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

        float scaleRatio = age/initialTime;
        if (scaleRatio > 1.0f){
            scaleRatio = 1.0f;
        }

        if(!isKill){
            r = (int)(r_e + (scaleRatio * (r_s - r_e)));
            g = (int)(g_e + (scaleRatio * (g_s - g_e)));
            b = (int)(b_e + (scaleRatio * (b_s - b_e)));
        }else{
            r = r_kill;
            g = g_kill;
            b = b_kill;
        }

        this.color = rgba(this.r,this.g,this.b,this.a);

        if (this.scale > baseScale){
            this.scale -= (decrementSpeed / 20) * timeDif;
        }

        this.xOffset += xVel * timeDif;
        this.yOffset += yVel * timeDif;

        this.age += timeDif;
        lastPartialTick = currentPartialTick;


    }



    public static int rgba(int r, int g, int b, int a) {
        return ((a) << 24) |
                ((r) << 16) |
                ((g) << 8)  |
                ((b));
    }
}
