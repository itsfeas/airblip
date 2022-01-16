import socket
import argparse

HOST = '127.0.0.1'

parser = argparse.ArgumentParser()
parser.add_argument("--port", dest="PORT", default=42069, type=int)
args = parser.parse_args()
PORT = args.PORT


print(HOST)
print(PORT)

count = -1

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print("Connected by", addr)
        while True:
            data = conn.recv(1024)
            print("Server Received:", data)
            if (data.decode() == 'Q'):
                conn.send('A'.encode())
                break
            conn.send('A'.encode())
            count += 1

print("number of packets received:", count)