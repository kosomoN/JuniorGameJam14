package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.game.entities.enemies.Bat;

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
	private Enemy enemy;
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed/Sprites.atlas"));
		Player.load(atlas);
		Bat.load(atlas);
		
		map = new DungeonMap("maps/Test.tmx", batch);
		
		
		player = new Player(64, 64, 0, this);
		enemy = new Bat(player, 1, 10, 100);
		enemy.setPosition(60, 60);
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
		
		enemy.update();
		
		if(player.update(map)) {
			player.kill();
			
			ghosts.add(player);
			player = new Player(64, 64, ticks, this);
			
			for(Player p : ghosts)
				p.respawnAsGhost();
		}
	}

	private void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(player.x, player.y, 0);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		map.render(camera);
		
		batch.begin();
		
		for(Player p : ghosts)
			p.render(batch);
		
		player.render(batch);
		enemy.render(batch);
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		
		unprocessed = 0;
		lastTickTime = System.nanoTime();
	}
	

	public int getCurrentTick() {
		return ticks;
	}
}
