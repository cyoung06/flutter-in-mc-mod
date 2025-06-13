package com.syeyoung.fluttermc;

import com.syeyoung.fluttermc.commands.OpenGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import java.io.File;

@Mod(modid = "examplemod", useMetadata=true)
public class ExampleMod {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Dirt: " + Blocks.dirt.getUnlocalizedName());
		// Below is a demonstration of an access-transformed class access.
        System.out.println("Color State: " + new GlStateManager.Color());
        System.load(new File("libflutterembedder.dylib").getAbsolutePath());
        System.load(new File("libnative.dylib").getAbsolutePath());


        OpenGui openGui = new OpenGui();
        ClientCommandHandler.instance.registerCommand(openGui);
        MinecraftForge.EVENT_BUS.register(openGui);
    }
}
