package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GameTestAddMarkerDebugPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, GameTestAddMarkerDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
        GameTestAddMarkerDebugPayload::write, GameTestAddMarkerDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_test_add_marker");

    private GameTestAddMarkerDebugPayload(FriendlyByteBuf p_300441_) {
        this(p_300441_.readBlockPos(), p_300441_.readInt(), p_300441_.readUtf(), p_300441_.readInt());
    }

    private void write(FriendlyByteBuf p_300444_) {
        p_300444_.writeBlockPos(this.pos);
        p_300444_.writeInt(this.color);
        p_300444_.writeUtf(this.text);
        p_300444_.writeInt(this.durationMs);
    }

    @Override
    public CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> type() {
        return TYPE;
    }
}