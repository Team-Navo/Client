package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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

public class SignUpScreen implements Screen {

    // 사운드 변수들
    private Sound clickbtnSound;
    private Sound failSound;
    private Sound succSound;

    private Texture background; // 배경

    private TextField idField; // 아이디 적는 필드
    private TextField pwField; // 비밀번호 적는 필드
    private TextField nameField; // 이름 적는 필드
    private TextField birthField; // 생일 적는 필드
    private TextField phoneField; // 전화번호 적는 필드

    private Label singUpLabel; // 회원가입 라벨
    private Label idLabel; // 아이디 라벨
    private Label pwLabel; // 비밀번호 라벨
    private Label nameLabel; // 이름 라벨
    private Label birthLabel; // 생일 라벨
    private Label phoneLabel; // 전화번호 라벨

    private TextButton submitBtn; // 제출 버튼
    private TextButton backBtn; // 뒤로가기 버튼


    // 결과
    private TextField resultField; // 결과 띄우는 배경 필드. 글자를 못쓰게 하고 배경으로 씀(꼼수)
    private Label resultLabel; // 결과를 띄울 라벨
    private TextButton resultBtn; // 결과 창 확인 버튼

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Skin skin; // 텍스트 필드나 라벨의 기본 스킨으로 넣을 스킨 변수
    private Viewport viewport; // 화면 뷰포트

    private Client client; // 서버랑 통신하기 위한 클라이언트 소켓 클래스(클라이언트 안에 다 들어 있음)

    public SignUpScreen(final NavoGame game){
        this.game = game; // Lig Gdx 게임 클래스 초기화
        skin = new Skin(Gdx.files.internal("uiskin.json")); // 컴포넌트 스킨 파일 가져오기
        background = new Texture("data/GameBack.png"); // 배경 이미지 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());// 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기
        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt")); //폰트 가져오기

        client = Client.getInstance(); // 서버랑 통신할 클라이언트 가져오기

        clickbtnSound = Gdx.audio.newSound(Gdx.files.internal("sound/clickbtn.wav")); // 각종 클릭 사운드 초기화
        failSound = Gdx.audio.newSound(Gdx.files.internal("sound/fail.wav")); // 각종 실패 사운드 초기화
        succSound = Gdx.audio.newSound(Gdx.files.internal("sound/succ.wav")); // 각종 성공 사운드 초기화

        // 필드 초기화
        idField = new TextField("", skin);
        pwField = new TextField("", skin);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        nameField = new TextField("", skin);
        nameField.getStyle().font = f;
        birthField = new TextField("", skin);
        phoneField = new TextField("", skin);

        //라벨 초기화
        singUpLabel = new Label("SIGN UP", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        idLabel = new Label("I          D", new Label.LabelStyle(f, Color.WHITE));
        pwLabel = new Label("P        W", new Label.LabelStyle(f, Color.WHITE));
        nameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));
        birthLabel = new Label("B I R T H", new Label.LabelStyle(f, Color.WHITE));
        phoneLabel = new Label("P H O N E", new Label.LabelStyle(f, Color.WHITE));
        singUpLabel.setBounds(0, 240, 400, 25);
        singUpLabel.setAlignment(Align.center);

        // 버튼 초기화
        submitBtn = new TextButton( "SUBMIT", skin );
        backBtn = new TextButton( "BACK", skin );

        // 필드 및 라벨, 버튼 위치 초기화
        int LabelStart = 80;
        int FieldStart = 150;
        idLabel.setBounds(LabelStart, 200, 50, 25);
        idField.setBounds(FieldStart, 200, 150, 25);

        pwLabel.setBounds(LabelStart, 170, 50, 25);
        pwField.setBounds(FieldStart, 170, 150, 25);

        nameLabel.setBounds(LabelStart, 140, 50, 25);
        nameField.setBounds(FieldStart, 140, 150, 25);

        birthLabel.setBounds(LabelStart, 110, 50, 25);
        birthField.setBounds(FieldStart, 110, 150, 25);

        phoneLabel.setBounds(LabelStart, 80, 50, 25);
        phoneField.setBounds(FieldStart, 80, 150, 25);

        backBtn.setBounds(210, 40, 80, 25);
        submitBtn.setBounds(110, 40, 80, 25);

        initResultSection();// 결과 창 초기화

        btnsAddListener(); // 버튼 리스너 초기화

        initActorOnStage();// 텍스트 필트 및 라벨 스테이지에 초기화
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
        stage.addActor(idField);
        stage.addActor(pwField);
        stage.addActor(nameField);
        stage.addActor(birthField);
        stage.addActor(phoneField);
        stage.addActor(singUpLabel);
        stage.addActor(idLabel);
        stage.addActor(pwLabel);
        stage.addActor(nameLabel);
        stage.addActor(birthLabel);
        stage.addActor(submitBtn);
        stage.addActor(backBtn);
        stage.addActor(phoneLabel);
        stage.addActor(resultField);
        stage.addActor(resultLabel);
        stage.addActor(resultBtn);
    }

    private void btnsAddListener() { // 리스너 초기화 메소드
        resultBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                clickbtnSound.play(0.7f);
                resultClose();
            }
        });

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(400, 300);
                clickbtnSound.play(0.7f);
                game.setScreen(new LoginScreen(game));
            }
        });


        submitBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    if (client.create(idField.getText(), pwField.getText(), nameField.getText(), birthField.getText(), phoneField.getText())){
                        resultLabel.setText("회원가입 성공!");
                        succSound.play(0.7f);
                        resultShow();
                    }else{
                        resultLabel.setText("회원가입 실패!");
                        failSound.play(0.7f);
                        resultShow();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initForm();
            }
        });
    }

    private void initForm(){ // 폼 초기화
        idField.setText("");
        pwField.setText("");
        nameField.setText("");
        birthField.setText("");
        phoneField.setText("");
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
