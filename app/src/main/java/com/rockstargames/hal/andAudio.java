package com.rockstargames.hal;

import java.util.ArrayList;
import java.util.List;

public class andAudio {
    private static List<andAudioSample> samples = new ArrayList();
    public static boolean m_audioMuted = false;

    public static void MuteAllAudio(boolean mute) {
        m_audioMuted = mute;
        for (int i = 0; i < samples.size(); i++) {
            andAudioSample sample = samples.get(i);
            if (sample != null) {
                sample.Mute(mute);
            }
        }
    }

    public static int PlayAudioFile(String file, float volume, String category, boolean autoRemove, int loopCount) {
        andAudioSample newSample = new andAudioSample(file, volume, category, autoRemove, loopCount);
        if (newSample.GetMediaPlayer() != null) {
            samples.add(newSample);
            newSample.Play();
            return newSample.getId();
        }
        return -1;
    }

    public static void StopCategory(String category) {
        for (int i = 0; i < samples.size(); i++) {
            andAudioSample sample = samples.get(i);
            if (sample != null && sample.GetCategory().equalsIgnoreCase(category)) {
                sample.Stop();
            }
        }
    }

    public static void UpdateSamples() {
        int size = samples.size();
        for (int i = size - 1; i >= 0; i--) {
            andAudioSample sample = samples.get(i);
            if (sample != null && sample.GetMediaPlayer() == null) {
                if (sample.AutoRemove()) {
                    samples.remove(i);
                } else {
                    samples.set(i, null);
                }
            }
        }
    }

    public static int getSample(int id) {
        for (int i = 0; i < samples.size(); i++) {
            if (samples.get(i) != null && samples.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public static boolean IsPlaying(int id) {
        andAudioSample sample;
        if (getSample(id) == -1 || (sample = samples.get(getSample(id))) == null) {
            return false;
        }
        return sample.IsPlaying();
    }

    public static void ReleaseHandle(int id) {
        int index = getSample(id);
        if (index != -1) {
            andAudioSample sample = samples.get(index);
            if (sample != null) {
                sample.Stop();
            }
            samples.remove(index);
        }
    }

    public static void Stop(int id) {
        andAudioSample sample;
        if (getSample(id) != -1 && (sample = samples.get(getSample(id))) != null) {
            sample.Stop();
        }
    }
}