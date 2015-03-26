/*
 * HexxitGear
 * Copyright (C) 2013  Ryan Cohen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sct.hexxitgear.coremod;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class HexxitGearResourcePack implements IResourcePack {

    protected ModContainer ownerContainer;

    public HexxitGearResourcePack(ModContainer pContainer) {
        ownerContainer = pContainer;
    }

    @Override
    public InputStream getInputStream(ResourceLocation par1ResourceLocation) throws IOException {
        InputStream inputstream = getResourceStream(par1ResourceLocation);

        if (inputstream != null) {
            return inputstream;
        } else {
            throw new FileNotFoundException(par1ResourceLocation.getResourcePath());
        }
    }

    private String getResourcePath(ResourceLocation location) {
        return "/assets/hexxitgear/"+location.getResourcePath();
    }

    private InputStream getResourceStream(ResourceLocation par1ResourceLocation) {
        InputStream lis = HexxitGearResourcePack.class.getResourceAsStream(getResourcePath(par1ResourceLocation));
        return lis;
    }

    @Override
    public boolean resourceExists(ResourceLocation par1ResourceLocation) {
        return getResourceStream(par1ResourceLocation) != null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Set getResourceDomains() {
        return ImmutableSet.of("hexxitgear");
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer par1MetadataSerializer, String par2Str) {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return ImageIO.read(DefaultResourcePack.class.getResourceAsStream(getResourcePath(new ResourceLocation("hexxitgear.png"))));
    }

    @Override
    public String getPackName() {
        return "Default";
    }
}
