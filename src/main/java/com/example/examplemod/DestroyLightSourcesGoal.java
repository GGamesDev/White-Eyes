package com.example.examplemod;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DestroyLightSourcesGoal extends Goal {
    private final Mob mob;
    private final double speed;
    private final int radius;  // Rayon dans lequel l'entité casse des blocs

    public DestroyLightSourcesGoal(Mob mob, double speed, int radius) {
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
    }

    @Override
    public boolean canUse() {
        // Ce goal est activé lorsque l'entité est prête à casser les blocs (toujours disponible ici).
        return true;
    }

    @Override
    public void start() {
        // Lance l'action de casser des blocs autour de l'entité dès que le goal est activé
        destroyBlocksAround();
    }

    @Override
    public void tick() {
        // Vérifie les blocs autour du mob à chaque tick
        destroyBlocksAround();
    }

    private void destroyBlocksAround() {
        Level world = mob.level();
        BlockPos mobPos = mob.blockPosition();

        // On vérifie tous les blocs autour du mob dans le rayon spécifié
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = mobPos.offset(x, y, z);
                    BlockState blockState = world.getBlockState(currentPos);
                    Block block = blockState.getBlock();

                    // Si le bloc est une torche ou une source de lave, on le détruit
                    if (isTorchOrLava(block)) {
                        world.destroyBlock(currentPos, true);  // Détruit le bloc et laisse tomber un item si possible
                    }
                }
            }
        }
    }

    private boolean isTorchOrLava(Block block) {
        // Vérifie si le bloc est une torche ou une source de lave
        return block == Blocks.TORCH || block == Blocks.FIRE || block == Blocks.LAVA || block == Blocks.GLOWSTONE;
    }
}
