package com.syeyoung.fluttermc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.security.Key;

public class FlutterGuiScreen extends GuiScreen {
    public static native long startFlutter(String assetsDir, String icudtlPath);
    public static native void bindTexture(long handle, long textureId);
    public static native void resize(long handle, int width, int height);
    public static native void mouseEvent(long handle, int type, int x, int y, int buttons);
    public static native void scrollEvent(long handle, int x,int y, int scroll);
    public static native boolean keyEvent(long handle, int type, int physical, int logical, char c);
    public static native void runTasks(long handle);

    private long handle;
    private long textureId;
    public FlutterGuiScreen(String assetsDir) {
        handle = startFlutter(assetsDir, "icudtl.dat");
        if (handle == 0) throw new RuntimeException("Failed to init");
        textureId = GlStateManager.generateTexture();
        bindTexture(handle, textureId);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);

        resize(handle, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        runTasks(handle);
        GuiScreen.drawRect(0,0,width, height, 0xFFFFFFFF);
        Tessellator tessellator = Tessellator.getInstance();

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0/scaledResolution.getScaleFactor(), 1.0/scaledResolution.getScaleFactor(), 1.0);

        int width = this.mc.displayWidth;
        int height = this.mc.displayHeight;

        GL11.glEnable(0x84F5);
        GL11.glBindTexture(0x84F5, (int) textureId);
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, height, 0).tex(0, height).endVertex();
        worldRenderer.pos(width,height,0).tex(width,height).endVertex();
        worldRenderer.pos(width,0,0).tex(width,0).endVertex();
        worldRenderer.pos(0,0, 0).tex(0,0).endVertex();
        tessellator.draw();
        GL11.glDisable(0x84F5);

        GlStateManager.popMatrix();
    }


    private int touchValue = 0;
    private int eventButton = 0;
    private long lastMouseEvent = 0;
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();


        int i = Mouse.getEventX();
        int j = this.mc.displayHeight - Mouse.getEventY() - 1;
        int k = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            mouseEvent(handle, 2, i, j, eventButton);
        } else if (k != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }

            this.eventButton = -1;
            mouseEvent(handle, 1, i, j, eventButton);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            mouseEvent(handle, 3, i, j, eventButton);
        } else if (this.eventButton == -1) {
            mouseEvent(handle, 6, i, j, eventButton);
        }
        if ( Mouse.getEventDWheel() != 0) {
            scrollEvent(handle,  i, j, Mouse.getEventDWheel() / 10);
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        Keyboard.enableRepeatEvents(true);

        boolean handled;
        int mod = 0;
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            mod |= 2;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            mod |= 1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            mod |= 4;
        }


        if (Keyboard.getEventKeyState() && Keyboard.isRepeatEvent())  {
//            Keyboard.getevent Mac
//            MacOSNativeKeyboard;
            handled = keyEvent(handle, 1, Keyboard.getEventKey(),mod,Keyboard.getEventCharacter());
        } else if (Keyboard.getEventKeyState()) {
            handled = keyEvent(handle, 0, Keyboard.getEventKey(),mod,Keyboard.getEventCharacter());
        } else {
            handled = keyEvent(handle, 2, Keyboard.getEventKey(),mod,Keyboard.getEventCharacter());
        }

        if (!handled) {
            super.handleKeyboardInput();
        }
    }
}