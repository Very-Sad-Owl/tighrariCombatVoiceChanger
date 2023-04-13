import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
    private List<String> skillSounds;
    private List<String> burstSounds;
    private Random random;

    public SoundPlayer() {
        skillSounds = new ArrayList<>();
        skillSounds.add("skill1.wav");
        skillSounds.add("skill2.wav");
        skillSounds.add("skill3.wav");

        burstSounds = new ArrayList<>();
        burstSounds.add("burst1.wav");
        burstSounds.add("burst2.wav");
        burstSounds.add("burst3.wav");

        random = new Random();
    }

    public void playRandomSkillLine() {
        String sound = skillSounds.get(random.nextInt(skillSounds.size()));
        playSound(sound);
    }

    public void playRandomBurstLine() {
        String sound = burstSounds.get(random.nextInt(burstSounds.size()));
        playSound(sound);
    }

    private void playSound(String fileName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sounds/" + fileName);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(inputStream));
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}
