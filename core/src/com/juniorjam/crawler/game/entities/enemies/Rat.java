package com.juniorjam.crawler.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.juniorjam.crawler.game.DungeonMap;
import com.juniorjam.crawler.game.GameState;
import com.juniorjam.crawler.game.Player;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.utils.Utils;

public class Rat extends Enemy {

	private static float HALF_WIDTH = 32, HALF_HEIGHT = 32;
	private static AtlasRegion texture;
	private GameState gs;
	
	public Rat(GameState gs, float x, float y) {
		this.gs = gs;
		this.x = x;
		this.y = y;
		this.attack = 25;
		this.life = 2;
		sightRange = 150;
		
		speed = 1;
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Mouse");
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Utils.drawCentered(batch, texture, x, y, 32, 32, (float) Math.toDegrees(direction) + 90);
	}

	@Override
	public boolean update(DungeonMap map) {
		updateMovement(map);
		
		return life <= 0;
	}
	
	public void updateAttack(Player player) {
		instantDelay--;
		if(instantDelay <= 0) {
			if(firstAttack) {
				player.addLife(-attack);
				firstAttack = false;
				instantDelay = 0;
			} else {
				if(-instantDelay > attackSpeed) {
					player.addLife(-attack);
					instantDelay = 0;
				}
			}
		}
	}
	
	public void updateMovement(DungeonMap map) {
		// Current closest player
		Player player = gs.getPlayer();
		float deltaX = player.getX() - x;
		float deltaY = player.getY() - y;
		for(Player p : gs.getGhosts()) {
			if(!p.ghostFinished) {
				float tempX = p.getX() - x;
				float tempY = p.getY() - y;
				
				if(Utils.getDistSqrd(tempX, tempY) < Utils.getDistSqrd(deltaX, deltaY)) {
					deltaX = tempX;
					deltaY = tempY;
					player = p;
				}
			}
		}
		
		float distanceSqrd = Utils.getDistSqrd(deltaX, deltaY);
		
		// If player is in range
		// 16 is the radius of the player and enemy
		if(distanceSqrd <= (sightRange + 32) * (sightRange + 32)) {
			direction = (float) Math.atan2(deltaY, deltaX);
			
			// Delta movement
			dx = (float) (Math.cos(direction) * speed);
			dy = (float) (Math.sin(direction) * speed);
			
			// Attack if close to enemy
			if(distanceSqrd <= 32 * 32) {
				dx = dy = 0;
				updateAttack(player);
			} else {
				firstAttack = true;
			}
		} else {
			dx = dy = 0;
		}
		
		// Applying movement
		x += dx;
		y += dy;
		
		//Fix collisions
		int tileX = (int) (x / map.getTileWidth());
		int tileY = (int) (y / map.getTileHeight());
		
		if(map.isBlocked((int) ((x + HALF_WIDTH) / map.getTileWidth()), tileY)) {
			x -= (float) (x + HALF_WIDTH) / map.getTileWidth() % 1.0 * map.getTileWidth();
		} else if(map.isBlocked((int) ((x - HALF_WIDTH) / map.getTileWidth()), tileY)) {
			x += (float) map.getTileWidth() - (x - HALF_WIDTH) / map.getTileWidth() % 1.0 * map.getTileWidth();
		}
		
		if(map.isBlocked(tileX, (int) ((y + HALF_HEIGHT) / map.getTileHeight()))) {
			y -= (float) (y + HALF_HEIGHT) / map.getTileHeight() % 1.0 * map.getTileHeight();
		} else if(map.isBlocked(tileX, (int) ((y - HALF_HEIGHT) / map.getTileHeight()))) {
			y += (float)  map.getTileHeight() - (y - HALF_HEIGHT) / map.getTileHeight() % 1.0 * map.getTileHeight();
		}
	}

	@Override
	public AtlasRegion getTexture() {
		return texture;
	}
}
