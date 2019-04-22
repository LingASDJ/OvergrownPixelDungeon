/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Lovecraft Pixel Dungeon
 * Copyright (C) 2016-2019 Anon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without eben the implied warranty of
 * GNU General Public License for more details.
 *
 * You should have have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

package com.lovecraftpixel.lovecraftpixeldungeon.items.potions.exotic.alchemy;

import com.lovecraftpixel.lovecraftpixeldungeon.Dungeon;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.buffs.Buff;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.buffs.Glowing;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.buffs.MindVision;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.hero.Hero;
import com.lovecraftpixel.lovecraftpixeldungeon.items.potions.exotic.ExoticPotion;
import com.lovecraftpixel.lovecraftpixeldungeon.messages.Messages;
import com.lovecraftpixel.lovecraftpixeldungeon.utils.GLog;

public class PotionOfInnerGlow extends ExoticPotion {
	
	{
		initials = 19;
	}

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect( hero, MindVision.class, MindVision.DURATION*2 );
        Buff.affect(hero, Glowing.class).reignite(hero);
        Dungeon.observe();

        if (Dungeon.level.mobs.size() > 0) {
            GLog.i( Messages.get(MindVision.class, "see_mobs") );
        } else {
            GLog.i( Messages.get(MindVision.class, "see_none") );
        }
    }
}
