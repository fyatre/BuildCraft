/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.core;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import buildcraft.api.BCModules;

import buildcraft.lib.BCLibConfig;
import buildcraft.lib.config.EnumRestartRequirement;
import buildcraft.lib.config.FileConfigManager;
import buildcraft.lib.misc.ConfigUtil;
import buildcraft.lib.registry.RegistryHelper;

public class BCCoreConfig {
    public static Configuration config;
    public static Configuration objConfig;
    public static FileConfigManager detailedConfigManager;

    public static boolean colourBlindMode;
    public static boolean worldGen;
    public static boolean worldGenWaterSpring;
    public static boolean useLocalServerOnClient;
    public static boolean minePlayerProteted;
    public static boolean useColouredLabels;
    public static boolean hidePower;
    public static boolean hideFluid;
    public static boolean useBucketsStatic;
    public static boolean useBucketsFlow;
    public static boolean useLongLocalizedName;
    public static int itemLifespan;
    public static int markerMaxDistance;
    public static int networkUpdateRate = 10;

    private static Property propColourBlindMode;
    private static Property propWorldGen;
    private static Property propWorldGenWaterSpring;
    private static Property propUseLocalServerOnClient;
    private static Property propMinePlayerProtected;
    private static Property propUseColouredLabels;
    private static Property propHidePower;
    private static Property propHideFluid;
    private static Property propUseBucketsStatic;
    private static Property propUseBucketsFlow;
    private static Property propUseLongLocalizedName;
    private static Property propItemLifespan;
    private static Property propMarkerMaxDistance;
    private static Property propNetworkUpdateRate;

    public static void preInit(File cfgFolder) {
        config = new Configuration(new File(cfgFolder, "main.cfg"));
        objConfig = RegistryHelper.setRegistryConfig(BCCore.MODID, new File(cfgFolder, "objects.cfg"));

        detailedConfigManager = new FileConfigManager(" The buildcraft detailed configuration file. This contains a lot of miscelaneous options that have no "
            + "affect on gameplay.\n You should refer to the BC source code for a detailed description of what these do. (https://github.com/BuildCraft/BuildCraft)\n"
            + " This file will be overwritten every time that buildcraft starts, so don't change anything other than the values.");
        detailedConfigManager.setConfigFile(new File(cfgFolder, "detailed.properties"));

        // Variables to make
        String general = Configuration.CATEGORY_GENERAL;
        String display = "display";
        String worldgen = "worldgen";

        EnumRestartRequirement none = EnumRestartRequirement.NONE;
        EnumRestartRequirement game = EnumRestartRequirement.GAME;

        propColourBlindMode = config.get(display, "colorBlindMode", false);
        propColourBlindMode.setComment("Should I enable colorblind mode?");
        game.setTo(propColourBlindMode);

        propWorldGen = config.get(worldgen, "enable", true);
        propWorldGen.setComment("Should BuildCraft generate anything in the world?");
        game.setTo(propWorldGen);

        propWorldGenWaterSpring = config.get(worldgen, "generateWaterSprings", true);
        propWorldGenWaterSpring.setComment("Should BuildCraft generate water springs?");
        game.setTo(propWorldGenWaterSpring);

        propUseLocalServerOnClient = config.get(general, "useServerDataOnClient", true);
        propUseLocalServerOnClient.setComment("Allows BuildCraft to use the integrated server's data on the client on singleplayer worlds. Disable if you're getting the odd crash caused by it.");
        none.setTo(propUseLocalServerOnClient);

        propMinePlayerProtected = config.get(general, "miningBreaksPlayerProtectedBlocks", false);
        propMinePlayerProtected.setComment("Should BuildCraft miners be allowed to break blocks using player-specific protection?");
        none.setTo(propMinePlayerProtected);

        propUseColouredLabels = config.get(display, "useColouredLabels", true);
        propUseColouredLabels.setComment("Should colours be displayed as their own (or a similar) colour in tooltips?");
        none.setTo(propUseColouredLabels);

        propHidePower = config.get(display, "hidePowerValues", false);
        propHidePower.setComment("Should all power values (MJ, MJ/t) be hidden?");
        none.setTo(propHidePower);

        propHideFluid = config.get(display, "hideFluidValues", false);
        propHideFluid.setComment("Should all fluid values (Buckets, mB, mB/t) be hidden?");
        none.setTo(propHideFluid);

        propUseBucketsStatic = config.get(display, "useBucketsStatic", false);
        propUseBucketsStatic.setComment("Should static fluid values be displayed in terms of buckets rather than thousandths of a bucket? (B vs mB)");
        none.setTo(propUseBucketsStatic);

        propUseBucketsFlow = config.get(display, "useBucketsFlow", false);
        propUseBucketsFlow.setComment("Should flowing fluid values be displayed in terms of buckets per second rather than thousandths of a bucket per tick? (B/s vs mB/t)");
        none.setTo(propUseBucketsFlow);

        propUseLongLocalizedName = config.get(display, "useLongLocalizedName", false);
        propUseLongLocalizedName.setComment("Should localised strings be displayed in long or short form (10 mB / t vs 10 milli buckets per tick");
        none.setTo(propUseLongLocalizedName);

        propItemLifespan = config.get(general, "itemLifespan", 60);
        propItemLifespan.setMinValue(5).setMaxValue(600);
        propItemLifespan.setComment("How long, in seconds, should items stay on the ground? (Vanilla = 300, default = 60)");
        none.setTo(propItemLifespan);

        propMarkerMaxDistance = config.get(general, "markerMaxDistance", 64);
        propMarkerMaxDistance.setMinValue(16).setMaxValue(256);
        propMarkerMaxDistance.setComment("How far, in minecraft blocks, should markers (volume and path) reach?");
        none.setTo(propMarkerMaxDistance);

        propNetworkUpdateRate = config.get(general, "updateFactor", networkUpdateRate);
        propNetworkUpdateRate.setMinValue(1).setMaxValue(100);
        propNetworkUpdateRate.setComment("How often, in ticks, should network update packets be sent? Increasing this might help network performance.");
        none.setTo(propNetworkUpdateRate);

        reloadConfig(game);

        MinecraftForge.EVENT_BUS.register(BCCoreConfig.class);
    }

