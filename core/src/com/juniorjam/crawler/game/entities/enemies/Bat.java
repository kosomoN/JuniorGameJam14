package com.juniorjam.crawler.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juniorjam.crawler.game.Player;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.utils.Utils;

public class Bat extends Enemy {

	private Player player;
	private boolean firstAttack = true;

	public Bat(Player player, float attack, float life, float range) {
		this.player = player;
		this.attack = attack;
		this.life = life;
		sightRange = range;
		
		speed = 0.5f;
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Bat");
	}
	
	public void render(SpriteBatch batch) {
		Utils.drawCentered(batch, texture, x, y, 32, 32);
	}
	
	public boolean update() {
		updateMovement();
		
		return life <= 0;
	}
	
	public void updateAttack() {
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
	
	public void updateMovement() {
		float deltaX = player.getX() - x;
		float deltaY = player.getY() - y;
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
				updateAttack();
			} else {
				firstAttack = true;
			}
		}
		
		// Applying movement
		x += dx;
		y += dy;
			
	}
}
