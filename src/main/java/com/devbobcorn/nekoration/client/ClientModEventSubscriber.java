package com.devbobcorn.nekoration.client;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blockentities.ModEntityType;
import com.devbobcorn.nekoration.blocks.DyeableBlock;
import com.devbobcorn.nekoration.blocks.DyeableDoorBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberPillarBlock;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.WindowBlock;
import com.devbobcorn.nekoration.client.rendering.EaselMenuRenderer;
import com.devbobcorn.nekoration.particles.FlameParticleFactory;
import com.devbobcorn.nekoration.particles.ModParticles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;

// Client-Side Only Things...
@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEventSubscriber {
	private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Client Mod Event Subscriber");

	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		LOGGER.info("Client Side Setup.");
		RenderType transparentRenderType = RenderType.cutoutMipped();
		// RenderType cutoutRenderType = RenderType.cutout();
		RenderType translucentRenderType = RenderType.translucent();

		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P0.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P1.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P2.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P3.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P4.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P5.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P6.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P7.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P8.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P9.get(), translucentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P0.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P1.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P2.get(), translucentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_ARCH.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_CROSS.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_SHADE.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_LANCET.get(), transparentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_1.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_2.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_3.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_1.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_2.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_3.get(), translucentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_PURE.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_PURE_SHORT.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_STRIPE.get(), translucentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_STRIPE_SHORT.get(), translucentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_IRON.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_GOLD.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_QUARTZ.get(), transparentRenderType);

		RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_IRON.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_GOLD.get(), transparentRenderType);
		RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_QUARTZ.get(), transparentRenderType);

		LOGGER.info("Block Render Type Registered.");

		ClientRegistry.bindTileEntityRenderer(ModEntityType.EASEL_MENU_TYPE, EaselMenuRenderer::new);

		LOGGER.info("Block Entity Renderer Binded.");
	}

	@SubscribeEvent
	public static void registerBlockColors(ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.STONE_BASE_BOTTOM.get(), ModBlocks.STONE_FRAME_BOTTOM.get(), ModBlocks.STONE_PILLAR_BOTTOM.get(),
				ModBlocks.STONE_POT.get(), ModBlocks.CANDLE_HOLDER_IRON.get(), ModBlocks.CANDLE_HOLDER_GOLD.get(), ModBlocks.CANDLE_HOLDER_QUARTZ.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getColor(2);
			return NekoColors.getColor(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.EASEL_MENU.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableVerticalConnectBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableVerticalConnectBlock.COLOR));
		}, ModBlocks.STONE_BASE.get(), ModBlocks.STONE_FRAME.get(), ModBlocks.STONE_PILLAR.get(),
				ModBlocks.STONE_DORIC.get(), ModBlocks.STONE_IONIC.get(), ModBlocks.STONE_CORINTHIAN.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableHorizontalConnectBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableHorizontalConnectBlock.COLOR));
		}, ModBlocks.WINDOW_SILL.get(), ModBlocks.WINDOW_TOP.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(HalfTimberBlock.COLOR));
		}, ModBlocks.HALF_TIMBER_P0.get(), ModBlocks.HALF_TIMBER_P1.get(), ModBlocks.HALF_TIMBER_P2.get(),
				ModBlocks.HALF_TIMBER_P3.get(), ModBlocks.HALF_TIMBER_P4.get(), ModBlocks.HALF_TIMBER_P5.get(),
				ModBlocks.HALF_TIMBER_P6.get(), ModBlocks.HALF_TIMBER_P7.get(), ModBlocks.HALF_TIMBER_P8.get(),
				ModBlocks.HALF_TIMBER_P9.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberPillarBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(HalfTimberPillarBlock.COLOR));
		}, ModBlocks.HALF_TIMBER_PILLAR_P0.get(), ModBlocks.HALF_TIMBER_PILLAR_P1.get(),
				ModBlocks.HALF_TIMBER_PILLAR_P2.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof WindowBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(WindowBlock.COLOR));
		}, ModBlocks.WINDOW_ARCH.get(), ModBlocks.WINDOW_CROSS.get(), ModBlocks.WINDOW_SHADE.get(),
				ModBlocks.WINDOW_LANCET.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableDoorBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableDoorBlock.COLOR));
		}, ModBlocks.DOOR_1.get(), ModBlocks.DOOR_2.get(), ModBlocks.DOOR_3.get(), ModBlocks.DOOR_TALL_1.get(),
				ModBlocks.DOOR_TALL_2.get(), ModBlocks.DOOR_TALL_3.get());

		LOGGER.info("Block Colors Registered.");
	}

	@SubscribeEvent
	public static void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getColor(14);
		}, ModBlocks.STONE_BASE_BOTTOM.get().asItem(), ModBlocks.STONE_FRAME_BOTTOM.get().asItem(),
				ModBlocks.STONE_PILLAR_BOTTOM.get().asItem(), ModBlocks.STONE_BASE.get().asItem(),
				ModBlocks.STONE_FRAME.get().asItem(), ModBlocks.STONE_PILLAR.get().asItem(),
				ModBlocks.STONE_DORIC.get().asItem(), ModBlocks.STONE_IONIC.get().asItem(),
				ModBlocks.STONE_CORINTHIAN.get().asItem(), ModBlocks.WINDOW_SILL.get().asItem(),
				ModBlocks.WINDOW_TOP.get().asItem(), ModBlocks.STONE_POT.get().asItem(),
				ModBlocks.CANDLE_HOLDER_IRON.get().asItem(), ModBlocks.CANDLE_HOLDER_GOLD.get().asItem(),ModBlocks.CANDLE_HOLDER_QUARTZ.get().asItem()
				);

		event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getColor(2);
		}, ModBlocks.HALF_TIMBER_P0.get().asItem(), ModBlocks.HALF_TIMBER_P1.get().asItem(),
				ModBlocks.HALF_TIMBER_P2.get().asItem(), ModBlocks.HALF_TIMBER_P3.get().asItem(),
				ModBlocks.HALF_TIMBER_P4.get().asItem(), ModBlocks.HALF_TIMBER_P5.get().asItem(),
				ModBlocks.HALF_TIMBER_P6.get().asItem(), ModBlocks.HALF_TIMBER_P7.get().asItem(),
				ModBlocks.HALF_TIMBER_P8.get().asItem(), ModBlocks.HALF_TIMBER_P9.get().asItem(),
				ModBlocks.HALF_TIMBER_PILLAR_P0.get().asItem(), ModBlocks.HALF_TIMBER_PILLAR_P1.get().asItem(),
				ModBlocks.HALF_TIMBER_PILLAR_P2.get().asItem(), ModBlocks.WINDOW_ARCH.get().asItem(),
				ModBlocks.WINDOW_CROSS.get().asItem(), ModBlocks.WINDOW_SHADE.get().asItem(),
				ModBlocks.WINDOW_LANCET.get().asItem(), ModBlocks.EASEL_MENU.get().asItem());

		LOGGER.info("Item Colors Registered.");
	}

	// Register the factory that will spawn our Particle from ParticleData
	@SubscribeEvent
	@SuppressWarnings({ "resource" })
	public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
		// beware - there are two registerFactory methods with different signatures.
		// If you use the wrong one it will put Minecraft into an infinite loading loop
		// with no console errors
		Minecraft.getInstance().particleEngine.register(ModParticles.FLAME, sprite -> new FlameParticleFactory(sprite));
		// This lambda may not be obvious: its purpose is:
		// the registerFactory method creates an IAnimatedSprite, then passes it to the
		// constructor of FlameParticleFactory

		// General rule of thumb:
		// If you are creating a TextureParticle with a corresponding json to specify
		// textures which will be stitched into the
		// particle texture sheet, then use the 1-parameter constructor method
		// If you're supplying the render yourself, or using a texture from the block
		// sheet, use the 0-parameter constructor method
		// (examples are MobAppearanceParticle, DiggingParticle). See
		// ParticleManager::registerFactories for more.
	}
}
