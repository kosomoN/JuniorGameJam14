package com.juniorjam.crawler.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juniorjam.crawler.game.Player;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.utils.Utils;

public class Rat extends Enemy {

	public Rat(Player player, float attack, float life, float range) {
		this.player = player;
		this.attack = attack;
		this.life = life;
		sightRange = range;
		
		speed = 1;
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Bat");
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Utils.drawCentered(batch, texture, x, y, 32, 32);
	}

	@Override
	public boolean update() {
		return life <= 0;
	}

}
