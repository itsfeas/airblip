from flask import Flask, render_template, jsonify, request, Response

app = Flask(__name__)

path = './web-app'


@app.route('/')
def index():
    return render_template('index.html')


@app.route("/record", methods=['POST'])
def record():
    json = request.get_json()
    print(json)
    len = json.time
    path = json.path
    #record()
    #analyze()
    global engaged
    if request.method == 'POST':
        message = {'message': 'Switched!'}
        return jsonify(message)


# @app.route("/left", methods=['GET'])
# def left():
#     global engaged, serialOn
#     if request.method == 'GET' and not engaged and serialOn:
#         message = {'message': 'Moved Left!'}
#         shift_left()
#         return jsonify(message)

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
