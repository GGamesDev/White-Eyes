package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ContainerEntity extends Container, MenuProvider {
    Vec3 position();

    AABB getBoundingBox();

    @Nullable
    ResourceKey<LootTable> getLootTable();

    void setLootTable(@Nullable ResourceKey<LootTable> p_327875_);

    long getLootTableSeed();

    void setLootTableSeed(long p_219925_);

    NonNullList<ItemStack> getItemStacks();

    void clearItemStacks();

    Level level();

    boolean isRemoved();

    @Override
    default boolean isEmpty() {
        return this.isChestVehicleEmpty();
    }

    default void addChestVehicleSaveData(CompoundTag p_219944_, HolderLookup.Provider p_329733_) {
        if (this.getLootTable() != null) {
            p_219944_.putString("LootTable", this.getLootTable().location().toString());
            if (this.getLootTableSeed() != 0L) {
                p_219944_.putLong("LootTableSeed", this.getLootTableSeed());
            }
        } else {
            ContainerHelper.saveAllItems(p_219944_, this.getItemStacks(), p_329733_);
        }
    }

    default void readChestVehicleSaveData(CompoundTag p_219935_, HolderLookup.Provider p_334732_) {
        this.clearItemStacks();
        if (p_219935_.contains("LootTable", 8)) {
            this.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(p_219935_.getString("LootTable"))));
            this.setLootTableSeed(p_219935_.getLong("LootTableSeed"));
        } else {
            ContainerHelper.loadAllItems(p_219935_, this.getItemStacks(), p_334732_);
        }
    }

    default void chestVehicleDestroyed(DamageSource p_219928_, Level p_219929_, Entity p_219930_) {
        if (p_219929_.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(p_219929_, p_219930_, this);
            if (!p_219929_.isClientSide) {
                Entity entity = p_219928_.getDirectEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    PiglinAi.angerNearbyPiglins((Player)entity, true);
                }
            }
        }
    }

    default InteractionResult interactWithContainerVehicle(Player p_270068_) {
        p_270068_.openMenu(this);
        return !p_270068_.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

    default void unpackChestVehicleLootTable(@Nullable Player p_219950_) {
        MinecraftServer minecraftserver = this.level().getServer();
        if (this.getLootTable() != null && minecraftserver != null) {
            LootTable loottable = minecraftserver.reloadableRegistries().getLootTable(this.getLootTable());
            if (p_219950_ != null) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)p_219950_, this.getLootTable());
            }

            this.setLootTable(null);
            LootParams.Builder lootparams$builder = new LootParams.Builder((ServerLevel)this.level()).withParameter(LootContextParams.ORIGIN, this.position());
            // Forge: set the chest to killer_entity for loot context.
            if (this instanceof AbstractMinecartContainer entityContainer)
                lootparams$builder.withParameter(LootContextParams.ATTACKING_ENTITY, entityContainer);
            if (p_219950_ != null) {
                lootparams$builder.withLuck(p_219950_.getLuck()).withParameter(LootContextParams.THIS_ENTITY, p_219950_);
            }

            loottable.fill(this, lootparams$builder.create(LootContextParamSets.CHEST), this.getLootTableSeed());
        }
    }

    default void clearChestVehicleContent() {
        this.unpackChestVehicleLootTable(null);
        this.getItemStacks().clear();
    }

    default boolean isChestVehicleEmpty() {
        for (ItemStack itemstack : this.getItemStacks()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    default ItemStack removeChestVehicleItemNoUpdate(int p_219946_) {
        this.unpackChestVehicleLootTable(null);
        ItemStack itemstack = this.getItemStacks().get(p_219946_);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.getItemStacks().set(p_219946_, ItemStack.EMPTY);
            return itemstack;
        }
    }

    default ItemStack getChestVehicleItem(int p_219948_) {
        this.unpackChestVehicleLootTable(null);
        return this.getItemStacks().get(p_219948_);
    }

    default ItemStack removeChestVehicleItem(int p_219937_, int p_219938_) {
        this.unpackChestVehicleLootTable(null);
        return ContainerHelper.removeItem(this.getItemStacks(), p_219937_, p_219938_);
    }

    default void setChestVehicleItem(int p_219941_, ItemStack p_219942_) {
        this.unpackChestVehicleLootTable(null);
        this.getItemStacks().set(p_219941_, p_219942_);
        p_219942_.limitSize(this.getMaxStackSize(p_219942_));
    }

    default SlotAccess getChestVehicleSlot(final int p_219952_) {
        return p_219952_ >= 0 && p_219952_ < this.getContainerSize() ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return ContainerEntity.this.getChestVehicleItem(p_219952_);
            }

            @Override
            public boolean set(ItemStack p_219964_) {
                ContainerEntity.this.setChestVehicleItem(p_219952_, p_219964_);
                return true;
            }
        } : SlotAccess.NULL;
    }

    default boolean isChestVehicleStillValid(Player p_219955_) {
        return !this.isRemoved() && p_219955_.canInteractWithEntity(this.getBoundingBox(), 4.0);
    }
}
