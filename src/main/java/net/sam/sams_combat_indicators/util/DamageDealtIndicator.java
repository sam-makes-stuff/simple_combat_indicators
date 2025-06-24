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

    public static int lifetime;
    public static int initialTime;

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
    public int color;

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

    public DamageDealtIndicator(int targetId, Entity target, float damage) {
        this.target = target;
        this.targetId = targetId;
        this.targetPos = target.position().add(new Vec3(0,target.getBbHeight() * 0.5f,0));
        this.damage = damage;

        int lifetime_temp;
        try{
            lifetime_temp = ClientConfig.NUMBER_DURATION.get();
        }catch (Exception e) {
            System.out.println("Invalid number lifetime given in client config");
            lifetime_temp = 30;
        }
        this.lifetime = lifetime_temp;
        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null){
            this.maxHealthProportion = damage / (Minecraft.getInstance().player.getMaxHealth());

            int r_temp;
            int g_temp;
            int b_temp;
            //set color from config
            try{
                r_temp = ClientConfig.END_NUMBER_COLOR_R.get();
                g_temp = ClientConfig.END_NUMBER_COLOR_G.get();
                b_temp = ClientConfig.END_NUMBER_COLOR_B.get();
            }catch(Exception e){
                System.out.println("Cannot convert given r,g,b to integer, defaulting to white");
                r_temp = 255;
                g_temp = 255;
                b_temp = 255;
            }

            this.r_e = r_temp;
            this.g_e = g_temp;
            this.b_e = b_temp;

            int initial_r_temp;
            int initial_g_temp;
            int initial_b_temp;

            try{
                initial_r_temp = ClientConfig.START_NUMBER_COLOR_R.get();
                initial_g_temp = ClientConfig.START_NUMBER_COLOR_G.get();
                initial_b_temp = ClientConfig.START_NUMBER_COLOR_B.get();
            }catch (Exception e){
                System.out.println("Cannot convert given r,g,b to integer, defaulting to white");
                initial_r_temp = 255;
                initial_g_temp = 255;
                initial_b_temp = 255;
            }

            this.r_s = initial_r_temp;
            this.g_s = initial_g_temp;
            this.b_s = initial_b_temp;

            int tempTime;
            try{
                tempTime = ClientConfig.INITIAL_HIT_TIME.get();
            }catch (Exception e){
                tempTime = 0;
            }
            initialTime = tempTime;

            if(ClientConfig.ROTATE_NUMBERS.get()) {
                this.rotation = (target.level().random.nextFloat() * ClientConfig.ROTATE_RANGE.get() * 2) - ClientConfig.ROTATE_RANGE.get();
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

        float colorRatio = age/initialTime;
        if (colorRatio > 1.0f){
            colorRatio = 1.0f;
        }

        r = (int)(r_e + (colorRatio * (r_s - r_e)));
        g = (int)(g_e + (colorRatio * (g_s - g_e)));
        b = (int)(b_e + (colorRatio * (b_s - b_e)));
        this.color = rgba(this.r,this.g,this.b,this.a);
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

    public static int rgba(int r, int g, int b, int a) {
        return ((a) << 24) |
                ((r) << 16) |
                ((g) << 8)  |
                ((b));
    }
}
