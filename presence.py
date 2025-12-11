from pypresence import Presence
import time

client_id = "YOUR_CLIENT_ID"  # ambil dari aplikasi Discord Developer Portal
RPC = Presence(client_id)
RPC.connect()

RPC.update(state="Coding Android", details="Opening Android Studio...", large_image="android_logo")

while True:
    time.sleep(15)
