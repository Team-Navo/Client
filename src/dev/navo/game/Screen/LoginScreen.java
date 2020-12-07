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


public class LoginScreen implements Screen {

    private TextField idField; // 아이디 적는 필드
    private TextField pwField; // 패스워드 적는 필드

    private Label title; // 제목 라벨
    private Label idLabel; // ID 라벨
    private Label pwLabel; // PW 라벨

    private TextButton loginBtn; // 로그인 버튼
    private TextButton signUpBtn; // 회원가입 버튼
    private TextButton IdPwFindBtn; // 아이디/패스워드 찾기 버튼

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Viewport viewport; // 화면 뷰포트

    private Client client; // 서버랑 통신하기 위한 클라이언트 소켓 클래스(클라이언트 안에 다 들어 있음

    private Result resultScene; // 결과 창

    public LoginScreen(final NavoGame game){
        this.game = game; // Lig Gdx 게임 클래스 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera()); // 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기

        client = Client.getInstance(); // 서버랑 통신할 클라이언트 가져오기

        Sounds.background.loop(); // 배경음악 반복재생

        initComponent(); // 필드, 라벨, 버튼 초기화

        resultScene = new Result(); // 결과 창 생성

        btnsAddListener(); // 버튼 리스너 초기화

        initActorOnStage(); // 텍스트 필트 및 라벨 스테이지에 초기화

        resultScene.resultOnStage(stage); // 결과 창 스테이지에 올리기
    }

    private void initComponent(){
        //라벨 및 텍스트 초기화 및 생성
        title = new Label( "Navo Ground", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        title.setFontScale(0.8f);
        idField = new TextField("", Util.skin);
        pwField = new TextField("", Util.skin);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        idLabel = new Label("I   D : ", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        pwLabel = new Label("PW : ", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        loginBtn = new TextButton( "LOG IN", Util.skin );
        signUpBtn = new TextButton( "SIGN UP", Util.skin );
        IdPwFindBtn = new TextButton( "Forgot ID/PW", Util.skin );

        // 라벨 및 텍스트 위치 지정
        idField.setBounds(125 , 150 , 150 , 25 );
        pwField.setBounds(125, 120 , 150 , 25 );
        idLabel.setBounds(80 , 150 , 45 , 25 );
        pwLabel.setBounds(80 , 120 , 45 , 25 );
        title.setBounds(0 , 185 , 400 , 25 );
        title.setAlignment(Align.center);
        loginBtn.setBounds(135 , 85 , 130 , 25 );
        signUpBtn.setBounds(135 , 50 , 130 , 25 );
        IdPwFindBtn.setBounds(135 , 15 , 130 , 25 );
    }

    private void initActorOnStage(){ // 컴포넌트 스테이지에 초기화
        stage.addActor(idField);
        stage.addActor(pwField);
        stage.addActor(idLabel);
        stage.addActor(pwLabel);
        stage.addActor(title);
        stage.addActor(loginBtn);
        stage.addActor(signUpBtn);
        stage.addActor(IdPwFindBtn);
    }

    private void btnsAddListener() { // 리스너 초기화 메소드
        loginBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) { // 로그인 버튼 리스너
                try {
                    if( client.login(idField.getText(), pwField.getText()) ){
                        Gdx.graphics.setWindowedMode(NavoGame.V_WIDTH * 2 , NavoGame.V_HEIGHT * 2 );
                        Sounds.success.play(1); // 성공 소리
                        client.setOwner(idField.getText());
                        game.setScreen(new LobbyScreen(game));
                        dispose();
                    } else {
                        resultScene.setResultLabel("로그인 실패!");
                        resultScene.resultShow();
                        Sounds.fail.play(1); // 실패 소리
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signUpBtn.addListener(new ClickListener(){ // 회원가입 화면 버튼 리스너
            public void clicked (InputEvent event, float x, float y) { // 회원가입 버튼 리스너
                Sounds.click.play(1);// 버튼 클릭 효과음
                game.setScreen(new SignUpScreen(game));
                dispose();
            }
        });

        IdPwFindBtn.addListener(new ClickListener(){ // 아이디 패스워드 찾기 화면 버튼 리스너
            public void clicked (InputEvent event, float x, float y) { // 아이디 패스워드 찾기 버튼 리스너
                Sounds.click.play(1); // 버튼 클릭 효과음
                game.setScreen(new IdPwFindScreen(game));
                dispose();
            }
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) { // Lib Gdx Game 클래스에 있는 그리는 메소드
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin(); // 배치에 그림 그리기 전에 시작하고 끝을 명시해줘야 함
        Images.renderBackground(delta, game.batch);
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
