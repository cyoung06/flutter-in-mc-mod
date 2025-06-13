package com.syeyoung.fluttermc.commands;

import com.syeyoung.fluttermc.ExampleMod;
import com.syeyoung.fluttermc.FlutterGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.List;

public class OpenGui extends CommandBase {

    @Override
    public String getCommandName() {
        return "opengui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }
    // In your main mod class
    public static GuiScreen screenToOpenNextTick = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (screenToOpenNextTick != null) {
            Minecraft.getMinecraft().displayGuiScreen(screenToOpenNextTick);
            screenToOpenNextTick = null;
        }
    }

    // In your command class:
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        OpenGui.screenToOpenNextTick = new FlutterGuiScreen("./myapp");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("dontcrashme");
    }
}