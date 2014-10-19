package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.entities.Entity;
import com.juniorjam.crawler.utils.Utils;

public class Player implements Entity  {
	private static final int HALF_PLAYER_WIDTH = 14, HALF_PLAYER_HEIGHT = 14;
	private static final float DIAGONAL_MOD = (float) Math.sqrt(0.5);

	public static final int KEY_UP = Keys.W, KEY_DOWN = Keys.S, KEY_RIGHT = Keys.D, KEY_LEFT = Keys.A;
	private static AtlasRegion texture;
	
	private Array<InputCommand> inputCommands = new Array<InputCommand>();
	private int nextInputIndex = 0;
	
	private int startingTick, respawnTick;
	private boolean up, down, left, right;
	private float x, y, startingX, startingY;
	private float speed = 2;
	private GameState gs;
	
	private int life = 100;
	
	public boolean isGhost, ghostFinished;
	
	public Player(float x, float y, int currentTick, GameState gs) {
		this.x = x;
		this.y = y;
		this.startingX = x;
		this.startingY = y;
		this.startingTick = currentTick;
		this.gs = gs;

		Gdx.input.setInputProcessor(new PlayerInputListener());
	}
	
	public void kill() {
		Gdx.input.setInputProcessor(null);
		
		isGhost = true;
	}
	
	public void respawnAsGhost() {
		isGhost = true;
		respawnTick = gs.getCurrentTick();
		nextInputIndex = 0;
		
		x = startingX;
		y = startingY;
		
		down = up = right = left = false;
		
		ghostFinished = false;
	}
	
	public boolean update(DungeonMap map) {
		if(ghostFinished)
			return true;
		
		if(isGhost) {
			if(nextInputIndex >= inputCommands.size) {
				ghostFinished = true;
				return true;
			}
				
			
			InputCommand command;
			while((command = inputCommands.get(nextInputIndex)).tick + respawnTick <= gs.getCurrentTick()) {
				nextInputIndex++;
				
				if(nextInputIndex >= inputCommands.size) {
					ghostFinished = true;
					return true;
				}
				
				switch(command.key) {
				case KEY_DOWN:
					down = command.down;
					break;
				case KEY_UP:
					up = command.down;
					break;
				case KEY_LEFT:
					left = command.down;
					break;
				case KEY_RIGHT:
					right = command.down;
					break;
				}
			}
		}
	
		float mod = 1;
		if((up || down) && (right || left)) {
			mod *= DIAGONAL_MOD;
		}
		
		if(up) {
			y += speed * mod;
		} 
		
		if(down) {
			y -= speed * mod;
		}
		
		if(right) {
			x += speed * mod;
		} 
		
		if(left) {
			x -= speed * mod;
		}
		
		//Fix collisions
		int tileX = (int) (x / map.getTileWidth());
		int tileY = (int) (y / map.getTileHeight());
		
		
		if(map.isBlocked((int) ((x + HALF_PLAYER_WIDTH) / map.getTileWidth()), tileY)) {
			x -= (float) (x + HALF_PLAYER_WIDTH) / map.getTileWidth() % 1.0 * map.getTileWidth();
		} else if(map.isBlocked((int) ((x - HALF_PLAYER_WIDTH) / map.getTileWidth()), tileY)) {
			x += (float) map.getTileWidth() - (x - HALF_PLAYER_WIDTH) / map.getTileWidth() % 1.0 * map.getTileWidth();
		}
		
		if(map.isBlocked(tileX, (int) ((y + HALF_PLAYER_HEIGHT) / map.getTileHeight()))) {
			y -= (float) (y + HALF_PLAYER_HEIGHT) / map.getTileHeight() % 1.0 * map.getTileHeight();
		} else if(map.isBlocked(tileX, (int) ((y - HALF_PLAYER_HEIGHT) / map.getTileHeight()))) {
			y += (float)  map.getTileHeight() - (y - HALF_PLAYER_HEIGHT) / map.getTileHeight() % 1.0 * map.getTileHeight();
		}
		
		return life <= 0;
	}
	
	public void render(SpriteBatch batch) {
		
		if(ghostFinished)
			batch.setColor(1, 1, 1, 0.4f);
		else if(isGhost)
			batch.setColor(1, 1, 1, 0.8f);
		
			
		Utils.drawCentered(batch, texture, x, y, texture.getRegionWidth(), texture.getRegionHeight(), 0);
		
		batch.setColor(1, 1, 1, 1);
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Player");
	}
	
	public class PlayerInputListener extends InputAdapter {
		
		@Override
		public boolean keyDown(int keycode) {
			switch(keycode) {
			case KEY_DOWN:
				down = true;
				inputCommands.add(new InputCommand(KEY_DOWN, true, gs.getCurrentTick() - startingTick));
				break;
			case KEY_UP:
				up = true;
				inputCommands.add(new InputCommand(KEY_UP, true, gs.getCurrentTick() - startingTick));
				break;
			case KEY_LEFT:
				left = true;
				inputCommands.add(new InputCommand(KEY_LEFT, true, gs.getCurrentTick() - startingTick));
				break;
			case KEY_RIGHT:
				right = true;
				inputCommands.add(new InputCommand(KEY_RIGHT, true, gs.getCurrentTick() - startingTick));
				break;
			}
			
			if(keycode == Keys.R) {
				life = 0;
			}
			return false;
		}
		
		@Override
		public boolean keyUp(int keycode) {
			switch(keycode) {
			case KEY_DOWN:
				down = false;
				inputCommands.add(new InputCommand(KEY_DOWN, false, gs.getCurrentTick() - startingTick));
				break;
			case KEY_UP:
				up = false;
				inputCommands.add(new InputCommand(KEY_UP, false, gs.getCurrentTick() - startingTick));
				break;
			case KEY_LEFT:
				left = false;
				inputCommands.add(new InputCommand(KEY_LEFT, false, gs.getCurrentTick() - startingTick));
				break;
			case KEY_RIGHT:
				right = false;
				inputCommands.add(new InputCommand(KEY_RIGHT, false, gs.getCurrentTick() - startingTick));
				break;
			}
			return false;
		}
	}
	
	private class InputCommand {

		private int key;
		private boolean down;
		private int tick;
		
		public InputCommand(int key, boolean down, int relativeTick) {
			this.key = key;
			this.down = down;
			this.tick = relativeTick;
		}
	}
	
	public void addLife(float life) {
		this.life += life;
	}

	@Override
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getLife() {
		return life;
	}
	
	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public AtlasRegion getTexture() {
		return texture;
	}
}