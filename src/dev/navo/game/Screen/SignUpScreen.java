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
import dev.navo.game.Scenes.Result;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Sounds;
import dev.navo.game.Tools.Util;

import java.io.IOException;

public class SignUpScreen implements Screen {

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

    private NavoGame game; // Lib Gdx 게임 클래스 저장할 변수
    private Stage stage; // 텍스트 필드나 라벨 올릴 곳.

    private Viewport viewport; // 화면 뷰포트

    private Client client; // 서버랑 통신하기 위한 클라이언트 소켓 클래스(클라이언트 안에 다 들어 있음)

    private Result resultScene; // 결과 창

    public SignUpScreen(final NavoGame game){
        this.game = game; // Lig Gdx 게임 클래스 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());// 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기

        client = Client.getInstance(); // 서버랑 통신할 클라이언트 가져오기

        initComponent(); // 필드, 라벨, 버튼 초기화

        resultScene = new Result(); // 결과 창 생성

        btnsAddListener(); // 버튼 리스너 초기화

        initActorOnStage();// 텍스트 필트 및 라벨 스테이지에 초기화
        resultScene.resultOnStage(stage);
    }

    private void initComponent(){
        // 필드 초기화
        idField = new TextField("", Util.skin);
        pwField = new TextField("", Util.skin);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        nameField = new TextField("", Util.skin);
        nameField.getStyle().font = FontGenerator.fontBold16;
        birthField = new TextField("", Util.skin);
        phoneField = new TextField("", Util.skin);

        //라벨 초기화
        singUpLabel = new Label("SIGN UP", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        idLabel = new Label("I          D", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        pwLabel = new Label("P        W", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        nameLabel = new Label("N A M E", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        birthLabel = new Label("B I R T H", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        phoneLabel = new Label("P H O N E", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
        singUpLabel.setBounds(0, 240, 400, 25);
        singUpLabel.setAlignment(Align.center);

        // 버튼 초기화
        submitBtn = new TextButton( "SUBMIT", Util.skin );
        backBtn = new TextButton( "BACK", Util.skin );

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
    }

    private void btnsAddListener() { // 리스너 초기화 메소드

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(400, 300);
                Sounds.click.play(1);
                game.setScreen(new LoginScreen(game));
            }
        });


        submitBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    if (client.create(idField.getText(), pwField.getText(), nameField.getText(), birthField.getText(), phoneField.getText())){
                        resultScene.setResultLabel("회원가입 성공!");
                        resultScene.resultShow();
                        Sounds.success.play(1);

                    }else{
                        resultScene.setResultLabel("회원가입 실패!");
                        resultScene.resultShow();
                        Sounds.fail.play(1);
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

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(Images.background, 0 , 0 );
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
