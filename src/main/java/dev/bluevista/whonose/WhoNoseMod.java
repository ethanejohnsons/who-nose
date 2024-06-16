package dev.bluevista.whonose;

import dev.bluevista.whonose.client.ClownNoseRenderer;
import dev.bluevista.whonose.item.ClownNoseItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * honk
 */
public class WhoNoseMod implements ModInitializer, ClientModInitializer {

	public static final String MODID = "whonose";
	public static final Logger LOGGER = LogManager.getLogger("Who Nose");

	// Item
	public static final Item CLOWN_NOSE_ITEM = Registry.register(Registries.ITEM, Identifier.of(MODID, "clown_nose"), new ClownNoseItem());

	// Sound
	public static Identifier HONK_SOUND_ID = Identifier.of(MODID, "honk");
	public static SoundEvent HONK_SOUND_EVENT = Registry.register(Registries.SOUND_EVENT, HONK_SOUND_ID, SoundEvent.of(HONK_SOUND_ID));

	public static boolean isWearingNose(Entity entity) {
		if (entity instanceof ServerPlayerEntity player) {
			return player.getInventory().armor.get(3).getItem() == CLOWN_NOSE_ITEM;
		}
		return false;
	}

	public static void playHonk(Entity source) {
		if (source.getWorld().isClient) return;

		source.getWorld().playSound(
			null,
			source.getBlockPos(),
			HONK_SOUND_EVENT,
			SoundCategory.PLAYERS,
			8.0f,
			1.0f
		);
	}

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
			content.add(CLOWN_NOSE_ITEM);
		});

		LOGGER.info("HONK");
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ArmorRenderer.register(new ClownNoseRenderer(), CLOWN_NOSE_ITEM);
	}

}
