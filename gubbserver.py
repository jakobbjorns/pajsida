from socket import *
server=socket()
server.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
server.bind(('', 50000))
server.listen(1)
#server.settimeout(10)


client = socket()
client.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
client.bind(('', 50001))
client.listen(1)
#client.settimeout(10)


try:
    while 1:
        server_conn, server_addr = server.accept()
        print(server_addr)
        client_conn, client_addr = client.accept()
        print("Anslutning från",client_addr)
        data = client_conn.recv(1024)
        try:
            server_conn.send(data)
        except BrokenPipeError:
            print("fel på serveranslutning")
            server_conn, server_addr = server.accept()
            print(server_addr)
        while 1:
            try:
                data = server_conn.recv(1024)
                client_conn.send(data)
                if not data:
                    break
            except BrokenPipeError:
                print("fel på klientanslutning")
                break
        print("klar")
        client_conn.close()
except KeyboardInterrupt:
    server_conn.close()
    client_conn.close()

