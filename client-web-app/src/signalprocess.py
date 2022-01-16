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
import pyaudio, wave




BAUD = 100
FREQ =  1000
SAMPLE_RATE = 48000
np.set_printoptions(threshold=sys.maxsize)




class Recorder:
    def __init__(self):
        self.recording = False

    
    def record(self, path, length):
        chunk = 1024
        # sample format
        FORMAT = pyaudio.paInt16
        # mono, change to 2 if you want stereo
        channels = 1
        
        # initialize PyAudio object
        p = pyaudio.PyAudio()
        # open stream object as input & output
        stream = p.open(format=FORMAT,
                        channels=channels,
                        rate=SAMPLE_RATE,
                        input=True,
                        output=True,
                        frames_per_buffer=chunk)
        frames = []
        print("Recording...")

        for i in range(int(SAMPLE_RATE / chunk * length)):
        
            data = stream.read(chunk)
            # if you want to hear your voice while recording
            # stream.write(data)
            frames.append(data)
        print("Finished recording.")

        wf = wave.open(path, "wb")
        # set the channels
        wf.setnchannels(1)
        # set the sample format
        wf.setsampwidth(p.get_sample_size(FORMAT))
        # set the sample rate
        wf.setframerate(SAMPLE_RATE)
        # write the frames as bytes
        wf.writeframes(b"".join(frames))
        # close the file
        wf.close()


    def return_np(self, file):
        y, sr = librosa.load(file)
        input_sound = librosa.resample(y, sr, SAMPLE_RATE)
        print(input_sound.shape, SAMPLE_RATE)

        return y

def bits_to_sound(bits):
    """
    bits: bits generator
    """
    bit_list = bits
    num_bits = len(bit_list)
    T_bit = 1/BAUD
    total_time = T_bit * num_bits

    out_array = np.zeros((int(total_time * SAMPLE_RATE)))

    for i in range(num_bits):
        
        if bit_list[i] > 0:
            out_array[i*int(T_bit*SAMPLE_RATE):(i+1)*int(T_bit*SAMPLE_RATE)] = wvfrm(FREQ, T_bit)
    
    # plt.plot(np.arange(out_array.shape[0]), out_array)
    # plt.show()
    return out_array


def starting_byte():

    T_bit = 1/BAUD
    total_time = 8*T_bit
    out_array = np.zeros((int(total_time * SAMPLE_RATE)))
    for i in range(4):
        out_array[2*i* int(T_bit * SAMPLE_RATE):(2*i + 1)* int(T_bit * SAMPLE_RATE)] = wvfrm(FREQ, T_bit)

    return out_array

def ending_byte():
    T_bit = 1/BAUD
    total_time = 8*T_bit
    out_array = np.zeros((int(total_time * SAMPLE_RATE)))
    for i in range(8):
        out_array[i* int(T_bit * SAMPLE_RATE):(i + 1)* int(T_bit * SAMPLE_RATE)] = wvfrm(FREQ, T_bit)

    return out_array



def wvfrm(freq, time):
    """
    freq: Hz
    time: amount of time to sustain the signal
    """
    sound_array = np.zeros((int(SAMPLE_RATE * time)))
    t = np.linspace(0, time, int(SAMPLE_RATE * time))

    sound_array = np.sin(2 * math.pi * freq * t)
    sound_array = np.int16(sound_array/np.max(np.abs(sound_array)) * 32767)

    return sound_array


def bits(path):
    l_bits = []

    with open(path, "rb") as f:
        bytes = (b for b in f.read())
        for b in bytes:
            for i in range(8):
                l_bits.append((b >> i) & 1)
    l_bits.reverse()
    return l_bits

def print_binary(path):
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


def moving_average_filter(data, scale=1):
    T_bit = 1/BAUD
    return np.convolve(data, np.ones(int(scale * T_bit * SAMPLE_RATE))/(scale * T_bit*SAMPLE_RATE), mode="same")

def rms_filter(data):
    a2 = np.power(data,2)
    T_bit = 1/BAUD
    window = np.ones(int(T_bit * SAMPLE_RATE))/float(int(T_bit * SAMPLE_RATE))
    return np.sqrt(np.convolve(a2, window, 'same'))

