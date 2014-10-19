package com.juniorjam.crawler.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.DungeonMap;
import com.juniorjam.crawler.game.Player;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.utils.Utils;

public class Bat extends Enemy {

	private static Array<Player> players = new Array<Player>();
	private static AtlasRegion texture;
	
	public Bat(Array<Player> players, float x, float y) {
		Bat.players = players;
		this.x = x;
		this.y = y;
		this.attack = 15;
		this.life = 1;
		sightRange = 100;
		
		speed = 1.5f;
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Bat");
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Utils.drawCentered(batch, texture, x, y, 32, 32);
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
		float deltaX = 10000;
		float deltaY = 10000;
		Player player = null;
		for(Player p : players) {
			float tempX = p.getX() - x;
			float tempY = p.getY() - y;
			
			if(Utils.getDistSqrd(tempX, tempY) < Utils.getDistSqrd(deltaX, deltaY)) {
				deltaX = tempX;
				deltaY = tempY;
				player = p;
			}
		}
		
		float distanceSqrd = Utils.getDistSqrd(deltaX, deltaY);
		
		// If player is in range
		// 16 is the radius of the player and enemy
		if(distanceSqrd <= (sightRange + 32) * (sightRange + 32)) {
			float direction = (float) Math.atan2(deltaY, deltaX);
			
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
		for(int i = 0; i < 4; i++) {
			float tileX = (float) (x + Player.HIT_DETECTION_OFFSETS[i][0]) / map.getTileWidth();
			float tileY = (float) (y + Player.HIT_DETECTION_OFFSETS[i][1]) / map.getTileHeight();
			
			if(map.isBlocked((int) tileX, (int) tileY)) {
				
				float xOverlap = tileX % 1.0f * map.getTileWidth();
				float yOverlap = tileY % 1.0f * map.getTileHeight();
				
				if(Player.HIT_DETECTION_OFFSETS[i][0] < 0)
					xOverlap = -(map.getTileWidth() - xOverlap);
				
				if(Player.HIT_DETECTION_OFFSETS[i][1] < 0)
					yOverlap = -(map.getTileHeight() - yOverlap);
				
				//Fix player getting stuck in walls
				if(Math.abs(xOverlap) == Math.abs(yOverlap) && yOverlap > 0) {
					x -= xOverlap;
				} else if(Math.abs(xOverlap) < Math.abs(yOverlap))
					x -= xOverlap;
				else
					y -= yOverlap;
			}
		}
			
	}

	@Override
	public AtlasRegion getTexture() {
		return texture;
	}
}
