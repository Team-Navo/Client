package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

import java.io.IOException;


public class LoginScreen implements Screen {

    private Texture background; // 배경

    private TextField idField; // 아이디 적는 필드
    private TextField pwField; // 패스워드 적는 필드

    private Label title; // 제목 라벨
    private Label idLabel; // ID 라벨
    private Label pwLabel; // PW 라벨

    private TextButton loginBtn; // 로그인 버튼
    private TextButton signUpBtn; // 회원가입 버튼
    private TextButton IdPwFindBtn; // 아이디/패스워드 찾기 버튼

    // 결과
    private TextField resultField; // 결과 띄우는 배경 필드. 글자를 못쓰게 하고 배경으로 씀(꼼수)
    private Label resultLabel; // 결과를 띄울 라벨
    private TextButton resultBtn; // 결과 창 확인 버튼

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Skin skin; // 텍스트 필드나 라벨의 기본 스킨으로 넣을 스킨 변수
    private Viewport viewport; // 화면 뷰포트

    private Client client; // 서버랑 통신하기 위한 클라이언트 소켓 클래스(클라이언트 안에 다 들어 있음

    public LoginScreen(final NavoGame game){
        this.game = game; // Lig Gdx 게임 클래스 초기화
        skin = new Skin(Gdx.files.internal("uiskin.json")); // 컴포넌트 스킨 파일 가져오기
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera()); // 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기
        background = new Texture("data/GameBack.png"); // 배경 이미지 초기화
        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt")); //폰트 가져오기

        client = Client.getInstance(); // 서버랑 통신할 클라이언트 가져오기


        //라벨 및 텍스트 초기화 및 생성
        title = new Label( "Navo Ground", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        title.setFontScale(0.8f);
        idField = new TextField("", skin);
        pwField = new TextField("", skin);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        idLabel = new Label("I   D : ", new Label.LabelStyle(f, Color.WHITE));
        pwLabel = new Label("PW : ", new Label.LabelStyle(f, Color.WHITE));
        loginBtn = new TextButton( "LOG IN", skin );
        signUpBtn = new TextButton( "SIGN UP", skin );
        IdPwFindBtn = new TextButton( "Forgot ID/PW", skin );

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

        initResultSection(); // 결과 화면 초기화

        btnsAddListener(); // 버튼 리스너 초기화

        resultClose(); // 결과 화면 초기화 후 닫아 놓기

        initActorOnStage(); // 텍스트 필트 및 라벨 스테이지에 초기화
    }

    private void initResultSection(){ // 결과창 띄울거 미리 생성
        resultField = new TextField("", skin);
        resultField.setBounds(40, 30, 320, 240);
        resultField.setDisabled(true);
        resultLabel = new Label("", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        resultLabel.setBounds(40, 150, 320, 25);
        resultLabel.setAlignment(Align.center);
        resultBtn = new TextButton( "OK", skin );
        resultBtn.setBounds(160, 50, 80, 25);
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

        stage.addActor(resultField);
        stage.addActor(resultLabel);
        stage.addActor(resultBtn);
    }

    private void btnsAddListener() { // 리스너 초기화 메소드
        loginBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) { // 로그인 버튼 리스너
                try {
                    if( client.login(idField.getText(), pwField.getText()) ){
                        Gdx.graphics.setWindowedMode(800 , 600 );
                        game.setScreen(new LobbyScreen(game));
                        client.setOwner(idField.getText());
                    }else{
                        resultLabel.setText("로그인 실패!");
                        resultShow();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        signUpBtn.addListener(new ClickListener(){ // 회원가입 화면 버튼 리스너
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new SignUpScreen(game));
            }
        });

        IdPwFindBtn.addListener(new ClickListener(){ // 아이디 패스워드 찾기 화면 버튼 리스너
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new IdPwFindScreen(game));
            }
        });
        resultBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) { // 결과 창 닫기 버튼 리스너
                resultClose();
            }
        });
    }

    private void resultShow(){ // 결과 창 띄우기
        resultLabel.setVisible(true);
        resultField.setVisible(true);
        resultBtn.setVisible(true);
    }

    private void resultClose(){ // 결과 창 닫기
        resultLabel.setVisible(false);
        resultField.setVisible(false);
        resultBtn.setVisible(false);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) { // Lib Gdx Game 클래스에 있는 그리는 메소드
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin(); // 배치에 그림 그리기 전에 시작하고 끝을 명시해줘야 함
        game.batch.draw(background, 0 , 0 ); // 배치에다가 배경을 그림
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
