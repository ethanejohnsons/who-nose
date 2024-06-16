package dev.bluevista.whonose.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.bluevista.whonose.WhoNoseMod.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	@Unique
	private boolean prevOnGround;

	/**
	 * HONK when damaged.
	 */
	@Inject(method = "damage", at = @At("RETURN"))
	public void whonose$damage$RETURN(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!isWearingNose(this)) return;
		playHonk(this);
	}

	/**
	 * HONK when hitting the ground.
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	public void whonose$tick$HEAD(CallbackInfo ci) {
		if (!isWearingNose(this)) return;

		boolean onGround = isOnGround();
		if (onGround && !prevOnGround) {
			playHonk(this);
		}
		prevOnGround = onGround;
	}

	private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

}
