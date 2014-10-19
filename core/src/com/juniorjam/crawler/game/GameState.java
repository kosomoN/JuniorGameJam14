package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
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
	public Array<Enemy> enemies = new Array<Enemy>();
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed/Sprites.atlas"));
		Player.load(atlas);
		Bat.load(atlas);
		Rat.load(atlas);
		
		player = new Player(1280, 160, 0, this);
		map = new DungeonMap(this, "maps/Start.tmx", batch);
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
	}
	
	@Override
	public void show() {
		super.show();
		
		unprocessed = 0;
		lastTickTime = System.nanoTime();
	}

	public void restart() {
		enemies.clear();
		Array<Player> players = new Array<Player>();
		players.add(player);
		players.addAll(ghosts);
		
		for(int i = 0; i < DungeonMap.enemyLayer.getWidth(); i++) {
			for(int j = 0; j < DungeonMap.enemyLayer.getHeight(); j++) {
				Cell cell = DungeonMap.enemyLayer.getCell(i, j);
				if(cell != null) {
					int enemyID = cell.getTile().getId();
					switch (enemyID) {
					case 0:
						addEnemy(new Bat(players, i * 32 - 16, j * 32 - 16));
						break;
					case 1:
						addEnemy(new Rat(players, i * 32 - 16, j * 32 - 16));
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getCurrentTick() {
		return ticks;
	}
}
