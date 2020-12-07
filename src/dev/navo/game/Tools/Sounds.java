package dev.navo.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
    public static final Sound start = Gdx.audio.newSound(Gdx.files.internal("sound/gamestart.wav")); // 게임 시작 소리

    public static final Sound background = Gdx.audio.newSound(Gdx.files.internal("sound/loginbgm.wav")); // 배경음악
    public static final Sound fail = Gdx.audio.newSound(Gdx.files.internal("sound/fail.wav")); // 실패 소리
    public static final Sound success = Gdx.audio.newSound(Gdx.files.internal("sound/succ.wav")); // 성공 소리

    public static final Sound click = Gdx.audio.newSound(Gdx.files.internal("sound/clickbtn.wav")); // 클릭 버튼 소리
    public static final Sound hover = Gdx.audio.newSound(Gdx.files.internal("sound/hover.wav")); // hover 사운드

    public static final Sound wait = Gdx.audio.newSound(Gdx.files.internal("sound/waitsound.wav")); // 대기실 입장 사운드

    public static final Sound footstep = Gdx.audio.newSound(Gdx.files.internal("sound/footstep.wav")); // 발소리
    public static final Sound gunShotSound = Gdx.audio.newSound(Gdx.files.internal("sound/gunshot.wav")); // 총알 발사 사운드
    public static final Sound kill = Gdx.audio.newSound(Gdx.files.internal("sound/killsound.wav")); // 사망 사운드

    public static final Sound hit = Gdx.audio.newSound(Gdx.files.internal("sound/hit.mp3")); // 피격 사운드
    public static final Sound magnetic = Gdx.audio.newSound(Gdx.files.internal("sound/magnetic.mp3")); // 자기장 사운드

    public static final Sound getGun = Gdx.audio.newSound(Gdx.files.internal("sound/gunshot2.wav")); // 총 획득 사운드 사운드
    public static final Sound hp = Gdx.audio.newSound(Gdx.files.internal("sound/hp.mp3")); // HP 사운드
    public static final Sound speed = Gdx.audio.newSound(Gdx.files.internal("sound/speed.mp3")); // Speed up사운드

}
