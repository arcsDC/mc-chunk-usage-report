package com.example.chunkreport;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ChunkReportMod implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean visible = false;
    private static int lastUpdate = 0;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Chunk Report",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F3,
                "category.debug"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                visible = !visible;
            }

            if (visible && client.world != null && client.player != null) {
                if (client.tickCount - lastUpdate > 20) {
                    lastUpdate = client.tickCount;
                }
            }
        });

        // Hook into render to draw text
        // In a real mod, you'd use HudRenderCallback or similar
        // For brevity, this demonstrates the structure.
        // Actual rendering would be in a separate class or lambda.
    }

    public static List<String> getReportLines() {
        MinecraftClient client = MinecraftClient.getInstance();
        List<String> lines = new ArrayList<>();
        if (client.world == null || client.player == null) return lines;

        // Accessing chunk manager is server-side, but for client-side estimation:
        // We can estimate based on view distance settings
        int viewDistance = client.options.getViewDistance().getValue();
        int radius = viewDistance * 16;
        int totalChunks = (radius * 2 + 1) * (radius * 2 + 1);
        
        lines.add("Chunk Report (Estimated)");
        lines.add("View Distance: " + viewDistance + " chunks");
        lines.add("Approx. Loaded: " + totalChunks + " chunks");
        lines.add("Memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB");
        
        return lines;
    }
}
