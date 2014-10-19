package com.juniorjam.crawler.game;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.game.entities.Entity;
import com.juniorjam.crawler.utils.Utils;

public class Player implements Entity  {
	private static final int HALF_PLAYER_WIDTH = 14, HALF_PLAYER_HEIGHT = 14;
	private static final float DIAGONAL_MOD = (float) Math.sqrt(0.5);

	public static final int KEY_UP = Keys.W, KEY_DOWN = Keys.S, KEY_RIGHT = Keys.D, KEY_LEFT = Keys.A;
	public static final int HIT_UP = Keys.UP, HIT_DOWN = Keys.DOWN, HIT_RIGHT = Keys.RIGHT, HIT_LEFT = Keys.LEFT;
	
	private static AtlasRegion texture, slashTexture;
	public static int[][] HIT_DETECTION_OFFSETS = { { HALF_PLAYER_WIDTH, HALF_PLAYER_HEIGHT }, { -HALF_PLAYER_WIDTH, HALF_PLAYER_HEIGHT }, { -HALF_PLAYER_WIDTH, -HALF_PLAYER_HEIGHT }, { HALF_PLAYER_WIDTH, -HALF_PLAYER_HEIGHT} };
	
	private Array<InputCommand> inputCommands = new Array<InputCommand>();
	private int nextInputIndex = 0;
	
	private float direction = 0;
	private int ticksSinceSlash = 0;
	private int startingTick, respawnTick, deathTick;
	private boolean up, down, left, right, hitUp, hitDown, hitLeft, hitRight;
	public float x, y, startingX, startingY;
	private float speed = 1.2f;
	private GameState gs;
	
	private int life = 100;
	
	public boolean isGhost, ghostFinished;
	public int slashSpeed = 20;
	
	public PointLight light;
	
	public Player(float x, float y, int currentTick, GameState gs) {
		this.x = x;
		this.y = y;
		this.startingX = x;
		this.startingY = y;
		this.startingTick = currentTick;
		this.gs = gs;

		Gdx.input.setInputProcessor(new PlayerInputListener());
		
		light = new PointLight(LightSystem.rayHandler, 32, new Color(1,1,1,1), 256, 1000, 0);
	}
	
	public void kill() {
		Gdx.input.setInputProcessor(null);
		
		deathTick = gs.getCurrentTick() - startingTick;
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
		if(isGhost && gs.getCurrentTick() - respawnTick >= deathTick) {
			ghostFinished = true;
			System.out.println("Done");
			return true;
		}
		
		if(isGhost && nextInputIndex < inputCommands.size) {
			
			InputCommand command;
			while((command = inputCommands.get(nextInputIndex)).tick + respawnTick <= gs.getCurrentTick()) {
				
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
					
				case HIT_DOWN:
					hitDown = command.down;
					break;
				case HIT_UP:
					hitUp = command.down;
					break;
				case HIT_LEFT:
					hitLeft = command.down;
					break;
				case HIT_RIGHT:
					hitRight = command.down;
					break;
				}
				
				nextInputIndex++;
				
				if(nextInputIndex >= inputCommands.size) {
					break;
				}
				
			}
		}
		
		light.setPosition(x, y);
		
		if(hitDown || hitUp || hitLeft || hitRight) {

			ticksSinceSlash = 0;
			float xMin = 0, xMax = 0;
			float yMin = 0, yMax = 0;
			
			if(hitRight) {
				direction = 270;
				xMin = x;
				xMax = x + 39;
				
				yMin = y - 17;
				yMax = y + 17;
			}
			
			if(hitLeft) {
				direction = 90;
				xMin = x - 39;
				xMax = x;
				
				yMin = y - 17;
				yMax = y + 17;
			}
			
			if(hitUp) {
				direction = 0;
				xMin = x - 17;
				xMax = x + 17;
				
				yMin = y;
				yMax = y + 39;
				
			}
			
			if(hitDown) {
				direction = 180;
				xMin = x - 17;
				xMax = x + 17;
				
				yMin = y - 39;
				yMax = y;
			}
			
			hitDown = hitUp = hitLeft = hitRight = false;
			
			for(Enemy enemy : gs.getEnemies()) {
				if(enemy.getX() + 14 > xMin && enemy.getX() - 14 < xMax && enemy.getY() + 14 > yMin && enemy.getY() - 14 < yMax) {
					enemy.addLife(-1);
				}
			}
		}
		
		ticksSinceSlash++;
	
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
		for(int i = 0; i < 4; i++) {
			float tileX = (float) (x + HIT_DETECTION_OFFSETS[i][0]) / map.getTileWidth();
			float tileY = (float) (y + HIT_DETECTION_OFFSETS[i][1]) / map.getTileHeight();
			
			if(map.isBlocked((int) tileX, (int) tileY)) {
				
				float xOverlap = tileX % 1.0f * map.getTileWidth();
				float yOverlap = tileY % 1.0f * map.getTileHeight();
				
				if(HIT_DETECTION_OFFSETS[i][0] < 0)
					xOverlap = -(map.getTileWidth() - xOverlap);
				
				if(HIT_DETECTION_OFFSETS[i][1] < 0)
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
		
		return life <= 0;
	}
	
	public void render(SpriteBatch batch) {
		
		if(ghostFinished)
			batch.setColor(1, 1, 1, 0.4f);
		else if(isGhost)
			batch.setColor(1, 1, 1, 0.8f);
		
		if(ticksSinceSlash > 12) {
			if(up && !down) {
				direction = 0;
				if(left && !right)
					direction = 45;
				else if(right && !left)
					direction = 315;
			} else if(down && !up) {
				direction = 180;
				if(left && !right)
					direction = 135;
				else if(right && !left)
					direction = 225;
			} else if(left && !right)
				direction = 90;
			else if(right && !left)
				direction = 270;
		} else {
			batch.draw(slashTexture, x - 17, y + 7, 17, -7, texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, direction);
		}
		
		
			
		Utils.drawCentered(batch, texture, x, y, texture.getRegionWidth(), texture.getRegionHeight(), direction);
		
		batch.setColor(1, 1, 1, 1);
	}
	
	public static void load(TextureAtlas atlas) {
		texture = atlas.findRegion("Character");
		slashTexture = atlas.findRegion("Slash");
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
				
				
				
			case HIT_DOWN:
				if(ticksSinceSlash >= slashSpeed) {
					hitDown = true;
					inputCommands.add(new InputCommand(HIT_DOWN, true, gs.getCurrentTick() - startingTick));
				}
				break;
			case HIT_UP:
				if(ticksSinceSlash >= slashSpeed) {
					hitUp = true;
					inputCommands.add(new InputCommand(HIT_UP, true, gs.getCurrentTick() - startingTick));
				}
				break;
			case HIT_RIGHT:
				if(ticksSinceSlash >= slashSpeed) {
					hitRight = true;
					inputCommands.add(new InputCommand(HIT_RIGHT, true, gs.getCurrentTick() - startingTick));
				}
				break;
			case HIT_LEFT:
				if(ticksSinceSlash >= slashSpeed) {
					hitLeft = true;
					inputCommands.add(new InputCommand(HIT_LEFT, true, gs.getCurrentTick() - startingTick));
				}
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