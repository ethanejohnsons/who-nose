package dev.bluevista.whonose.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;

import java.util.function.BiFunction;

import static dev.bluevista.whonose.WhoNoseMod.*;
import static net.minecraft.client.render.RenderPhase.*;

@Environment(EnvType.CLIENT)
public class ClownNoseRenderer implements ArmorRenderer {

	public static final Identifier TEXTURE = Identifier.of(MODID, "textures/item/clown_nose.png");
	public static final BiFunction<Identifier, Boolean, RenderLayer> RENDER_LAYER = Util.memoize((texture, affectsOutline) -> {
		var params = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.program(POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM)
			.transparency(NO_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.build(affectsOutline);
		return RenderLayer.of("nose", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP, 256, false, false, params);
	});

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vcp, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> model) {
		matrices.push();

		// Transform
		float scale = 0.1f;
		matrices.scale(scale, scale, scale);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(model.getHead().yaw)));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(model.getHead().pitch)));
		matrices.translate(0.0, -1.5, -3.0);

		// Draw
		float brightness = light / (float) LightmapTextureManager.MAX_LIGHT_COORDINATE;
		var consumer = vcp.getBuffer(RENDER_LAYER.apply(TEXTURE, false));
		sphere(matrices, consumer, 25, brightness);

		matrices.pop();
	}

	private void sphere(MatrixStack matrices, VertexConsumer consumer, int segments, float brightness) {
		var position = matrices.peek().getPositionMatrix();

		for (int lat = 0; lat < segments; lat++) {
			float latAngle1 = (float) Math.PI * (-0.5f + (float) lat / segments);
			float latAngle2 = (float) Math.PI * (-0.5f + (float) (lat + 1) / segments);

			for (int lon = 0; lon <= segments; lon++) {
				for (int i = 0; i < 2; i++) {
					float latAngle = (i == 0) ? latAngle1 : latAngle2;
					float sinLat = (float) Math.sin(latAngle);
					float cosLat = (float) Math.cos(latAngle);

					float lonAngle = 2 * (float) Math.PI * lon / segments;
					float sinLon = (float) Math.sin(lonAngle);
					float cosLon = (float) Math.cos(lonAngle);

					float x = cosLon * cosLat;
					float y = sinLon * cosLat;
					float z = sinLat;

					consumer
						.vertex(position, x, z, y)
						.color(1.0f, 1.0f, 1.0f, 1.0f)
						.texture(0.9f * brightness, 0.9f * brightness)
						.light(255);
				}
			}
		}
	}

}