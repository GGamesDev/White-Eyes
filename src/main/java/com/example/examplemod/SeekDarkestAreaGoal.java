package com.example.examplemod;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SeekDarkestAreaGoal extends Goal {
    private final Mob mob;
    private Vec3 targetPosition;
    private final double speed;

    public SeekDarkestAreaGoal(Mob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.targetPosition = null;
    }

    @Override
    public boolean canUse() {
        // Ce goal sera activé si le mob est dans une zone lumineuse et cherche un endroit sombre.
        return this.mob.level().getBrightness(LightLayer.BLOCK, this.mob.blockPosition()) > 0.5;  // Si la luminosité est trop haute
    }

    @Override
    public void start() {
        // Trouver la zone la plus sombre proche
        this.targetPosition = findDarkestArea();
        if (this.targetPosition != null) {
            PathNavigation navigation = mob.getNavigation();
            navigation.moveTo(targetPosition.x, targetPosition.y, targetPosition.z, this.speed);
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Le mob continue à chercher la zone sombre tant qu'il ne l'a pas trouvée ou si la luminosité change
        return this.targetPosition != null && this.mob.level().getBrightness(LightLayer.BLOCK, this.mob.blockPosition()) > 0.5;
    }

    @Override
    public void tick() {
        // Le mob se déplace vers la cible (zone sombre)
        if (this.targetPosition != null) {
            mob.getNavigation().moveTo(targetPosition.x, targetPosition.y, targetPosition.z, this.speed);
        }
    }

    private Vec3 findDarkestArea() {
        // Cette méthode recherche les blocs environnants pour trouver l'endroit le plus sombre.
        Level world = mob.level();
        double darkestLightLevel = 15.0;  // La luminosité maximale est de 15, donc on cherche à avoir une valeur la plus basse
        Vec3 bestPosition = null;

        for (int x = -5; x < 5; x++) {
            for (int z = -5; z < 5; z++) {
                for (int y = 0; y < 5; y++) {
                    int blockX = mob.getBlockX() + x;
                    int blockY = mob.getBlockY() + y;
                    int blockZ = mob.getBlockZ() + z;

                    // Crée un BlockPos pour les coordonnées
                    BlockPos pos = new BlockPos(blockX, blockY, blockZ);

                    // Vérifie la luminosité du bloc à la position (pos)
                    double lightLevel = world.getBrightness(LightLayer.BLOCK, pos);
                    if (lightLevel < darkestLightLevel) {
                        darkestLightLevel = lightLevel;
                        bestPosition = new Vec3(blockX, blockY, blockZ);
                    }
                }
            }
        }

        return bestPosition;
    }
}
