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

package sct.hexxitgear.mixinsupport.climbing;

import net.minecraftforge.common.util.ForgeDirection;

public class VectorTransformer {
    private ForgeDirection xPos;
    private ForgeDirection yPos;
    private ForgeDirection zPos;

    public ForgeDirection getAxisX() {
        return xPos;
    }

    public ForgeDirection getAxisY() {
        return yPos;
    }

    public ForgeDirection getAxisZ() {
        return zPos;
    }

    public VectorTransformer(ForgeDirection floor) {
        yPos = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[floor.ordinal()]];
        zPos = ForgeDirection.SOUTH;
        xPos = ForgeDirection.EAST;

        if (floor == ForgeDirection.UP) {
            xPos = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[xPos.ordinal()]];
        } else if (floor == ForgeDirection.EAST)
            xPos = ForgeDirection.UP;
        else if (floor == ForgeDirection.WEST)
            xPos = ForgeDirection.DOWN;
        else if (floor == ForgeDirection.NORTH)
            zPos = ForgeDirection.DOWN;
        else if (floor == ForgeDirection.SOUTH)
            zPos = ForgeDirection.UP;
    }

    public double getX(double x, double y, double z) {
        return xPos.offsetX * x + xPos.offsetY * y + xPos.offsetZ * z;
    }

    public double getY(double x, double y, double z) {
        return yPos.offsetX * x + yPos.offsetY * y + yPos.offsetZ * z;
    }

    public double getZ(double x, double y, double z) {
        return zPos.offsetX * x + zPos.offsetY * y + zPos.offsetZ * z;
    }

    public double unGetX(double x, double y, double z) {
        return xPos.offsetX * x + yPos.offsetX * y + zPos.offsetX * z;
    }

    public double unGetY(double x, double y, double z) {
        return xPos.offsetY * x + yPos.offsetY * y + zPos.offsetY * z;
    }

    public double unGetZ(double x, double y, double z) {
        return xPos.offsetZ * x + yPos.offsetZ * y + zPos.offsetZ * z;
    }
}
