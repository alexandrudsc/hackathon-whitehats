#!/usr/bin/env python
"""
Multi-threaded TCP Server
multithreadedServer.py acts as a standard TCP server, with multi-threaded integration, spawning a new thread for each
client request it receives.
"""

import requests
import json
from argparse import ArgumentParser
from threading import Lock, Thread
from socket import SO_REUSEADDR, SOCK_STREAM, socket, SOL_SOCKET, AF_INET

#---------------------------------------#
########## USER INPUT HANDLING ##########
#---------------------------------------#

# Initialize instance of an argument parser
parser = ArgumentParser(description='Multi-threaded TCP Server')

# Add optional argument, with given default values if user gives no arg
parser.add_argument('-p', '--port', default=9000, type=int, help='Port over which to connect')

# Get the arguments
args = parser.parse_args()

# -------------------------------------------#
########## DEFINE GLOBAL VARIABLES ##########
#-------------------------------------------#

counter = 1
response_message = "Response no : "
thread_lock = Lock()

# Create a server TCP socket and allow address re-use
s = socket(AF_INET, SOCK_STREAM)
s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
s.bind(('localhost', args.port))

# Create a list in which threads will be stored in order to be joined later
threads = []

headers = {
    'Content-type': 'application/json',
}
#---------------------------------------------------------#
########## THREADED CLIENT HANDLER CONSTRUCTION ###########
#---------------------------------------------------------#


class ClientHandler(Thread):
    def __init__(self, address, port, socket, response_message, lock):
        Thread.__init__(self)
        self.address = address
        self.port = port
        self.socket = socket
        self.response_message = response_message
        self.lock = lock

    # Define the actions the thread will execute when called.
    def run(self):
        print("--> User connect ( " + self.address + ":" + str(self.port) + " )")

        data = self.socket.recv(1024)
        
        while data.strip() != "exit":
            
            try:
                if "post" in data:
                    data = data.replace("post", "")
                    d = data.split()
                    
                    data_input = {"name":d[0],"email":d[1],"token":d[2],"phone":d[3],"friends":{"_id":d[4],"name":d[5]}}

                    print data_input
                    r = requests.post(url = "http://192.168.220.14:8080/api/users", headers = headers, data = json.dumps(data_input))
                    print(r.content, r.status_code, r.reason)


                elif "delete" in data:
                    data = data.replace("delete","")
                    
                    data_input = data.strip()
                    
                    print data_input
                    
                    r = requests.delete(url = "http://192.168.220.14:8080/api/user/" + data_input) #, headers = headers, data = data_input)
                    
                    print(r.content, r.status_code, r.reason)


                    
                    
                    
                else:
                    # if data is not received break
                    print("--- From connected user ( " + self.address + ":" + str(self.port) + " ) : " + str(data))
                    global counter
                    self.socket.send(self.response_message + str(counter) + "\n")
                    
                    #content = urllib2.urlopen("http://www.python.org").read()
                    #self.socket.send("Content = " + content)

                    # Lock the changing of the shared counter value to prevent erratic multithread changing behavior
                    with self.lock:
                        counter += 1
                
                data = self.socket.recv(1024)
                
            except KeyboardInterrupt:
                break

        print("<-- User disconnect ( " + self.address + ":" + str(self.port) + " )")
        self.socket.close()

#-----------------------------------------------#
########## MAIN-THREAD SERVER INSTANCE ##########
#-----------------------------------------------#

# Continuously listen for a client request and spawn a new thread to handle every request
while 1:

    try:
        # Listen for a request
        s.listen(1)
        # Accept the request
        sock, addr = s.accept()
        # Spawn a new thread for the given request
        newThread = ClientHandler(addr[0], addr[1], sock, response_message, thread_lock)
        newThread.start()
        threads.append(newThread)
    except KeyboardInterrupt:
        print "\nExiting Server\n"
        break

# When server ends gracefully (through user keyboard interrupt), wait until remaining threads finish
for item in threads:
    item.join()
exit(0)
