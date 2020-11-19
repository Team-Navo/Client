package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

import java.io.IOException;

public class IdPwFindScreen implements Screen {

    // 사운드 변수들
    private Sound clickbtnSound;
    private Sound failSound;
    private Sound succSound;

    // ID : 이름 생년월일
    private Label idFindLabel; // 아이디 찾기 라벨
    private Label idNameLabel; // 아이디 찾기 - 이름 라벨
    private Label idBirthLabel; // 아이디 찾기 - 생일 라벨

    private TextField idNameField; // 아이디 찾기 - 이름 필드
    private TextField idBirthField; // 아이디 찾기 - 생일 적는 필드

    private TextButton idFindBtn; // 아이디 찾기 버튼

    private Label pwFindLabel; // 비밀번호 찾기 라벨
    private Label pwIdLabel; // 비밀번호 찾기 - 아이디 라벨
    private Label pwNameLabel; // 비밀번호 찾기 - 이름 라벨

    private TextField pwIdField; // 비밀번호 찾기 - 아이디 적는 필드
    private TextField pwNameField; // 비밀번호 찾기 - 이름 적는 필드
    private TextButton pwFindBtn; // 비밀번호 찾기 버튼

    // 결과
    private TextField resultField; // 결과 띄우는 배경 필드. 글자를 못쓰게 하고 배경으로 씀(꼼수)
    private Label resultLabel; // 결과를 띄울 라벨
    private TextButton resultBtn; // 결과 창 확인 버튼

    private TextButton backBtn; // 뒤로가기 버튼

    private Texture background; // 배경

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Skin skin; // 텍스트 필드나 라벨의 기본 스킨으로 넣을 스킨 변수
    private Viewport viewport; // 화면 뷰포트

    private Client client; // 서버랑 통신하기 위한 클라이언트 소켓 클래스(클라이언트 안에 다 들어 있음

    public IdPwFindScreen(final NavoGame game) {
        this.game = game; // Lig Gdx 게임 클래스 초기화
        skin = new Skin(Gdx.files.internal("uiskin.json")); // 컴포넌트 스킨 파일 가져오기
        background = new Texture("data/GameBack.png"); // 배경 이미지 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera()); // 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기
        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt")); //폰트 가져오기

        clickbtnSound = Gdx.audio.newSound(Gdx.files.internal("sound/clickbtn.wav")); // 각종 클릭 사운드 초기화
        failSound = Gdx.audio.newSound(Gdx.files.internal("sound/fail.wav")); // 각종 실패 사운드 초기화
        succSound = Gdx.audio.newSound(Gdx.files.internal("sound/succ.wav")); // 각종 성공 사운드 초기화

        client = Client.getInstance(); // 서버랑 통신할 클라이언트 가져오기

        //라벨 및 텍스트 초기화 및 생성
        idFindLabel = new Label("Find ID", new Label.LabelStyle(f, Color.WHITE));
        idNameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));
        idBirthLabel = new Label("B I R T H", new Label.LabelStyle(f, Color.WHITE));
        idNameField = new TextField("", skin);
        idBirthField = new TextField("", skin);
        idFindBtn = new TextButton("Find ID", skin);
        idFindBtn = new TextButton("Find ID", skin);
        pwFindLabel = new Label("Find PW", new Label.LabelStyle(f, Color.WHITE));
        pwIdLabel = new Label("I       D", new Label.LabelStyle(f, Color.WHITE));
        pwNameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));
        pwIdField = new TextField("", skin);
        pwNameField = new TextField("", skin);
        pwFindBtn = new TextButton("Find PW", skin);
        backBtn = new TextButton("BACK", skin);

        // 라벨 및 텍스트 위치 지정
        backBtn.setBounds(310, 10, 80, 25);
        pwFindBtn.setBounds(250, 100, 80, 25);
        pwFindLabel.setBounds(10, 160, 400, 25);
        pwIdLabel.setBounds(20, 130, 50, 25);
        pwIdField.setBounds(80, 130, 150, 25);
        pwNameLabel.setBounds(20, 100, 50, 25);
        pwNameField.setBounds(80, 100, 150, 25);
        idFindBtn.setBounds(250, 200, 80, 25);
        idFindLabel.setBounds(10, 260, 400, 25);
        idNameLabel.setBounds(20, 230, 50, 25);
        idNameField.setBounds(80, 230, 150, 25);
        idBirthLabel.setBounds(20, 200, 50, 25);
        idBirthField.setBounds(80, 200, 150, 25);

        initResultSection(); // 결과 화면 초기화

        btnsAddListener(); // 버튼 리스너 초기화

        resultClose(); // 결과 화면 초기화 후 닫아 놓기

        initActorOnStage(); // 텍스트 필트 및 라벨 스테이지에 초기화
    }
    private void initResultSection() { // 결과창 띄울거 미리 생성
        resultField = new TextField("", skin);
        resultField.setBounds(40, 30, 320, 240);
        resultField.setDisabled(true);
        resultLabel = new Label("", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        resultLabel.setBounds(40, 150, 320, 25);
        resultLabel.setAlignment(Align.center);
        resultBtn = new TextButton("OK", skin);
        resultBtn.setBounds(160, 50, 80, 25);
        resultClose();
    }
    private void initActorOnStage() { // 컴포넌트 스테이지에 초기화
        stage.addActor(idFindLabel);
        stage.addActor(idNameLabel);
        stage.addActor(idBirthLabel);
        stage.addActor(idNameField);
        stage.addActor(idBirthField);
        stage.addActor(idFindBtn);
        stage.addActor(pwFindLabel);
        stage.addActor(pwIdLabel);
        stage.addActor(pwNameLabel);
        stage.addActor(pwIdField);
        stage.addActor(pwNameField);
        stage.addActor(pwFindBtn);
        stage.addActor(backBtn);
        stage.addActor(resultField);
        stage.addActor(resultLabel);
        stage.addActor(resultBtn);
    }
    private void btnsAddListener() { // 리스너 초기화 메소드
        idFindBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    String result = client.idFind(idNameField.getText(), idBirthField.getText());
                    if(result != null){
                        resultLabel.setText("아이디 : " + result);
                        succSound.play(0.7f); // 아이디 찾기 성공 사운드
                        resultShow();
                    }else{
                        resultLabel.setText("아이디 찾기 실패");
                        failSound.play(0.7f); // 아이디 찾기 실패 사운드
                        resultShow();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                idNameField.setText("");
                idBirthField.setText("");
            }
        });
        pwFindBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    String result = client.pwFind(pwIdField.getText(), pwNameField.getText());
                    if(result != null){
                        resultLabel.setText("패스워드 : " + result);
                        succSound.play(0.7f);
                        resultShow();
                    }else{
                        resultLabel.setText("패스워드 찾기 실패");
                        failSound.play(0.7f);
                        resultShow();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pwIdField.setText("");
                pwNameField.setText("");
            }
        });
        resultBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                resultClose();
            }
        });
        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                clickbtnSound.play(0.7f);
                game.setScreen(new LoginScreen(game));
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
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(background, 0 , 0 );
        game.batch.end();
        stage.draw();
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
