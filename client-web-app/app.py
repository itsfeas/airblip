from flask import Flask, render_template, jsonify, request, Response, redirect, url_for, session
from src.signalprocess import *
from livereload import Server

app = Flask(__name__)
app.secret_key = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'
app.config['SECRET_KEY'] = 'super secret key'
app.config['TEMPLATES_AUTO_RELOAD'] = True


rec = Recorder()

path = './src/record/'
fileName = 'record.wav'
recTime = 7


@app.route('/')
def index():
    msg = session.get('msg')
    return render_template('index.html', msg=msg)


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
        except:
            msg = 'failed'

        # return jsonify({"msg": msg})
        session['msg'] = msg
        # return redirect(url_for("disp"))
        # return render_template("index.html", msg=msg)
        return redirect("/")

@app.route("/disp")
def disp():
    print(msg)
    return render_template("index_read.html", msg=msg)


if __name__ == '__main__':
    # msg = session.get('msg')
    # msg['msg'] = 'Your Message Is...'
    app.run(host='0.0.0.0', debug=True)
