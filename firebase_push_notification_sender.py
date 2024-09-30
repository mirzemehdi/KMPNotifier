import json
import requests
import google.auth.transport.requests
from google.oauth2 import service_account

PROJECT_ID = '' #find it in firebase -> project settings -> General
SERVICE_ACCOUNT_FILE = 'service_account.json' #Download service account service file from project settings -> Service Accounts -> Generate new private key

FCM_API_URL = f"https://fcm.googleapis.com/v1/projects/{PROJECT_ID}/messages:send" # DON'T CHANGE THIS
SCOPES = ['https://www.googleapis.com/auth/firebase.messaging'] # DON'T CHANGE THIS


def test_send_data_and_notification_message():
    token = ""
    data = {'key1': 'value2', 'key2': 'value2'}
    title = "Test Notification Title from FCM"
    body = "Test Notification Body message from FCM"
    response = send_data_and_notification_message(token, title, body, data)
    if response.status_code == 200:
        print("Message sent successfully")
    else:
        print("Error sending message:", response.text)


def get_access_token():
    credentials = service_account.Credentials.from_service_account_file(SERVICE_ACCOUNT_FILE, scopes=SCOPES)
    request = google.auth.transport.requests.Request()
    credentials.refresh(request)
    return credentials.token

def send_data_and_notification_message(token, title, body, data):
    access_token = get_access_token()
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Content-Type': 'application/json'
    }
    message = {
        "message": {
            "token": token,
            'notification': {
                'title': title,
                'body': body
            },
            "data": data,
            "apns": {
                 "headers": {
                     "apns-priority": "10"
                 },
                "payload": {
                    "aps": {
                        "badge": 0,
                        # "content-available": 1
                    },
                }
            }
        }
    }
    response = requests.post(FCM_API_URL, headers=headers, data=json.dumps(message))
    return response


def main():
    test_send_data_and_notification_message()

if __name__ == "__main__":
    main()
