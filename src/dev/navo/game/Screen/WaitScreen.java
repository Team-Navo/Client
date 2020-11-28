package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.Client.Client;
import dev.navo.game.Client.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.*;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;

public class WaitScreen implements Screen {
    private NavoGame game;

    private Room room;

    private TextButton startBtn;
    private TextButton backBtn;

    private ArrayList<Label> users;
    private String nickname;

    private ShapeRenderer shapeRenderer;

    private Stage stage; // 텍스트 필드나 라벨 올릴 곳

    private Viewport viewport;

    Client client;

    public WaitScreen(NavoGame game, String nickname) throws ParseException {
        this.game = game; // Lig Gdx 게임 클래스 초기화
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera()); // 뷰포트 생성
        stage = new Stage(viewport, game.batch); // 스테이지 생성
        Gdx.input.setInputProcessor(stage); // 스테이지에 마우스 및 키보드 입력을 받기

        users = new ArrayList<>();
        this.nickname = nickname;

        shapeRenderer = new ShapeRenderer();

        initComponent();
        btnsAddListener();

        //client.enter(myCrewmate.getCrewmateInitJson());
        //JSONObject roomInfo = EventBuffer.getInstance().get();
        //room = new Room(world, atlas, roomInfo, hud);

        //client.setIsInGameThread(true);
        //client.updateSender(myCrewmate, room);
        //client.updateReceiver(room, world, atlas, hud);
        //client.eventHandler(room, hud);
    }

    //컴포넌트 초기화
    private void initComponent(){

        startBtn = new TextButton( "START", Util.skin );
        startBtn.setBounds(170, 34,60, 22);

        backBtn = new TextButton( "EXIT", Util.skin );
        backBtn.setBounds(350, 10, 40, 17);

        for(int i = 0 ; i < 5 ; i++){ // Room.getRoom().getCrewmates().size
            Label temp = new Label("", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
            temp.setBounds(80, 200- i * 30, 200, 20);
            users.add(temp);
        }

        for(Label label : users)
            stage.addActor(label);

        stage.addActor(startBtn);
        stage.addActor(backBtn);

    }

    //버튼 리스너
    private void btnsAddListener(){
        startBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                startBtn.clear();
                backBtn.clear();
                Sounds.start.play(); // 게임 시작 사운드 출력
                game.setScreen(new PlayScreen(game)); // PlayScreen으로 넘어가기
                dispose();
            }
        });

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                startBtn.clear();
                backBtn.clear();
                Sounds.click.play();
                game.setScreen(new LobbyScreen(game));
//                client.exit();
                System.out.println("Exit");
                Client.getInstance().exit();
                dispose();
            }
        });


    }

    @Override
    public void show() {

    }

    private void update(float delta){

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        Images.renderBackground(delta, game.batch);
        game.batch.end();

        drawRect();

        game.batch.begin();
        for(int i = 0 ; i < users.size() ; i++){
            CrewmateMulti temp;
            if(i < Room.getRoom().getCrewmates().size() ){
                temp = Room.getRoom().getCrewmates().get(i);
                game.batch.draw(Images.header[0],270,198 - i * 30);
                users.get(i).setText(temp.getName());
                continue;
            }
            users.get(i).setText("");
        }

        game.batch.end();

        stage.draw();
    }

    private void drawRect() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for(int i = 0 ; i < 5 ; i++) {
            shapeRenderer.rect(150, 400 - i * 60, 500, 40);// 사각형 그리기
        }
        shapeRenderer.end();
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