    @SubscribeEvent
    public static void onConfigChange(OnConfigChangedEvent cce) {
        if (BCModules.isBcMod(cce.getModID())) {
            EnumRestartRequirement req = EnumRestartRequirement.NONE;
            if (Loader.instance().isInState(LoaderState.AVAILABLE)) {
                // The loaders state will be LoaderState.SERVER_STARTED when we are in a world
                req = EnumRestartRequirement.WORLD;
            }
            reloadConfig(req);
        }
    }

    public static void postInit() {
        ConfigUtil.setLang(config);
        if (config.hasChanged()) {
            config.save();
        }
        if (objConfig.hasChanged()) {
            objConfig.save();
        }
    }

    public static void reloadConfig(EnumRestartRequirement restarted) {
        useLocalServerOnClient = propUseLocalServerOnClient.getBoolean();
        minePlayerProteted = propMinePlayerProtected.getBoolean();
        useColouredLabels = propUseColouredLabels.getBoolean();
        BCLibConfig.useColouredLabels = useColouredLabels;
        hidePower = propHidePower.getBoolean();
        hideFluid = propHideFluid.getBoolean();
        BCLibConfig.useBucketsStatic = useBucketsStatic = propUseBucketsStatic.getBoolean();
        BCLibConfig.useBucketsFlow = useBucketsFlow = propUseBucketsFlow.getBoolean();
        BCLibConfig.useLongLocalizedName = useLongLocalizedName = propUseLongLocalizedName.getBoolean();
        itemLifespan = propItemLifespan.getInt();
        BCLibConfig.itemLifespan = itemLifespan;
        markerMaxDistance = propMarkerMaxDistance.getInt();

        if (EnumRestartRequirement.GAME.hasBeenRestarted(restarted)) {
            colourBlindMode = propColourBlindMode.getBoolean();
            worldGen = propWorldGen.getBoolean();
            worldGenWaterSpring = propWorldGenWaterSpring.getBoolean();
        }
        BCLibConfig.refreshConfigs();
        if (config.hasChanged()) {
            config.save();
        }
        if (objConfig.hasChanged()) {
            objConfig.save();
        }
    }
}
