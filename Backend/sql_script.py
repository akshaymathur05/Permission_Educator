# -*- coding: utf-8 -*-
"""
Created on Tue Sep 22 17:25:44 2020

@author: aksha
"""

import mysql.connector

mydb = mysql.connector.connect(
  host="localhost",
  user="akshay",
  password="Cstar_2033",
  database="naticus"
)

cursor = mydb.cursor()

#INSERT/UPDATE/CREATE/DELETE
cursor.execute("CREATE DATABASE naticus")
cursor.execute("DROP TABLE applications")
cursor.execute("CREATE TABLE applications (app_id INTEGER PRIMARY KEY AUTO_INCREMENT, app_name VARCHAR(255), app_about LONGTEXT, app_perms LONGTEXT, app_classification VARCHAR(10))")


cursor.execute("DROP TABLE permissions")
cursor.execute("CREATE TABLE permissions (perm_name VARCHAR(255) PRIMARY KEY, perm_about LONGTEXT, perm_category VARCHAR(20))")



#SELECT/SHOW
cursor.execute("SHOW TABLES")
cursor.execute("SHOW COLUMNS FROM applications")
cursor.execute("SELECT * from applications")
cursor.execute("SHOW COLUMNS FROM permissions")
cursor.execute("SELECT * from permissions")


#PRINT RESULTS
result = cursor.fetchall()
for x in result:
    print(x) 
    print("\n")
    
    
    
    



#Permission Table Entries
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCEPT_HANDOVER', 'Allows a calling app to continue a call which was started in another app. An example is a video calling app that wants to continue a voice call on the users mobile network.\nWhen the handover of a call from one app to another takes place, there are two devices which are involved in the handover; the initiating and receiving devices. The initiating device is where the request to handover the call was started, and the receiving device is where the handover request is confirmed by the other party.','Dangerous')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_BACKGROUND_LOCATION', 'Allows an app to access location in the background. If you are requesting this permission, you must also request either ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION. Requesting this permission by itself does not give you location access.','Dangerous')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_CHECKIN_PROPERTIES', 'Allows read/write access to the PROPERTIES table in the checkin database, to change values that get uploaded.','Signature')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_COARSE_LOCATION', 'Allows an app to access approximate geographical location.','Dangerous')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.READ_CONTACTS', 'Allows an application to read the users Contacts.','Dangerous')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.INTERNET', 'Allows applications to open network sockets.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_FINE_LOCATION', 'Allows applications to open network sockets.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE', 'Used by Firebase to recognize where the app was installed from.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('com.android.vending.BILLING', 'An interface for sending In-app Billing requests and managing In-app Billing transactions using Google Play.','Dangerous')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.WAKE_LOCK', 'Allows using PowerManager WakeLocks to keep processor from sleeping or screen from dimming.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.RECEIVE_BOOT_COMPLETED', 'Allows an application to receive a broadcast after the system finishes booting. Though holding this permission has no security implications, it can have a negative impact on the user experience.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_WIFI_STATE', 'Allows applications to access information about Wi-Fi networks.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.ACCESS_NETWORK_STATE', 'Allows applications to access information about networks.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.VIBRATE', 'Allows access to the vibrator.','Normal')")
mydb.commit()
cursor.execute("INSERT INTO permissions VALUES('android.permission.SYSTEM_ALERT_WINDOW', 'Allows an app to create windows shown on top of all other apps. Very few apps should use this permission; these windows are intended for system-level interaction with the user.','Signature')")
mydb.commit()




mydb.close()