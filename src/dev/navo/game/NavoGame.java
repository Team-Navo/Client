package dev.navo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.navo.game.Screen.LoginScreen;
import dev.navo.game.Screen.PlayScreen;

public class NavoGame extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 300;
	public static final int PPM = 100;

	public SpriteBatch batch;

	private Texture background;
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new LoginScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

}
