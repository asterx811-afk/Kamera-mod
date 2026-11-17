package com.uzbekdev.litecinecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class LiteCineCamClient implements ClientModInitializer {

    // Minecraft Controls menyusiga qo'shiladigan tugmalar
    public static KeyBinding orbitKey;      // Bino atrofida aylanish (O)
    public static KeyBinding slowMoKey;     // Sekinlashtirish (Down)
    public static KeyBinding fastMoKey;     // Tezlashtirish (Up)
    public static KeyBinding normalTimeKey; // Oddiy tezlikka qaytish (R)
    public static KeyBinding savePosKey;    // Kadr nuqtasini saqlash (P)
    public static KeyBinding cutCamKey;     // Saqlangan kadrga keskin o'tish / Cut (K)

    public static boolean isOrbiting = false;
    public static Vec3d savedPos = null;
    public static float savedYaw = 0;
    public static float savedPitch = 0;

    @Override
    public void onInitializeClient() {
        orbitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.orbit", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.litecinecam.main"));

        slowMoKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.slowmo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.litecinecam.main"));

        fastMoKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.fastmo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.litecinecam.main"));

        normalTimeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.normal", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.litecinecam.main"));

        savePosKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.savepos", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "category.litecinecam.main"));

        cutCamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litecinecam.cut", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.litecinecam.main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // 1. Aylanish rejimi (Orbit)
            while (orbitKey.wasPressed()) {
                isOrbiting = !isOrbiting;
                client.player.sendMessage(Text.literal("§a[LiteCineCam] Aylanish: " + (isOrbiting ? "YONIQ" : "O'CHIQ")), true);
            }

            // 2. Sekinlashtirish (Slow-Mo 0.25x)
            while (slowMoKey.wasPressed()) {
                client.world.getTickManager().setTickRate(5.0f);
                client.player.sendMessage(Text.literal("§b[LiteCineCam] Sekinlashtirildi (0.25x)"), true);
            }

            // 3. Tezlashtirish (Time-Lapse 3x)
            while (fastMoKey.wasPressed()) {
                client.world.getTickManager().setTickRate(60.0f);
                client.player.sendMessage(Text.literal("§e[LiteCineCam] Tezlashtirildi (3x)"), true);
            }

            // 4. Oddiy vaqtga qaytish (1x)
            while (normalTimeKey.wasPressed()) {
                client.world.getTickManager().setTickRate(20.0f);
                client.player.sendMessage(Text.literal("§f[LiteCineCam] Oddiy tezlik (1x)"), true);
            }

            // 5. Rakursni xotiraga saqlash
            while (savePosKey.wasPressed()) {
                savedPos = client.player.getPos();
                savedYaw = client.player.getYaw();
                savedPitch = client.player.getPitch();
                client.player.sendMessage(Text.literal("§d[LiteCineCam] Rakurs saqlandi!"), true);
            }

            // 6. Saqlangan rakursga keskin o'tish (Cut)
            while (cutCamKey.wasPressed()) {
                if (savedPos != null) {
                    client.player.refreshPositionAndAngles(savedPos.x, savedPos.y, savedPos.z, savedYaw, savedPitch);
                    client.player.sendMessage(Text.literal("§6[LiteCineCam] Kadr kesildi (Cut)!"), true);
                }
            }

            // Silliq burilish harakati
            if (isOrbiting) {
                client.player.setYaw(client.player.getYaw() + 0.7f);
            }
        });
    }
}
