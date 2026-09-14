package com.example.neomocreatures.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class FullBrightVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;

    public FullBrightVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        delegate.setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        // we always force the "top" normal (shade = 1.0), regardless of
        // which face it actually is; this doesn't move anything.
        delegate.setNormal(0F, 1F, 0F);
        return this;
    }
}