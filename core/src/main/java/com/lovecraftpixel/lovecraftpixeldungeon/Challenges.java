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

package com.lovecraftpixel.lovecraftpixeldungeon;

import com.lovecraftpixel.lovecraftpixeldungeon.items.Dewdrop;
import com.lovecraftpixel.lovecraftpixeldungeon.items.Item;
import com.lovecraftpixel.lovecraftpixeldungeon.items.armor.Armor;
import com.lovecraftpixel.lovecraftpixeldungeon.items.armor.ClassArmor;
import com.lovecraftpixel.lovecraftpixeldungeon.items.armor.ClothArmor;
import com.lovecraftpixel.lovecraftpixeldungeon.items.artifacts.HornOfPlenty;
import com.lovecraftpixel.lovecraftpixeldungeon.items.food.Blandfruit;
import com.lovecraftpixel.lovecraftpixeldungeon.items.food.Food;
import com.lovecraftpixel.lovecraftpixeldungeon.items.food.SmallRation;
import com.lovecraftpixel.lovecraftpixeldungeon.items.potions.PotionOfHealing;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
    public static final int AQUAPHOBIA		    = 128;

	public static final int MAX_VALUE           = 256;

	public static final String[] NAME_IDS = {
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls",
            "aquaphobia"
	};

	public static final int[] MASKS = {
			NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS, AQUAPHOBIA
	};

	public static boolean isItemBlocked( Item item ){
		if (Dungeon.isChallenged(NO_FOOD)){
			if (item instanceof Food && !(item instanceof SmallRation)) {
				return true;
			} else if (item instanceof HornOfPlenty){
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_ARMOR)){
			if (item instanceof Armor && !(item instanceof ClothArmor || item instanceof ClassArmor)) {
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_HEALING)){
			if (item instanceof PotionOfHealing){
				return true;
			} else if (item instanceof Blandfruit
					&& ((Blandfruit) item).potionAttrib instanceof PotionOfHealing){
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_HERBALISM)){
			if (item instanceof Dewdrop) {
				return true;
			}
		}

		return false;

	}

}