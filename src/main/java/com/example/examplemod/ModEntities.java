package com.example.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);
	
    public static final RegistryObject<EntityType<WhiteEyesEntity>> WHITE_EYES =
            ENTITY_TYPES.register("white_eyes", () -> EntityType.Builder.of(WhiteEyesEntity::new, MobCategory.CREATURE)
                    .sized(1.5f, 1.5f).build("white_eyes"));
	
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
