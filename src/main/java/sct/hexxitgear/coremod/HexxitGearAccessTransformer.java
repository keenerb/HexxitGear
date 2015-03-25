package sct.hexxitgear.coremod;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class HexxitGearAccessTransformer extends AccessTransformer {
    public HexxitGearAccessTransformer() throws IOException {
        super("hexxitgear_at.cfg");
    }
}
