package dev.navo.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Sounds;
import dev.navo.game.Tools.Util;

public class Result {
    private TextField resultField; // 결과 띄우는 배경 필드. 글자를 못쓰게 하고 배경으로 씀(꼼수)
    private Label resultLabel; // 결과를 띄울 라벨
    private TextButton resultBtn; // 결과 창 확인 버튼

    public Result(){
        resultField = new TextField("", Util.skin);
        resultField.setBounds(40, 30, 320, 240);
        resultField.setDisabled(true);
        resultLabel = new Label("", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        resultLabel.setBounds(40, 150, 320, 25);
        resultLabel.setAlignment(Align.center);
        resultBtn = new TextButton( "OK", Util.skin );
        resultBtn.setBounds(160, 50, 80, 25);
        resultClose();// 결과 화면 초기화 후 닫아 놓기

        resultBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) { // 결과 창 닫기 버튼 리스너
                Sounds.click.play(1); // 버튼 클릭 효과음
                resultClose();
            }
        });
    }

    public void setResultLabel(String str){
        resultLabel.setText(str);
    }

    public void resultShow(){ // 결과 창 띄우기
        resultLabel.setVisible(true);
        resultField.setVisible(true);
        resultBtn.setVisible(true);
    }

    public void resultClose(){ // 결과 창 닫기
        resultLabel.setVisible(false);
        resultField.setVisible(false);
        resultBtn.setVisible(false);
    }

    public void resultOnStage(Stage stage){
        stage.addActor(resultField);
        stage.addActor(resultLabel);
        stage.addActor(resultBtn);
    }
}
