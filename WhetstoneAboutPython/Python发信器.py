import socket

def send_message(sock, message: str):
    """发送消息：长度前缀(4字节大端) + UTF-8编码内容"""
    data = message.encode('utf-8')
    length = len(data)
    # 将长度打包为4字节大端整数
    length_prefix = length.to_bytes(4, byteorder='big')
    sock.sendall(length_prefix + data)

def main():

    host = '127.0.0.1'
    port = int(input("输入端口："))
    while True:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((host, port))
            send_message(sock, input("输入信息："))
            # 发送完后主动关闭连接（服务端会读到EOF）



if __name__ == '__main__':
    main()