def find_peak_midpoint(data, mean):

    rise = 0
    fall = 0
    detect_fall = False
    for i in range(data.shape[0]):
        if not detect_fall:
            if data[i] > mean:
                rise = i 
                detect_fall = True
        else:
            if data[i] < mean:
                fall = i 
                break 

        mid = (fall + rise)//2
        return mid



def crop(data):
    avg = rms_filter(data)
    low_bound = 0
    high_bound = 1000000000
    for i in range(data.shape[0]):
        if avg[i] > 0.05:
            low_bound = i
            break
    for i in reversed(range(data.shape[0])):
        if avg[i] > 0.05:
            high_bound = i
            break

    return data[low_bound:high_bound], low_bound, high_bound

def clip_front(string):
    next_state = False
    if string[0] == "0":
        next_state = True

    idx = 0
    for i in range(1, len(string)):
        if string[i] == str(int(next_state)):
            next_state = not next_state
        else:
            idx = i
            break
    return string[idx:]

def clip_back(string):
    idx = string.find("10101010")
    return string[:idx]

def clip_string(string):
    return clip_back(clip_front(string))



def audio_to_message(data):
    t = np.linspace(0, data.shape[0]/SAMPLE_RATE, data.shape[0])
    cleaned = butter_bandpass_filter(data, FREQ-100, FREQ+100, SAMPLE_RATE)



    cleaned , low, high = crop(cleaned)
    t_cropped = t[low:high]

    avg = rms_filter(cleaned)
    mean_avg = np.mean(avg)

    pseudo_binary = np.zeros(avg.shape).astype(int)
    pseudo_binary[avg > mean_avg] = 1

    # p = figure()

    # p.line(t_cropped, cleaned, line_width=2)



    # p.line(t_cropped, avg, line_width=2, line_color="orange")



    # p.line(t_cropped, mean_avg, line_width=2, line_color="green")

    T_bit = 1/BAUD
    step = int(T_bit * SAMPLE_RATE)
    
    points = []

    binary = ""
    ind = find_peak_midpoint(avg, mean_avg)
    while ind < pseudo_binary.shape[0]:

        binary += str(pseudo_binary[ind])
        points.append(t_cropped[ind])
        ind += step
    # p.circle(points, mean_avg, color="red")

    # show(p)

    return binary


def binary_string_to_ascii(string):

    j = 0
    chars = []
    bin_char = ""
    for b in string:
        
        bin_char += b

        j+=1
        if j >= 8:
            j=0
            integer = int(bin_char, 2)
            ascii_char = chr(integer)
            chars.append(ascii_char)
            bin_char = ""

    chars.reverse()
    return "".join(chars)



if __name__ == "__main__":
    
    sound = wvfrm(FREQ, 2)
    
    # sd.play(sound, SAMPLE_RATE)
    # time.sleep(4)
    # sd.stop()
    
    with open("testfile", "wb") as testfile:
        bytes = "Hello World".encode("utf-8")
        testfile.write(bytes)

    print_binary("testfile")


    # plays the binary encoding of the contents of testfile
    start_beeps = starting_byte()
    start_beeps = np.concatenate([start_beeps, start_beeps,start_beeps,start_beeps], axis=0)

    end_beeps = starting_byte()
    end_beeps = np.concatenate([end_beeps, end_beeps, end_beeps, end_beeps], axis=0)

    sound = bits_to_sound(bits("testfile"))
    sound = np.concatenate([start_beeps, sound, end_beeps], axis=0)

    sd.play(sound, SAMPLE_RATE)
    time.sleep(4)
    sd.stop()


    # Records for 6 seconds, saves to sample.wav
    rec = Recorder()
    rec.record("sample.wav", 6)


    # loads from sample.wav
    y, sr = librosa.load("sample.wav")
    input_sound = librosa.resample(y, sr, SAMPLE_RATE)
    print(input_sound.shape, SAMPLE_RATE)

    # process back into bits, then into text
    decoded_raw = audio_to_message(input_sound)
    decoded_cleaned = clip_string(decoded_raw) # gets rid of end pieces
    print(decoded_cleaned)
    print(binary_string_to_ascii(decoded_cleaned))



