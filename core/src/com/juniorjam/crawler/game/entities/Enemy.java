package com.juniorjam.crawler.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.juniorjam.crawler.game.DungeonMap;

public abstract class Enemy implements Entity {

	protected float x, y;
	protected float dx, dy;
	protected float direction;
	protected float life, attack;
	protected float speed;
	protected float sightRange;
	protected float instantDelay = 20;
	protected float attackSpeed = 120;
	protected boolean firstAttack = true;
	protected int ticksSinceHit = 100;
	
	public abstract void render(SpriteBatch batch);
	public abstract boolean update(DungeonMap map);
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
	public float getAttack() { return attack; }
	public float getLif() { return life; }
	public void addLife(float life) { 
		this.life += life; 
		if(life < 0)
			ticksSinceHit = 0;
	}
	public abstract AtlasRegion getTexture();
	
}
