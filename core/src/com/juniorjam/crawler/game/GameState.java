package com.juniorjam.crawler.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
	
	private LightSystem lightSystem;
	private Array<Enemy> enemies = new Array<Enemy>();
	private Array<Door> doors = new Array<Door>();
	
	// Triggers
	private boolean keyPickedUp = false;
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed/Sprites.atlas"));
		Player.load(atlas);
		Bat.load(atlas);
		Rat.load(atlas);
		map = new DungeonMap(this, "maps/Start.tmx", batch);
		lightSystem = new LightSystem(map);
		
		player = new Player(1280, 160, 0, this);
		
		
		
		map.spawnEnemies(this);
		
		Door door = new Door(new Trigger() {
			
			@Override
			public boolean isTriggered() {
				return keyPickedUp;
			}
		}, 38, 6);
		
		doors.add(door);
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
		
		for(Iterator<Enemy> it = enemies.iterator(); it.hasNext();) {
			Enemy e = it.next();
			
			if(e.update(map))
				it.remove();
		}
		
		for(Door d : doors)
			d.update(map);
		
		if(player.update(map)) {
			player.kill();
			
			ghosts.add(player);
			player = new Player(1280, 160, ticks, this);
			
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
		
		lightSystem.render(camera);
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
