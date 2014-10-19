package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.game.entities.enemies.Bat;
import com.juniorjam.crawler.game.entities.enemies.Rat;

public class GameState extends ScreenAdapter {
	
	private static final float TICK_LENGTH = 1000000000 / 60f;
	
	private float unprocessed;
	private long lastTickTime;
	private int ticks;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private DungeonMap map;
	private Player player;
	private Array<Player> ghosts = new Array<Player>();
	private Array<Enemy> enemies = new Array<Enemy>();
	private Door[] doors = new Door[5];
	private Button[] btns = new Button[3];
	
	// Triggers
	private boolean btn1, btn2, btn3;
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed/Sprites.atlas"));
		Player.load(atlas);
		Bat.load(atlas);
		Rat.load(atlas);
		
		player = new Player(5 * 32, 5 * 32, 0, this);
		map = new DungeonMap(this, "maps/Map.tmx", batch);
		map.spawnEnemies(this);
		
		Button btn = new Button(this, map, false, 15, 13);
		btns[0] = btn;
		
		btn = new Button(this, map, false, 39, 23);
		btns[1] = btn;
		
		btn = new Button(this, map, false, 2, 47);
		btns[2] = btn;
		
		Trigger trig = new Trigger() {
			
			@Override
			public boolean isTriggered() {
				return btns[0].isToggled;
			}
		};
		
		Door door = new Door(trig, 5, 8, true);
		doors[0] = door;
		
		door = new Door(trig, 6, 8, true);
		doors[3] = door;
		
		door = new Door(trig, 4, 8, true);
		doors[4] = door;
		
		door = new Door(new Trigger() {
			
			@Override
			public boolean isTriggered() {
				return btns[1].isToggled;
			}
		}, 7, 32, true);
		doors[1] = door;
		
		door = new Door(new Trigger() {
			
			@Override
			public boolean isTriggered() {
				return btns[2].isToggled;
			}
		}, 51, 60, true);
		doors[2] = door;
	}

	public void addEnemy(Enemy e) {
		enemies.add(e);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		long currTime = System.nanoTime();
        unprocessed += (currTime - lastTickTime) / TICK_LENGTH;
        lastTickTime = currTime;
        while(unprocessed >= 1) {
        	unprocessed--;
        	update();
        	ticks++;
        }
		
		render();
		
		
	}

	private void update() {
		
		for(Player p : ghosts)
			p.update(map);
		
		for(Enemy e : enemies)
			e.update(map);
		
		for(Door d : doors)
			d.update(map);
		
		for(Button b: btns)
			b.update();
		
		if(player.update(map)) {
			player.kill();
			
			ghosts.add(player);
			player = new Player(5 * 32, 5 * 32, ticks, this);
			
			for(Player p : ghosts)
				p.respawnAsGhost();
			
			restart();
		}
		
		if(ghosts.size >= 5)
			Gdx.app.exit();
	}

	private void render() {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(player.getX(), player.getY(), 0);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		map.render(camera);
		
		batch.begin();
		
		for(Player p : ghosts)
			p.render(batch);
		
		for(Enemy e : enemies)
			e.render(batch);
		
		player.render(batch);
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		
		unprocessed = 0;
		lastTickTime = System.nanoTime();
	}

	public void restart() {
		enemies.clear();
		map.spawnEnemies(this);
	}
	
	public Array<Enemy> getEnemies() {
		return enemies;
	}
	
	public Array<Player> getGhosts() {
		return ghosts;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getCurrentTick() {
		return ticks;
	}
}
