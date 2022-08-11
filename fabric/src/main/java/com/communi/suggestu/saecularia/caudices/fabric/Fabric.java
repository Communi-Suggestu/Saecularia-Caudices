package com.communi.suggestu.saecularia.caudices.fabric;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Fabric implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("SaeculariaCaudices-Fabric");

	@Override
	public void onInitialize() {
        LOGGER.info("Initialized Saecularia-Caudices");
	}
}
