package io.github.kurrycat.mpkmod.network.impl;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.events.OnModuleMessageEvent;
import io.github.kurrycat.mpkmod.gui.screens.LandingBlockGuiScreen;
import io.github.kurrycat.mpkmod.landingblock.LandingBlock;
import io.github.kurrycat.mpkmod.modules.ModuleManager;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.kurrycat.mpknetapi.common.network.packet.impl.clientbound.*;
import io.github.kurrycat.mpknetapi.common.network.packet.impl.serverbound.MPKPacketModuleUpdate;
import io.github.kurrycat.mpknetapi.common.network.packet.impl.shared.MPKPacketModuleMessage;

import java.util.ArrayList;
import java.util.List;

public class MPKPacketListenerClientImpl implements MPKPacketListenerClient {
    @Override
    public void handleDisableModules(MPKPacketDisableModules packet) {
        List<String> blacklist = packet.getModulesToDisable();
        ModuleManager.moduleMap.forEach(((id, module) -> {
            if (blacklist.contains(id)) {
                ModuleManager.unloadModule(module);
            }
        }));

        sendModuleUpdate();
    }

    @Override
    public void handleModuleMessage(MPKPacketModuleMessage packet) {
        EventAPI.postEvent(new OnModuleMessageEvent(packet));
    }

    @Override
    public void handleSetLandingBlock(MPKPacketSetLandingBlock packet) {
        LandingBlockGuiScreen.lbs.add(new LandingBlock(
                BoundingBox3D.asBlockPos(new Vector3D(packet.getX(), packet.getY(), packet.getZ())),
                LandingBlock.LandingMode.values()[packet.getLandingModeId()]
        ));
    }

    private void sendModuleUpdate() {
        List<String> modules = new ArrayList<>();
        ModuleManager.moduleMap.forEach((id, module) -> modules.add(id));

        Minecraft.Interface.get().ifPresent(i -> i.sendPacket(new MPKPacketModuleUpdate(modules)));
    }
}