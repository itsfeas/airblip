from flask import Flask, render_template, jsonify, request, Response, redirect, url_for, session
from src.signalprocess import *
import requests

app = Flask(__name__)
app.secret_key = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'
app.config['SECRET_KEY'] = 'super secret key'
rec = Recorder()

path = './src/record/'
fileName = 'record.wav'
recTime = 2


@app.route('/')
def index():
    return render_template('index.html', msg="Your message is...")


@app.route("/start", methods=['POST'])
def start():
    json = request.get_json()
    print(json)
    global engaged
    if request.method == 'POST':
        print('begin record')
        rec.record(path+fileName, recTime)
        wav = rec.return_np(path+fileName)

        msg = ''
        try:
            msg = audio_to_message(wav)
            msg = clip_string(msg)
            msg = binary_string_to_ascii(msg)
            msg = '23112'
        except:
            msg = 'failed'

        # return jsonify({"msg": msg})
        # return render_template("index_read.html", msg=msg)
        session['msg'] = msg
        # return render_template("index_read.html", msg=msg)
        return redirect(url_for("disp"))


@app.route("/disp")
def disp():
    msg = session.get('msg')
    print(msg)
    return render_template("index_read.html", msg=msg)




if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
