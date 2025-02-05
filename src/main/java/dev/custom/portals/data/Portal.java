package dev.custom.portals.data;

import java.util.List;

import dev.custom.portals.config.CPSettings;
import net.minecraft.block.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Portal {

    public float offsetX;
    public float offsetZ;

    private final String frameId;
    private final String dimensionId;
    private final MapColor color;

    private BlockPos spawnPos;
    private List<BlockPos> portalBlocks;
    private int hasteRunes;
    private int gateRunes;
    private int weakEnhancerRunes;
    private int strongEnhancerRunes;
    private int infinityRunes;

    private boolean hasRedstoneSignal = false;
    
    private Portal linked;

    public Portal(final String frameId, final String dimensionId, final MapColor color, final BlockPos spawnPos,
            final List<BlockPos> portalBlocks, final float offsetX, final float offsetZ) {
        this.frameId = frameId;
        this.dimensionId = dimensionId;
        this.color = color;
        this.spawnPos = spawnPos;
        this.portalBlocks = portalBlocks;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        hasteRunes = 0;
        gateRunes = 0;
        weakEnhancerRunes = 0;
        strongEnhancerRunes = 0;
        infinityRunes = 0;
    }

    public Portal(final String frameId, final String dimensionId, final MapColor color, final BlockPos spawnPos,
            final List<BlockPos> portalBlocks, final float offsetX, final float offsetZ, int hasteRunes, int gateRunes,
            int weakEnhancerRunes, int strongEnhancerRunes, int infinityRunes) {
        this.frameId = frameId;
        this.dimensionId = dimensionId;
        this.color = color;
        this.spawnPos = spawnPos;
        this.portalBlocks = portalBlocks;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.hasteRunes = hasteRunes;
        this.gateRunes = gateRunes;
        this.weakEnhancerRunes = weakEnhancerRunes;
        this.strongEnhancerRunes = strongEnhancerRunes;
        this.infinityRunes = infinityRunes;
    }

    public String getFrameId() {
        return frameId;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public MapColor getColor() {
        return color;
    }

    public Portal getLinked() {
        return linked;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public List<BlockPos> getPortalBlocks() {
        return portalBlocks;
    }

    public boolean hasLinked() {
        return linked != null;
    }

    public boolean isInterdimensional() {
        return hasLinked() && !dimensionId.equals(linked.getDimensionId());
    }

    public boolean hasRedstoneSignal() {
        return hasRedstoneSignal;
    }

    public void setHasRedstoneSignal(boolean hasRedstoneSignal) {
        this.hasRedstoneSignal = hasRedstoneSignal;
    }

    public void setLinked(final Portal portal) {
        linked = portal;
    }

    public void setSpawnPos(BlockPos newSpawn) {
        spawnPos = newSpawn;
        offsetX = 0.5f;
        offsetZ = 0.5f;
    }

    public int getHasteRunes() { return hasteRunes; }
    public int getGateRunes() { return gateRunes; }
    public int getWeakEnhancerRunes() { return weakEnhancerRunes; }
    public int getStrongEnhancerRunes() { return strongEnhancerRunes; }
    public int getInfinityRunes() { return infinityRunes; }

    public void addHaste() { hasteRunes++; }
    public void addGate() { gateRunes++; }
    public void addWeakEnhancer() { weakEnhancerRunes++; }
    public void addStrongEnhancer() { strongEnhancerRunes++; }
    public void addInfinity() { infinityRunes++; }

    public void removeHaste() { hasteRunes--; }
    public void removeGate() { gateRunes--; }
    public void removeWeakEnhancer() { weakEnhancerRunes--; }
    public void removeStrongEnhancer() { strongEnhancerRunes--; }
    public void removeInfinity() { infinityRunes--; }

    public boolean hasHaste() {
        int i = this.hasLinked() ? linked.getHasteRunes() : 0; 
        return (hasteRunes + i) > 0;
    }

    public boolean hasGate() {
        int i = this.hasLinked() ? linked.getGateRunes() : 0; 
        return (gateRunes + i) > 0;
    }

    public int getEnhanceTier() {
        int i = this.hasLinked() ? linked.getInfinityRunes() : 0;
        int j = this.hasLinked() ? linked.getStrongEnhancerRunes() : 0;
        int k = this.hasLinked() ? linked.getWeakEnhancerRunes() : 0; 
        if ((infinityRunes + i) > 0)
            return 3;
        if ((strongEnhancerRunes + j) > 0)
            return 2;
        if ((weakEnhancerRunes + k) > 0)
            return 1;
        return 0;
    }

    public void tryLink(final Portal portal) {
        if (portal.getColor() == color && portal.getFrameId().equals(frameId) && portal != this) {
            if (portal.hasLinked()) {
                if (portal.getLinked() != this)
                    return;
            }
            if (!CPSettings.PortalRuneSettings.portalsAlwaysUnlimited() || !CPSettings.PortalRuneSettings.portalsAlwaysInterdim()) {
                int distance;
                if (!portal.getDimensionId().equals(dimensionId)) {
                    if (!this.hasGate() && !portal.hasGate() && !CPSettings.PortalRuneSettings.portalsAlwaysInterdim())
                        return;
                    if (portal.getDimensionId().equals("minecraft:the_nether")) {
                        int translatedX = portal.getSpawnPos().getX() * 8;
                        int translatedZ = portal.getSpawnPos().getZ() * 8;
                        BlockPos translatedSpawnPos = new BlockPos(new Vec3i(
                                translatedX, portal.getSpawnPos().getY(), translatedZ));
                        distance = distance(spawnPos, translatedSpawnPos);
                    } else if (this.dimensionId.equals("minecraft:the_nether")) {
                        int translatedX = spawnPos.getX() * 8;
                        int translatedZ = spawnPos.getZ() * 8;
                        BlockPos translatedSpawnPos = new BlockPos(new Vec3i(
                                translatedX, spawnPos.getY(), translatedZ));
                        distance = distance(translatedSpawnPos, portal.getSpawnPos());
                    } else distance = distance(spawnPos, portal.getSpawnPos());
                } else distance = distance(spawnPos, portal.getSpawnPos());
                if (!CPSettings.PortalRuneSettings.portalsAlwaysUnlimited()) {
                    int tier = Math.max(portal.getEnhanceTier(), this.getEnhanceTier());
                    switch (tier) {
                        case 0 -> {
                            if (distance > CPSettings.PortalRangeSettings.getDefaultRange())
                                return;
                        }
                        case 1 -> {
                            if (distance > CPSettings.PortalRangeSettings.getRangeWithEnhancer())
                                return;
                        }
                        case 2 -> {
                            if (distance > CPSettings.PortalRangeSettings.getRangeWithStrongEnhancer())
                                return;
                        }
                    }
                }
            }
            linked = portal;
            portal.setLinked(this);
        }
    }

    private int distance(BlockPos pos1, BlockPos pos2) {
        int x1 = pos1.getX();
        int x2 = pos2.getX();
        int y1 = pos1.getY();
        int y2 = pos2.getY();
        int z1 = pos1.getZ();
        int z2 = pos2.getZ();
        double d = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
        return (int)d;
    }
}
