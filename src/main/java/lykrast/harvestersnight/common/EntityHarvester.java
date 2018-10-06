package lykrast.harvestersnight.common;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHarvester extends EntityMob {
	public static final ResourceLocation LOOT = new ResourceLocation(HarvestersNight.MODID, "entities/harvester");
    protected static final DataParameter<Byte> FLAGS = EntityDataManager.<Byte>createKey(EntityHarvester.class, DataSerializers.BYTE);
    private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS);

	public EntityHarvester(World worldIn) {
		super(worldIn);
        setSize(0.7F, 2.4F);
        experienceValue = 50;
        moveHelper = new AIMoveControl(this);
	}
	
	//If you look carefully it is very similar to the Mourner from Defiled Lands
	//Which already had a lot of stuff from Vexes
	@Override
	protected void initEntityAI() {
        tasks.addTask(1, new EntityAISwimming(this));
        //TODO: AI
        //tasks.addTask(3, new AIRangedAttack(this));
        tasks.addTask(4, new AIChargeAttack(this));
        tasks.addTask(8, new AIMoveRandom(this));
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    	targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
        targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
    }

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        //getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
    }

	@Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLAGS, (byte)0);
    }

	@Override
	public void move(MoverType type, double x, double y, double z) {
        super.move(type, x, y, z);
        doBlockCollisions();
    }

	@Override
    public void onUpdate() {
        noClip = true;
        super.onUpdate();
        noClip = false;
        setNoGravity(true);
    }
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		//TODO: fancy particles?
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
    @Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		setEquipmentBasedOnDifficulty(difficulty);
		setEnchantmentBasedOnDifficulty(difficulty);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(HarvestersNight.harvesterScythe));
		setDropChance(EntityEquipmentSlot.MAINHAND, 1);
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}
	
	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		bossInfo.setName(getDisplayName());
	}
	
	@Override
    @Nullable
    protected ResourceLocation getLootTable() {
        //return LOOT;
		return super.getLootTable();
    }

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLOCK_FIRE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITHER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_DEATH;
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
	private static class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(EntityHarvester harvester) {
            super(harvester);
        }

        public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = posX - entity.posX;
                double d1 = posY - entity.posY;
                double d2 = posZ - entity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);

                if (d3 < entity.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    entity.motionX *= 0.5D;
                    entity.motionY *= 0.5D;
                    entity.motionZ *= 0.5D;
                }
                else
                {
                    entity.motionX += d0 / d3 * 0.05D * speed;
                    entity.motionY += d1 / d3 * 0.05D * speed;
                    entity.motionZ += d2 / d3 * 0.05D * speed;

                    if (entity.getAttackTarget() == null)
                    {
                        entity.rotationYaw = -((float)MathHelper.atan2(entity.motionX, entity.motionZ)) * (180F / (float)Math.PI);
                        entity.renderYawOffset = entity.rotationYaw;
                    }
                    else
                    {
                        double d4 = entity.getAttackTarget().posX - entity.posX;
                        double d5 = entity.getAttackTarget().posZ - entity.posZ;
                        entity.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        entity.renderYawOffset = entity.rotationYaw;
                    }
                }
            }
        }
    }
	
	private static class AIMoveRandom extends EntityAIBase
    {
		private EntityHarvester harvester;

		public AIMoveRandom(EntityHarvester mourner) {
			this.setMutexBits(1);
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
		public void updateTask() {
			BlockPos blockpos;
			if (harvester.getAttackTarget() != null && harvester.getAttackTarget().isEntityAlive()) blockpos = new BlockPos(harvester.getAttackTarget());
			else blockpos = new BlockPos(harvester);
			for (int i = 0; i < 3; ++i)
			{
				BlockPos blockpos1 = blockpos.add(harvester.rand.nextInt(15) - 7, harvester.rand.nextInt(11) - 5, harvester.rand.nextInt(15) - 7);

				if (harvester.world.isAirBlock(blockpos1)) {
					harvester.moveHelper.setMoveTo(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 0.25);

					if (harvester.getAttackTarget() == null) {
						harvester.getLookHelper().setLookPosition(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}

					break;
				}
			}
		}
    }
	
	private static class AIChargeAttack extends EntityAIBase
    {
		private EntityHarvester harvester;

		public AIChargeAttack(EntityHarvester harvester) {
			setMutexBits(1);
			this.harvester = harvester;
		}

		@Override
		public boolean shouldExecute() {
			if (harvester.getAttackTarget() != null && !harvester.getMoveHelper().isUpdating() && harvester.rand.nextInt(7) == 0) {
				return harvester.getDistanceSq(harvester.getAttackTarget()) > 4.0D;
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return harvester.getMoveHelper().isUpdating() && harvester.isCharging() && harvester.getAttackTarget() != null && harvester.getAttackTarget().isEntityAlive();
		}

		@Override
		public void startExecuting() {
			EntityLivingBase entitylivingbase = harvester.getAttackTarget();
			Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
			harvester.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			harvester.setCharging(true);
			harvester.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		@Override
		public void resetTask() {
			harvester.setCharging(false);
		}

		@Override
		public void updateTask() {
			EntityLivingBase entitylivingbase = harvester.getAttackTarget();

			if (harvester.getEntityBoundingBox().grow(0.5).intersects(entitylivingbase.getEntityBoundingBox())) {
				harvester.attackEntityAsMob(entitylivingbase);
				harvester.setCharging(false);
			} else {
				double d0 = harvester.getDistanceSq(entitylivingbase);

				if (d0 < 9.0D) {
					Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
					harvester.moveHelper.setMoveTo(vec3d.x, vec3d.y - 1, vec3d.z, 1.0);
				}
			}
		}
	}

}
