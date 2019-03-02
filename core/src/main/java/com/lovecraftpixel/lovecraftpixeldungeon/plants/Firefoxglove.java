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

package com.lovecraftpixel.lovecraftpixeldungeon.plants;

import com.lovecraftpixel.lovecraftpixeldungeon.Assets;
import com.lovecraftpixel.lovecraftpixeldungeon.Dungeon;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.Actor;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.Char;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.blobs.Blob;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.blobs.Fire;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.hero.Hero;
import com.lovecraftpixel.lovecraftpixeldungeon.actors.mobs.Mob;
import com.lovecraftpixel.lovecraftpixeldungeon.effects.MagicMissile;
import com.lovecraftpixel.lovecraftpixeldungeon.effects.particles.poisonparticles.FirefoxglovePoisonParticle;
import com.lovecraftpixel.lovecraftpixeldungeon.mechanics.Ballistica;
import com.lovecraftpixel.lovecraftpixeldungeon.scenes.GameScene;
import com.lovecraftpixel.lovecraftpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Firefoxglove extends Plant {

	{
		image = 16;
	}

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace fire shots to, for visual effects.
    private HashSet<Integer> visualCells;
    private int direction = 0;

	@Override
	public void activate( Char ch ) {

	    int pos = this.pos;

	    if(ch instanceof Hero){
	        if(!Dungeon.hero.visibleMobs().isEmpty()){
                pos = Random.element(Dungeon.hero.visibleMobs()).pos;
            }
        }

        if(ch instanceof Mob){
            if(Dungeon.hero != null){
                pos = Dungeon.hero.pos;
            }
        }

        final Ballistica shot = new Ballistica( ch.pos, pos, Ballistica.PROJECTILE);
        fx(shot, new Callback() {
            @Override
            public void call() {
                onZap(shot);
            }
        }, ch);
	}

    @Override
    public void activatePosionDangerous(Char attacker, Char defender) {
        final Ballistica shot = new Ballistica( attacker.pos, defender.pos, Ballistica.PROJECTILE);
        fx(shot, new Callback() {
            @Override
            public void call() {
                onZap(shot);
            }
        }, attacker);
    }

    protected void fx(Ballistica bolt, Callback callback, Char ch ) {
        //need to perform flame spread logic here so we can determine what cells to put flames in.
        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        int maxDist = 2 + 2*2;
        int dist = Math.min(bolt.dist, maxDist);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
            if (bolt.sourcePos+PathFinder.CIRCLE8[i] == bolt.path.get(1)){
                direction = i;
                break;
            }
        }

        float strength = maxDist;
        for (int c : bolt.subPath(1, dist)) {
            strength--; //as we start at dist 1, not 0.
            affectedCells.add(c);
            if (strength > 1) {
                spreadFlames(c + PathFinder.CIRCLE8[left(direction)], strength - 1);
                spreadFlames(c + PathFinder.CIRCLE8[direction], strength - 1);
                spreadFlames(c + PathFinder.CIRCLE8[right(direction)], strength - 1);
            } else {
                visualCells.add(c);
            }
        }

        //going to call this one manually
        visualCells.remove(bolt.path.get(dist));

        for (int cell : visualCells){
            //this way we only get the cells at the tip, much better performance.
            ((MagicMissile)ch.sprite.parent.recycle( MagicMissile.class )).reset(
                    MagicMissile.FIRE_CONE,
                    ch.sprite,
                    cell,
                    null
            );
        }
        MagicMissile.boltFromChar( ch.sprite.parent,
                MagicMissile.FIRE_CONE,
                ch.sprite,
                bolt.path.get(dist/2),
                callback );
        Sample.INSTANCE.play( Assets.SND_ZAP );
    }

    //burn... BURNNNNN!.....
    private void spreadFlames(int cell, float strength){
        if (strength >= 0 && (Dungeon.level.passable[cell] || Dungeon.level.flamable[cell])){
            affectedCells.add(cell);
            if (strength >= 1.5f) {
                visualCells.remove(cell);
                spreadFlames(cell + PathFinder.CIRCLE8[left(direction)], strength - 1.5f);
                spreadFlames(cell + PathFinder.CIRCLE8[direction], strength - 1.5f);
                spreadFlames(cell + PathFinder.CIRCLE8[right(direction)], strength - 1.5f);
            } else {
                visualCells.add(cell);
            }
        } else if (!Dungeon.level.passable[cell])
            visualCells.add(cell);
    }

    private int left(int direction){
        return direction == 0 ? 7 : direction-1;
    }

    private int right(int direction){
        return direction == 7 ? 0 : direction+1;
    }

    protected void onZap( Ballistica bolt ) {

        ArrayList<Char> affectedChars = new ArrayList<>();
        for( int cell : affectedCells){

            //ignore caster cell
            if (cell == bolt.sourcePos){
                continue;
            }

            //only ignite cells directly near caster if they are flammable
            if (!Dungeon.level.adjacent(bolt.sourcePos, cell)
                    || Dungeon.level.flamable[cell]){
                GameScene.add( Blob.seed( cell, 1+2, Fire.class ) );
            }

            Char ch = Actor.findChar( cell );
            if (ch != null) {
                affectedChars.add(ch);
            }
        }
    }

	public static class Seed extends Plant.Seed{

		{
			image = ItemSpriteSheet.SEED_FIREFOXGLOVE;

			plantClass = Firefoxglove.class;
			heroDanger = HeroDanger.DANGEROUS;
		}

        @Override
        public Emitter.Factory getPixelParticle() {
            return FirefoxglovePoisonParticle.FACTORY;
        }
		
		@Override
		public int price() {
			return 30 * quantity;
		}
	}
}