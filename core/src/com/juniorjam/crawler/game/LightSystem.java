package com.juniorjam.crawler.game;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LightSystem {
	private World world;
	public static RayHandler rayHandler;
	
	public LightSystem(DungeonMap map) {	
		world = new World(new Vector2(), true);
		
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0, 0, 0, 0);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(2);
		rayHandler.setShadows(true);
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		
		
		for(int i = 0; i < map.blockedTiles.length; i++) {
			for(int j = 0; j < map.blockedTiles[0].length; j++) {
				if(map.isBlocked(i, j)) {
					bd.position.set(i * 32 + 16, j * 32 + 16);
					Body b = world.createBody(bd);
					PolygonShape groundBox = new PolygonShape();  
					groundBox.setAsBox(16, 16);
					b.createFixture(groundBox, 0.0f); 
				}
			}
		}
	}
	
	public void render(OrthographicCamera camera) {
		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.updateAndRender();
	}
}
