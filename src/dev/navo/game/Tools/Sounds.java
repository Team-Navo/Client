package dev.navo.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
    public static final Sound background = Gdx.audio.newSound(Gdx.files.internal("sound/loginbgm.wav")); // 배경음악
    public static final Sound fail = Gdx.audio.newSound(Gdx.files.internal("sound/fail.wav")); // 실패 소리
    public static final Sound success = Gdx.audio.newSound(Gdx.files.internal("sound/succ.wav")); // 성공 소리
    public static final Sound click = Gdx.audio.newSound(Gdx.files.internal("sound/clickbtn.wav")); // 클릭 버튼 소리

}
