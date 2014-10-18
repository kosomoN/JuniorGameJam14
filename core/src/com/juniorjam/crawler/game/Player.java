package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Player {
	private static final int HALF_PLAYER_WIDTH = 16, HALF_PLAYER_HEIGHT = 16;

	public static final int KEY_UP = Keys.W;
	private static AtlasRegion texture;
	
	private boolean up, down, left, right;
	private float x, y;
	
	
	public Player(float x, float y) {
		this.x = x;
		this.y = y;
		
		Gdx.input.setInputProcessor(new PlayerInputListener());
	}
	
	public void update(float delta, DungeonMap map) {
	
		if(up) {
			y += 0.1;
		}
		
		//Fix collisions
		int tileX = (int) (x / map.getTileWidth());
		int tileY = (int) (y / map.getTileHeight());
		
		if(map.isBlocked(tileX + HALF_PLAYER_WIDTH, tileY)) {
			x -= (float) (tileX + HALF_PLAYER_WIDTH) / map.getTileWidth() % 1.0;
		} else if(map.isBlocked(tileX - HALF_PLAYER_WIDTH, tileY)) {
			x += (float) (tileX - HALF_PLAYER_WIDTH) / map.getTileWidth() % 1.0;
		}
		
		if(map.isBlocked(tileX, tileY + HALF_PLAYER_HEIGHT)) {
			y -= (float) (tileY + HALF_PLAYER_HEIGHT) / map.getTileHeight() % 1.0;
		} else if(map.isBlocked(tileX, tileY - HALF_PLAYER_HEIGHT)) {
			y += (float) (tileY - HALF_PLAYER_HEIGHT) / map.getTileHeight() % 1.0;
		} 
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(texture, x - texture.getRegionWidth() / 2, y - texture.getRegionHeight() / 2);
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Player");
	}
	
	public class PlayerInputListener extends InputAdapter {
		
		@Override
		public boolean keyDown(int keycode) {
			if(keycode == KEY_UP) {
				up = true;
			}
			
			return false;
		}
		
		@Override
		public boolean keyUp(int keycode) {
			if(keycode == KEY_UP) {
				up = false;
			}
			
			return false;
		}
	}
}