package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundTeleportEntityPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTeleportEntityPacket> STREAM_CODEC = Packet.codec(
        ClientboundTeleportEntityPacket::write, ClientboundTeleportEntityPacket::new
    );
    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final byte yRot;
    private final byte xRot;
    private final boolean onGround;

    public ClientboundTeleportEntityPacket(Entity p_133538_) {
        this.id = p_133538_.getId();
        Vec3 vec3 = p_133538_.trackingPosition();
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.yRot = (byte)((int)(p_133538_.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte)((int)(p_133538_.getXRot() * 256.0F / 360.0F));
        this.onGround = p_133538_.onGround();
    }

    private ClientboundTeleportEntityPacket(FriendlyByteBuf p_179437_) {
        this.id = p_179437_.readVarInt();
        this.x = p_179437_.readDouble();
        this.y = p_179437_.readDouble();
        this.z = p_179437_.readDouble();
        this.yRot = p_179437_.readByte();
        this.xRot = p_179437_.readByte();
        this.onGround = p_179437_.readBoolean();
    }

    private void write(FriendlyByteBuf p_133547_) {
        p_133547_.writeVarInt(this.id);
        p_133547_.writeDouble(this.x);
        p_133547_.writeDouble(this.y);
        p_133547_.writeDouble(this.z);
        p_133547_.writeByte(this.yRot);
        p_133547_.writeByte(this.xRot);
        p_133547_.writeBoolean(this.onGround);
    }

    @Override
    public PacketType<ClientboundTeleportEntityPacket> type() {
        return GamePacketTypes.CLIENTBOUND_TELEPORT_ENTITY;
    }

    public void handle(ClientGamePacketListener p_133544_) {
        p_133544_.handleTeleportEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}