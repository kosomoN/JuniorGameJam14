package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juniorjam.crawler.game.entities.Enemy;
import com.juniorjam.crawler.game.entities.enemies.Bat;

public class GameState extends ScreenAdapter {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private DungeonMap map;
	private Player player;
	private Enemy enemy;
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed/Sprites.atlas"));
		Player.load(atlas);
		Bat.load(atlas);
		
		map = new DungeonMap("maps/Test.tmx", batch);
		
		
		player = new Player(0, 0);
		enemy = new Bat(player, 1, 10, 100);
		enemy.setPosition(60, 60);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		
		player.update(delta, map);
		enemy.update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		map.render(camera);
		
		batch.begin();
		player.render(batch);
		enemy.render(batch);
		batch.end();
	}
}
