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

package com.lovecraftpixel.lovecraftpixeldungeon.items.potions.alchemy;

import com.lovecraftpixel.lovecraftpixeldungeon.Assets;
import com.lovecraftpixel.lovecraftpixeldungeon.Dungeon;
import com.lovecraftpixel.lovecraftpixeldungeon.LovecraftPixelDungeon;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.blobs.Blob;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.blobs.Miasma;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.diseases.Disease;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.hero.Hero;
import com.lovecraftpixel.lovecraftpixeldungeon.items.potions.Potion;
import com.lovecraftpixel.lovecraftpixeldungeon.messages.Messages;
import com.lovecraftpixel.lovecraftpixeldungeon.plants.Snowhedge;
import com.lovecraftpixel.lovecraftpixeldungeon.scenes.GameScene;
import com.lovecraftpixel.lovecraftpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class PotionOfDisease extends Potion {

	{
		initials = 13;
	}
	
	@Override
	public void apply( Hero hero ) {
		setKnown();
        try {
            Disease d = (Disease) Random.element(Snowhedge.diseases).newInstance();
            d.infect(hero);
            GLog.n(Messages.get(this, "infected"));
        } catch (Exception e){
            LovecraftPixelDungeon.reportException(e);
        }
	}

    @Override
    public void shatter(int cell) {

	    if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash( cell );
            Sample.INSTANCE.play( Assets.SND_SHATTER );
        }

        GameScene.add( Blob.seed( cell, 1000, Miasma.class ) );
    }

    @Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}