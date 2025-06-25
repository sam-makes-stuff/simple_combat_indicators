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

    public static int lifetime = ConfigGetter.getOrDefault(ClientConfig.NUMBER_DURATION);
    public static int initialTime = ConfigGetter.getOrDefault(ClientConfig.INITIAL_HIT_TIME);

    public static float maxRotation = ConfigGetter.getOrDefault(ClientConfig.ROTATE_RANGE);

    public Entity target;
    public int targetId;
    public Vec3 targetPos;
    public float damage;

    public float age = 0;
    public float currentPartialTick = 0f;
    public float lastPartialTick = 0f;
    public float rotation; //in degrees
    public float maxHealthProportion = 0f;
    public float baseScale;

    public float incrementScale;

    public float scale = 0.0f;
    public int color;

    public int stackCount;

    public int r_s;
    public int g_s;
    public int b_s;

    public int r_e;
    public int g_e;
    public int b_e;

    public int r;
    public int g;
    public int b;
    public int a;

    public DamageDealtIndicator(int targetId, Entity target, float damage, int stackCount) {
        this.target = target;
        this.targetId = targetId;
        this.targetPos = target.position().add(new Vec3(0,target.getBbHeight() * 0.5f,0));
        this.damage = damage;
        this.stackCount = stackCount;

        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null){
            this.maxHealthProportion = damage / (Minecraft.getInstance().player.getMaxHealth());

            this.r_e = ConfigGetter.getOrDefault(ClientConfig.END_NUMBER_COLOR_R);
            this.g_e = ConfigGetter.getOrDefault(ClientConfig.END_NUMBER_COLOR_G);
            this.b_e = ConfigGetter.getOrDefault(ClientConfig.END_NUMBER_COLOR_B);

            this.r_s = ConfigGetter.getOrDefault(ClientConfig.START_NUMBER_COLOR_R);
            this.g_s = ConfigGetter.getOrDefault(ClientConfig.START_NUMBER_COLOR_G);
            this.b_s = ConfigGetter.getOrDefault(ClientConfig.START_NUMBER_COLOR_B);

            this.baseScale = (float) (double) ConfigGetter.getOrDefault(ClientConfig.NUMBER_BASE_SCALE);

            this.incrementScale = (float) (double) ConfigGetter.getOrDefault(ClientConfig.NUMBER_INCREMENT_SCALE);

            if(maxRotation != 0.0) {
                this.rotation = (target.level().random.nextFloat() * maxRotation * 2) - maxRotation;
            }else{
                this.rotation = 0.0f;
            }

            this.a = 255;

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

        r = (int)(r_e + (scaleRatio * (r_s - r_e)));
        g = (int)(g_e + (scaleRatio * (g_s - g_e)));
        b = (int)(b_e + (scaleRatio * (b_s - b_e)));
        this.color = rgba(this.r,this.g,this.b,this.a);
        this.scale = 3 * (baseScale + (stackCount * incrementScale)) / Minecraft.getInstance().options.guiScale().get();
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
