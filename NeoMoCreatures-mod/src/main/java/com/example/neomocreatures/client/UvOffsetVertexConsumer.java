package com.example.neomocreatures.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Shifts every texture coordinate by a fixed amount. Lets one model part be drawn with any tile of
 * a texture atlas (e.g. the Big Golem's 28 cube textures) instead of baking one part per tile.
 */
public class UvOffsetVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final float uOffset;
    private final float vOffset;

    public UvOffsetVertexConsumer(VertexConsumer delegate, float uOffset, float vOffset) {
        this.delegate = delegate;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        this.delegate.setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        this.delegate.setUv(u + this.uOffset, v + this.vOffset);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        this.delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        this.delegate.setNormal(x, y, z);
        return this;
    }
}