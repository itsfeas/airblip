import numpy as np
import pandas as pd
import sklearn
import math
from scipy.io.wavfile import write
import sounddevice as sd
import time
import sys
import matplotlib.pyplot as plt
import librosa
import scipy
from scipy.signal import butter, lfilter
import bokeh
from bokeh.plotting import figure, show


BAUD = 200
FREQ =  2000
SAMPLE_RATE = 48000
np.set_printoptions(threshold=sys.maxsize)

def bits_to_sound(bits):
    """
    bits: bits generator
    """
    bit_list = list(bits)
    num_bits = len(bit_list)
    T_bit = 1/BAUD
    total_time = T_bit * num_bits

    out_array = np.zeros((int(total_time * SAMPLE_RATE)))

    for i in range(num_bits):
        
        if bit_list[i] > 0:
            out_array[i*int(T_bit*SAMPLE_RATE):(i+1)*int(T_bit*SAMPLE_RATE)] = wvfrm(FREQ, T_bit)
    
    plt.plot(np.arange(out_array.shape[0]), out_array)
    plt.show()
    return out_array


def starting_byte():

    T_bit = 1/BAUD
    total_time = 8*T_bit
    out_array = np.zeros((int(total_time * SAMPLE_RATE)))
    for i in range(4):
        out_array[2*i* int(T_bit * SAMPLE_RATE):(2*i + 1)* int(T_bit * SAMPLE_RATE)] = wvfrm(FREQ, T_bit)

    return out_array



def wvfrm(freq, time):
    """
    freq: Hz
    time: amount of time to sustain the signal
    """
    sound_array = np.zeros((int(SAMPLE_RATE * time)))
    t = np.linspace(0, time, int(SAMPLE_RATE*time))

    sound_array = np.sin(2 * math.pi * freq * t)
    sound_array = np.int16(sound_array/np.max(np.abs(sound_array)) * 32767)

    return sound_array


def bits(path):
    with open(path, "rb") as f:
        bytes = (b for b in f.read())
        for b in bytes:
            for i in range(8):
                yield (b >> i) & 1

for b in bits("testfile"):
    print(b, end="")
print("")


def butter_bandpass(lowcut, highcut, fs, order=5):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')
    return b, a


def butter_bandpass_filter(data, lowcut, highcut, fs, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    y = lfilter(b, a, data)
    return y



if __name__ == "__main__":
    
    sound = wvfrm(FREQ, 2)
    
    # sd.play(sound, SAMPLE_RATE)
    # time.sleep(4)
    # sd.stop()
    
    with open("testfile", "wb") as testfile:
        bytes = "Hello World".encode("utf-8")
        testfile.write(bytes)


    start_beeps = starting_byte()
    start_beeps = np.concatenate([start_beeps, start_beeps,start_beeps,start_beeps], axis=0)
    sd.play(start_beeps, SAMPLE_RATE)
    time.sleep(2)
    sd.stop()

    sound = bits_to_sound(bits("testfile"))
    sound = np.concatenate([sound, start_beeps], axis=0)

    sd.play(sound, SAMPLE_RATE)
    time.sleep(4)
    sd.stop()

    y, sr = librosa.load("testaudio.mp3")
    input_sound = librosa.resample(y, sr, SAMPLE_RATE)
    print(input_sound.shape, SAMPLE_RATE)


    t = np.linspace(0, input_sound.shape[0]/SAMPLE_RATE, input_sound.shape[0])

    plt.plot(t, input_sound)
    plt.show()


    fft_sound = scipy.fft.fft(input_sound)
    freq_bins = scipy.fft.fftfreq(input_sound.shape[0], 1/SAMPLE_RATE)[0:input_sound.shape[0]//2]

    plt.plot(freq_bins, 2.0/input_sound.shape[0] * np.abs(fft_sound[0:input_sound.shape[0]//2]))
    plt.grid()
    plt.show()

    band_pass_sound = butter_bandpass_filter(input_sound, 1800, 2200, SAMPLE_RATE)
    plt.plot(freq_bins, 2.0/input_sound.shape[0] * np.abs(scipy.fft.fft(band_pass_sound)[0:input_sound.shape[0]//2]))
    plt.grid()
    plt.show()

    plt.plot(t, band_pass_sound)
    plt.show()

    sd.play(input_sound, SAMPLE_RATE)
    time.sleep(4)
    sd.stop()


    sd.play(band_pass_sound, SAMPLE_RATE)
    time.sleep(4)
    sd.stop()

    p = figure()
    p.line(t, band_pass_sound, line_width=2)
    show(p)
   
    write("filtered_testaudio.wav", SAMPLE_RATE, band_pass_sound)