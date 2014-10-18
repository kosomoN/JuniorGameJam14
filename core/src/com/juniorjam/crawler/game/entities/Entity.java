package com.juniorjam.crawler.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public interface Entity {

	public void setPosition(float x, float y);
	public float getX();
	public float getY();
	public AtlasRegion getTexture();
	
}
