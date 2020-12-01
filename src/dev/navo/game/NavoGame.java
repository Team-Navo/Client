package dev.navo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.navo.game.Screen.InfoScreen;
import dev.navo.game.Screen.LoginScreen;
import dev.navo.game.Screen.PlayScreen;


public class NavoGame extends Game {
	public static final int V_WIDTH = 400; // 화면 가상의 넓이
	public static final int V_HEIGHT = 300; // 화면 가상의 높이
	public static final int PPM = 100; // Pixel Per Miter

	public SpriteBatch batch; // 스프라이트들을 배치하는 곳
	public static NavoGame game;
	public static NavoGame getGame() {
		if(game==null) game=new NavoGame();
		return game;
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new InfoScreen(game)); // 로그인 스크린을 띄워줌
	}

	@Override
	public void render () {
		super.render();
	}

}
