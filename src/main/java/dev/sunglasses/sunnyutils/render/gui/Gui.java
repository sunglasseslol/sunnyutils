package dev.sunglasses.sunnyutils.render.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import dev.sunglasses.sunnyutils.modules.base.Module;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;

import java.util.List;

@EventBusSubscriber(modid = SunnyUtils.MODID, value = Dist.CLIENT)
public class Gui {
    /*
    5, 5, = TOP LEFT
    5, mc.getWindow().getGuiScaledHeight(), = BOTTOM LEFT
    mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, 5 = TOP RIGHT
    mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, mc.getWindow().getGuiScaledHeight(), = BOTTOM RIGHT
    */

    public static void drawString(Minecraft mc, GuiGraphics gui, String text, int x, int y, int colour) {
        gui.drawString(mc.font, text, x, y, colour);
    }

    public static void renderTextOverlay(PoseStack poseStack, Component text, double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(x - camPos.x, y - camPos.y, z - camPos.z);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.02F, -0.02F, 0.02F); // text size

        Matrix4f matrix = poseStack.last().pose();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        Font font = mc.font;
        int color = 0xFFFFFF;
        int halfWidth = font.width(text) / 2;

        font.drawInBatch(
                text,
                -halfWidth, 0,
                color,
                false,
                matrix,
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                0,
                15728880
        );

        buffer.endBatch(); // flush fonts
        poseStack.popPose();
    }

    /*
    Creates a button with given position, size, text, and action.
    */

    public static Button createButton(int x, int y, int width, int height, String text, Button.OnPress onPress) {
        return Button.builder(Component.literal(text), onPress).bounds(x, y, width, height).build();
    }

    public static CycleButton<Boolean> createCycleButton(int x, int y, int width, int height, String text, CycleButton.OnValueChange<Boolean> change, boolean initial) {
        return CycleButton.onOffBuilder()
                .withInitialValue(Ruler.getAreaMode())
                .create(x, y, width, height, Component.literal(text),
                        (btn, value) -> Ruler.setAreaMode(value));
    }

    public static void drawModules(Minecraft mc, GuiGraphics gui, List<Module> modules) {
        int startY = 5;                // starting Y position for the first box
        int gap = 3;                   // pixels between boxes
        int padding = 6;               // vertical padding inside each box
        int boxHeight = mc.font.lineHeight + padding * 2;

        int y = startY;

        for (Module m : modules) {
            if (m instanceof ToggleModule toggle && toggle.isEnabled()) {
                int boxMinY = y;
                int boxMaxY = boxMinY + boxHeight;

                // background box
                gui.fill( 5, boxMinY, mc.font.width(m.getName()) + 25, boxMaxY, 0x7F000000);

                // vertically centered text
                int textY = boxMinY + (boxHeight - mc.font.lineHeight) / 2;
                drawString(mc, gui, m.getName(), 15, textY, 0xFFFFFFFF);

                // move down for next box
                y += boxHeight + gap;
            }
        }
    }

}

