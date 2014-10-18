package com.juniorjam.crawler.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.juniorjam.crawler.game.DungeonMap;
import com.juniorjam.crawler.game.Player;

public abstract class Enemy implements Entity {

	protected static AtlasRegion texture;
	protected Player player;
	protected float x, y;
	protected float dx, dy;
	protected float life, attack;
	protected float speed;
	protected float sightRange;
	protected float instantDelay = 20;
	protected float attackSpeed = 120;
	
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
	public AtlasRegion getTexture() { return texture; }
	
}
