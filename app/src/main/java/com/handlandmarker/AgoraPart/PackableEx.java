package com.handlandmarker.AgoraPart;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
