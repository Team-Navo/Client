package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.Client.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Result;
import dev.navo.game.Tools.*;

import java.io.IOException;


public class LoseScreen implements Screen {

    private TextButton lobbyBtn;

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Viewport viewport; // 화면 뷰포트

    private Label title;
    private Label lose;

    Client client;

    public LoseScreen(final NavoGame game){
        this.game = game; // Lig Gdx 게임 클래스 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera()); // 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기

        this.client = Client.getInstance();

        initComponent(); // 필드, 라벨, 버튼 초기화
        btnsAddListener(); // 버튼 리스너 초기화
        initActorOnStage(); // 텍스트 필트 및 라벨 스테이지에 초기화

    }

    private void initComponent(){
        //라벨 및 텍스트 초기화 및 생성
        lobbyBtn = new TextButton( "Main", Util.skin );
        lose = new Label( "You Lose", new Label.LabelStyle(FontGenerator.font32, Color.RED ));

        lose.setAlignment(Align.center);

        // 라벨 및 텍스트 위치 지정
        lobbyBtn.setBounds(100, 50, 200, 32);
        lose.setBounds(100, 200, 200, 40);
    }

    private void initActorOnStage(){ // 컴포넌트 스테이지에 초기화
        stage.addActor(lose);
        stage.addActor(lobbyBtn);
    }

    private void btnsAddListener() { // 리스너 초기화 메소드
        lobbyBtn.addListener(new ClickListener(){ // 아이디 패스워드 찾기 화면 버튼 리스너
            public void clicked (InputEvent event, float x, float y) { // 아이디 패스워드 찾기 버튼 리스너
                Sounds.click.play(1); // 버튼 클릭 효과음
                game.setScreen(new LobbyScreen(game));
                dispose();
            }
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) { // Lib Gdx Game 클래스에 있는 그리는 메소드
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin(); // 배치에 그림 그리기 전에 시작하고 끝을 명시해줘야 함
        game.batch.end(); // 배치의 끝
        stage.draw(); // 스테이지에 올라간 액터들을 그림(텍스트 필드나 라벨)

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}
