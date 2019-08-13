package lykrast.harvestersnight.common;

import java.util.EnumSet;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.ServerBossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EntityHarvester extends MonsterEntity {
	public static final ResourceLocation LOOT = new ResourceLocation(HarvestersNight.MODID, "entities/harvester");
    protected static final DataParameter<Byte> FLAGS = EntityDataManager.<Byte>createKey(EntityHarvester.class, DataSerializers.BYTE);
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS);

	public EntityHarvester(EntityType<? extends EntityHarvester> type, World worldIn) {
		super(HarvestersNight.harvester, worldIn);
        experienceValue = 50;
        moveController = new AIMoveControl(this);
	}
	
	//If you look carefully it is very similar to the Mourner from Defiled Lands
	//Which already had a lot of stuff from Vexes
	@Override
	protected void registerGoals() {
        goalSelector.addGoal(1, new SwimGoal(this));
        goalSelector.addGoal(3, new AIClawAttack(this));
        goalSelector.addGoal(4, new AIChargeAttack(this));
        goalSelector.addGoal(8, new AIMoveRandom(this));
        goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        goalSelector.addGoal(10, new LookAtGoal(this, LivingEntity.class, 8.0F));
        goalSelector.addGoal(11, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

	@Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        //getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
    }

	@Override
    protected void registerData() {
        super.registerData();
        dataManager.register(FLAGS, (byte)0);
    }

	@Override
	public void move(MoverType type, Vec3d pos) {
        super.move(type, pos);
        doBlockCollisions();
    }

	@Override
    public void tick() {
        noClip = true;
        super.tick();
        noClip = false;
        setNoGravity(true);
    }
	
	@Override
	public void livingTick() {
		//Disappear in sunlight when it has no attack target
		if (this.isAlive()) {
			if (getAttackTarget() == null && this.isInDaylight()) {
				ItemStack itemstack = getItemStackFromSlot(EquipmentSlotType.HEAD);
				if (!itemstack.isEmpty()) {
					if (itemstack.isDamageable()) {
						itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
						if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
							this.sendBreakAnimation(EquipmentSlotType.HEAD);
							this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
						}
					}
				}
				else remove();
			}
		}
	      
		super.livingTick();
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		bossInfo.setPercent(getHealth() / getMaxHealth());
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		//Triple damage from fire
		if (source == DamageSource.HOT_FLOOR || source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA)
			amount *= 3;
		return super.attackEntityFrom(source, amount);
    }
	
	@Override
	public boolean getCanSpawnHere() {
		return ArrayUtils.contains(HarvestersNightConfig.dimList, world.provider.getDimension()) == HarvestersNightConfig.whiteList
				&& posY > 40
				&& rand.nextInt(HarvestersNightConfig.harvesterChance) == 0
				&& world.canSeeSky(new BlockPos(posX, posY + getEyeHeight(), posZ))
				&& super.getCanSpawnHere();
	}
	
	@Override
    @Nullable
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		setEquipmentBasedOnDifficulty(difficultyIn);
		//setEnchantmentBasedOnDifficulty(difficulty);
		
        if (HarvestersNightConfig.lightning) ((ServerWorld)world).addLightningBolt(new LightningBoltEntity(world, posX, posY, posZ, true));
        if (HarvestersNightConfig.laugh) playSound(HarvestersNight.harvesterSpawn, 8, 1);
		
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(HarvestersNight.harvesterScythe));
		setDropChance(EquipmentSlotType.MAINHAND, 0);
	}

	@Override
	public void addTrackingPlayer(ServerPlayerEntity player) {
		super.addTrackingPlayer(player);
		if (HarvestersNightConfig.healthBar) bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(ServerPlayerEntity player) {
		super.removeTrackingPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	public boolean isNonBoss() {
		return !HarvestersNightConfig.isBoss;
	}
	
	@Override
	public void setCustomName(@Nullable ITextComponent name) {
		super.setCustomName(name);
		bossInfo.setName(getDisplayName());
	}
	
	@Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLOCK_FIRE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return HarvestersNight.harvesterHurt;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return HarvestersNight.harvesterDie;
	}
	
	private boolean getHarvesterFlag(int mask) {
        int i = dataManager.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setHarvesterFlag(int mask, boolean value) {
        int i = dataManager.get(FLAGS);

        if (value) i |= mask;
        else i &= ~mask;

        dataManager.set(FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return getHarvesterFlag(1);
    }
    public void setCharging(boolean value) {
        setHarvesterFlag(1, value);
    }

    public boolean isCasting() {
        return getHarvesterFlag(2);
    }
    public void setCasting(boolean value) {
        setHarvesterFlag(2, value);
    }
	
	//Damn it Majong and your inner classes
	//Bunch of stuff copied from Vexes (and also from the Mourner)
	private static class AIMoveControl extends MovementController
    {
        public AIMoveControl(EntityHarvester harvester) {
            super(harvester);
        }

		@Override
		public void tick() {
			if (this.action == MovementController.Action.MOVE_TO) {
				Vec3d vec3d = new Vec3d(posX - mob.posX, posY - mob.posY, posZ - mob.posZ);
				double d0 = vec3d.length();
				if (d0 < mob.getBoundingBox().getAverageEdgeLength()) {
					this.action = MovementController.Action.WAIT;
					mob.setMotion(mob.getMotion().scale(0.5D));
				}
				else {
					mob.setMotion(mob.getMotion().add(vec3d.scale(this.speed * 0.05D / d0)));
					if (mob.getAttackTarget() == null) {
						Vec3d vec3d1 = mob.getMotion();
						mob.rotationYaw = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI);
						mob.renderYawOffset = mob.rotationYaw;
					}
					else {
						double d2 = mob.getAttackTarget().posX - mob.posX;
						double d1 = mob.getAttackTarget().posZ - mob.posZ;
						mob.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
						mob.renderYawOffset = mob.rotationYaw;
					}
				}

			}
        }
    }
	
	private static class AIMoveRandom extends Goal
    {
		private EntityHarvester harvester;

		public AIMoveRandom(EntityHarvester mourner) {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
			this.harvester = mourner;
		}

		@Override
		public boolean shouldExecute() {
			return !harvester.getMoveHelper().isUpdating() && harvester.rand.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void tick() {
			BlockPos blockpos;
			if (harvester.getAttackTarget() != null && harvester.getAttackTarget().isAlive()) blockpos = new BlockPos(harvester.getAttackTarget());
			else blockpos = new BlockPos(harvester);
			for (int i = 0; i < 3; ++i)
			{
				BlockPos blockpos1 = blockpos.add(harvester.rand.nextInt(15) - 7, harvester.rand.nextInt(11) - 5, harvester.rand.nextInt(15) - 7);

				if (harvester.world.isAirBlock(blockpos1)) {
					harvester.moveController.setMoveTo(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 0.25);

					if (harvester.getAttackTarget() == null) {
						harvester.getLookController().setLookPosition(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}

					break;
				}
			}
		}
    }
	
	private static class AIChargeAttack extends Goal
    {
		private EntityHarvester harvester;

		public AIChargeAttack(EntityHarvester harvester) {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
			this.harvester = harvester;
		}

		@Override
		public boolean shouldExecute() {
			if (harvester.getAttackTarget() != null && !harvester.getMoveHelper().isUpdating() && harvester.rand.nextInt(4) == 0) {
				return harvester.getDistanceSq(harvester.getAttackTarget()) > 4.0D;
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return harvester.getMoveHelper().isUpdating() && harvester.isCharging() && harvester.getAttackTarget() != null && harvester.getAttackTarget().isAlive();
		}

		@Override
		public void startExecuting() {
			LivingEntity entitylivingbase = harvester.getAttackTarget();
			Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
			harvester.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			harvester.setCharging(true);
			harvester.playSound(HarvestersNight.harvesterCharge, 1.0F, 1.0F);
		}

		@Override
		public void resetTask() {
			harvester.setCharging(false);
		}

		@Override
		public void tick() {
			LivingEntity entitylivingbase = harvester.getAttackTarget();

			if (harvester.getBoundingBox().grow(0.5).intersects(entitylivingbase.getBoundingBox())) {
				harvester.attackEntityAsMob(entitylivingbase);
				harvester.setCharging(false);
			} else {
				double d0 = harvester.getDistanceSq(entitylivingbase);

				if (d0 < 9.0D) {
					Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
					harvester.moveController.setMoveTo(vec3d.x, vec3d.y - 1, vec3d.z, 1.0);
				}
			}
		}
	}
	
	private static class AIClawAttack extends Goal
    {
		private EntityHarvester harvester;
		//Phase 0 = startup, 1 = attack, 2 = ending
		private int time, phase;

		public AIClawAttack(EntityHarvester harvester) {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
			this.harvester = harvester;
			time = 0;
			phase = 0;
		}

		@Override
		public boolean shouldExecute() {
			if (harvester.getAttackTarget() != null && !harvester.getMoveHelper().isUpdating() && harvester.rand.nextInt(5) == 0) {
				return harvester.getDistanceSq(harvester.getAttackTarget()) > 4.0D;
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return time > 0 && harvester.isCasting() && harvester.getAttackTarget() != null && harvester.getAttackTarget().isEntityAlive();
		}

		@Override
		public void startExecuting() {
			harvester.setCasting(true);
			harvester.playSound(HarvestersNight.harvesterSpell, 1.0F, 1.0F);
			time = 10;
			phase = 0;
		}

		@Override
		public void resetTask() {
			harvester.setCasting(false);
			time = 0;
			phase = 0;
		}

		@Override
		public void tick() {
			LivingEntity target = harvester.getAttackTarget();
			time--;
			//Attack
			if (phase == 1 && time % 10 == 0) {
				if (target != null && target.isAlive()) {
					double yMin = target.onGround ? target.posY - 1 : target.posY - 3;
		            float f = (float)MathHelper.atan2(target.posZ - harvester.posZ, target.posX - harvester.posX);
					spawnFangs(target.posX, target.posZ, yMin, target.posY + 1, f, 0);
				}
			}
			//Change phase
			if (time <= 0 && phase < 3) {
				if (phase == 0) time = 60 + harvester.rand.nextInt(5)*10;
				else if (phase == 1) time = 30;
				phase++;
			}
			harvester.getLookController().setLookPositionWithEntity(target, 10, 10);
		}
		
		//Adapted from the Evoker
		private void spawnFangs(double x, double z, double yMin, double yStart, float yaw, int delayTick) {
            BlockPos blockpos = new BlockPos(x, yStart, z);
            boolean flag = false;
            double d0 = 0.0D;

            while (true)
            {
            	//TODO: dive back in Evoker code to fix
                if (!harvester.world.isBlockNormalCube(blockpos, true) && harvester.world.isBlockNormalCube(blockpos.down(), true))
                {
                    if (!harvester.world.isAirBlock(blockpos))
                    {
                        BlockState iblockstate = harvester.world.getBlockState(blockpos);
                        AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(harvester.world, blockpos);

                        if (axisalignedbb != null)
                        {
                            d0 = axisalignedbb.maxY;
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.down();

                if (blockpos.getY() < MathHelper.floor(yMin) - 1)
                {
                    break;
                }
            }

            if (flag)
            {
                EvokerFangsEntity entityevokerfangs = new EvokerFangsEntity(harvester.world, x, (double)blockpos.getY() + d0, z, yaw, delayTick, harvester);
                harvester.world.addEntity(entityevokerfangs);
            }
        }
	}

}
