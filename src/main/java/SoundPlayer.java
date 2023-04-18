import java.io.InputStream;
import java.util.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class SoundPlayer {
    private List<String> skillSounds;
    private List<String> burstSounds;
    private List<String> holdSounds;
    private List<String> sprintSounds;
    private List<String> jumpSounds;
    private Map<Integer, List<String>> attackSounds;
    private Random random;
    private boolean isPlaying = false;

    public SoundPlayer() {
        skillSounds = new ArrayList<>();
        skillSounds.add("skill1.wav");
        skillSounds.add("skill2.wav");
        skillSounds.add("skill3.wav");

        burstSounds = new ArrayList<>();
        burstSounds.add("burst1.wav");
        burstSounds.add("burst2.wav");
        burstSounds.add("burst3.wav");

        holdSounds = new ArrayList<>();
        holdSounds.add("hold1.wav");
        holdSounds.add("hold2.wav");

        sprintSounds = new ArrayList<>();
        sprintSounds.add("sprint1.wav");
        sprintSounds.add("sprint2.wav");

        jumpSounds = new ArrayList<>();
        jumpSounds.add("jump1.wav");
        jumpSounds.add("jump2.wav");

        attackSounds = new HashMap<>();
        attackSounds.put(1, Arrays.asList("a1-1.wav", "a1-2.wav", "a1-3.wav"));
        attackSounds.put(2, Arrays.asList("a2-1.wav", "a2-2.wav"));
        attackSounds.put(3, Arrays.asList("a3-1.wav", "a3-2.wav"));
        attackSounds.put(4, Arrays.asList("a4-1.wav", "a4-2.wav"));
        attackSounds.put(5, Arrays.asList("a5-1.wav", "a5-2.wav", "a5-3.wav"));

        random = new Random();
    }

    public void playRandomAttackLine(int nA) {
        String sound = attackSounds.get(nA).get(random.nextInt(attackSounds.get(nA).size()));
        if (!isPlaying) playSound(sound);
    }

    public void playRandomSprintLine() {
        int randomChoice = random.nextInt(sprintSounds.size());
        if (randomChoice == 1) {
            String sound = sprintSounds.get(random.nextInt(sprintSounds.size()));
            if (!isPlaying) playSound(sound);
        }
    }

    public void playRandomJumpLine() {
        int randomChoice = random.nextInt(jumpSounds.size());
        if (randomChoice == 1) {
            String sound = jumpSounds.get(random.nextInt(jumpSounds.size()));
            if (!isPlaying) playSound(sound);
        }
    }

    public void playRandomHoldLine() {
        String sound = holdSounds.get(random.nextInt(holdSounds.size()));
        playSound(sound);
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
            isPlaying = true;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sounds/" + fileName);
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    isPlaying = false;
                }
            });
            clip.open(AudioSystem.getAudioInputStream(inputStream));
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}
